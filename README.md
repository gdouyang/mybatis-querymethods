# 通过方法名来动态生成sql查询

- 通过QueryMethodsHelper来重新生成sqlSource
- 通过QueryMethodsInterceptor来生成where条件

spring boot 启动方式
```
@tk.mybatis.spring.annotation.MapperScan(
		factoryBean = QueryMethodsMapperFactoryBean.class
)
@EnableTransactionManagement
@SpringBootApplication
public class QuickDuckApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickDuckApplication.class, args);
	}

}
```
spring mvc启动方式
```
@Component
public class QuerymethodsConfig implements ApplicationListener<ApplicationStartedEvent> {

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		SqlSessionFactory factory = event.getApplicationContext().getBean(SqlSessionFactory.class);
		QueryMethodsHelper.processConfiguration(factory.getConfiguration());
		
	}
}
```

在Mapper使用`Select`注解，给于空字符串
```
public interface BlogMapper extends Mapper<Blog>
{	
	@Select("")
	Blog findByIdAndFirstName(int id, String name);
	
	@Select("")
	Blog findByIdOrFirstName(int id, String name);
	
	@Select("")
	Blog findById(int id);
	
	@Select("")
	Integer countById(int id);
}
```

```
CREATE DATABASE IF NOT EXISTS querymethods CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use querymethods;

DROP TABLE IF EXISTS `blog`;
CREATE TABLE `blog` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
```