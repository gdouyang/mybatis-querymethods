package querymethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;

import querymethods.util.SqlUtil;

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

	/**
	 * 对已经注册的Mapper文件做处理，如果方法上使用了@Select("")注册并且sql为空字符串时就会动态创建sql
	 * <br>处理configuration中全部的MappedStatement
	 * @param configuration
	 * @param mapperHelper
	 */
	public static void processConfiguration(Configuration configuration) {
		processConfiguration(configuration, null);
	}
	
	/**
     * 配置指定的接口
     *
     * @param configuration
     * @param mapperInterface
     */
    public static void processConfiguration(Configuration configuration, Class<?> mapperInterface) {
        String prefix;
        if (mapperInterface != null) {
            prefix = mapperInterface.getCanonicalName();
        } else {
            prefix = "";
        }
        for (Object object : new ArrayList<Object>(configuration.getMappedStatements())) {
            if (object instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) object;
                if (ms.getId().startsWith(prefix)) {
                    processMappedStatement(ms);
                }
            }
        }
    }
	
	private static final XMLLanguageDriver     languageDriver = new XMLLanguageDriver();
	
	/**
     * 处理 MappedStatement
     *
     * @param ms
     */
	public static void processMappedStatement(MappedStatement ms) {
		SqlSource sqlSource = ms.getSqlSource();
		if (sqlSource instanceof ProviderSqlSource || sqlSource instanceof DynamicSqlSource) {
			return;
		}
		String msId = ms.getId();
		BoundSql boundSql = sqlSource.getBoundSql(null);
		String sql = boundSql.getSql();
		if (SqlCommandType.SELECT.equals(ms.getSqlCommandType()) && "".equals(sql)) {

			try {
				if(queryMethod.containsKey(msId)) {
					return;
				}
				queryMethod.put(msId, Boolean.TRUE);

				String xmlSql = SqlUtil.getSqlByMsId(msId);
				
				SqlSource s = languageDriver.createSqlSource(ms.getConfiguration(), xmlSql, null);
				// 重新设置SqlSource
				MetaObject msObject = SystemMetaObject.forObject(ms);
				msObject.setValue("sqlSource", s);

			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
