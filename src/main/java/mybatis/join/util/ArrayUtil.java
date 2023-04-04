package mybatis.join.util;

import java.util.Arrays;
import java.util.Objects;

public class ArrayUtil {


    /**
     * 判断数组是否为空
     *
     * @param array
     * @param <T>
     * @return 空 true
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }


    /**
     * 判断数组是否不为空
     *
     * @param array
     * @param <T>
     * @return
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }


    /**
     * 合并两个数组为一个新的数组
     *
     * @param first  第一个数组
     * @param second 第二个数组
     * @param <T>
     * @return 新的数组
     */
    public static <T> T[] concat(T[] first, T[] second) {
        if (first == null && second == null) {
            throw new IllegalArgumentException("not allow first and second are null.");
        } else if (isEmpty(first)) {
            return second;
        } else if (isEmpty(second)) {
            return first;
        } else {
            T[] result = Arrays.copyOf(first, first.length + second.length);
            System.arraycopy(second, 0, result, first.length, second.length);
            return result;
        }
    }


    /**
     * 查看数组中是否包含某一个值
     *
     * @param arrays 数组
     * @param object 用于检测的值
     * @param <T>
     * @return true 包含
     */
    public static <T> boolean contains(T[] arrays, T object) {
        if (isEmpty(arrays)) {
            return false;
        }
        for (T array : arrays) {
            if (Objects.equals(array, object)) {
                return true;
            }
        }
        return false;
    }


}
