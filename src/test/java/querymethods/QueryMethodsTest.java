package querymethods;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

public class QueryMethodsTest {
  public static void test(QueryMethodsMapper mapper, Customer b) {
    Integer id = b.getId();

    log("countById with null");
    Integer countById = mapper.countById(null);
    assert countById == 0;

    log("findByIdOrFirstName");
    Customer customer = mapper.findByIdOrFirstName(null, "OY1");
    assert customer == null;

    log("findByIdOrFirstName");
    customer = mapper.findByIdOrFirstName(id, "OY1");
    assert customer != null;
//
    log("countById not empty");
    countById = mapper.countById(id);
    assert countById != 0;
//        
    log("findByIdAndFirstName");
    customer = mapper.findByIdAndFirstName(b.getId(), b.getFirstName());
    assert customer != null;

    log("findByFirstNameOrderByIdAsc");
    List<Customer> list = mapper.findByFirstNameOrderByIdAsc(b.getFirstName());
    assert (list != null && list.size() > 0);

    log("findByFirstNameStartingWith");
    list = mapper.findByFirstNameStartingWith(b.getFirstName());
    assert (list != null && list.size() > 0);

    log("findByIdInOrId");
    list = mapper.findByIdInOrId(Arrays.asList(id, 2, 3), id);
    assert (list != null && list.size() > 0);

    log("findByIdIn");
    list = mapper.findByIdIn(Arrays.asList(id, 2, 3));
    assert (list != null && list.size() > 0);

    log("findFirstNameById");
    String firstName = mapper.findFirstNameById(id);
    assert firstName != null;

    log("findDistinctFirstNameById");
    firstName = mapper.findDistinctFirstNameById(id);
    assert firstName != null;

    log("deleteByFirstNameAndId");
    int num = mapper.deleteByFirstNameAndId(b.getFirstName(), id);
    assertEquals(1, num);
  }

  public static void log(String str) {
    System.out.println(String.format("------- %s ---------", str));
  }
}
