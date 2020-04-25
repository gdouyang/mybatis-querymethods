package querymethods;


import querymethods.springdata.query.parser.PartTree;

/**
 * PartTreeTest
 * @author OYGD
 *
 */
public class PartTreeTest {
	
	public static void main(String[] agrs) throws NoSuchMethodException, SecurityException {
		
		PartTree tree = new PartTree("findNameByIdOrFirstNameLikeOrderById");
		
		System.out.println(tree);
		
		tree = new PartTree("findByFirstNameOrderByIdAscFristNameDesc");
		
		System.out.println(tree);
		
		tree = new PartTree("findAllOrderByIdAsc");
		
		System.out.println(tree);
		
		tree = new PartTree("find");
		
		System.out.println(tree);
		
		tree = new PartTree("findByFirstNameOrderByIdAsc");
		
		System.out.println(tree);
	}
	
}
