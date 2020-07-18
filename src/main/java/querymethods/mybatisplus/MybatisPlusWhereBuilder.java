package querymethods.mybatisplus;

import java.util.Queue;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import querymethods.springdata.PartTreeFactory;
import querymethods.springdata.mapping.PropertyPath;
import querymethods.springdata.query.domain.Sort;
import querymethods.springdata.query.parser.Part;
import querymethods.springdata.query.parser.Part.Type;
import querymethods.springdata.query.parser.PartTree;
import querymethods.springdata.query.parser.PartTree.OrPart;
import querymethods.util.MsIdUtil;
import tk.mybatis.mapper.util.StringUtil;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
public class MybatisPlusWhereBuilder {

  /**
   * 根据MappedStatement id 与参数生成Example
   * 
   * @param msId
   * @param params
   * @return
   * @throws ClassNotFoundException
   */
  public static QueryWrapper<?> getExampleByMsId(String msId, Queue<Object> params)
      throws ClassNotFoundException {

    Class<?> entityClass = MsIdUtil.getEntityClass(msId);

    String methodName = MsIdUtil.getMethodName(msId);

    PartTree tree = PartTreeFactory.create(msId, methodName);
    QueryWrapper example = new QueryWrapper(entityClass);
//    example.setDistinct(tree.isDistinct());
    String queryProperty = tree.getQueryProperty();
    if (StringUtil.isNotEmpty(queryProperty)) {
      TableInfo tableInfo = MybatisPlusUtil.getTableInfo(entityClass);
      TableFieldInfo tableFieldInfo = tableInfo.getFieldList().stream()
          .filter(p -> p.getProperty().equals(queryProperty)).findFirst().get();
      example.select(tableFieldInfo.getColumn());
    }
    for (OrPart node : tree) {
      for (Part part : node) {
        build(part, example, params);
      }
      example.or();
    }
    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        if (order.isAscending()) {
          example.orderByAsc(order.getProperty());
        } else {
          example.orderByDesc(order.getProperty());
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
  public static void build(Part part, QueryWrapper root, Queue<Object> args) {

    PropertyPath property = part.getProperty();
    Type type = part.getType();
    String segment = property.getSegment();
    switch (type) {
      case BETWEEN:
        root.between(segment, args.poll(), args.poll());
        break;
      case AFTER:
      case GREATER_THAN:
        root.gt(segment, args.poll());
        break;
      case GREATER_THAN_EQUAL:
        root.ge(segment, args.poll());
        break;
      case BEFORE:
      case LESS_THAN:
        root.lt(segment, args.poll());
        break;
      case LESS_THAN_EQUAL:
        root.le(segment, args.poll());
        break;
      case IS_NULL:
        root.isNull(segment);
        break;
      case IS_NOT_NULL:
        root.isNotNull(segment);
        break;
      case NOT_IN:
        root.notIn(segment, (Iterable<?>) args.poll());
        break;
      case IN:
        root.in(segment, (Iterable<?>) args.poll());
        break;
      case STARTING_WITH:
        root.like(segment, "%" + args.poll().toString());
        break;
      case ENDING_WITH:
        root.like(segment, args.poll().toString() + "%");
        break;
      case CONTAINING:
        root.in(segment, "%" + args.poll().toString() + "%");
        break;
      case NOT_CONTAINING:
        root.notIn(segment, (Iterable<?>) args.poll());
        break;
      case LIKE:
        root.like(segment, args.poll().toString());
        break;
      case NOT_LIKE:
        root.notLike(segment, args.poll().toString());
        break;
      case TRUE:
        root.eq(segment, true);
        break;
      case FALSE:
        root.eq(segment, false);
        break;
      case SIMPLE_PROPERTY:
        root.eq(segment, args.poll());
        break;
      case NEGATING_SIMPLE_PROPERTY:
        root.ne(segment, args.poll());
        break;
      default:
        throw new IllegalArgumentException("Unsupported keyword " + type);
    }
  }


}
