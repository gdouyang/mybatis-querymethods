package querymethods.mybatisplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import mybatis.Init;
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
    Init.setup(sqlSessionFactory.openSession());

    QueryMethodsConfig.setOrmType(QueryMethodsConfig.ORM_TYPE_MYBATISPLUS);
    QueryMethodsConfig
        .setMapperClasss(Arrays.asList(com.baomidou.mybatisplus.core.mapper.BaseMapper.class));
    QueryMethodsHelper.processConfiguration(sqlSessionFactory.getConfiguration());

  }

}
