# 通过方法名来动态生成sql查询

- 通过QueryMethodsHelper来重新生成sqlSource
- 通过QueryMethodsInterceptor来生成where条件

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
CREATE TABLE `Customer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `firstName` varchar(255) DEFAULT NULL,
  `lastName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;
```