package querymethods.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import querymethods.QueryMethodsConfig;
import querymethods.QueryMethodsException;

/**
 * msId工具类，通过msId获取MapperClass, methodName, entityClass
 * 
 * @author OYGD
 *
 */
public class MsIdUtil {

  private MsIdUtil() {}

  private static Map<String, String> cacheMethodName = new ConcurrentHashMap<>();
  private static Map<String, Class<?>> cacheMapperClass = new ConcurrentHashMap<>();
  private static Map<String, Class<?>> cacheEntityClass = new ConcurrentHashMap<>();

  /**
   * 根据msId来获取对应的方法名
   * 
   * @param msId
   * @return
   */
  public static String getMethodName(String msId) {
    String methodName = cacheMethodName.get(msId);
    if (methodName != null) {
      return methodName;
    }
    int lastIndexOf = msId.lastIndexOf(".");
    methodName = msId.substring(lastIndexOf + 1);
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
    Class<?> mapperClass = cacheMapperClass.get(msId);
    if (mapperClass != null) {
      return mapperClass;
    }
    int lastIndexOf = msId.lastIndexOf(".");
    String mapperName = msId.substring(0, lastIndexOf);

    try {
      mapperClass = Class.forName(mapperName);
      cacheMapperClass.put(msId, mapperClass);
      return mapperClass;
    } catch (ClassNotFoundException e) {
      throw new QueryMethodsException(e);
    }
  }

  /**
   * 通过msId来获取对应的EntityClass
   * 
   * @param msId
   * @return
   */
  public static Class<?> getEntityClass(String msId) {
    Class<?> entityClass = cacheEntityClass.get(msId);
    if (entityClass != null) {
      return entityClass;
    }
    entityClass = getEntityClass(getMapperClass(msId));
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
        if (QueryMethodsConfig.getMapperClasss().contains(t.getRawType())) {
          try {
            entityClass = Class.forName(t.getActualTypeArguments()[0].getTypeName());
          } catch (ClassNotFoundException e) {
            throw new QueryMethodsException(e);
          }
          break;
        }
      }
    }
    return entityClass;
  }

}
