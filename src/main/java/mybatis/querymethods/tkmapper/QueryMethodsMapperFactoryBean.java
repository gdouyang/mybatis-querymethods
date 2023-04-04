package mybatis.querymethods.tkmapper;

import mybatis.querymethods.QueryMethodsHelper;

public class QueryMethodsMapperFactoryBean<T> extends tk.mybatis.spring.mapper.MapperFactoryBean<T> {

	public QueryMethodsMapperFactoryBean() {
	}

	public QueryMethodsMapperFactoryBean(Class<T> mapperInterface) {
		super(mapperInterface);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void checkDaoConfig() {
		super.checkDaoConfig();
		QueryMethodsHelper.processConfiguration(getSqlSession().getConfiguration(), getObjectType());
	}
}