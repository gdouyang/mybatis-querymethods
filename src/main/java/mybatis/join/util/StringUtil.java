package mybatis.join.util;


import java.util.Collection;
import java.util.function.Function;
import java.util.regex.Pattern;

public class StringUtil {


    /**
     * 第一个字符转换为小写
     *
     * @param string
     */
    public static String firstCharToLowerCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = string.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return string;
    }


    /**
     * 第一个字符转换为大写
     *
     * @param string
     */
    public static String firstCharToUpperCase(String string) {
        char firstChar = string.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = string.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return string;
    }


    /**
     * 驼峰转下划线格式
     *
     * @param string
     */
    public static String camelToUnderline(String string) {
        if (isBlank(string)) {
            return "";
        }
        int strLen = string.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = string.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /**
     * 下划线转驼峰格式
     *
     * @param string
     */
    public static String underlineToCamel(String string) {
        if (isBlank(string)) {
            return "";
        }
        String temp = string.toLowerCase();
        int strLen = temp.length();
        StringBuilder sb = new StringBuilder(strLen);
        for (int i = 0; i < strLen; i++) {
            char c = temp.charAt(i);
            if (c == '_') {
                if (++i < strLen) {
                    sb.append(Character.toUpperCase(temp.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 字符串为 null 或者内部字符全部为 ' ', '\t', '\n', '\r' 这四类字符时返回 true
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        }

        for (int i = 0, len = str.length(); i < len; i++) {
            if (str.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }


    public static boolean isAnyBlank(String... strings) {
        if (strings == null || strings.length == 0) {
            throw new IllegalArgumentException("args is empty.");
        }

        for (String str : strings) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }


    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }


    public static boolean areNotBlank(String... strings) {
        return !isAnyBlank();
    }


    /**
     * 这个字符串是否是全是数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (isBlank(str)) {
            return false;
        }
        for (int i = str.length(); --i >= 0; ) {
            int chr = str.charAt(i);
            if (chr < 48 || chr > 57) {
                return false;
            }
        }
        return true;
    }


    public static boolean startsWithAny(String str, String... prefixes) {
        if (isBlank(str) || prefixes == null || prefixes.length == 0) {
            return false;
        }

        for (String prefix : prefixes) {
            if (str.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }


    public static boolean endsWithAny(String str, String... suffixes) {
        if (isBlank(str) || suffixes == null || suffixes.length == 0) {
            return false;
        }

        for (String suffix : suffixes) {
            if (str.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 正则匹配
     *
     * @param regex
     * @param input
     * @return
     */
    public static boolean matches(String regex, String input) {
        if (null == regex || null == input) {
            return false;
        }
        return Pattern.matches(regex, input);
    }

    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param elements
     * @return 新拼接好的字符串
     * @see String#join(CharSequence, CharSequence...)
     */
    public static String join(String delimiter, CharSequence... elements) {
        if (elements == null || elements.length == 0) {
            return "";
        } else if (elements.length == 1) {
            return String.valueOf(elements[0]);
        } else {
            return String.join(delimiter, elements);
        }
    }


    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param elements
     * @return 新拼接好的字符串
     * @see String#join(CharSequence, CharSequence...)
     */
    public static String join(String delimiter, Collection<? extends CharSequence> elements) {
        if (elements == null || elements.isEmpty()) {
            return "";
        } else if (elements.size() == 1) {
            return String.valueOf(elements.iterator().next());
        } else {
            return String.join(delimiter, elements);
        }
    }

    /**
     * 合并字符串，优化 String.join() 方法
     *
     * @param delimiter
     * @param objs
     * @param function
     * @param <T>
     * @return
     */
    public static <T> String join(String delimiter, Collection<T> objs, Function<T, String> function) {
        if (objs == null || objs.isEmpty()) {
            return "";
        } else if (objs.size() == 1) {
            T next = objs.iterator().next();
            return String.valueOf(function.apply(next));
        } else {
            String[] strings = new String[objs.size()];
            int index = 0;
            for (T obj : objs) {
                strings[index++] = function.apply(obj);
            }
            return String.join(delimiter, strings);
        }
    }


}
