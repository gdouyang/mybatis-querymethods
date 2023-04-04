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
地址：`https://mvnrepository.com/artifact/com.github.gdouyang/mybatis-querymethods`
## 使用方式
```
<dependency>
    <groupId>com.github.gdouyang</groupId>
    <artifactId>mybatis-querymethods</artifactId>
    <version>${version}</version>
</dependency>
```

### spring boot方式（tkmapper）
```
@tk.mybatis.spring.annotation.MapperScan(
		factoryBean = mybatis.querymethods.tkmapper.QueryMethodsMapperFactoryBean.class
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
### spring boot方式（mybatis-plus）
```
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@MapperScan(basePackages = "com.example.demo", factoryBean = mybatis.querymethods.mybatisplus.QueryMethodsMapperFactoryBean.class)
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
`mybatis-config.xml`配置（放在`src/main/resources`下）
```
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
  
  <plugins>
    <!-- 分页插件 -->
    <plugin interceptor="com.github.pagehelper.PageInterceptor"></plugin>
    <!-- 查询方法插件 -->
    <plugin interceptor="mybatis.querymethods.intercepts.QueryMethodsInterceptor"></plugin>
 </plugins>
  
</configuration>
```
### spring mvc方式（tkmapper）
```
<bean class="mybatis.querymethods.tkmapper.MapperScannerConfigurer">
    <property name="basePackage" value="org.mybatis.spring.sample.mapper" />
    <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
</bean>

# 配置拦截器
<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
   <property name="plugins">
    <array>
	<bean class="com.github.pagehelper.PageInterceptor"></bean>
        <bean class="mybatis.querymethods.intercepts.QueryMethodsInterceptor"></bean>
    </array>
   </property>
</bean>
```
### 样例
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

