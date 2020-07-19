package querymethods;

import java.util.ArrayList;
import java.util.List;

public class QueryMethodsConfig {

  public static final String ORM_TYPE_TKMAPPER = "tkmapper";

  public static final String ORM_TYPE_MYBATISPLUS = "mybatis-plus";

  /**
   * ORM类型支持tkmapper和mybatis-plus
   */
  private static String ormType = ORM_TYPE_TKMAPPER;

  /**
   * mapper父接口
   */
  private static List<Class<?>> mapperClasss = new ArrayList<>();

  public static String getOrmType() {
    return ormType;
  }

  public static void setOrmType(String ormType) {
    QueryMethodsConfig.ormType = ormType;
  }

  // mapper父接口
  public static List<Class<?>> getMapperClasss() {
    if (mapperClasss.isEmpty()) {
      Class<?> clazz = QueryMethodsConfig.getTkMapperBaseMapperClass();
      if (clazz != null) {
        setOrmType(ORM_TYPE_TKMAPPER);
        mapperClasss.add(clazz);
      } else {
        clazz = QueryMethodsConfig.getMybatisPlusBaseMapperClass();
        if (clazz != null) {
          setOrmType(ORM_TYPE_MYBATISPLUS);
          mapperClasss.add(clazz);
        }
      }
    }
    return mapperClasss;
  }

  public static void setMapperClasss(List<Class<?>> mapperClasss) {
    if (mapperClasss == null) {
      mapperClasss = new ArrayList<>();
    }
    QueryMethodsConfig.mapperClasss = mapperClasss;
  }

  //
  public static boolean isTkMapper() {
    boolean isTrue = ORM_TYPE_TKMAPPER.equals(ormType);
    return isTrue;
  }

  public static boolean isMybatisPlus() {
    return ORM_TYPE_MYBATISPLUS.equals(ormType);
  }


  public static Class<?> getTkMapperBaseMapperClass() {
    Class<?> clazz = findClass("tk.mybatis.mapper.common.Mapper");
    return clazz;
  }

  public static Class<?> getMybatisPlusBaseMapperClass() {
    Class<?> clazz = findClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
    return clazz;
  }

  public static Class<?> findClass(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
