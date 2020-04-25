package querymethods.springdata;

import java.util.HashMap;
import java.util.Map;

import querymethods.springdata.query.parser.PartTree;

/**
 * PartTree工厂类，创建PartTree并缓存
 * @author OYGD
 *
 */
public class PartTreeFactory {

	private PartTreeFactory() {
	}
	
	static Map<String, PartTree> cache = new HashMap<>();
	
	/**
	 * 创建PartTree并缓存起来
	 * @param msId
	 * @param methodName
	 * @return
	 */
	public static PartTree create(String msId, String methodName) {
		if(cache.containsKey(msId)) {
			return cache.get(msId);
		}
		PartTree partTree = new PartTree(methodName);
		cache.put(msId, partTree);
		
		return partTree;
	}

}
