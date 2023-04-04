package joinquery.processer;


import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class MybatisPlusQueryEntityProcesser extends AbstractQueryEntityProcesser {

  @Override
  public String getTableName(Element t) {
    Map<String, AnnotationValue> defaultValues =
        getAnnotationDefaultValues(t, "com.baomidou.mybatisplus.annotation.TableName");
    Object tabnename = defaultValues.get("value");
    if (tabnename != null && !"\"\"".equals(tabnename.toString())
        && tabnename.toString().trim().length() > 0) {
      return tabnename.toString().trim().replace("\"", "");
    }
    return null;
  }

  @Override
  public String getColumnName(Element t) {
    Map<String, AnnotationValue> defaultValues =
        getAnnotationDefaultValues(t, "com.baomidou.mybatisplus.annotation.TableField");
    Object fieldname = defaultValues.get("value");
    if (fieldname != null && !"\"\"".equals(fieldname.toString())
        && fieldname.toString().trim().length() > 0) {
      return fieldname.toString().trim().replace("\"", "");
    }
    return null;
  }

  @Override
  public Boolean isIgnoreColumn(Element t) {
    Map<String, AnnotationValue> defaultValues =
        getAnnotationDefaultValues(t, "com.baomidou.mybatisplus.annotation.TableField");
    AnnotationValue exist = defaultValues.get("exist");
    if (exist != null) {
      return Boolean.FALSE.equals(exist.getValue());
    }
    return false;
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    if (!roundEnv.processingOver()) {
      for (TypeElement annotation : annotations) {
        // Indicates that the annotation's type isn't on the class path of the compiled
        // project. Let the compiler deal with that and print an appropriate error.
        if (annotation.getKind() != ElementKind.ANNOTATION_TYPE) {
          continue;
        }

        try {
          Set<? extends Element> annotatedMappers = roundEnv.getElementsAnnotatedWith(annotation);
          super.process(annotatedMappers);
        } catch (Throwable t) { // whenever that may happen, but just to stay on the save side
          handleUncaughtError(annotation, t);
          continue;
        }
      }
    }

    return false;
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    Set<String> supportedAnnotationTypes = new HashSet<>();
    supportedAnnotationTypes.add("com.baomidou.mybatisplus.annotation.TableName");
    return supportedAnnotationTypes;
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

}
