package co.ecso.jdao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * StatementFiller.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 02.09.16
 */
interface StatementFiller {

    default PreparedStatement fillStatement(final String finalQuery, final List<DatabaseField<?>> columnsWhere,
                                            final List<?> valuesWhere, final PreparedStatement stmt)
            throws SQLException {
        // System.out.println("SETTING in " + finalQuery + ": " + Arrays.toString(columnsWhere.toArray()));
        for (int i = 0; i < columnsWhere.size(); i++) {
            final Object valueToSet = valuesWhere.get(i);
            final int sqlType = columnsWhere.get(i).sqlType();
            try {
                // System.out.println("SETTING " + valueToSet + " to " + sqlType);
                stmt.setObject(i + 1, valueToSet, sqlType);
            } catch (final SQLException e) {
                throw new SQLException(String.format("Could not set %s to %d: %s", valueToSet, sqlType, e));
            }
        }
        return stmt;
    }

}
