package mybatis.joinquery.util;

import java.util.*;
import java.util.function.Function;


public class CollectionUtil {


    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }


    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }


    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }


    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 合并 list
     */
    public static <T> List<T> merge(List<T> list, List<T> other) {
        if (list == null && other == null) {
            return new ArrayList<>();
        } else if (isEmpty(other)) {
            return list;
        } else if (isEmpty(list)) {
            return other;
        }
        List<T> newList = new ArrayList<>(list);
        newList.addAll(other);
        return newList;
    }


    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    /**
     * 主要是用于修复 concurrentHashMap 在 jdk1.8 下的死循环问题
     *
     * @see <a href="https://bugs.openjdk.org/browse/JDK-8161372">https://bugs.openjdk.org/browse/JDK-8161372</a>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> concurrentHashMap, K key, Function<? super K, ? extends V> mappingFunction) {
        V v = concurrentHashMap.get(key);
        if (v != null) {
            return v;
        }
        return concurrentHashMap.computeIfAbsent(key, mappingFunction);
    }

    public static <T> Set<T> newHashSet(@SuppressWarnings("unchecked") T... elements) {
        return new HashSet<>(Arrays.asList(elements));
    }

    public static <T> List<T> newArrayList(@SuppressWarnings("unchecked") T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

}
