package querymethods.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import querymethods.Customer;

public interface QueryMethodsMapper {

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id = ? and
   * first_name = ? ) )
   */
  @Select("")
  Customer findByIdAndFirstName(Integer id, String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is null ) or (
   * first_name = ? ) )
   */
  @Select("")
  Customer findByIdOrFirstName(Integer id, String name);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  Customer findById(Integer id);

  /** SELECT COUNT(id) FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  Integer countById(Integer id);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name = ? ) )
   * order by id ASC
   */
  @Select("")
  List<Customer> findByFirstNameOrderByIdAsc(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name = ? ) )
   * order by create_time_ ASC
   */
  @Select("")
  List<Customer> findByFirstNameOrderByCreateTimeAsc(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * 'ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameStartingWith(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * '%ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameEndingWith(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * '%ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameContaining(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name not like
   * '%ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameNotContaining(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * 'ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameLike(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name not like
   * 'ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameNotLike(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id in ( ? , ? , ? )
   * ) or ( id = ? ) )
   */
  @Select("")
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id in ( ? , ? , ? )
   * ) )
   */
  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id not in ( ? , ? ,
   * ? ) ) )
   */
  @Select("")
  List<Customer> findByIdNotIn(List<Integer> idList);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is null ) ) */
  @Select("")
  List<Customer> findByIdIsNull();

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is not null ) )
   */
  @Select("")
  List<Customer> findByIdIsNotNull();

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( active = 1 ) ) */
  @Select("")
  List<Customer> findByActiveTrue();

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id between ? and ? )
   * )
   */
  @Select("")
  List<Customer> findByIdBetween(Integer from, Integer to);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id > ? ) ) */
  @Select("")
  List<Customer> findByIdAfter(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id > ? ) ) */
  @Select("")
  List<Customer> findByIdGreaterThan(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id >= ? ) ) */
  @Select("")
  List<Customer> findByIdGreaterThanEqual(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id < ? ) ) */
  @Select("")
  List<Customer> findByIdBefore(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id < ? ) ) */
  @Select("")
  List<Customer> findByIdLessThan(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id <= ? ) ) */
  @Select("")
  List<Customer> findByIdLessThanEqual(Integer from);

  /** SELECT first_name FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  String findFirstNameById(Integer id);

  /** SELECT distinct first_name FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  String findDistinctFirstNameById(Integer id);

  /** DELETE FROM customer WHERE ( ( first_name = ? ) ) */
  @Delete("")
  int deleteByFirstName(String name);

  /** DELETE FROM customer WHERE ( ( first_name = ? and id = ? ) ) */
  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);
}
