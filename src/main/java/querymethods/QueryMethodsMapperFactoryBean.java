package querymethods;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryMethodsMapperFactoryBean<T> extends tk.mybatis.spring.mapper.MapperFactoryBean<T> {

	protected final Log logger = LogFactory.getLog(getClass());
	
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
		// QueryMethods
		logger.info("begin process query methods");
		QueryMethodsHelper.processConfiguration(getSqlSession().getConfiguration(), getObjectType());
		logger.info("end process query methods");
	}
}