package querymethods.mybatisplus;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.ibatis.mapping.MappedStatement;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import querymethods.spring.data.mapping.PropertyPath;
import querymethods.spring.data.query.domain.Sort;
import querymethods.spring.data.query.parser.Part;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.spring.data.query.parser.PartTree.OrPart;

public class MybatisPlusUtil {

  public static Map<Class<?>, MPTableInfo> map = new ConcurrentHashMap<>();

  public static MPTableInfo getMPTableInfo(Class<?> entityClass) {
    return map.get(entityClass);
  }

  public static TableInfo getTableInfo(Class<?> entityClass) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(entityClass);
    if (null != tableInfo && !map.containsKey(entityClass)) {
      map.put(entityClass, new MPTableInfo(tableInfo));
    }
    return tableInfo;
  }

  private static AbstractMethodImpl u = new AbstractMethodImpl();

  public static String selectCountByExample(Class<?> entityClass) {
    TableInfo tableInfo = getTableInfo(entityClass);
    SqlMethod sqlMethod = SqlMethod.SELECT_COUNT;
    String sql = String.format(sqlMethod.getSql(), u.sqlFirst(), u.sqlCount(),
        tableInfo.getTableName(), u.sqlWhereEntityWrapper(true, tableInfo), u.sqlComment());
    return sql.toString();
  }


  /**
   * 根据Example查询
   *
   * @param ms
   * @return
   */
  public static String selectByExample(MappedStatement ms, Class<?> entityClass, PartTree tree) {
    TableInfo tableInfo = getTableInfo(entityClass);
    if (StringUtils.isBlank(tree.getQueryProperty())) {
      // 将返回值修改为实体类型
      ResultMapUtil.setResultType(ms, tableInfo);
    }

    SqlMethod sqlMethod = SqlMethod.SELECT_MAPS;
    String sql =
        String.format(sqlMethod.getSql(), u.sqlFirst(), u.sqlSelectColumns(tableInfo, true),
            tableInfo.getTableName(), u.sqlWhereEntityWrapper(true, tableInfo), u.sqlComment());

    return sql.toString();
  }

  /**
   * 根据Example删除
   *
   * @param ms
   * @return
   */
  public static String deleteByExample(MappedStatement ms, Class<?> entityClass) {
    TableInfo tableInfo = getTableInfo(entityClass);
    String sql;
    SqlMethod sqlMethod = SqlMethod.LOGIC_DELETE;
    if (tableInfo.isLogicDelete()) {
      sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), u.sqlLogicSet(tableInfo),
          u.sqlWhereEntityWrapper(true, tableInfo), u.sqlComment());
    } else {
      sqlMethod = SqlMethod.DELETE;
      sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(),
          u.sqlWhereEntityWrapper(true, tableInfo), u.sqlComment());
    }
    return sql;
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
    TableInfo tableInfo = getTableInfo(entityClass);
    List<TableFieldInfo> fieldList = tableInfo.getFieldList();
    Set<String> propertys =
        fieldList.stream().map(TableFieldInfo::getProperty).collect(Collectors.toSet());
    propertys.add(tableInfo.getKeyProperty());

    for (OrPart node : tree) {
      // 检查查询字段是否为null
      if (StringUtils.isNotBlank(tree.getQueryProperty()) && !propertys.contains(tree.getQueryProperty())) {
        throw new NoSuchFieldException(String.format("%s -> %s", tree.getQueryProperty(), msId));
      }
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
