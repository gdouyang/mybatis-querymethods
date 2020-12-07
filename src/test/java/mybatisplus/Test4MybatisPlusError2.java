package mybatisplus;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;

import querymethods.QueryMethodsConfig;
import querymethods.QueryMethodsException;
import querymethods.QueryMethodsHelper;

/**
 * 测试property不存在时的场景findId1ById
 * @author OYGD
 *
 */
public class Test4MybatisPlusError2 {

  @Test
  public void test() {
    SqlSessionFactory sqlSessionFactory = null;
    QueryMethodsException e = null;
    try {
      String resource = "mybatis-config-mp-error2.xml";
      try {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, null, null);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      QueryMethodsConfig.setOrmType(QueryMethodsConfig.ORM_TYPE_MYBATISPLUS);
      QueryMethodsConfig
          .setMapperClasss(Arrays.asList(com.baomidou.mybatisplus.core.mapper.BaseMapper.class));
      QueryMethodsHelper.processConfiguration(sqlSessionFactory.getConfiguration());


    } catch (QueryMethodsException e1) {
      e = e1;
    }

    Assert.notNull(e, "异常条件Test不通过");
  }


}
