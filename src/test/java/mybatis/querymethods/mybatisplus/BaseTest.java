package mybatis.querymethods.mybatisplus;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;

import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;

import mybatis.querymethods.QueryMethodsConfig;
import mybatis.querymethods.QueryMethodsHelper;


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
    SqlSession session = sqlSessionFactory.openSession();
    try {
        session.getConnection().prepareStatement(
  				"create table if not exists customer (id bigint primary key not null,"
  				+ " first_name varchar(255),"
  				+ " last_name varchar(255),"
  				+ " active tinyint)").execute();
    } catch (SQLException e) {
		e.printStackTrace();
	} finally {
    	session.commit();
		session.close();
	}
    QueryMethodsConfig.setOrmType(QueryMethodsConfig.ORM_TYPE_MYBATISPLUS);
    QueryMethodsConfig
        .setMapperClasss(Arrays.asList(com.baomidou.mybatisplus.core.mapper.BaseMapper.class));
    QueryMethodsHelper.processConfiguration(sqlSessionFactory.getConfiguration());

  }

}
