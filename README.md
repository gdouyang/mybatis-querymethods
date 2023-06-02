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
### spring boot方式（mybatis-plus）
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
    <plugin interceptor="querymethods.intercepts.QueryMethodsInterceptor"></plugin>
 </plugins>
  
</configuration>
```
### spring mvc方式（tkmapper）
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
### 样例
在Mapper使用`Select`注解，给空字符串， 没有注解的需要在xml文件中配置
```
create table if not exists customer (
  id bigint primary key not null,
  first_name varchar(255),
  last_name varchar(255),
  create_time_ datetime,
  active tinyint
)
```
```
public interface CustomerMapper extends Mapper<Customer> {

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id = ? and
   * first_name = ? ) )
   */
  @Select("")
  Customer findByIdAndFirstName(Integer id, String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is null ) or (
   * first_name = ? ) )
   */
  @Select("")
  Customer findByIdOrFirstName(Integer id, String name);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  Customer findById(Integer id);

  /** SELECT COUNT(id) FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  Integer countById(Integer id);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name = ? ) )
   * order by id ASC
   */
  @Select("")
  List<Customer> findByFirstNameOrderByIdAsc(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name = ? ) )
   * order by create_time_ ASC
   */
  @Select("")
  List<Customer> findByFirstNameOrderByCreateTimeAsc(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * 'ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameStartingWith(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * '%ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameEndingWith(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * '%ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameContaining(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name not like
   * '%ABC%' ) )
   */
  @Select("")
  List<Customer> findByFirstNameNotContaining(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name like
   * 'ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameLike(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( first_name not like
   * 'ABC' ) )
   */
  @Select("")
  List<Customer> findByFirstNameNotLike(String name);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id in ( ? , ? , ? )
   * ) or ( id = ? ) )
   */
  @Select("")
  List<Customer> findByIdInOrId(List<Integer> idList, Integer id);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id in ( ? , ? , ? )
   * ) )
   */
  @Select("")
  List<Customer> findByIdIn(List<Integer> idList);

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id not in ( ? , ? ,
   * ? ) ) )
   */
  @Select("")
  List<Customer> findByIdNotIn(List<Integer> idList);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is null ) ) */
  @Select("")
  List<Customer> findByIdIsNull();

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id is not null ) )
   */
  @Select("")
  List<Customer> findByIdIsNotNull();

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( active = 1 ) ) */
  @Select("")
  List<Customer> findByActiveTrue();

  /**
   * SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id between ? and ? )
   * )
   */
  @Select("")
  List<Customer> findByIdBetween(Integer from, Integer to);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id > ? ) ) */
  @Select("")
  List<Customer> findByIdAfter(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id > ? ) ) */
  @Select("")
  List<Customer> findByIdGreaterThan(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id >= ? ) ) */
  @Select("")
  List<Customer> findByIdGreaterThanEqual(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id < ? ) ) */
  @Select("")
  List<Customer> findByIdBefore(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id < ? ) ) */
  @Select("")
  List<Customer> findByIdLessThan(Integer from);

  /** SELECT id,first_name,last_name,active,create_time_ FROM customer WHERE ( ( id <= ? ) ) */
  @Select("")
  List<Customer> findByIdLessThanEqual(Integer from);

  /** SELECT first_name FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  String findFirstNameById(Integer id);

  /** SELECT distinct first_name FROM customer WHERE ( ( id = ? ) ) */
  @Select("")
  String findDistinctFirstNameById(Integer id);

  /** DELETE FROM customer WHERE ( ( first_name = ? ) ) */
  @Delete("")
  int deleteByFirstName(String name);

  /** DELETE FROM customer WHERE ( ( first_name = ? and id = ? ) ) */
  @Delete("")
  int deleteByFirstNameAndId(String name, Integer id);

}
```

