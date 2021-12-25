package tkmapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import querymethods.Customer;
import querymethods.tkmapper.TkMapperWhereFactory;
import tk.mybatis.mapper.entity.Example;

/**
 * 
 * @author OYGD
 *
 */
public class Test4TkMapperFill extends BaseTest {
  @Test
  public void test() {
    SqlSession session = sqlSessionFactory.openSession();

    try {
      Example example = new Example(Customer.class);
      Map<String, Object> param = new HashMap<>();
      param.put("id", 1);
//      param.put("firstName", "OY");
      TkMapperWhereFactory.fillExample("findByIdAndFirstNameEquels", example, param);
      CustomerMapper mapper = session.getMapper(CustomerMapper.class);
      
      List<Customer> list = mapper.selectByExample(example);
      assertTrue(list != null && list.size() > 0);
      assertNotNull(list.get(0));

    } finally {
      session.commit();
      session.close();
    }
  }

}
