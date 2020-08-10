package querymethods.tkmapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;

import querymethods.spring.data.mapping.PropertyPath;
import querymethods.spring.data.query.domain.Sort;
import querymethods.spring.data.query.parser.Part;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.spring.data.query.parser.PartTree.OrPart;
import querymethods.util.MetaObjectUtil;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;

/**
 * TkMapper工具类
 * 
 * @see tk.mybatis.mapper.provider.ExampleProvider
 * 
 * @author OYGD
 *
 */
public class TkMapperUtil {

  private TkMapperUtil() {}

  public static String selectCountByExample(Class<?> entityClass) {
    StringBuilder sql = new StringBuilder();
    sql.append(SqlHelper.selectCount(entityClass));
    sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
    sql.append(SqlHelper.exampleWhereClause());
    sql.append(SqlHelper.exampleForUpdate());
    return sql.toString();
  }

  /**
   * 根据Example查询
   *
   * @param ms
   * @return
   */
  public static String selectByExample(MappedStatement ms, Class<?> entityClass, PartTree tree) {
    if (StringUtil.isEmpty(tree.getQueryProperty())) {
      // 将返回值修改为实体类型
      // tk.mybatis.mapper.provider.ExampleProvider#selectByExample
      TkMapperUtil.setResultType(ms, entityClass);
    }

    StringBuilder sql = new StringBuilder("SELECT ");
    sql.append("<if test=\"distinct\">distinct</if>");
    // 支持查询指定列
    sql.append(SqlHelper.exampleSelectColumns(entityClass));
    sql.append(SqlHelper.fromTable(entityClass, tableName(entityClass)));
    sql.append(SqlHelper.exampleWhereClause());
    sql.append(SqlHelper.exampleOrderBy(entityClass));
    sql.append(SqlHelper.exampleForUpdate());
    return sql.toString();
  }

  /**
   * 根据Example删除
   *
   * @param ms
   * @return
   */
  public static String deleteByExample(MappedStatement ms, Class<?> entityClass) {
    StringBuilder sql = new StringBuilder();
    if (SqlHelper.hasLogicDeleteColumn(entityClass)) {
      sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
      sql.append("<set>");
      sql.append(SqlHelper.logicDeleteColumnEqualsValue(entityClass, true));
      sql.append("</set>");
      MetaObjectUtil.forObject(ms).setValue("sqlCommandType", SqlCommandType.UPDATE);
    } else {
      sql.append(SqlHelper.deleteFromTable(entityClass, tableName(entityClass)));
    }
    sql.append(SqlHelper.exampleWhereClause());
    return sql.toString();
  }

  /**
   * 获取实体类的表名
   *
   * @param entityClass
   * @return
   */
  public static String tableName(Class<?> entityClass) {
    EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
    String prefix = entityTable.getPrefix();
    if (StringUtil.isNotEmpty(prefix)) {
      return prefix + "." + entityTable.getName();
    }
    return entityTable.getName();
  }

  /**
   * 设置返回值类型 - 为了让typeHandler在select时有效，改为设置resultMap
   *
   * @param ms
   * @param entityClass
   * @see MapperTemplate#setResultType
   */
  public static void setResultType(MappedStatement ms, Class<?> entityClass) {
    EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
    List<ResultMap> resultMaps = new ArrayList<ResultMap>();
    resultMaps.add(entityTable.getResultMap(ms.getConfiguration()));
    MetaObject metaObject = MetaObjectUtil.forObject(ms);
    metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
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
