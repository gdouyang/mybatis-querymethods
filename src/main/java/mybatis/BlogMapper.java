package mybatis;


import org.apache.ibatis.annotations.Select;

import tk.mybatis.mapper.common.Mapper;

public interface BlogMapper extends Mapper<Blog>
{
	Blog selectBlog(int id);
	
	void insert1(Blog b);
	
	@Select("")
//	@AutoSql
	Blog findByIdAndFirstName(int id, String name);
	
	@Select("")
	Blog findByIdOrFirstName(int id, String name);
}
