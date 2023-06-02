package querymethods.util;

import org.apache.ibatis.mapping.MappedStatement;
import querymethods.QueryMethodsConfig;
import querymethods.mybatisplus.MybatisPlusUtil;
import querymethods.spring.data.PartTreeFactory;
import querymethods.spring.data.query.parser.PartTree;
import querymethods.tkmapper.TkMapperUtil;

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
      if (QueryMethodsConfig.isTkMapper()) {
        xmlSql = tkMapper(ms, msId, entityClass, tree);
      } else if (QueryMethodsConfig.isMybatisPlus()) {
        xmlSql = mybatisPlus(ms, msId, entityClass, tree);
      }
      return xmlSql;
    }
    return null;
  }

  private static String tkMapper(MappedStatement ms, String msId, Class<?> entityClass,
      PartTree tree) throws NoSuchFieldException {
    String xmlSql;
    TkMapperUtil.checkProperty(msId, entityClass, tree);
    if (tree.isCountProjection()) {
      xmlSql = TkMapperUtil.selectCountByExample(entityClass);
    } else if (tree.isDelete()) {
      xmlSql = TkMapperUtil.deleteByExample(ms, entityClass);
    } else {
      xmlSql = TkMapperUtil.selectByExample(ms, entityClass, tree);
    }
    return "<script>\n\t" + xmlSql + "</script>";
  }

  private static String mybatisPlus(MappedStatement ms, String msId, Class<?> entityClass,
      PartTree tree) throws NoSuchFieldException {
    String xmlSql;
    MybatisPlusUtil.checkProperty(msId, entityClass, tree);
    if (tree.isCountProjection()) {
      xmlSql = MybatisPlusUtil.selectCountByExample(entityClass);
    } else if (tree.isDelete()) {
      xmlSql = MybatisPlusUtil.deleteByExample(ms, entityClass);
    } else {
      xmlSql = MybatisPlusUtil.selectByExample(ms, entityClass, tree);
    }
    return xmlSql;
  }
}
