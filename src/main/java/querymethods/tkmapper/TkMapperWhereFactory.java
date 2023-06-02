package querymethods.tkmapper;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import querymethods.spring.data.PartTreeFactory;
import querymethods.spring.data.mapping.PropertyPath;
import querymethods.spring.data.query.domain.Sort;
import querymethods.spring.data.query.parser.Part;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.spring.data.query.parser.Part.Type;
import querymethods.spring.data.query.parser.PartTree.OrPart;
import querymethods.util.IfThen;
import querymethods.util.MsIdUtil;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;
import tk.mybatis.mapper.entity.Example.OrderBy;
import tk.mybatis.mapper.util.StringUtil;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
public class TkMapperWhereFactory {

  protected final static Log logger = LogFactory.getLog(TkMapperWhereFactory.class);

  private TkMapperWhereFactory() {}

  /**
   * 根据MappedStatement id 与参数生成Example
   * 
   * @param msId
   * @param params
   * @return
   * @throws ClassNotFoundException
   */
  public static Example getExampleByMsId(String msId, Queue<Object> params)
      throws ClassNotFoundException {

    Class<?> entityClass = MsIdUtil.getEntityClass(msId);

    String methodName = MsIdUtil.getMethodName(msId);

    PartTree tree = PartTreeFactory.create(msId, methodName);
    Example example = new Example(entityClass, true);
    example.setDistinct(tree.isDistinct());
    if (StringUtil.isNotEmpty(tree.getQueryProperty())) {
      example.selectProperties(tree.getQueryProperty());
    }
    try {
      Example.Criteria criteria = example.createCriteria();
      for (OrPart node : tree) {
        for (Part part : node) {
          build(part, criteria, params, msId);
        }
        criteria = example.createCriteria();
        example.or(criteria);
      }
    } catch (MapperException e) {
      throw new MapperException(e.getMessage() + " -> " + msId, e);
    }
    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        OrderBy orderBy = example.orderBy(order.getProperty());
        if (order.isAscending()) {
          orderBy.asc();
        } else {
          orderBy.desc();
        }
      }
    }
    return example;
  }
  
  /**
   * 根据方法名和参数填充example
   * @param methodName
   * @param example
   * @param param
   */
  public static Example createExample(String methodName, Class entityClass, Map<String, Object> param) {
    if (StringUtil.isEmpty(methodName)) {
      throw new IllegalArgumentException("methodName must not be empty! [" + methodName + "]");
    }
    if (entityClass == null) {
      throw new IllegalArgumentException("entityClass must not be null! [" + methodName + "]");
    }
    Example example = new Example(entityClass);
	PartTree tree = new PartTree(methodName);
	example.setDistinct(tree.isDistinct());
    if (StringUtil.isNotEmpty(tree.getQueryProperty())) {
      example.selectProperties(tree.getQueryProperty());
    }
    try {
      Example.Criteria criteria = example.createCriteria();
      Queue<Object> args = new LinkedList<>();
      for (OrPart node : tree) {
        for (Part part : node) {
          Type type = part.getType();
          boolean converted = (type != Type.IS_NOT_NULL && type != Type.IS_NULL 
                               && type != Type.TRUE && type != Type.FALSE);
          if (converted) {
            PropertyPath pp = part.getProperty();
            String fieldName = pp.getSegment();
            Object object = param.get(fieldName);
            if (IfThen.isEmpty(object)) {
              continue;
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
          build(part, criteria, args, methodName);
        }
        criteria = example.createCriteria();
        example.or(criteria);
      }
    } catch (MapperException e) {
      throw new MapperException(e.getMessage() + " -> " + methodName, e);
    }
    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        OrderBy orderBy = example.orderBy(order.getProperty());
        if (order.isAscending()) {
          orderBy.asc();
        } else {
          orderBy.desc();
        }
      }
    }
    return example;
  }

  /**
   * 拼装where条件，参考spring data PredicateBuilder
   * 
   * @return
   */
  public static Criteria build(Part part, Criteria root, Queue<Object> args, String msId) {

    PropertyPath pp = part.getProperty();
    Type type = part.getType();
    String property = pp.getSegment();
    switch (type) {
      case BETWEEN:
        Object a = checkNotNull(property, args, msId);
        Object b = checkNotNull(property, args, msId);
        return root.andBetween(property, a, b);
      case AFTER:
      case GREATER_THAN:
        a = checkNotNull(property, args, msId);
        return root.andGreaterThan(property, a);
      case GREATER_THAN_EQUAL:
        a = checkNotNull(property, args, msId);
        return root.andGreaterThanOrEqualTo(property, a);
      case BEFORE:
      case LESS_THAN:
        a = checkNotNull(property, args, msId);
        return root.andLessThan(property, a);
      case LESS_THAN_EQUAL:
        a = checkNotNull(property, args, msId);
        return root.andLessThanOrEqualTo(property, a);
      case IS_NULL:
        return root.andIsNull(property);
      case IS_NOT_NULL:
        return root.andIsNotNull(property);
      case NOT_IN:
        a = checkNotNull(property, args, msId);
        return root.andNotIn(property, (Iterable<?>) a);
      case IN:
        a = checkNotNull(property, args, msId);
        return root.andIn(property, (Iterable<?>) a);
      case STARTING_WITH:
        a = checkNotNull(property, args, msId);
        return root.andLike(property, a.toString() + "%");
      case ENDING_WITH:
        a = checkNotNull(property, args, msId);
        return root.andLike(property, "%" + a.toString());
      case CONTAINING:
        a = checkNotNull(property, args, msId);
        return root.andLike(property, "%" + a.toString() + "%");
      case NOT_CONTAINING:
        a = checkNotNull(property, args, msId);
        return root.andNotLike(property, "%" + a.toString() + "%");
      case LIKE:
        a = checkNotNull(property, args, msId);
        return root.andLike(property, a.toString());
      case NOT_LIKE:
        a = checkNotNull(property, args, msId);
        return root.andNotLike(property, a.toString());
      case TRUE:
        return root.andEqualTo(property, 1);
      case FALSE:
        return root.andEqualTo(property, 0);
      case SIMPLE_PROPERTY:
        a = args.poll();
        if (a == null) {
          return root.andIsNull(property);
        }
        return root.andEqualTo(property, a);
      case NEGATING_SIMPLE_PROPERTY:
        a = args.poll();
        if (a == null) {
          return root.andIsNotNull(property);
        }
        return root.andNotEqualTo(property, a);
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
