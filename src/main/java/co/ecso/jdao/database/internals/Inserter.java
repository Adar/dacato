package co.ecso.jdao.database.internals;

import co.ecso.jdao.config.ConfigGetter;
import co.ecso.jdao.database.DatabaseEntity;
import co.ecso.jdao.database.query.DatabaseField;
import co.ecso.jdao.database.query.DatabaseResultField;
import co.ecso.jdao.database.query.InsertQuery;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Inserter.
 *
 * @param <T> Type of insert, p.e. Long -> type to return.
 * @param <R> DatabaseEntity which is being returned. For future use.
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 12.09.16
 */
public interface Inserter<T, R extends DatabaseEntity<T>> extends ConfigGetter {

    /**
     * Statement filler.
     *
     * @return Statement filler.
     */
    default StatementFiller statementFiller() {
        return new StatementFiller() {
        };
    }

    /**
     * Add.
     *
     * @param query Query.
     * @return DatabaseResultField of type T.
     */
    default CompletableFuture<DatabaseResultField<T>> add(final InsertQuery<T> query) {

        final CompletableFuture<DatabaseResultField<T>> returnValueFuture = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            final List<DatabaseField<?>> keys = new LinkedList<>();
            keys.add(query.columnToReturn());
            keys.addAll(query.values().keySet());
            final String finalQuery = String.format(query.query(), keys.toArray());
            try (final Connection c = config().databaseConnectionPool().getConnection()) {
                try (final PreparedStatement stmt = c.prepareStatement(finalQuery, Statement.RETURN_GENERATED_KEYS)) {
                    statementFiller().fillStatement(keys, new LinkedList<>(query.values().values()), stmt);
                    returnValueFuture.complete(getResult(finalQuery, query.columnToReturn(), stmt));
                }
            } catch (final SQLException e) {
                returnValueFuture.completeExceptionally(e);
            }
        }, config().threadPool());

        return returnValueFuture;
    }

    /**
     * Get result.
     *
     * @param finalQuery Final query.
     * @param columnToSelect Column to select.
     * @param stmt Statement.
     * @return DatabaseResultField of type T.
     * @throws SQLException if sql fails.
     */
    default DatabaseResultField<T> getResult(final String finalQuery,
                                             final DatabaseField<T> columnToSelect,
                                             final PreparedStatement stmt) throws SQLException {
        stmt.executeUpdate();
        try (final ResultSet generatedKeys = stmt.getGeneratedKeys()) {
            if (!generatedKeys.next()) {
                throw new SQLException(String.format("Query %s failed, resultset empty", finalQuery));
            }
            //noinspection unchecked
            return new DatabaseResultField<>(columnToSelect, generatedKeys.getObject(1, columnToSelect.valueClass()));
        }
    }

}