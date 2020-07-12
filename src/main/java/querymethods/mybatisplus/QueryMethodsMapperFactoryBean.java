package querymethods.mybatisplus;

import querymethods.QueryMethodsHelper;

/**
 * mybatis-plus对应的MapperFactoryBean
 * 
 * @author OYGD
 *
 * @param <T>
 */
public class QueryMethodsMapperFactoryBean<T>
    extends org.mybatis.spring.mapper.MapperFactoryBean<T> {

  public QueryMethodsMapperFactoryBean() {}

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
