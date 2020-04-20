package querymethods.query;

import java.util.HashMap;
import java.util.Map;

public class PartTreeFactory {

	private PartTreeFactory() {
	}
	
	static Map<String, PartTree> cache = new HashMap<>();
	
	public static PartTree create(String msId, String methodName) {
		return cache.getOrDefault(msId, new PartTree(methodName));
	}

}
