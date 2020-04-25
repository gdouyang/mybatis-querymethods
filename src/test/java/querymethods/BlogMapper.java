package querymethods;


import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

/**
 * query methods Mapper
 * @author OYGD
 *
 */
public interface BlogMapper extends Mapper<Customer>
{
	Customer selectBlog(int id);
	
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
	Customer findByFirstNameOrderByIdAsc(String name);
	
	@Select("")
	Customer findByFirstNameStartingWith(String name);
	
}
