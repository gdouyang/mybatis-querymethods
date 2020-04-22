package querymethods.util;

import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;

/**
 * TkMapper工具类
 * @author OYGD
 *
 */
public class TkMapperUtil {

	private TkMapperUtil() {
	}

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
	public static String selectByExample(Class<?> entityClass) {
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
}
