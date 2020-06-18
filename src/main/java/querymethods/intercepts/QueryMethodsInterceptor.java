package querymethods.intercepts;

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
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;

import querymethods.QueryMethodsHelper;
import querymethods.util.ExampleUtil;
import tk.mybatis.mapper.entity.Example;

/**
 * 查询方法拦截器
 * 
 * @author OYGD
 *
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class,
        RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "update", args = {MappedStatement.class,
        Object.class})})
public class QueryMethodsInterceptor implements Interceptor {

  /**
   * 
   */
  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Executor executor = (Executor) invocation.getTarget();
    Object[] args = invocation.getArgs();
    MappedStatement ms = (MappedStatement) args[0];

    String msId = ms.getId();
    if (QueryMethodsHelper.isQueryMethods(msId)) {
      Object parameter = args[1];
      RowBounds rbs = (RowBounds) args[2];
      ResultHandler<?> rh = (ResultHandler<?>) args[3];

      Example example = getExample(msId, parameter);

      return invocation.getMethod().invoke(executor, ms, example, rbs, rh);
    } else if (QueryMethodsHelper.isDeleteMethods(msId)) {
      Object parameter = args[1];
      Example example = getExample(msId, parameter);
      return invocation.getMethod().invoke(executor, ms, example);
    }
    return invocation.proceed();
  }

  private Example getExample(String msId, Object parameter) throws ClassNotFoundException {
    Queue<Object> params = new LinkedList<>();
    if (parameter instanceof ParamMap) {
      @SuppressWarnings("unchecked")
      ParamMap<Object> pm = (ParamMap<Object>) parameter;

      String regex = "^param(\\d)+$";
      String[] keys = pm.keySet().toArray(new String[] {});
      Arrays.sort(keys);
      for (String key : keys) {
        if (key.matches(regex)) {
          params.add(pm.get(key));
        }
      }
    } else if (parameter instanceof StrictMap) {
      @SuppressWarnings("unchecked")
      StrictMap<Object> pm = (StrictMap<Object>) parameter;
      String[] keys = pm.keySet().toArray(new String[] {});
      Arrays.sort(keys);
      for (String key : keys) {
        params.add(pm.get(key));
      }
    } else {
      params.add(parameter);
    }
    Example example = ExampleUtil.getExampleByMsId(msId, params);
    return example;
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {

  }

}
