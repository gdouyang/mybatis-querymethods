在使用spring data jpa的时候可以通过方法名来动态的创建查询语句，于是有了想把这个功能移植到mybaits的想法

# 通过方法名来动态生成sql查询

- 通过QueryMethodsHelper来重新生成sqlSource
- 通过QueryMethodsInterceptor来生成where条件

tkmapper版本
```
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper</artifactId>
    <version>4.1.5</version>
</dependency>
```

使用方式
```
<dependency>
    <groupId>com.github.gdouyang</groupId>
    <artifactId>mybatis-querymethods</artifactId>
    <version>${version}</version>
</dependency>
```

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
<bean class="querymethods.MapperScannerConfigurer">
    <property name="basePackage" value="org.mybatis.spring.sample.mapper" />
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>
```
配置拦截器`mybatis-config.xml`
```
 <plugins>
      <plugin interceptor="com.github.pagehelper.PageInterceptor"></plugin>
      <plugin interceptor="querymethods.intercepts.QueryMethodsInterceptor"></plugin>
 </plugins>
```
配置拦截器`spring xml`
```
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   <property name="plugins">
    <array>
	<bean class="com.github.pagehelper.PageInterceptor"></bean>
        <bean class="querymethods.intercepts.QueryMethodsInterceptor"></bean>
    </array>
   </property>
</bean>
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
	
	@Select("")
    Blog findByIdInOrId(List<Integer> idList, Integer id);
    
    @Select("")
    Blog findByIdIn(List<Integer> idList);
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