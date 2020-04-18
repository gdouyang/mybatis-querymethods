package mybatis.override;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.springframework.util.ReflectionUtils;

public class MyXMLConfigBuilder extends XMLConfigBuilder {

	public MyXMLConfigBuilder(InputStream inputStream, String environment, Properties props) {
		super(inputStream, environment, props);
		
		Field mapperRegistry = ReflectionUtils.findField(this.configuration.getClass(), "mapperRegistry");
		if(mapperRegistry != null) {
			mapperRegistry.setAccessible(true);
			ReflectionUtils.setField(mapperRegistry, this.configuration, new MyMapperRegistry(this.configuration));
			mapperRegistry.setAccessible(false);
		}
	}
	
}
