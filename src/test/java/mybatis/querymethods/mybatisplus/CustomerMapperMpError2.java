package mybatis.querymethods.mybatisplus;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import mybatis.querymethods.Customer;
import mybatis.querymethods.QueryMethodsMapperError2;

/**
 * 测试property不存在时的场景findId1ById
 * 
 * @author OYGD
 *
 */
public interface CustomerMapperMpError2 extends BaseMapper<Customer>, QueryMethodsMapperError2 {



}
