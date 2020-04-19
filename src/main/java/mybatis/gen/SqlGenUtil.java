package mybatis.gen;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import mybatis.query.PartTree;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.EntityTable;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;


public class SqlGenUtil {

	static Config config = new Config();
	
	public static String[] gen(Method method, Class<?> type) {
		String sqlXml = gen(method.getName(), type, config);
		if(null == sqlXml) {
			return null;
		}
		return new String[] { sqlXml };
	}
	
	public static String gen(String methodName, Class<?> type, Config config) {
		Class<?> entityClass = getEntityClass(type);
		
		if(entityClass != null) {
			PartTree tree = new PartTree(methodName);
			EntityHelper.initEntityNameMap(entityClass, config);
			String xmlSql = null;
			if(tree.isCountProjection()) {
				xmlSql = selectCountByExample(entityClass);
			}
			xmlSql = selectByExample(entityClass);
			return "<script>\n\t" + xmlSql + "</script>";
		}
		return null;
	}

	public static Class<?> getEntityClass(Class<?> type) {
		Class<?> entityClass = null;
		Type[] genericInterfaces = type.getGenericInterfaces();
		if (genericInterfaces != null && genericInterfaces.length > 0) {
			for (Type type1 : genericInterfaces) {
				ParameterizedType t = ((ParameterizedType) type1);
				if(t.getRawType() == Mapper.class) {
					try {
						entityClass = Class.forName(t.getActualTypeArguments()[0].getTypeName());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					break;
				}
			}
		}
		return entityClass;
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
        //支持查询指定列
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
    static String tableName(Class<?> entityClass) {
        EntityTable entityTable = EntityHelper.getEntityTable(entityClass);
        String prefix = entityTable.getPrefix();
        if (StringUtil.isNotEmpty(prefix)) {
            return prefix + "." + entityTable.getName();
        }
        return entityTable.getName();
    }
}
