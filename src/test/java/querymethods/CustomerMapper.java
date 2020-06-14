package querymethods;


import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

/**
 * query methods Mapper
 * 
 * @author OYGD
 *
 */
public interface CustomerMapper extends Mapper<Customer> {
  Customer selectCustomer(int id);

  void insert1(Customer b);

  @Select("")
  Customer findByIdAndFirstName(int id, String name);

  @Select("")
  Customer findByIdOrFirstName(int id, String name);

  @Select("")
  Customer findById(int id);

  @Select("")
  Integer countById(int id);

  @Select("")
  List<Customer> findByFirstNameOrderByIdAsc(String name);

  @Select("")
  List<Customer> findByFirstNameStartingWith(String name);

  @Select("")
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);

  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);

  @Select("")
  String findFirstNameById(int id);

  @Delete("")
  int deleteById(int id);

  @Delete("")
  int deleteByFirstName(String name);

  @Delete("")
  int deleteByFirstNameAndId(String name, int id);

}
