package mybatis.querymethods;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

public interface QueryMethodsMapper {

  @Select("")
  Customer findByIdAndFirstName(Integer id, String name);

  @Select("")
  Customer findByIdOrFirstName(Integer id, String name);

  @Select("")
  Customer findById(Integer id);

  @Select("")
  Integer countById(Integer id);

//  @Select("")
//  Integer countById1(Integer id);

  @Select("")
  List<Customer> findByFirstNameOrderByIdAsc(String name);

  @Select("")
  List<Customer> findByFirstNameStartingWith(String name);
  
  @Select("")
  List<Customer> findByFirstNameEndingWith(String name);
  
  @Select("")
  List<Customer> findByFirstNameContaining(String name);
  
  @Select("")
  List<Customer> findByFirstNameNotContaining(String name);
  
  @Select("")
  List<Customer> findByFirstNameLike(String name);
  
  @Select("")
  List<Customer> findByFirstNameNotLike(String name);
  
  @Select("")
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);
  
  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);
  
  @Select("")
  List<Customer> findByIdNotIn(List<Integer> idList);
  
  @Select("")
  List<Customer> findByIdIsNull();
  
  @Select("")
  List<Customer> findByIdIsNotNull();
  
  @Select("")
  List<Customer> findByActiveTrue();
  
  @Select("")
  List<Customer> findByIdBetween(Integer from, Integer to);
  
  @Select("")
  List<Customer> findByIdAfter(Integer from);
  @Select("")
  List<Customer> findByIdGreaterThan(Integer from);
  @Select("")
  List<Customer> findByIdGreaterThanEqual(Integer from);
  
  @Select("")
  List<Customer> findByIdBefore(Integer from);
  @Select("")
  List<Customer> findByIdLessThan(Integer from);
  @Select("")
  List<Customer> findByIdLessThanEqual(Integer from);

  @Select("")
  String findFirstNameById(Integer id);

  @Select("")
  String findDistinctFirstNameById(Integer id);

  @Delete("")
  int deleteByFirstName(String name);

  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);
}
