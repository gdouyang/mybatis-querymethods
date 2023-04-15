package mybatis.querymethods.mybatisplus;

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

import mybatis.querymethods.QueryMethodsConfig;
import mybatis.querymethods.QueryMethodsException;
import mybatis.querymethods.QueryMethodsHelper;

/**
 * 测试property不存在时的场景findId1ById
 * @author OYGD
 *
 */
public class MybatisPlusErrorTest2 {

  @Test
  public void test() {
    SqlSessionFactory sqlSessionFactory = null;
    QueryMethodsException e = null;
    try {
      String resource = "mybatis-config-mp-error2.xml";
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
