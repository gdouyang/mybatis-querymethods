package querymethods;

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
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);

  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);

  @Select("")
  String findFirstNameById(Integer id);

  @Select("")
  String findDistinctFirstNameById(Integer id);

  @Delete("")
  int deleteByFirstName(String name);

  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);
}
