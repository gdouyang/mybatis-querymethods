package querymethods;


import querymethods.query.Part;
import querymethods.query.PartTree;
import querymethods.query.PartTree.OrPart;
import tk.mybatis.mapper.entity.Config;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.mapperhelper.EntityHelper;

/**
 * PartTreeTest
 * @author OYGD
 *
 */
public class PartTreeTest {
	
	public static void main(String[] agrs) throws NoSuchMethodException, SecurityException {
		
		Class<?> entityClass = Blog.class;
		PartTree tree = new PartTree("findNameByIdOrFirstNameLikeOrderById");
		Config config = new Config();
		EntityHelper.initEntityNameMap(Blog.class, config);
		Example example = new Example(Blog.class);
		Example.Criteria criteria = example.createCriteria();
		for (OrPart node : tree) {

			for (Part part : node) {
//				new WhereBuilder(part, criteria).build();
			}
		}
		
		System.out.println(tree);
		System.out.println(tree.getSort());
	}
	
}
