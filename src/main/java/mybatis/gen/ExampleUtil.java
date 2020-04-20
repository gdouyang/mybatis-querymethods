package mybatis.gen;

import java.util.Queue;

import mybatis.query.Part;
import mybatis.query.PartTree;
import mybatis.query.PartTree.OrPart;
import tk.mybatis.mapper.entity.Example;

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
		int lastIndexOf = msId.lastIndexOf(".");
		String mapperName = msId.substring(0, lastIndexOf);

		Class<?> clazz = Class.forName(mapperName);
		Class<?> entityClass = SqlUtil.getEntityClass(clazz);

		String methodName = msId.substring(lastIndexOf + 1);

		PartTree tree = new PartTree(methodName);

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
