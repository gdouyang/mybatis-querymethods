package querymethods;


import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

/**
 * query methods Mapper
 * @author OYGD
 *
 */
public interface BlogMapper extends Mapper<Blog>
{
	Blog selectBlog(int id);
	
	void insert1(Blog b);
	
	@Select("")
//	@AutoSql
	Blog findByIdAndFirstName(int id, String name);
	
	@Select("")
	Blog findByIdOrFirstName(int id, String name);
	
	@Select("")
	Blog findById(int id);
	
	@Select("")
	Integer countById(int id);
	
}
