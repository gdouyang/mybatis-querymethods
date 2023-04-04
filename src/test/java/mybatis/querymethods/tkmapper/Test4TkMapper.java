package mybatis.querymethods.tkmapper;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import mybatis.querymethods.Customer;
import mybatis.querymethods.QueryMethodsTest;

/**
 * 
 * @author OYGD
 *
 */
public class Test4TkMapper extends BaseTest {
  @Test
  public void test() {
    SqlSession session = sqlSessionFactory.openSession();

    try {
      Integer id = 1;
      CustomerMapper mapper = session.getMapper(CustomerMapper.class);

      QueryMethodsTest.log("deleteByPrimaryKey");
      mapper.deleteByPrimaryKey(id);

      QueryMethodsTest.log("insert1");
      Customer b = new Customer();
      b.setId(id);
      b.setFirstName("OY");
      b.setLastName("GD");
      mapper.insert1(b);

      QueryMethodsTest.test(mapper, b);

    } finally {
      session.commit();
      session.close();
    }
  }

}
