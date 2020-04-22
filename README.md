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
