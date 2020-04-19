package mybatis.gen;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.session.Configuration;

import tk.mybatis.mapper.mapperhelper.MapperHelper;

public class MethodQueryHelper {

	private MethodQueryHelper() {
	}
	
	private static final Set<String> methodQuery = new HashSet<>();
	
	public static boolean isMethodQuery(String id) {
		return methodQuery.contains(id);
	}

	public static void processConfiguration(Configuration configuration, MapperHelper mapperHelper) {
		Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
		for (Object object : mappedStatements) {
			if (object instanceof MappedStatement) {
				MappedStatement ms = (MappedStatement) object;
				SqlSource sqlSource = ms.getSqlSource();
				if (sqlSource instanceof ProviderSqlSource || sqlSource instanceof DynamicSqlSource) {
					continue;
				}
				String id = ms.getId();
				BoundSql boundSql = sqlSource.getBoundSql(null);
				String sql = boundSql.getSql();
				if (SqlCommandType.SELECT.equals(ms.getSqlCommandType()) && "".equals(sql)) {

					int lastIndexOf = id.lastIndexOf(".");
					String mapperName = id.substring(0, lastIndexOf);
					String methodName = id.substring(lastIndexOf + 1);
					try {
						methodQuery.add(id);
						
						Class<?> mapperClass = Class.forName(mapperName);
//        				Class<?> entityClass = SqlGenUtil.getEntityClass(mapperClass);

						String xmlSql = SqlGenUtil.gen(methodName, mapperClass, mapperHelper.getConfig());
						LanguageDriver defaultDriver = configuration.getLanguageRegistry().getDefaultDriver();
						SqlSource s = defaultDriver.createSqlSource(ms.getConfiguration(), xmlSql, null);

						MetaObject msObject = SystemMetaObject.forObject(ms);
						msObject.setValue("sqlSource", s);

					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
