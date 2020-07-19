package mybatisplus;


import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import querymethods.Customer;

/**
 * query methods Mapper
 * 
 * @author OYGD
 *
 */
public interface CustomerMapperMp extends BaseMapper<Customer> {

  @Select("")
  Customer findByIdAndFirstName(Integer id, String name);

  @Select("")
  Customer findByIdOrFirstName(Integer id, String name);

  @Select("")
  Customer findById(Integer id);

  @Select("")
  Integer countById(Integer id);

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
  int deleteById(Integer id);

  @Delete("")
  int deleteByFirstName(String name);

  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);

}
