package querymethods.mybatisplus;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.ibatis.binding.MapperMethod.ParamMap;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import querymethods.spring.data.PartTreeFactory;
import querymethods.spring.data.mapping.PropertyPath;
import querymethods.spring.data.query.domain.Sort;
import querymethods.spring.data.query.parser.Part;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.spring.data.query.parser.Part.Type;
import querymethods.spring.data.query.parser.PartTree.OrPart;
import querymethods.util.IfThen;
import querymethods.util.MsIdUtil;
import tk.mybatis.mapper.util.StringUtil;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MybatisPlusWhereFactory {

  /**
   * 根据MappedStatement id 与参数生成Example
   * 
   * @param msId
   * @param params
   * @return
   * @throws ClassNotFoundException
   */
  public static ParamMap<Object> getExampleByMsId(String msId, Queue<Object> params)
      throws ClassNotFoundException {

    Class<?> entityClass = MsIdUtil.getEntityClass(msId);

    String methodName = MsIdUtil.getMethodName(msId);

    PartTree tree = PartTreeFactory.create(msId, methodName);

    QueryWrapper<?> wrapper = new QueryWrapper();
    TableInfo tbInfo = MybatisPlusUtil.getTableInfo(entityClass);
    MPTableInfo tableInfo = MybatisPlusUtil.getMPTableInfo(entityClass);

    String sqlSelect = Constants.EMPTY;
    if (tree.isDistinct()) {
      sqlSelect = " distinct ";
    }

    String queryProperty = tree.getQueryProperty();
    if (StringUtils.isNotBlank(queryProperty)) {
      sqlSelect += tableInfo.getColumnByProperty(queryProperty);
    } else if (tree.isDistinct()) {
      sqlSelect += tbInfo.getAllSqlSelect();
    }

    if (!Constants.EMPTY.equals(sqlSelect)) {
      wrapper.select(sqlSelect);
    }

    build(params, tree, wrapper, tableInfo, msId);

    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        String column = tableInfo.getColumnByProperty(order.getProperty());
        if (order.isAscending()) {
          wrapper.orderByAsc(column);
        } else {
          wrapper.orderByDesc(column);
        }
      }
    }
    ParamMap<Object> pm = new ParamMap<>();
    pm.put(Constants.WRAPPER, wrapper);
    if (!params.isEmpty()) {
    	for (Object p : params) {
    		ParameterUtils.findPage(p).ifPresent(page -> {
    			pm.put("$querymethods-ipage", page);
    		});
		}
    }
    return pm;
  }
  
  /**
   * 根据方法名和参数填充example
   * @param methodName
   * @param example
   * @param param
   */
  public static <T> QueryWrapper<T> createQueyWrapper(String methodName, Class<T> entityClass, Map<String, Object> param) {
    if (StringUtil.isEmpty(methodName)) {
      throw new IllegalArgumentException("methodName must not be empty! [" + methodName + "]");
    }
    if (entityClass == null) {
      throw new IllegalArgumentException("entityClass must not be null! [" + methodName + "]");
    }
    QueryWrapper<T> wrapper = new QueryWrapper<>();
    wrapper.setEntityClass(entityClass);
    TableInfo tbInfo = MybatisPlusUtil.getTableInfo(entityClass);
    MPTableInfo tableInfo = MybatisPlusUtil.getMPTableInfo(entityClass);

    PartTree tree = new PartTree(methodName);
    String sqlSelect = Constants.EMPTY;
    if (tree.isDistinct()) {
      sqlSelect = " distinct ";
    }

    String queryProperty = tree.getQueryProperty();
    if (StringUtils.isNotBlank(queryProperty)) {
      sqlSelect += tableInfo.getColumnByProperty(queryProperty);
    } else if (tree.isDistinct()) {
      sqlSelect += tbInfo.getAllSqlSelect();
    }

    if (!Constants.EMPTY.equals(sqlSelect)) {
      wrapper.select(sqlSelect);
    }

    Queue<Object> args = new LinkedList<>();
    int index = 0;
    for (OrPart node : tree) {
      if (index == 0) {
        for (Part part : node) {
          if (!setArgs(part, param, args)) {
            continue;
          }
          build(part, wrapper, args, tableInfo, methodName);
        }
      } else {
        wrapper.or(i -> {
          for (Part part : node) {
            if (!setArgs(part, param, args)) {
              continue;
            }
            build(part, i, args, tableInfo, methodName);
          }
        });
      }
      index++;
    }

    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        if (order.isAscending()) {
          wrapper.orderByAsc(order.getProperty());
        } else {
          wrapper.orderByDesc(order.getProperty());
        }
      }
    }
    return wrapper;
  }
  
  private static boolean setArgs(Part part, Map<String, Object> param, Queue<Object> args) {
    Type type = part.getType();
    boolean converted = (type != Type.IS_NOT_NULL && type != Type.IS_NULL 
                         && type != Type.TRUE && type != Type.FALSE);
    if (converted) {
      PropertyPath pp = part.getProperty();
      String fieldName = pp.getSegment();
      Object object = param.get(fieldName);
      if (IfThen.isEmpty(object)) {
        return false;
      }
      if (type == Type.BETWEEN) {
        if (object != null && object.getClass().isArray()) {
          List<Object> list = Arrays.asList((Object[])object);
          args.addAll(list);
        } else if (object instanceof Collection) {
          args.addAll((Collection)object);
        } else {
          args.add(object);
        } 
      } else {
        args.add(object);
      }
    }
    return true;
  }

  private static void build(Queue<Object> params, PartTree tree, QueryWrapper<?> wrapper,
      MPTableInfo tableInfo, String msId) {
    int index = 0;
    for (OrPart node : tree) {
      if (index == 0) {
        andSql(params, wrapper, node, tableInfo, msId);
      } else {
        wrapper.or(i -> {
          andSql(params, i, node, tableInfo, msId);
        });
      }
      index++;
    }
  }

  private static void andSql(Queue<Object> params, QueryWrapper<?> wrapper, OrPart node,
      MPTableInfo tableInfo, String msId) {
    for (Part part : node) {
      build(part, wrapper, params, tableInfo, msId);
    }
  }

  /**
   * 拼装where条件，参考spring data PredicateBuilder
   * 
   * @return
   */
  public static void build(Part part, QueryWrapper root, Queue<Object> args, MPTableInfo tableInfo,
      String msId) {

    PropertyPath pp = part.getProperty();
    Type type = part.getType();
    String property = pp.getSegment();
    String column = tableInfo.getColumnByProperty(property);
    switch (type) {
      case BETWEEN:
        Object a = checkNotNull(property, args, msId);
        Object b = checkNotNull(property, args, msId);
        root.between(column, a, b);
        break;
      case AFTER:
      case GREATER_THAN:
        a = checkNotNull(property, args, msId);
        root.gt(column, a);
        break;
      case GREATER_THAN_EQUAL:
        a = checkNotNull(property, args, msId);
        root.ge(column, a);
        break;
      case BEFORE:
      case LESS_THAN:
        a = checkNotNull(property, args, msId);
        root.lt(column, a);
        break;
      case LESS_THAN_EQUAL:
        a = checkNotNull(property, args, msId);
        root.le(column, a);
        break;
      case IS_NULL:
        root.isNull(column);
        break;
      case IS_NOT_NULL:
        root.isNotNull(column);
        break;
      case NOT_IN:
        a = checkNotNull(property, args, msId);
        root.notIn(column, ((Collection<?>) a).toArray());
        break;
      case IN:
        a = checkNotNull(property, args, msId);
        root.in(column, ((Collection<?>) a).toArray());
        break;
      case STARTING_WITH:
        a = checkNotNull(property, args, msId);
        root.likeLeft(column, a);
        break;
      case ENDING_WITH:
        a = checkNotNull(property, args, msId);
        root.likeRight(column, a);
        break;
      case CONTAINING:
        a = checkNotNull(property, args, msId);
        root.like(column, a);
        break;
      case NOT_CONTAINING:
        a = checkNotNull(property, args, msId);
        root.notLike(column, a);
        break;
      case LIKE:
        a = checkNotNull(property, args, msId);
        root.apply(column + " like {0}", a);
        break;
      case NOT_LIKE:
        a = checkNotNull(property, args, msId);
        root.apply(column + " not like {0}", a);
        break;
      case TRUE:
        root.eq(column, 1);
        break;
      case FALSE:
        root.eq(column, 0);
        break;
      case SIMPLE_PROPERTY:
        a = args.poll();
        if (a == null) {
          root.isNull(column);
        } else {
          root.eq(column, a);
        }
        break;
      case NEGATING_SIMPLE_PROPERTY:
        a = args.poll();
        if (a == null) {
          root.isNotNull(column);
        } else {
          root.ne(column, a);
        }
        break;
      default:
        throw new IllegalArgumentException("Unsupported keyword " + type);
    }
  }

  static Object checkNotNull(String property, Queue<Object> args, String msId) {
    Object param = args.poll();
    if (param == null) {
      throw new IllegalArgumentException("Value must not be null! [" + property + " -> " + msId + "]");
    }
    return param;
  }


}
