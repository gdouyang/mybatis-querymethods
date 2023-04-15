package mybatis.querymethods.tkmapper;

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

import mybatis.querymethods.Customer;
import mybatis.querymethods.QueryMethodsTest;
import mybatis.querymethods.tkmapper.TkMapperWhereFactory;
import tk.mybatis.mapper.entity.Example;

/**
 * 
 * @author OYGD
 *
 */
public class TkMapperFillTest extends BaseTest {
  @Test
  public void test() {
    SqlSession session = sqlSessionFactory.openSession();

    CustomerMapper mapper = session.getMapper(CustomerMapper.class);
    try {
      Integer id = 1;
      QueryMethodsTest.log("deleteByPrimaryKey");
      mapper.deleteByPrimaryKey(id);

      QueryMethodsTest.log("insert1");
      Customer b = new Customer();
      b.setId(id);
      b.setFirstName("OY");
      b.setLastName("GD");
      b.setActive(false);
      mapper.insert1(b);
      
      b = new Customer();
      b.setId(2);
      b.setFirstName("OY-aa");
      b.setLastName("GD-aa");
      b.setActive(false);
      mapper.insert(b);
      
      test(mapper);

    } finally {
      QueryMethodsTest.log("delete all");
      mapper.delete(new Customer());
      
      session.commit();
      session.close();
    }
  }
  
  public static void test(CustomerMapper mapper) {
    
    Map<String, Object> param = new HashMap<>();
    log("findByIdAndFirstName");
    param.put("id", 1);
    param.put("firstName", "OY");
    Example example = TkMapperWhereFactory.createExample("findByIdAndFirstName", Customer.class, param);
    List<Customer> list = mapper.selectByExample(example);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    Integer id = 1;

    log("findByIdOrFirstName");
    param.put("id", null);
    param.put("firstName", "OY1");
    example = TkMapperWhereFactory.createExample("findByIdOrFirstName", Customer.class, param);
    Customer customer = mapper.selectOneByExample(example);
    assertNull(customer);

    //
    log("findByFirstNameOrderByIdAsc");
    param.put("firstName", "OY");
    example = TkMapperWhereFactory.createExample("findByFirstNameOrderByIdAsc", Customer.class, param);
    list = mapper.selectByExample(example);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByFirstNameStartingWith");
    example = TkMapperWhereFactory.createExample("findByFirstNameStartingWith", Customer.class, param);
    list = mapper.selectByExample(example);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

//    log("findByIdInOrId");
    List<Integer> asList = Arrays.asList(id, 2, 3);
//    example = TkMapperWhereFactory.createExample("findByIdInOrId", Customer.class, param);
//    list = mapper.selectByExample(example);
////    list = mapper.findByIdInOrId(asList, id);
//    assertTrue(list != null && list.size() > 0);
//    assertNotNull(list.get(0));

    log("findByIdIn");
    param.put("id", asList);
    example = TkMapperWhereFactory.createExample("findByIdIn", Customer.class, param);
    list = mapper.selectByExample(example);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByActiveTrue");
    example = TkMapperWhereFactory.createExample("findByActiveTrue", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() == 0, "错误");
    
    log("findByIdBetween");
    param.put("id", new Integer[] {1, 2});
    example = TkMapperWhereFactory.createExample("findByIdBetween", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdAfter");
    param.put("id", 0);
    example = TkMapperWhereFactory.createExample("findByIdAfter", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThan");
    param.put("id", 0);
    example = TkMapperWhereFactory.createExample("findByIdGreaterThan", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThanEqual");
    param.put("id", 1);
    example = TkMapperWhereFactory.createExample("findByIdGreaterThanEqual", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdBefore");
    param.put("id", 2);
    example = TkMapperWhereFactory.createExample("findByIdBefore", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdLessThan");
    param.put("id", 2);
    example = TkMapperWhereFactory.createExample("findByIdLessThan", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdLessThanEqual");
    param.put("id", 2);
    example = TkMapperWhereFactory.createExample("findByIdLessThanEqual", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdNotIn");
    param.put("id", asList);
    example = TkMapperWhereFactory.createExample("findByIdNotIn", Customer.class, param);
    list = mapper.selectByExample(example);
    Assert.isTrue(list != null && list.size() == 0, "错误");

  }

  public static void log(String str) {
    System.out.println(String.format("\n------- %s ---------", str));
  }

}
