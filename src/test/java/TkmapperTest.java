

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import querymethods.Blog;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tkmapper.BlogMapper1;

public class TkmapperTest {
	public static void main(String[] args) throws IOException {
		String resource = "mybatis-config.xml";
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
		try {
//			EntityHelper.initEntityNameMap(Blog.class, mapperHelper.getConfig());
			BlogMapper1 mapper = session.getMapper(BlogMapper1.class);
			Example example = new Example(Blog.class);
			Example.Criteria criteria = example.createCriteria();
			criteria.andEqualTo("id", 1);
			Blog blog = mapper.selectOneByExample(example);
			System.out.println(blog);
		} finally {
			session.commit();
			session.close();
		}
	}
}
