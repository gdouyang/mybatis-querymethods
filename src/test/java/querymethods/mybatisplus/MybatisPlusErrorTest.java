package querymethods.mybatisplus;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import querymethods.QueryMethodsConfig;
import querymethods.QueryMethodsException;
import querymethods.QueryMethodsHelper;

/**
 * 
 * @author OYGD
 *
 */
public class MybatisPlusErrorTest {

  @Test
  public void test() {
    SqlSessionFactory sqlSessionFactory = null;
    QueryMethodsException e = null;
    try {
      String resource = "mybatis-config-mp-error.xml";
      try {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new MybatisSqlSessionFactoryBuilder().build(inputStream, null, null);
        SqlSession session = sqlSessionFactory.openSession();
        try {
        	session.getConnection().prepareStatement(
        			"create table if not exists customer (id bigint primary key not null,"
        					+ " first_name varchar(255),"
        					+ " last_name varchar(255),"
        					+ " active tinyint)").execute();
        } catch (SQLException e1) {
        	e1.printStackTrace();
        } finally {
        	session.commit();
        	session.close();
        }
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
