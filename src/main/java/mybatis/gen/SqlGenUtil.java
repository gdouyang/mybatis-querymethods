package mybatis.gen;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import tk.mybatis.mapper.common.Mapper;


public class SqlGenUtil {

	public static String[] gen(Method method, Class<?> type) {
		Class<?> entityClass = null;
		Type[] genericInterfaces = type.getGenericInterfaces();
		if (genericInterfaces != null && genericInterfaces.length > 0) {
			for (Type type1 : genericInterfaces) {
				ParameterizedType t = ((ParameterizedType) type1);
				if(t.getRawType() == Mapper.class) {
					try {
						entityClass = Class.forName(t.getActualTypeArguments()[0].getTypeName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		if(entityClass != null) {
			System.out.println(entityClass);
			Method[] methods = entityClass.getMethods();
			for (Method method2 : methods) {
				System.out.println(method2.getName());
			}
			return new String[] { "select * from Customer where id = #{id}" };
		}
		return null;
	}
}
