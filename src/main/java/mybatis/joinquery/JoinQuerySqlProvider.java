package mybatis.joinquery;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.builder.annotation.ProviderContext;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import mybatis.joinquery.dialect.DialectFactory;

public class JoinQuerySqlProvider {
	
	protected final static Log logger = LogFactory.getLog(JoinQuerySqlProvider.class);

	public static final String QUERY = "$$query";
    /**
     * 不让实例化，使用静态方法的模式，效率更高，非静态方法每次都会实例化当前类
     * 参考源码: {{@link org.apache.ibatis.builder.annotation.ProviderSqlSource#getBoundSql(Object)}
     */
    private JoinQuerySqlProvider() {
    }

    @SuppressWarnings("rawtypes")
	public static JoinQueryWrapper getQueryWrapper(Map params) {
        return (JoinQueryWrapper) params.get(QUERY);
    }
    
    /**
     * selectListByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see mybatis.joinquery.JoinQueryMapper#selectListByQuery(QueryWrapper)
     */
    @SuppressWarnings("unchecked")
	public static String selectListByQuery(@SuppressWarnings("rawtypes") Map params, ProviderContext context) {
    	JoinQueryWrapper queryWrapper = getQueryWrapper(params);
        if (queryWrapper == null) {
            throw new IllegalArgumentException("joinQueryWrapper can not be null.");
        }
        
        String sql = DialectFactory.getDialect().forSelectListByQuery(queryWrapper);
        if (logger.isDebugEnabled()) {
        	logger.debug(sql);
        }

        Map<String, Object> valueMap = queryWrapper.getValueMap();
        if (null != valueMap) {
        	params.putAll(valueMap);
        }
        
        return sql;
    }

    /**
     * selectCountByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see mybatis.joinquery.JoinQueryMapper#selectCountByQuery(QueryWrapper)
     */
    @SuppressWarnings("unchecked")
    public static String selectCountByQuery(@SuppressWarnings("rawtypes") Map params, ProviderContext context) {
    	JoinQueryWrapper queryWrapper = getQueryWrapper(params);
        if (queryWrapper == null) {
        	throw new IllegalArgumentException("joinQueryWrapper can not be null.");
        }

        String sql = DialectFactory.getDialect().forSelectCountByQuery(queryWrapper);
        if (logger.isDebugEnabled()) {
        	logger.debug(sql);
        }

        Map<String, Object> valueMap = queryWrapper.getValueMap();
        if (null != valueMap) {
        	params.putAll(valueMap);
        }
        return sql;
    }


}
