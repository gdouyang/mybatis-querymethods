package mybatisplus;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import querymethods.Customer;
import querymethods.QueryMethodsTest;

/**
 * 
 * @author OYGD
 *
 */
public class Test4MybatisPlus extends BaseTest {

  @Test
  public void test() {
    SqlSession session = sqlSessionFactory.openSession();

    try {
      CustomerMapperMp mapper = session.getMapper(CustomerMapperMp.class);

      QueryMethodsTest.log("deleteById");
      int id = 1;
      mapper.deleteById(id);

      QueryMethodsTest.log("insert");
      Customer b = new Customer();
      b.setId(id);
      b.setFirstName("OY");
      b.setLastName("GD");
      mapper.insert(b);


      QueryMethodsTest.test(mapper, b);


    } finally {
      session.commit();
      session.close();
    }
  }


}
