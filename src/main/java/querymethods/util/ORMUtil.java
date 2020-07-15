package querymethods.util;

public class ORMUtil {

  public static boolean isTkMapper() {
    Class<?> clazz = findClass("tk.mybatis.mapper.common.Mapper");
    return clazz != null;
  }

  public static boolean isMybatisPlus() {
    Class<?> clazz = findClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
    return clazz != null;
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
