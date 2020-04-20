package mybatis.gen;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

/**
 * 查询方法帮助类，通过方法名称动态的生成sql
 * @author OYGD
 *
 */
public class QueryMethodsHelper {

	private QueryMethodsHelper() {
	}
	
	private static final Map<String, Boolean> queryMethod = new HashMap<>();
	
	public static boolean isQueryMethods(String msId) {
		return queryMethod.containsKey(msId);
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
				String msId = ms.getId();
				BoundSql boundSql = sqlSource.getBoundSql(null);
				String sql = boundSql.getSql();
				if (SqlCommandType.SELECT.equals(ms.getSqlCommandType()) && "".equals(sql)) {

					try {
						queryMethod.put(msId, Boolean.TRUE);

						String xmlSql = SqlUtil.getSqlByMsId(msId, mapperHelper.getConfig());
						
						LanguageDriver defaultDriver = configuration.getLanguageRegistry().getDefaultDriver();
						
						SqlSource s = defaultDriver.createSqlSource(ms.getConfiguration(), xmlSql, null);
						// 重新设置SqlSource
						MetaObject msObject = SystemMetaObject.forObject(ms);
						msObject.setValue("sqlSource", s);

					} catch (ClassNotFoundException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	
}
