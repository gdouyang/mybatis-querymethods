package querymethods.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

import tk.mybatis.mapper.util.StringUtil;

/**
 * 动态SQL，可以 判断条件是否满足
 * <ul>
 * <li><b>notNullThen</b> - 值不等于null时执行</li>
 * <li><b>nullThen</b> - 值等于null时执行</li>
 * <li><b>notEmptyThen</b> - 值不等于null和空时执行(可以是String, Map, Collection, Array)</li>
 * </ul>
 * 例：
 * 
 * <pre>
 * Example example = new Example(Blog.class);
 * Example.Criteria criteria = example.createCriteria();
 * 
 * IfThen ds = new IfThen();
 * ds.notEmptyThen(id, val -> criteria.andEqualTo("id", val)).isNotEmptyThen(name,
 * 		val -> criteria.andEqualTo("name", val));
 * </pre>
 * 
 * @author OYGD
 *
 */
public class IfThen {

	/**
	 * 当不为null的时候执行Consumer
	 * 
	 * @param value
	 * @param val
	 * @return
	 */
	public <V> IfThen notNullThen(V value, Consumer<V> val) {
		if (Objects.nonNull(value)) {
			val.accept(value);
		}
		return this;
	}

	/**
	 * 当为null的时候执行Consumer
	 * 
	 * @param value
	 * @param val
	 * @return
	 */
	public <V> IfThen nullThen(V value, Consumer<V> val) {
		if (Objects.isNull(value)) {
			val.accept(value);
		}
		return this;
	}

	/**
	 * 判断是否为null或空
	 * 
	 * @param value
	 *            需要判断的值，可以为 集合、数组、字符串、Map
	 * @param val
	 * @return
	 */
	public <V> IfThen notEmptyThen(V value, Consumer<V> val) {
		if (!isEmpty(value)) {
			val.accept(value);
		}
		return this;
	}

	/**
	 * 判断是否为null或空
	 * 
	 * @param value
	 *            需要判断的值，可以为 集合、数组、字符串、Map
	 * @param con
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		// 判断其它类型是不是null或empty
		if (value instanceof String) {
			return StringUtil.isEmpty((String) value);
		} else if (value instanceof Collection) {
			return ((Collection) value).isEmpty();
		} else if (value instanceof Map) {
			return ((Map) value).isEmpty();
		} else if (value.getClass().isArray()) {
			return Array.getLength(value) == 0;
		}

		return false;
	}
}
