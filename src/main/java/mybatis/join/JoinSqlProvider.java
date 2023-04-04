package mybatis.join;

import java.util.Map;

import org.apache.ibatis.builder.annotation.ProviderContext;

import mybatis.join.dialect.DialectFactory;

public class JoinSqlProvider {

	public static final String QUERY = "$$query";
	public static final String SQL_ARGS = "$$sql_args";
    /**
     * 不让实例化，使用静态方法的模式，效率更高，非静态方法每次都会实例化当前类
     * 参考源码: {{@link org.apache.ibatis.builder.annotation.ProviderSqlSource#getBoundSql(Object)}
     */
    private JoinSqlProvider() {
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
     * @see mybatis.join.JoinMapper#selectListByQuery(QueryWrapper)
     */
    @SuppressWarnings("unchecked")
	public static String selectListByQuery(@SuppressWarnings("rawtypes") Map params, ProviderContext context) {
    	JoinQueryWrapper queryWrapper = getQueryWrapper(params);
        if (queryWrapper == null) {
            throw new IllegalArgumentException("joinQueryWrapper can not be null.");
        }

        Map<String, Object> valueMap = queryWrapper.getValueMap();
        if (null != valueMap) {
        	params.putAll(valueMap);
        }
        System.out.println(params);

        return DialectFactory.getDialect().forSelectListByQuery(queryWrapper);
    }

    /**
     * selectCountByQuery 的 sql 构建
     *
     * @param params
     * @param context
     * @return sql
     * @see mybatis.join.JoinMapper#selectCountByQuery(QueryWrapper)
     */
    @SuppressWarnings("unchecked")
    public static String selectCountByQuery(@SuppressWarnings("rawtypes") Map params, ProviderContext context) {
    	JoinQueryWrapper queryWrapper = getQueryWrapper(params);
        if (queryWrapper == null) {
        	throw new IllegalArgumentException("joinQueryWrapper can not be null.");
        }

        Map<String, Object> valueMap = queryWrapper.getValueMap();
        if (null != valueMap) {
        	params.putAll(valueMap);
        }

        return DialectFactory.getDialect().forSelectCountByQuery(queryWrapper);
    }


}
