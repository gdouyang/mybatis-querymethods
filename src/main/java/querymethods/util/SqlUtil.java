package querymethods.util;

import querymethods.query.PartTree;
import querymethods.query.PartTreeFactory;
import tk.mybatis.mapper.entity.Config;

/**
 * sql重写工具类
 * @author OYGD
 *
 */
public class SqlUtil {
	
	/**
	 * 根据MappedStatement id来生成sql
	 * @param msId
	 * @param mapperClass
	 * @param config
	 * @return
	 * @throws ClassNotFoundException 
	 */
	public static String getSqlByMsId(String msId, Config config) throws ClassNotFoundException {
		
		Class<?> entityClass = MsIdUtil.getEntityClass(msId);
		if (entityClass != null) {
			String methodName = MsIdUtil.getMethodName(msId);
			PartTree tree = PartTreeFactory.create(msId, methodName);
			String xmlSql = null;
			if (tree.isCountProjection()) {
				xmlSql = TkMapperUtil.selectCountByExample(entityClass);
			}else {
				xmlSql = TkMapperUtil.selectByExample(entityClass);
			}
			return "<script>\n\t" + xmlSql + "</script>";
		}
		return null;
	}
}