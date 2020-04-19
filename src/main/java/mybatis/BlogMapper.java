package mybatis;


import mybatis.annotation.AutoSql;
import tk.mybatis.mapper.common.Mapper;

public interface BlogMapper extends Mapper<Blog>
{
	Blog selectBlog(int id);
	
	void insert1(Blog b);
	
//	@Select("select * from Customer where id = #{id}")
	@AutoSql
	Blog findByIdAndFirstName(int id, String name, String name1);
}
