package querymethods.mybatisplus;

import org.apache.ibatis.mapping.MappedStatement;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;

import querymethods.springdata.query.parser.PartTree;

public class MybatisPlusUtil {
  public static void getTableInfo(Class<?> clazz) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
    System.out.println(tableInfo.getKeyColumn());
  }

  public static String selectCountByExample(Class<?> entityClass) {
    // TODO
    StringBuilder sql = new StringBuilder();
    return sql.toString();
  }

  /**
   * 根据Example查询
   *
   * @param ms
   * @return
   */
  public static String selectByExample(MappedStatement ms, Class<?> entityClass, PartTree tree) {
    // TODO
    if (isEmpty(tree.getQueryProperty())) {
      // 将返回值修改为实体类型
      // tk.mybatis.mapper.provider.ExampleProvider#selectByExample
    }

    StringBuilder sql = new StringBuilder("SELECT ");
    return sql.toString();
  }

  /**
   * 根据Example删除
   *
   * @param ms
   * @return
   */
  public static String deleteByExample(MappedStatement ms, Class<?> entityClass) {
    // TODO
    StringBuilder sql = new StringBuilder();
    return sql.toString();
  }

  /**
   * 空
   *
   * @param str
   * @return
   */
  public static boolean isEmpty(String str) {
    return str == null || str.length() == 0;
  }
}
