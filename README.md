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

使用方式 <https://mvnrepository.com/artifact/com.github.gdouyang/mybatis-querymethods>
```
<dependency>
    <groupId>com.github.gdouyang</groupId>
    <artifactId>mybatis-querymethods</artifactId>
    <version>${version}</version>
</dependency>
```

spring boot 启动方式（tkmapper）
```
@tk.mybatis.spring.annotation.MapperScan(
		factoryBean = querymethods.tkmapper.QueryMethodsMapperFactoryBean.class
)
@EnableTransactionManagement
@SpringBootApplication
public class QuickDuckApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuickDuckApplication.class, args);
	}
}

# 配置application.yml
mybatis:
  config-location: classpath:mybatis-config.xml
```
spring boot 启动方式（mybatis-plus）
```
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(basePackages = "com.example.demo", factoryBean = querymethods.mybatisplus.QueryMethodsMapperFactoryBean.class)
@SpringBootApplication
public class QuickDuckApplication {

  public static void main(String[] args) {
    SpringApplication.run(QuickDuckApplication.class, args);
  }

}

# 配置application.yml
mybatis-plus:
  config-location: classpath:mybatis-config.xml
```
配置拦截器`mybatis-config.xml`
```
 <plugins>
    <!-- 分页 -->
    <plugin interceptor="com.github.pagehelper.PageInterceptor"></plugin>
    <!-- 查询方法 -->
    <plugin interceptor="querymethods.intercepts.QueryMethodsInterceptor"></plugin>
 </plugins>
```
spring mvc启动方式
```
<bean class="querymethods.tkmapper.MapperScannerConfigurer">
    <property name="basePackage" value="org.mybatis.spring.sample.mapper" />
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>

# 配置拦截器
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   <property name="plugins">
    <array>
	<bean class="com.github.pagehelper.PageInterceptor"></bean>
        <bean class="querymethods.intercepts.QueryMethodsInterceptor"></bean>
    </array>
   </property>
</bean>
```

在Mapper使用`Select`注解，给空字符串， 没有注解的需要在xml文件中配置
```
public interface CustomerMapper extends Mapper<Customer> {
  @Select("")
  Customer findByIdAndFirstName(Integer id, String name);

  @Select("")
  Customer findByIdOrFirstName(Integer id, String name);

  @Select("")
  Customer findById(Integer id);

  @Select("")
  Integer countById(Integer id);

  @Select("")
  List<Customer> findByFirstNameOrderByIdAsc(String name);

  @Select("")
  List<Customer> findByFirstNameStartingWith(String name);
  
  @Select("")
  List<Customer> findByFirstNameEndingWith(String name);
  
  @Select("")
  List<Customer> findByFirstNameContaining(String name);
  
  @Select("")
  List<Customer> findByFirstNameNotContaining(String name);
  
  @Select("")
  List<Customer> findByFirstNameLike(String name);
  
  @Select("")
  List<Customer> findByFirstNameNotLike(String name);
  
  @Select("")
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);
  
  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);
  
  @Select("")
  List<Customer> findByIdNotIn(List<Integer> idList);
  
  @Select("")
  List<Customer> findByIdIsNull();
  
  @Select("")
  List<Customer> findByIdIsNotNull();
  
  @Select("")
  List<Customer> findByActiveTrue();
  
  @Select("")
  List<Customer> findByIdBetween(Integer from, Integer to);
  
  @Select("")
  List<Customer> findByIdAfter(Integer from);
  @Select("")
  List<Customer> findByIdGreaterThan(Integer from);
  @Select("")
  List<Customer> findByIdGreaterThanEqual(Integer from);
  
  @Select("")
  List<Customer> findByIdBefore(Integer from);
  @Select("")
  List<Customer> findByIdLessThan(Integer from);
  @Select("")
  List<Customer> findByIdLessThanEqual(Integer from);

  @Select("")
  String findFirstNameById(Integer id);

  @Select("")
  String findDistinctFirstNameById(Integer id);

  @Delete("")
  int deleteByFirstName(String name);

  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);
}
```

```
CREATE DATABASE IF NOT EXISTS querymethods CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
use querymethods;

DROP TABLE IF EXISTS `customer`;
CREATE TABLE `customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
```