package mybatis.querymethods;

import static org.apache.ibatis.mapping.SqlCommandType.DELETE;
import static org.apache.ibatis.mapping.SqlCommandType.SELECT;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import mybatis.querymethods.util.SqlUtil;

/**
 * 查询方法帮助类，通过方法名称动态的生成sql
 * 
 * @author OYGD
 *
 */
public class QueryMethodsHelper {

  protected final static Log logger = LogFactory.getLog(QueryMethodsHelper.class);

  private QueryMethodsHelper() {}

  private static final Map<String, Boolean> queryMethod = new ConcurrentHashMap<>();

  private static final Map<String, Boolean> deleteMethod = new ConcurrentHashMap<>();

  public static boolean isQueryMethod(String msId) {
    return queryMethod.containsKey(msId);
  }

  public static boolean isDeleteMethod(String msId) {
    return deleteMethod.containsKey(msId);
  }

  /**
   * 对已经注册的Mapper文件做处理，如果方法上使用了@Select("")注册并且sql为空字符串时就会动态创建sql <br>
   * 处理configuration中全部的MappedStatement
   * 
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

  private static final XMLLanguageDriver languageDriver = new XMLLanguageDriver();

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

    SqlCommandType sqlCommandType = ms.getSqlCommandType();
    if ("".equals(sql) && (SELECT.equals(sqlCommandType) || DELETE.equals(sqlCommandType))) {

      try {
        if (queryMethod.containsKey(msId) || deleteMethod.containsKey(msId)) {
          return;
        }
        // QueryMethods
        if (logger.isInfoEnabled()) {
          logger.info("find query methods => " + msId);
        }
        if (DELETE.equals(sqlCommandType)) {
          deleteMethod.put(msId, Boolean.TRUE);
        } else {
          queryMethod.put(msId, Boolean.TRUE);
        }

        String xmlSql = SqlUtil.getSqlByMs(ms);
        SqlSource s = languageDriver.createSqlSource(ms.getConfiguration(), xmlSql, null);
        // 重新设置SqlSource
        MetaObject msObject = SystemMetaObject.forObject(ms);
        msObject.setValue("sqlSource", s);

      } catch (ClassNotFoundException | NoSuchFieldException e) {
        throw new QueryMethodsException(e);
      }
    }
  }

}
