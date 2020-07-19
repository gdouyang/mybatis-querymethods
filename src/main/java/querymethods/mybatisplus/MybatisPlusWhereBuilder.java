package querymethods.mybatisplus;

import java.util.Collection;
import java.util.Queue;

import org.apache.ibatis.binding.MapperMethod.ParamMap;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;

import querymethods.spring.data.PartTreeFactory;
import querymethods.spring.data.mapping.PropertyPath;
import querymethods.spring.data.query.domain.Sort;
import querymethods.spring.data.query.parser.Part;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.spring.data.query.parser.Part.Type;
import querymethods.spring.data.query.parser.PartTree.OrPart;
import querymethods.util.MsIdUtil;

/**
 * where条件构建类 参考spring data PredicateBuilder
 * 
 * @author OYGD
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class MybatisPlusWhereBuilder {

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
    wrapper.getSqlSelect();
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

    build(params, tree, wrapper, tableInfo);

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
    ParamMap<Object> pm = new ParamMap<>();
    pm.put(Constants.WRAPPER, wrapper);
    return pm;
  }

  private static void build(Queue<Object> params, PartTree tree, QueryWrapper<?> wrapper,
      MPTableInfo tableInfo) {
    int index = 0;
    for (OrPart node : tree) {
      if (index == 0) {
        andSql(params, wrapper, node, tableInfo);
      } else {
        wrapper.or(i -> {
          andSql(params, i, node, tableInfo);
        });
      }
      index++;
    }
  }

  private static void andSql(Queue<Object> params, QueryWrapper<?> wrapper, OrPart node,
      MPTableInfo tableInfo) {
    for (Part part : node) {
      build(part, wrapper, params, tableInfo);
    }
  }

  /**
   * 拼装where条件，参考spring data PredicateBuilder
   * 
   * @return
   */
  public static void build(Part part, QueryWrapper root, Queue<Object> args,
      MPTableInfo tableInfo) {

    PropertyPath property = part.getProperty();
    Type type = part.getType();
    String column = tableInfo.getColumnByProperty(property.getSegment());
    switch (type) {
      case BETWEEN:
        root.between(column, args.poll(), args.poll());
        break;
      case AFTER:
      case GREATER_THAN:
        root.gt(column, args.poll());
        break;
      case GREATER_THAN_EQUAL:
        root.ge(column, args.poll());
        break;
      case BEFORE:
      case LESS_THAN:
        root.lt(column, args.poll());
        break;
      case LESS_THAN_EQUAL:
        root.le(column, args.poll());
        break;
      case IS_NULL:
        root.isNull(column);
        break;
      case IS_NOT_NULL:
        root.isNotNull(column);
        break;
      case NOT_IN:
        root.notIn(column, ((Collection<?>) args.poll()).toArray());
        break;
      case IN:
        root.in(column, ((Collection<?>) args.poll()).toArray());
        break;
      case STARTING_WITH:
        root.likeLeft(column, args.poll());
        break;
      case ENDING_WITH:
        root.likeRight(column, args.poll());
        break;
      case CONTAINING:
        root.like(column, args.poll());
        break;
      case NOT_CONTAINING:
        root.notLike(column, args.poll());
        break;
      case LIKE:
        root.apply(column + " like {0}", args.poll().toString());
        break;
      case NOT_LIKE:
        root.apply(column + " not like {0}", args.poll());
        break;
      case TRUE:
        root.eq(column, true);
        break;
      case FALSE:
        root.eq(column, false);
        break;
      case SIMPLE_PROPERTY:
        root.eq(column, args.poll());
        break;
      case NEGATING_SIMPLE_PROPERTY:
        root.ne(column, args.poll());
        break;
      default:
        throw new IllegalArgumentException("Unsupported keyword " + type);
    }
  }


}
