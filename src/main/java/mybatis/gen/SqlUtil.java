package mybatis.gen;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import mybatis.query.PartTree;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;

public class SqlUtil {
	
	/**
	 * 根据MappedStatement id来生成sql
	 * @param msId
	 * @param mapperClass
	 * @param config
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static String getSqlByMsId(String msId, Config config) throws ClassNotFoundException {
		int lastIndexOf = msId.lastIndexOf(".");
		String mapperName = msId.substring(0, lastIndexOf);
		String methodName = msId.substring(lastIndexOf + 1);
		
		Class<?> mapperClass = Class.forName(mapperName);
		
		return getSql(methodName, mapperClass, config);
	}

	/**
	 * 根据方法名与Mapper接口来生成相应的sql
	 * @param methodName
	 * @param type
	 * @param config
	 * @return
	 */
	public static String getSql(String methodName, Class<?> mapperClass, Config config) {
		Class<?> entityClass = getEntityClass(mapperClass);

		if (entityClass != null) {
			PartTree tree = new PartTree(methodName);
			String xmlSql = null;
			if (tree.isCountProjection()) {
				xmlSql = TkMapperUtil.selectCountByExample(entityClass);
			}
			xmlSql = TkMapperUtil.selectByExample(entityClass);
			return "<script>\n\t" + xmlSql + "</script>";
		}
		return null;
	}

	/**
	 * 根据XxxxMapper接口来查找对应的实体对象
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
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return entityClass;
	}
}
