package co.ecso.jdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * SingleReturnFinder.
 *
 * @param <T> value to Return, i.E. Long
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
public interface SingleReturnFinder<T> extends ConfigGetter, StatementFiller {

    default CompletableFuture<List<T>> find(final SingleFindQuery<T> query) {
        final DatabaseField<T> columnToSelect = query.columnSelect();
        final List<DatabaseField<?>> columnsWhere = query.columnsWhere();
        final CompletableFuture<?> whereFuture = query.whereFuture();

        final CompletableFuture<List<T>> returnValueFuture = new CompletableFuture<>();

        final List<Object> format = new ArrayList<>();
        whereFuture.thenAccept(whereColumn -> {
            format.add(columnToSelect);
            format.addAll(columnsWhere);
            //find a way to find out if format.toArray has the right amount of entries needed to solve query.query()
            final String finalQuery = String.format(query.query(), format.toArray());
            try (Connection c = config().getConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery)) {
                    returnValueFuture.complete(getResult(finalQuery, columnToSelect,
                            fillStatement(finalQuery, columnsWhere, stmt)));
                }
            } catch (final Exception e) {
                returnValueFuture.completeExceptionally(e);
            }
        });
        return returnValueFuture;
    }

    //* @todo map back to DatabaseField with value rather than types.
    default List<T> getResult(final String finalQuery, final DatabaseField<T> columnToSelect,
                              final PreparedStatement stmt) throws SQLException {
        final List<T> rvalList = new LinkedList<>();
        try (final ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                final T rval = (T) rs.getObject(1, columnToSelect.valueClass());
                if (rval == null) {
                    rvalList.add(null);
                } else {
                    if (columnToSelect.valueClass() == String.class) {
                        //noinspection unchecked
                        rvalList.add((T) rval.toString().trim());
                    } else if (columnToSelect.valueClass() == Boolean.class) {
                        final Boolean boolVal = rval.toString().trim().equals("1");
                        //noinspection unchecked
                        rvalList.add((T) boolVal);
                    } else {
                        rvalList.add(rval);
                    }
                }
            }
        }
        return rvalList;
    }

}