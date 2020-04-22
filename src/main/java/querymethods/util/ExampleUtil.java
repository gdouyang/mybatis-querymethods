package querymethods.util;

import java.util.Queue;

import querymethods.query.Part;
import querymethods.query.PartTree;
import querymethods.query.PartTree.OrPart;
import querymethods.query.PartTreeFactory;
import tk.mybatis.mapper.entity.Example;

/**
 * Example工具类
 * 
 * @author OYGD
 *
 */
public class ExampleUtil {

	private ExampleUtil() {
	}
	
	/**
	 * 根据MappedStatement id 与参数生成Example
	 * @param msId
	 * @param params
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Example getExampleByMsId(String msId, Queue<Object> params) throws ClassNotFoundException {
		
		Class<?> entityClass = MsIdUtil.getEntityClass(msId);

		String methodName = MsIdUtil.getMethodName(msId);

		PartTree tree = PartTreeFactory.create(msId, methodName);

		Example example = new Example(entityClass);
		Example.Criteria criteria = example.createCriteria();
		for (OrPart node : tree) {
			for (Part part : node) {
				WhereBuilder.build(part, criteria, params);
			}
			criteria = example.createCriteria();
			example.or(criteria);
		}
		return example;
	}

}
