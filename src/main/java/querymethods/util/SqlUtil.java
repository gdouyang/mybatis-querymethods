package querymethods.util;

import org.apache.ibatis.mapping.MappedStatement;

import querymethods.springdata.PartTreeFactory;
import querymethods.springdata.query.parser.PartTree;

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
	public static String getSqlByMs(MappedStatement ms) throws ClassNotFoundException {
		String msId = ms.getId();
		Class<?> entityClass = MsIdUtil.getEntityClass(msId);
		if (entityClass != null) {
			String methodName = MsIdUtil.getMethodName(msId);
			PartTree tree = PartTreeFactory.create(msId, methodName);
			String xmlSql = null;
			
			if (tree.isCountProjection()) {
				xmlSql = TkMapperUtil.selectCountByExample(entityClass);
			}else {
				xmlSql = TkMapperUtil.selectByExample(ms, entityClass);
			}
			return "<script>\n\t" + xmlSql + "</script>";
		}
		return null;
	}
}
