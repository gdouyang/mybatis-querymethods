package querymethods.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.reflection.MetaObject;

import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.MetaObjectUtil;
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
	public static String selectByExample(MappedStatement ms, Class<?> entityClass) {
		// 将返回值修改为实体类型
		// tk.mybatis.mapper.provider.ExampleProvider#selectByExample
		TkMapperUtil.setResultType(ms, entityClass);
		
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
}
