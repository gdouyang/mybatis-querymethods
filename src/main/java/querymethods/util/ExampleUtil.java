package querymethods.util;

import java.util.Queue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import querymethods.springdata.PartTreeFactory;
import querymethods.springdata.query.domain.Sort;
import querymethods.springdata.query.parser.Part;
import querymethods.springdata.query.parser.PartTree;
import querymethods.springdata.query.parser.PartTree.OrPart;
import tk.mybatis.mapper.MapperException;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.OrderBy;
import tk.mybatis.mapper.util.StringUtil;

/**
 * Example工具类
 * 
 * @author OYGD
 *
 */
public class ExampleUtil {

  protected final static Log logger = LogFactory.getLog(ExampleUtil.class);

  private ExampleUtil() {}

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
    Example example = new Example(entityClass, true, true);
    example.setDistinct(tree.isDistinct());
    if (StringUtil.isNotEmpty(tree.getQueryProperty())) {
      example.selectProperties(tree.getQueryProperty());
    }
    try {
      Example.Criteria criteria = example.createCriteria();
      for (OrPart node : tree) {
        for (Part part : node) {
          WhereBuilder.build(part, criteria, params);
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

}
