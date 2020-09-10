package querymethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;

public class QueryMethodsTest {
  public static void test(QueryMethodsMapper mapper, Customer b) {
    Integer id = b.getId();

    log("countById with null");
    Integer countById = mapper.countById(null);
    assertEquals(countById, Integer.valueOf(0));

    log("findByIdOrFirstName");
    Customer customer = mapper.findByIdOrFirstName(null, "OY1");
    assertNull(customer);

    log("findByIdOrFirstName");
    customer = mapper.findByIdOrFirstName(id, "OY1");
    assertTrue(customer != null);
//
    log("countById not empty");
    countById = mapper.countById(id);
    assertTrue(countById != 0);
//        
    log("findByIdAndFirstName");
    customer = mapper.findByIdAndFirstName(b.getId(), b.getFirstName());
    assertTrue(customer != null);

    log("findByFirstNameOrderByIdAsc");
    List<Customer> list = mapper.findByFirstNameOrderByIdAsc(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByFirstNameStartingWith");
    list = mapper.findByFirstNameStartingWith(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByIdInOrId");
    List<Integer> asList = Arrays.asList(id, 2, 3);
    list = mapper.findByIdInOrId(asList, id);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByIdIn");
    list = mapper.findByIdIn(asList);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByActiveTrue");
    list = mapper.findByActiveTrue();
    Assert.isTrue(list != null && list.size() == 0, "错误");
    
    log("findByIdBetween");
    list = mapper.findByIdBetween(1, 2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdAfter");
    list = mapper.findByIdAfter(0);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThan");
    list = mapper.findByIdGreaterThan(0);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdGreaterThanEqual");
    list = mapper.findByIdGreaterThanEqual(1);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdBefore");
    list = mapper.findByIdBefore(2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    log("findByIdBefore");
    list = mapper.findByIdLessThan(2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    log("findByIdBefore");
    list = mapper.findByIdLessThanEqual(2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdNotIn");
    list = mapper.findByIdNotIn(asList);
    Assert.isTrue(list != null && list.size() == 0, "错误");

    log("findFirstNameById");
    String firstName = mapper.findFirstNameById(id);
    assertEquals("OY", firstName);

    log("findDistinctFirstNameById");
    firstName = mapper.findDistinctFirstNameById(id);
    assertEquals("OY", firstName);

    log("deleteByFirstNameAndId");
    int num = mapper.deleteByFirstNameAndId(b.getFirstName(), id);
    assertEquals(1, num);
  }

  public static void log(String str) {
    System.out.println(String.format("\n------- %s ---------", str));
  }
}
