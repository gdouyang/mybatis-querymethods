package mybatis.joinquery.dialect;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import mybatis.joinquery.util.StringUtil;

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
    getDialect(ms);
    return invocation.proceed();
  }
  
//缓存
  private Map<String, DbType> urlDbTypeMap      = new ConcurrentHashMap<>();
  private ReentrantLock       lock               = new ReentrantLock();
  /**
   * 根据 jdbcUrl 获取数据库方言
   *
   * @param ms
   * @return
   */
  private DbType getDialect(MappedStatement ms) {
      //改为对dataSource做缓存
      DataSource dataSource = ms.getConfiguration().getEnvironment().getDataSource();
      String url = getUrl(dataSource);
      if (urlDbTypeMap.containsKey(url)) {
          return urlDbTypeMap.get(url);
      }
      try {
          lock.lock();
          if (urlDbTypeMap.containsKey(url)) {
              return urlDbTypeMap.get(url);
          }
          if (StringUtil.isBlank(url)) {
              throw new RuntimeException("无法自动获取jdbcUrl，请手动指定DbType!");
          }
          DbType dbType = DbTypeUtil.parseDbType(url);
          if (dbType == DbType.OTHER) {
              throw new RuntimeException("无法自动获取数据库类型，请手动指定DbType!");
          }
          DialectFactory.setHintDbType(dbType);
          urlDbTypeMap.put(url, dbType);
          return dbType;
      } finally {
          lock.unlock();
      }
  }
  
  /**
   * 获取url
   *
   * @param dataSource
   * @return
   */
  private String getUrl(DataSource dataSource) {
      Connection conn = null;
      try {
          conn = dataSource.getConnection();
          return conn.getMetaData().getURL();
      } catch (SQLException e) {
          throw new RuntimeException(e);
      } finally {
          if (conn != null) {
              try {
                  conn.close();
              } catch (SQLException e) {
                  //ignore
              }
          }
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
