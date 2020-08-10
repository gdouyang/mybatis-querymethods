package querymethods;

import java.util.ArrayList;
import java.util.List;

/**
 * 配置类，指定当前是使用tk.mapper还是mybatis-plus
 * 
 * @author OYGD
 *
 */
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

  /**
   * 当前ORM，默认为tkmapper，可以使用setOrmType设置
   * 
   * @return
   */
  public static String getOrmType() {
    return ormType;
  }

  /**
   * 设置当前ORM实现tkmapper或mybatis-plus
   * 
   * @see QueryMethodsConfig#ORM_TYPE_TKMAPPER
   * @see QueryMethodsConfig#ORM_TYPE_MYBATISPLUS
   * 
   * @param ormType
   */
  public static void setOrmType(String ormType) {
    QueryMethodsConfig.ormType = ormType;
  }

  /**
   * Mapper父接口,用于获取实体对象
   * 
   * @return
   */
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

  /**
   * Mapper父接口,用于获取实体对象
   * 
   * @return
   */
  public static void setMapperClasss(List<Class<?>> mapperClasss) {
    if (mapperClasss == null) {
      mapperClasss = new ArrayList<>();
    }
    QueryMethodsConfig.mapperClasss = mapperClasss;
  }

  /**
   * 是否为tkmapper
   * 
   * @return true表示使用tkmapper
   */
  public static boolean isTkMapper() {
    boolean isTrue = ORM_TYPE_TKMAPPER.equals(ormType);
    return isTrue;
  }

  /**
   * 是否使用mybatis-plus
   * 
   * @return true表示使用mybatis-plus
   */
  public static boolean isMybatisPlus() {
    return ORM_TYPE_MYBATISPLUS.equals(ormType);
  }


  /**
   * 获取tk.mapper的基础Mapper接口
   * 
   * @return
   */
  public static Class<?> getTkMapperBaseMapperClass() {
    Class<?> clazz = findClass("tk.mybatis.mapper.common.Mapper");
    return clazz;
  }

  /**
   * 获取mybatis-plus的基础Mapper接口
   * 
   * @return
   */
  public static Class<?> getMybatisPlusBaseMapperClass() {
    Class<?> clazz = findClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
    return clazz;
  }

  /**
   * 根据类名查找Class
   * 
   * @param className
   * @return null没有找到对应的类否则返回Class
   */
  public static Class<?> findClass(String className) {
    try {
      Class<?> clazz = Class.forName(className);
      return clazz;
    } catch (ClassNotFoundException e) {
      return null;
    }
  }
}
