package querymethods.spring.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import querymethods.spring.data.query.parser.PartTree;

/**
 * PartTree工厂类，创建PartTree并缓存
 * @author OYGD
 *
 */
public class PartTreeFactory {

	private PartTreeFactory() {
	}
	
	static Map<String, PartTree> cache = new ConcurrentHashMap<>();
	
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
		PartTree partTree = null;
		synchronized (PartTreeFactory.class) {
			partTree = new PartTree(methodName);
			cache.put(msId, partTree);
		}
		
		return partTree;
	}

}
