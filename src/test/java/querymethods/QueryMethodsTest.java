package querymethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.Assert;
import querymethods.mapper.QueryMethodsMapper;

public class QueryMethodsTest {
  public static void test(QueryMethodsMapper mapper, Customer b) {
    Integer id = b.getId();

    log("countById with null");
    Integer countById = mapper.countById(null);
    assertEquals(countById, Integer.valueOf(0));

    Customer customer = null;
    log("findByIdAndFirstName");
    customer = mapper.findByIdAndFirstName(b.getId(), b.getFirstName());
    assertTrue(customer != null);
    
    log("findByIdOrFirstName");
    customer = mapper.findByIdOrFirstName(null, "OY1");
    assertNull(customer);

    
    log("findById");
    customer = mapper.findById(b.getId());
    assertTrue(customer != null);
    
    log("countById");
    countById = mapper.countById(id);
    assertTrue(countById != 0);

    log("findByFirstNameOrderByIdAsc");
    List<Customer> list = mapper.findByFirstNameOrderByIdAsc(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByFirstNameOrderByCreateTimeAsc");
    list = mapper.findByFirstNameOrderByCreateTimeAsc(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByFirstNameStartingWith");
    list = mapper.findByFirstNameStartingWith(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByFirstNameEndingWith");
    list = mapper.findByFirstNameEndingWith(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByFirstNameContaining");
    list = mapper.findByFirstNameContaining(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByFirstNameNotContaining");
    list = mapper.findByFirstNameNotContaining(b.getFirstName());
    assertTrue(list != null && list.size() == 0);
    
    log("findByFirstNameLike");
    list = mapper.findByFirstNameLike(b.getFirstName());
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByFirstNameNotLike");
    list = mapper.findByFirstNameNotLike(b.getFirstName());
    assertTrue(list != null && list.size() == 0);

    log("findByIdInOrId");
    List<Integer> asList = Arrays.asList(id, 2, 3);
    list = mapper.findByIdInOrId(asList, id);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByIdIn");
    list = mapper.findByIdIn(asList);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByIdInAndIdIn");
    list = mapper.findByIdInAndIdIn(asList, asList);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));
    
    log("findByIdNotIn");
    list = mapper.findByIdNotIn(asList);
    Assert.isTrue(list != null && list.size() == 0, "错误");
    
    log("findByIdIsNull");
    list = mapper.findByIdIsNull();
    Assert.isTrue(list != null && list.size() == 0, "错误");
    
    log("findByIdIsNotNull");
    list = mapper.findByIdIsNotNull();
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
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
    
    log("findByIdLessThan");
    list = mapper.findByIdLessThan(2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findByIdLessThanEqual");
    list = mapper.findByIdLessThanEqual(2);
    Assert.isTrue(list != null && list.size() > 0, "错误");
    
    log("findFirstNameById");
    String firstName = mapper.findFirstNameById(id);
    assertEquals("OY", firstName);

    log("findDistinctFirstNameById");
    firstName = mapper.findDistinctFirstNameById(id);
    assertEquals("OY", firstName);
    
    log("findNameByIdOrFirstNameAndId");
    mapper.findByIdOrFirstNameAndId(id, "OY", id);

    log("deleteByFirstNameAndId");
    int num = mapper.deleteByFirstName("test");
    assertEquals(0, num);
    
    log("deleteByFirstNameAndId");
    num = mapper.deleteByFirstNameAndId(b.getFirstName(), id);
    assertEquals(1, num);
  }

  public static void log(String str) {
    System.out.println(String.format("\n------- %s ---------", str));
  }
}
