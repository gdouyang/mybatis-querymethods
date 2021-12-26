package tkmapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;
import org.springframework.util.Assert;

import querymethods.QueryMethodsConfig;
import querymethods.QueryMethodsException;
import querymethods.QueryMethodsHelper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

/**
 * 
 * @author OYGD
 *
 */
public class Test4TkMapperError2 {
  @Test
  public void test() {
    QueryMethodsException e = null;
    SqlSessionFactory sqlSessionFactory = null;
    try {
      String resource = "mybatis-config-tk-error2.xml";
      try {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
      } catch (IOException e1) {
        e1.printStackTrace();
      }

      // 创建一个MapperHelper
      MapperHelper mapperHelper = new MapperHelper();
      // 特殊配置
      Config config = new Config();
      // 主键自增回写方法,默认值MYSQL,详细说明请看文档
      config.setIDENTITY("MYSQL");
      // 支持getter和setter方法上的注解
      config.setEnableMethodAnnotation(true);
      // 设置 insert 和 update 中，是否判断字符串类型!=''
      config.setNotEmpty(true);
      // 校验Example中的类型和最终调用时Mapper的泛型是否一致
      config.setCheckExampleEntityClass(true);
      // 启用简单类型
      config.setUseSimpleType(true);
      // 枚举按简单类型处理
      config.setEnumAsSimpleType(true);
      // 自动处理关键字 - mysql
      config.setWrapKeyword("`{0}`");
      // 设置配置
      mapperHelper.setConfig(config);
      // 注册通用接口，和其他集成方式中的 mappers 参数作用相同
      // 4.0 之后的版本，如果类似 Mapper.class 这样的基础接口带有 @RegisterMapper 注解，就不必在这里注册
      mapperHelper.registerMapper(Mapper.class);
      mapperHelper.processConfiguration(sqlSessionFactory.getConfiguration());
      QueryMethodsConfig.setOrmType(QueryMethodsConfig.ORM_TYPE_TKMAPPER);
      QueryMethodsConfig.setMapperClasss(Arrays.asList(tk.mybatis.mapper.common.Mapper.class));
      QueryMethodsHelper.processConfiguration(sqlSessionFactory.getConfiguration());
      
      SqlSession session = sqlSessionFactory.openSession();
      session.getMapper(CustomerMapperError2.class);
    } catch (QueryMethodsException e1) {
//      e1.printStackTrace();
      e = e1;
    }

    Assert.notNull(e, "异常条件Test不通过");
  }

}
