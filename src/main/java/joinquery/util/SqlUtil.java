package joinquery.util;

public class SqlUtil {


    public static void keepColumnSafely(String column) {
        if (StringUtil.isBlank(column)) {
            throw new IllegalArgumentException("Column must not be empty");
        } else {
            column = column.trim();
        }

        int strLen = column.length();
        for (int i = 0; i < strLen; ++i) {
            char ch = column.charAt(i);
            if (Character.isWhitespace(ch)) {
                throw new IllegalArgumentException("Column must not has space char.");
            }
            if (isUnSafeChar(ch)) {
                throw new IllegalArgumentException("Column has unsafe char: [" + ch + "].");
            }
        }
    }


    /**
     * 仅支持字母、数字、下划线、空格、逗号、小数点（支持多个字段排序）
     */
    private static String SQL_ORDER_BY_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public static void keepOrderBySqlSafely(String value) {
        if (!value.matches(SQL_ORDER_BY_PATTERN)) {
            throw new IllegalArgumentException("Order By sql not safe, order by string: " + value);
        }
    }


    private static final char[] UN_SAFE_CHARS = "'`\"<>&*+=#-;".toCharArray();

    private static boolean isUnSafeChar(char ch) {
        for (char c : UN_SAFE_CHARS) {
            if (c == ch) {
                return true;
            }
        }
        return false;
    }


}
