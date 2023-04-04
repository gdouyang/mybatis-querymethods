package mybatis.join.processer;

import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;

public interface QueryEntityProcesser {
	Set<? extends Element> getEntityClassElement(RoundEnvironment roundEnv);
	
}
