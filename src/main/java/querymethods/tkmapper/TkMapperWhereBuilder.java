package querymethods.tkmapper;

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
import querymethods.tkmapper.Example.Criteria;
import querymethods.tkmapper.Example.OrderBy;
import querymethods.util.MsIdUtil;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.util.StringUtil;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
public class TkMapperWhereBuilder {

  protected final static Log logger = LogFactory.getLog(TkMapperWhereBuilder.class);

  private TkMapperWhereBuilder() {}

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
          build(part, criteria, params);
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
   * 拼装where条件，参考spring data PredicateBuilder
   * 
   * @return
   */
  public static Criteria build(Part part, Criteria root, Queue<Object> args) {

    PropertyPath property = part.getProperty();
    Type type = part.getType();
    String segment = property.getSegment();
    switch (type) {
      case BETWEEN:
        return root.andBetween(segment, args.poll(), args.poll());
      case AFTER:
      case GREATER_THAN:
        return root.andGreaterThan(segment, args.poll());
      case GREATER_THAN_EQUAL:
        return root.andGreaterThanOrEqualTo(segment, args.poll());
      case BEFORE:
      case LESS_THAN:
        return root.andLessThan(segment, args.poll());
      case LESS_THAN_EQUAL:
        return root.andLessThanOrEqualTo(segment, args.poll());
      case IS_NULL:
        return root.andIsNull(segment);
      case IS_NOT_NULL:
        return root.andIsNotNull(segment);
      case NOT_IN:
        return root.andNotIn(segment, (Iterable<?>) args.poll());
      case IN:
        return root.andIn(segment, (Iterable<?>) args.poll());
      case STARTING_WITH:
        return root.andLike(segment, "%" + args.poll().toString());
      case ENDING_WITH:
        return root.andLike(segment, args.poll().toString() + "%");
      case CONTAINING:
        return root.andLike(segment, "%" + args.poll().toString() + "%");
      case NOT_CONTAINING:
        return root.andNotIn(segment, (Iterable<?>) args.poll());
      case LIKE:
        return root.andLike(segment, args.poll().toString());
      case NOT_LIKE:
        return root.andNotLike(segment, args.poll().toString());
      case TRUE:
        return root.andEqualTo(segment, true);
      case FALSE:
        return root.andEqualTo(segment, false);
      case SIMPLE_PROPERTY:
        return root.andEqualTo(segment, args.poll());
      case NEGATING_SIMPLE_PROPERTY:
        return root.andNotEqualTo(segment, args.poll());
      default:
        throw new IllegalArgumentException("Unsupported keyword " + type);
    }
  }

}
