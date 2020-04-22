package querymethods.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import tk.mybatis.mapper.common.Mapper;

/**
 * msId工具类，通过msId获取MapperClass, methodName, entityClass
 * 
 * @author OYGD
 *
 */
public class MsIdUtil {

	private MsIdUtil() {
	}

	private static Map<String, String> cacheMethodName = new HashMap<>();
	private static Map<String, Class<?>> cacheMapperClass = new HashMap<>();
	private static Map<String, Class<?>> cacheEntityClass = new HashMap<>();

	/**
	 * 根据msId来获取对应的方法名
	 * 
	 * @param msId
	 * @return
	 */
	public static String getMethodName(String msId) {
		return cacheMethodName.getOrDefault(msId, _getMethodName(msId));
	}

	private static String _getMethodName(String msId) {
		int lastIndexOf = msId.lastIndexOf(".");
		String methodName = msId.substring(lastIndexOf + 1);
		cacheMethodName.put(msId, methodName);
		return methodName;
	}
	
	/**
	 * 通过msId来获取对应的MapperClass
	 * 
	 * @param msId
	 * @return
	 */
	public static Class<?> getMapperClass(String msId) {
		return cacheMapperClass.getOrDefault(msId, _getMapperClass(msId));
	}
	
	private static Class<?> _getMapperClass(String msId) {
		int lastIndexOf = msId.lastIndexOf(".");
		String mapperName = msId.substring(0, lastIndexOf);

		try {
			Class<?> mapperClass = Class.forName(mapperName);
			
			cacheMapperClass.put(msId, mapperClass);
			return mapperClass;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 通过msId来获取对应的EntityClass
	 * @param msId
	 * @return
	 */
	public static Class<?> getEntityClass(String msId) {
		if(cacheEntityClass.containsKey(msId)) {
			return cacheEntityClass.get(msId);
		}
		Class<?> entityClass = getEntityClass(getMapperClass(msId));
		cacheEntityClass.put(msId, entityClass);
		return entityClass;
	}

	/**
	 * 根据MapperClass获取的EntityClass
	 * 
	 * @param mapperClass
	 * @return
	 */
	public static Class<?> getEntityClass(Class<?> mapperClass) {
		Class<?> entityClass = null;
		Type[] genericInterfaces = mapperClass.getGenericInterfaces();
		if (genericInterfaces != null && genericInterfaces.length > 0) {
			for (Type type1 : genericInterfaces) {
				ParameterizedType t = ((ParameterizedType) type1);
				if (t.getRawType() == Mapper.class) {
					try {
						entityClass = Class.forName(t.getActualTypeArguments()[0].getTypeName());
					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
					break;
				}
			}
		}
		return entityClass;
	}


	
}
