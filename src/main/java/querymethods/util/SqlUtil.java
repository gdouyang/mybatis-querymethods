package querymethods.util;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.mapping.MappedStatement;

import querymethods.springdata.PartTreeFactory;
import querymethods.springdata.mapping.PropertyPath;
import querymethods.springdata.query.domain.Sort;
import querymethods.springdata.query.parser.Part;
import querymethods.springdata.query.parser.PartTree;
import querymethods.springdata.query.parser.PartTree.OrPart;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * sql重写工具类
 * 
 * @author OYGD
 *
 */
public class SqlUtil {

  /**
   * 根据MappedStatement id来生成sql
   * 
   * @param msId
   * @param mapperClass
   * @param config
   * @return
   * @throws ClassNotFoundException
   * @throws NoSuchFieldException
   */
  public static String getSqlByMs(MappedStatement ms)
      throws ClassNotFoundException, NoSuchFieldException {
    String msId = ms.getId();
    Class<?> entityClass = MsIdUtil.getEntityClass(msId);
    if (entityClass != null) {
      String methodName = MsIdUtil.getMethodName(msId);
      PartTree tree = PartTreeFactory.create(msId, methodName);
      String xmlSql = null;

      checkProperty(msId, entityClass, tree);
      if (tree.isCountProjection()) {
        xmlSql = TkMapperUtil.selectCountByExample(entityClass);
      } else if (tree.isDelete()) {
        xmlSql = TkMapperUtil.deleteByExample(ms, entityClass);
      } else {
        xmlSql = TkMapperUtil.selectByExample(ms, entityClass, tree);
      }
      return "<script>\n\t" + xmlSql + "</script>";
    }
    return null;
  }

  /**
   * 检查property是否在entityClass中
   * 
   * @param entityClass
   * @param tree
   * @throws NoSuchFieldException 当property不存在entityClass中时
   */
  public static void checkProperty(String msId, Class<?> entityClass, PartTree tree)
      throws NoSuchFieldException {
    Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
    Set<String> propertys =
        columnSet.stream().map(EntityColumn::getProperty).collect(Collectors.toSet());

    String name = entityClass.getName();
    for (OrPart node : tree) {
      for (Part part : node) {
        PropertyPath property = part.getProperty();
        String segment = property.getSegment();
        if (!propertys.contains(segment)) {
          throw new NoSuchFieldException(String.format("%s -> %s", segment, msId));
        }
      }
    }
    Sort sort = tree.getSort();
    if (sort != null) {
      for (Sort.Order order : sort) {
        String property = order.getProperty();
        if (!propertys.contains(property)) {
          throw new NoSuchFieldException(String.format("%s -> %s", property, msId));
        }
      }
    }
  }
}
