package mybatis.joinquery;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import javax.sql.DataSource;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import mybatis.joinquery.dialect.DbType;
import mybatis.joinquery.dialect.DbTypeUtil;
import mybatis.joinquery.dialect.DialectFactory;

public class JoinQuery {
  private JoinQuery() {}

  // 缓存
  private static Map<String, DbType> urlDbTypeMap = new ConcurrentHashMap<>();
  private static ReentrantLock lock = new ReentrantLock();
  
  public static <T> List<T> queryList(SqlSession sqlSession, JoinQueryWrapper wrapper, Class<T> resultType) {
    Configuration configuration = sqlSession.getConfiguration();
    DbType dbType = getDbType(configuration);
    try {
      DialectFactory.setHintDbType(dbType);
      
      String sql = DialectFactory.getDialect().buildSelectSql(wrapper);
      if (!configuration.hasStatement(sql)) {
        RawSqlSource sqlSource = new RawSqlSource(configuration, sql, Map.class);
        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, sql, sqlSource, SqlCommandType.SELECT);
        List<ResultMap> resultMaps = new ArrayList<>(); 
        resultMaps.add(new ResultMap.Builder(configuration, "defaultResultMap", resultType, new ArrayList<>(0))
                .build());
        builder.resultMaps(resultMaps);
        MappedStatement ms = builder.build();
        configuration.addMappedStatement(ms);
      }
      Map<String, Object> parameter = wrapper.getValueMap();
      List<T> selectList = sqlSession.selectList(sql, parameter);
      return selectList;
    } finally {
      DialectFactory.clearHintDbType();
    }
  }

  /**
   * 根据 jdbcUrl 获取数据库方言
   *
   * @param ms
   * @return
   */
  public static DbType getDbType(Configuration configuration) {
    // 改为对dataSource做缓存
    DataSource dataSource = configuration.getEnvironment().getDataSource();
    String url = getUrl(dataSource);
    if (urlDbTypeMap.containsKey(url)) {
      return urlDbTypeMap.get(url);
    }
    try {
      lock.lock();
      if (urlDbTypeMap.containsKey(url)) {
        return urlDbTypeMap.get(url);
      }
      DbType dbType = DbTypeUtil.parseDbType(url);
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
  private static String getUrl(DataSource dataSource) {
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
          // ignore
        }
      }
    }
  }
}
