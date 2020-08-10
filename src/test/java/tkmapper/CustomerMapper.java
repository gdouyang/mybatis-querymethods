package tkmapper;


import querymethods.Customer;
import querymethods.QueryMethodsMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * query methods Mapper
 * 
 * @author OYGD
 *
 */
public interface CustomerMapper extends Mapper<Customer>, QueryMethodsMapper {
  Customer selectCustomer(int id);

  void insert1(Customer b);


}
