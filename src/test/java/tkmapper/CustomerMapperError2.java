package tkmapper;


import querymethods.Customer;
import querymethods.QueryMethodsMapperError2;
import tk.mybatis.mapper.common.Mapper;

/**
 * 测试property不存在时的场景findId1ById
 * 
 * @author OYGD
 *
 */
public interface CustomerMapperError2 extends Mapper<Customer>, QueryMethodsMapperError2 {

}
