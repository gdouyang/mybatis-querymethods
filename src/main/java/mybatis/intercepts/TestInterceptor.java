package mybatis.intercepts;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;

import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import mybatis.gen.SqlGenUtil;
import mybatis.gen.WhereBuilder;
import mybatis.query.Part;
import mybatis.query.PartTree;
import mybatis.query.PartTree.OrPart;
import tk.mybatis.mapper.entity.Example;

@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class TestInterceptor implements Interceptor {
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Executor statementHandler = (Executor) invocation.getTarget();
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement)args[0];
		Object parameter = args[1];
		RowBounds rbs = (RowBounds)args[2];
		ResultHandler rh = (ResultHandler)args[3];
		
		if(parameter instanceof ParamMap) {
			ParamMap<Object> pm = (ParamMap)parameter;
			
			Queue<Object> params = new LinkedList<>();
			
			String regex = "^param(\\d)+$";
			String[] keys = pm.keySet().toArray(new String[] {});
			Arrays.sort(keys);
			for (String key : keys) {
				if(key.matches(regex)) {
					params.add(pm.get(key));
				}
			}
			String id = ms.getId();
			int lastIndexOf = id.lastIndexOf(".");
			String mapperName = id.substring(0, lastIndexOf);
			
			Class<?> clazz = Class.forName(mapperName);
			Class<?> entityClass = SqlGenUtil.getEntityClass(clazz);
			
			String methodName = id.substring(lastIndexOf + 1);
			
			PartTree tree = new PartTree(methodName);
			
			Example example = new Example(entityClass);
			Example.Criteria criteria = example.createCriteria();
			for (OrPart node : tree) {
				for (Part part : node) {
					WhereBuilder.build(part, criteria, params);
				}
			}
			
			return invocation.getMethod().invoke(statementHandler, ms, example, rbs, rh);
		}
        return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {

	}
	
}