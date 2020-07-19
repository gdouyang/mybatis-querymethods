package mybatisplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;

import querymethods.QueryMethodsConfig;
import querymethods.QueryMethodsHelper;


public class BaseTest {

  public BaseTest() {}

  SqlSessionFactory sqlSessionFactory = null;

  @Before
  public void before() {

    String resource = "mybatis-config-mp.xml";
    try {
      InputStream inputStream = Resources.getResourceAsStream(resource);
      sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, null, null);
    } catch (IOException e) {
      e.printStackTrace();
    }
    QueryMethodsConfig.setOrmType(QueryMethodsConfig.ORM_TYPE_MYBATISPLUS);
    QueryMethodsConfig
        .setMapperClasss(Arrays.asList(com.baomidou.mybatisplus.core.mapper.BaseMapper.class));
    QueryMethodsHelper.processConfiguration(sqlSessionFactory.getConfiguration());

  }

//  public static SqlSessionFactory initSqlSessionFactory() {
//    DataSource dataSource = dataSource();
//    TransactionFactory transactionFactory = new JdbcTransactionFactory();
//    Environment environment = new Environment("Production", transactionFactory, dataSource);
//    MybatisConfiguration configuration = new MybatisConfiguration(environment);
//    configuration.addMapper(CustomerMapperMp.class);
//    configuration.setLogImpl(StdOutImpl.class);
//    return new MybatisSqlSessionFactoryBuilder().build(configuration);
//  }
//
//  public static DataSource dataSource() {
//    SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
//    dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
//    dataSource.setUrl(
//        "jdbc:mysql://192.168.31.197:3306/querymethods?useUnicode=true&amp;characterEncoding=UTF-8");
//    dataSource.setUsername("root");
//    dataSource.setPassword("root");
//    return dataSource;
//  }

}
