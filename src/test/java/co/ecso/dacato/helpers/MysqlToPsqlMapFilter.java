package co.ecso.dacato.helpers;

/**
 * MysqlToPsqlMapFilter.
 *
 * @author Christian Senkowski (cs@2scale.net)
 * @version $Id:$
 * @since 25.04.16
 */
public final class MysqlToPsqlMapFilter {

    private MysqlToPsqlMapFilter() {
        //not needed
    }

    public static String filter(final String s) {
        return s
                .replaceAll("/\\*.*?\\*/", "")
                .replaceAll("`|´", "")
                .replaceAll("(?i)\\) ENGINE.*?;", ");")
                .replaceAll("(?i)BIGINT.*?\\([0-9]+\\).*?AUTO_INCREMENT", "BIGSERIAL")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\).*?AUTO_INCREMENT", "SERIAL")
                .replaceAll("(?i)BIGINT.*?\\([0-9]+\\)", "BIGINT")
                .replaceAll("(?i)(TINY)?INT.*?\\([0-9]+\\)", "INTEGER")
                .replaceAll("(?i)AUTO_INCREMENT", "")
                .replaceAll("(?i)DATETIME", "TIMESTAMP")
                .replaceAll("(?i)NOT NULL", "")
                .replaceAll("(?i)((VAR)?CHAR)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)(LONGTEXT)([\\s ]+)?(\\([0-9]+\\))?", "TEXT")
                .replaceAll("(?i)UNSIGNED", "")
                .replaceAll("\\),\\)", "))");
    }

}