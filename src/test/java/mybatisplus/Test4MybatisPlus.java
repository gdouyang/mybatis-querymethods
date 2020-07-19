package mybatisplus;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import querymethods.Customer;

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

      Integer id = 1;
      mapper.deleteById(id);

      Customer b = new Customer();
      b.setId(id);
      b.setFirstName("OY");
      b.setLastName("GD");
      mapper.insert(b);

      Integer countById = mapper.countById(null);
      assert countById == 0;

      Customer customer = mapper.findByIdOrFirstName(null, "OY1");
      assert customer == null;

      customer = mapper.findByIdOrFirstName(id, "OY1");
      assert customer != null;
//
      countById = mapper.countById(id);
      assert countById != null;
//			
      customer = mapper.findByIdAndFirstName(b.getId(), b.getFirstName());
      assert customer != null;

      List<Customer> list = mapper.findByFirstNameOrderByIdAsc(b.getFirstName());
      assert (list != null && list.size() > 0);

      list = mapper.findByFirstNameStartingWith(b.getFirstName());
      assert (list != null && list.size() > 0);

      list = mapper.findByIdInOrId(Arrays.asList(id, 2, 3), id);
      assert (list != null && list.size() > 0);

      list = mapper.findByIdIn(Arrays.asList(id, 2, 3));
      assert (list != null && list.size() > 0);

      String firstName = mapper.findFirstNameById(id);
      assert firstName != null;

      firstName = mapper.findDistinctFirstNameById(id);
      assert firstName != null;

      int num = mapper.deleteByFirstNameAndId(b.getFirstName(), id);
      assertEquals(1, num);

    } finally {
      session.commit();
      session.close();
    }
  }

}
