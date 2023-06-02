package joinquery.processer;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class TkMapperQueryEntityProcesser extends AbstractQueryEntityProcesser {

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        super.filer = processingEnvironment.getFiler();
    }
    
    @Override
	public String getTableName(Element t) {
    	Map<String, AnnotationValue> defaultValues = getAnnotationDefaultValues(t, "javax.persistence.Table");
        Object tabnename = defaultValues.get("name");
        if (tabnename != null && !"\"\"".equals(tabnename.toString()) && tabnename.toString().trim().length() > 0) {
        	return tabnename.toString().trim().replace("\"", "");
        }
		return null;
	}

	@Override
	public String getColumnName(Element t) {
		Map<String, AnnotationValue> defaultValues = getAnnotationDefaultValues(t, "javax.persistence.Column");
		Object fieldname = defaultValues.get("name");
		if (fieldname != null && !"\"\"".equals(fieldname.toString()) && fieldname.toString().trim().length() > 0) {
        	return fieldname.toString().trim().replace("\"", "");
        }
		return null;
	}

	@Override
	public Boolean isIgnoreColumn(Element t) {
		AnnotationMirror mirror = getAnnotationMirror(t, "javax.persistence.Transient");
		return mirror != null;
	}


	@Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		if (!roundEnv.processingOver()) {
    		for ( TypeElement annotation : annotations ) {
                //Indicates that the annotation's type isn't on the class path of the compiled
                //project. Let the compiler deal with that and print an appropriate error.
                if ( annotation.getKind() != ElementKind.ANNOTATION_TYPE ) {
                    continue;
                }

                try {
                	Set<? extends Element> annotatedMappers = roundEnv.getElementsAnnotatedWith( annotation );
                	super.process(annotatedMappers);
                } catch ( Throwable t ) { // whenever that may happen, but just to stay on the save side
                	handleUncaughtError( annotation, t );
                	continue;
                }
            }
        }

        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add("javax.persistence.Table");
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
