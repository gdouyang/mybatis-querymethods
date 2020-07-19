package querymethods;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

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
    list = mapper.findByIdInOrId(Arrays.asList(id, 2, 3), id);
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findByIdIn");
    list = mapper.findByIdIn(Arrays.asList(id, 2, 3));
    assertTrue(list != null && list.size() > 0);
    assertNotNull(list.get(0));

    log("findFirstNameById");
    String firstName = mapper.findFirstNameById(id);
    assertNotNull(firstName);

    log("findDistinctFirstNameById");
    firstName = mapper.findDistinctFirstNameById(id);
    assertNotNull(firstName);

    log("deleteByFirstNameAndId");
    int num = mapper.deleteByFirstNameAndId(b.getFirstName(), id);
    assertEquals(1, num);
  }

  public static void log(String str) {
    System.out.println(String.format("------- %s ---------", str));
  }
}
