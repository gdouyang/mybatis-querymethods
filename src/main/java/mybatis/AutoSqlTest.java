package mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import mybatis.override.MySqlSessionFactoryBuilder;

public class AutoSqlTest
{
	public static void main(String[] args) throws IOException
	{
		String resource = "mybatis/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new MySqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		try {
		  BlogMapper mapper = session.getMapper(BlogMapper.class);
		  
//		  insert(mapper);
		  
		  Blog blog = 
//				  mapper.selectBlog(1); 
		  		mapper.findByIdAndFirstName(1, "OY", "");
		  System.out.println(blog);
		} finally {
			session.commit();
		  session.close();
		}
		
		
	}

	static void insert(BlogMapper mapper) {
		Blog b = new Blog();
		  b.setId(1);
		  b.setFirstName("OY");
		  b.setLastName("GD");
		  mapper.insert1(b);
	}
}
