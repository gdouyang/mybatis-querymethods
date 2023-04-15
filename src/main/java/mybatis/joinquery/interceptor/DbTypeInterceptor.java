package mybatis.joinquery.interceptor;

import java.util.Properties;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import mybatis.joinquery.JoinQuery;
import mybatis.joinquery.dialect.DbType;
import mybatis.joinquery.dialect.DialectFactory;

/**
 * 查询方法拦截器
 * 
 * @author gdouyang
 *
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class}),
    })
public class DbTypeInterceptor implements Interceptor {

  /**
   * 
   */
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];
    DbType dbType = JoinQuery.getDbType(ms.getConfiguration());
    try {
      DialectFactory.setHintDbType(dbType);
      return invocation.proceed();
    } finally {
      DialectFactory.clearHintDbType();
    }
  }
  

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {

  }

}
