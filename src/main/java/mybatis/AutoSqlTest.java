package mybatis;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import mybatis.gen.QueryMethodsHelper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

public class AutoSqlTest
{
	public static void main(String[] args) throws IOException, ClassNotFoundException
	{
		String resource = "mybatis/mybatis-config.xml";
		InputStream inputStream = Resources.getResourceAsStream(resource);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
		SqlSession session = sqlSessionFactory.openSession();
		
		//创建一个MapperHelper
		MapperHelper mapperHelper = new MapperHelper();
		//特殊配置
		Config config = new Config();
		//主键自增回写方法,默认值MYSQL,详细说明请看文档
		config.setIDENTITY("MYSQL");
		//支持getter和setter方法上的注解
		config.setEnableMethodAnnotation(true);
		//设置 insert 和 update 中，是否判断字符串类型!=''
		config.setNotEmpty(true);
		//校验Example中的类型和最终调用时Mapper的泛型是否一致
		config.setCheckExampleEntityClass(true);
		//启用简单类型
		config.setUseSimpleType(true);
		//枚举按简单类型处理
		config.setEnumAsSimpleType(true);
		//自动处理关键字 - mysql
		config.setWrapKeyword("`{0}`");
		//设置配置
		mapperHelper.setConfig(config);
		//注册通用接口，和其他集成方式中的 mappers 参数作用相同
		//4.0 之后的版本，如果类似 Mapper.class 这样的基础接口带有 @RegisterMapper 注解，就不必在这里注册
		mapperHelper.registerMapper(Mapper.class);
		mapperHelper.processConfiguration(session.getConfiguration());
		QueryMethodsHelper.processConfiguration(session.getConfiguration(), mapperHelper);
				
		try {
		  BlogMapper mapper = session.getMapper(BlogMapper.class);
		  
//		  insert(mapper);
		  
		  Blog blog = 
//				  mapper.selectBlog(1); 
		  		mapper.findByIdOrFirstName(1, "OY1");
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