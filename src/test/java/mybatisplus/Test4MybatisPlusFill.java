package mybatisplus;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import org.springframework.util.Assert;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import querymethods.Customer;
import querymethods.QueryMethodsTest;
import querymethods.mybatisplus.MybatisPlusWhereFactory;

/**
 * 
 * @author OYGD
 *
 */
public class Test4MybatisPlusFill extends BaseTest {

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
      
      test(mapper);

      mapper.deleteById(id);
    } finally {
      session.commit();
      session.close();
    }
  }
  
public static void test(CustomerMapperMp mapper) {
    
    Map<String, Object> param = new HashMap<>();
    log("findByIdAndFirstName");
    param.put("id", 1);
    param.put("firstName", "OY");
    QueryWrapper<Customer> wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdAndFirstName", Customer.class, param);
    List<Customer> list = mapper.selectList(wrapper);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    Integer id = 1;

    log("findByIdOrFirstName");
    param.put("id", null);
    param.put("firstName", "OY1");
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdOrFirstName", Customer.class, param);
    Customer customer = mapper.selectOne(wrapper);
    assertNull(customer);

    //
    log("findByFirstNameOrderByIdAsc");
    param.put("firstName", "OY");
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByFirstNameOrderByIdAsc", Customer.class, param);
    list = mapper.selectList(wrapper);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByFirstNameStartingWith");
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByFirstNameStartingWith", Customer.class, param);
    list = mapper.selectList(wrapper);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

//    log("findByIdInOrId");
    List<Integer> asList = Arrays.asList(id, 2, 3);
//    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdInOrId", Customer.class, param);
//    list = mapper.selectList(wrapper);
////    list = mapper.findByIdInOrId(asList, id);
//    assertTrue(list != null && list.size() > 0);
//    assertNotNull(list.get(0));

    log("findByIdIn");
    param.put("id", asList);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdIn", Customer.class, param);
    list = mapper.selectList(wrapper);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByActiveTrue");
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByActiveTrue", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() == 0, "错误");
    
    log("findByIdBetween");
    param.put("id", new Integer[] {1, 2});
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdBetween", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdAfter");
    param.put("id", 0);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdAfter", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThan");
    param.put("id", 0);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdGreaterThan", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThanEqual");
    param.put("id", 1);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdGreaterThanEqual", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdBefore");
    param.put("id", 2);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdBefore", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdLessThan");
    param.put("id", 2);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdLessThan", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdLessThanEqual");
    param.put("id", 2);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdLessThanEqual", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdNotIn");
    param.put("id", asList);
    wrapper = MybatisPlusWhereFactory.createQueyWrapper("findByIdNotIn", Customer.class, param);
    list = mapper.selectList(wrapper);
    Assert.isTrue(list != null && list.size() == 0, "错误");

  }

  public static void log(String str) {
    System.out.println(String.format("\n------- %s ---------", str));
  }


}
