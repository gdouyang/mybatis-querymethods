package mybatis;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class MybatisTest {
  String EMPTY_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\r\n" //
      + "<!DOCTYPE configuration\r\n" //
      + " PUBLIC \"-//mybatis.org//DTD Config 3.0//EN\"\r\n" //
      + " \"http://mybatis.org/dtd/mybatis-3-config.dtd\">\r\n"//
      + "<configuration>\r\n" //
      + "</configuration>";
  InputStream inputStream = new ByteArrayInputStream(EMPTY_XML.getBytes(StandardCharsets.UTF_8));
  XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(inputStream, null, null);
  Configuration configuration = xmlConfigBuilder.parse();
  
  /**
   * 从MP复制过来的脚本解析方法
   */
  private SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
      XMLScriptBuilder builder = new XMLScriptBuilder(configuration, script, parameterType);
      return builder.parseScriptNode();
  }
  
  @Test
  public void test() throws SQLException {
    JdbcConnectionPool cp = JdbcConnectionPool.create(
        "jdbc:h2:mem:mp;MODE=MySQL;CASE_INSENSITIVE_IDENTIFIERS=TRUE;", "sa", "sa");
    JdbcTemplate jdbcTemplate = new JdbcTemplate(cp);
    jdbcTemplate.execute(Init.CREATE_CUSTOMER_SQL);
    jdbcTemplate.execute("insert into customer(id, first_name, last_name) values("
        + "1, 'OY', 'GD')");
    jdbcTemplate.execute("insert into customer(id, first_name, last_name) values("
        + "2, 'LL', 'BB')");
    
    
    String script = "<script>\nSELECT * FROM customer where 1=1 "
        + "<if test=\"firstName != null\"> AND first_name = #{firstName}</if> "
        + "<if test=\"ids != null\"> AND id in "
        + "<foreach collection=\"ids\" item=\"item\" open=\"(\" separator=\",\" close=\")\" > #{item} </foreach>"
        + "</if> "
        + "\n</script>";
    XPathParser parser = new XPathParser(script, false, new Properties(), new XMLMapperEntityResolver());
    SqlSource source = createSqlSource(configuration, parser.evalNode("/script"), null);

    Map<String, Object> params = new HashMap<>();
    params.put("firstName", "OY");
    params.put("ids", new Integer[] {1, 2});
    BoundSql boundSql = source.getBoundSql(params);
    String sql = boundSql.getSql();
    System.out.println(sql);
    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
    System.out.println(parameterMappings);
    System.out.println(boundSql.getParameterObject());
    
    Object[] args = new Object[parameterMappings.size()];
    int index = 0;
    for (ParameterMapping pm : parameterMappings) {
      Object arg = params.get(pm.getProperty());
      if (arg == null) {
        arg = boundSql.getAdditionalParameter(pm.getProperty());
      }
      args[index] = arg;
      index++;
    }
    List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql, args);
    System.out.println(queryForList);
  }
}
