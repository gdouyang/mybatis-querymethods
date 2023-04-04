package joinquery.processer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.Consumer;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;


public abstract class AbstractQueryEntityProcesser extends AbstractProcessor {
  
  public AnnotationMirror getAnnotationMirror(Element t, String annotationClassName) {
    AnnotationMirror mirror = t.getAnnotationMirrors().stream()
        .filter(a -> annotationClassName
            .contentEquals(((TypeElement) a.getAnnotationType().asElement()).getQualifiedName()))
        .findAny().orElse(null);
    return mirror;
  }

  public Map<String, AnnotationValue> getAnnotationDefaultValues(Element t,
      String annotationClassName) {
    AnnotationMirror mirror = getAnnotationMirror(t, annotationClassName);
    if (mirror == null) {
      return new HashMap<>(0);
    }
    List<ExecutableElement> enclosed =
        ElementFilter.methodsIn(mirror.getAnnotationType().asElement().getEnclosedElements());
    // fetch all explicitely set annotation values in the annotation instance
    Map<String, AnnotationValue> values = new HashMap<>(enclosed.size());
    mirror.getElementValues().entrySet()
        .forEach(e -> values.put(e.getKey().getSimpleName().toString(), e.getValue()));
    return values;
  }

  private static final String classTableTemplate = "package @package;\n" //
      + "\n" //
      + "import joinquery.QueryColumn;\n" //
      + "import joinquery.TableDef;\n" //
      + "\n" //
      + "// Auto generate by mybatis-joinquery, do not modify it.\n" //
      + "public class Tables {\n" //
      + "@classesInfo"//
      + "}\n";

  private static final String tableDefTemplate =
      "\n\n    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";


  private static final String classTemplate = "\n" //
      + "    public static class @entityClassTableDef extends TableDef {\n" //
      + "\n"//
      + "@queryColumns" //
      + "@defaultColumns" //
      + "@allColumns" //
      + "\n"//
      + "        public @entityClassTableDef(String tableName) {\n"//
      + "            super(tableName);\n" + "        }\n" + "    }\n";


  private static final String columnsTemplate =
      "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";

  private static final String defaultColumnsTemplate =
      "\n        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{@allColumns};\n";
  private static final String allColumnsTemplate =
      "        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};\n\n";

  protected Filer filer;

  public abstract String getTableName(Element t);

  public abstract String getColumnName(Element t);

  public abstract Boolean isIgnoreColumn(Element t);

  private Options options;
  
  @Override
  public synchronized void init(ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);
    this.filer = processingEnvironment.getFiler();
    this.options = new Options();
    this.initOptions();
  }
  
  private void initOptions() {
    String enable = processingEnv.getOptions().getOrDefault("joinqueryprocess.enable", "true");
    this.options.setEnable(Boolean.TRUE.equals(Boolean.valueOf(enable)));
    String genPath = processingEnv.getOptions().get("joinqueryprocess.genPath");
    this.options.setGenPath(genPath);
    String tablesPackage = processingEnv.getOptions().get("joinqueryprocess.tablesPackage");
    this.options.setTablesPackage(tablesPackage);
    String tablesClassName = processingEnv.getOptions().getOrDefault("joinqueryprocess.tablesClassName", "Tables");
    this.options.setTablesClassName(tablesClassName);
  }

  public boolean process(Set<? extends Element> elementsAnnotatedWith) {


    if (Boolean.FALSE.equals(this.options.getEnable())) {
      return true;
    }
    String genPath = this.options.getGenPath();
    String genTablesPackage = this.options.getTablesPackage();
    String className = this.options.getTablesClassName();

    StringBuilder guessPackage = new StringBuilder();

    StringBuilder tablesContent = new StringBuilder();
    elementsAnnotatedWith.forEach((Consumer<Element>) entityClassElement -> {

      if (entityClassElement.getAnnotation(Ignore.class) != null) {
        return;
      }

      String tableName = firstCharToLowerCase(entityClassElement.getSimpleName().toString());
      String n = getTableName(entityClassElement);
      if (n != null && !n.isEmpty()) {
        tableName = n;
      }
      // init genPackage
      if ((genTablesPackage == null || genTablesPackage.trim().length() == 0)
          && guessPackage.length() == 0) {
        String entityClassName = entityClassElement.toString();
        if (!entityClassName.contains(".")) {
          guessPackage.append("table");// = "table";
        } else {
          guessPackage
              .append(entityClassName.substring(0, entityClassName.lastIndexOf(".")) + ".table");
        }
      }

      Map<String, String> propertyAndColumns = new LinkedHashMap<>();
      List<String> defaultColumns = new ArrayList<>();

      TypeElement classElement = (TypeElement) entityClassElement;
      for (Element fieldElement : classElement.getEnclosedElements()) {

        if (fieldElement.getModifiers().contains(Modifier.STATIC)) {
          continue;
        }
        // all fields
        if (ElementKind.FIELD == fieldElement.getKind()) {

          if (Boolean.TRUE.equals(isIgnoreColumn(fieldElement))) {
            continue;
          }

          String columnName = camelToUnderline(fieldElement.toString());
          String n1 = getColumnName(fieldElement);
          if (n1 != null && !n1.isEmpty()) {
            columnName = n1;
          }

          propertyAndColumns.put(fieldElement.toString(), columnName);
          defaultColumns.add(fieldElement.toString());
        }
      }

      String entityClassName = entityClassElement.getSimpleName().toString();
      tablesContent
          .append(buildTablesClass(entityClassName, tableName, propertyAndColumns, defaultColumns));

    });

    if (tablesContent.length() > 0) {
      String realGenPackage = genTablesPackage == null || genTablesPackage.trim().length() == 0
          ? guessPackage.toString()
          : genTablesPackage.trim();
      genTablesClass(genPath, realGenPackage, className, tablesContent.toString());
    }

    return false;
  }


  private String buildTablesClass(String entityClass, String tableName,
      Map<String, String> propertyAndColumns, List<String> defaultColumns) {

    // tableDefTemplate = " public static final @entityClassTableDef @tableField = new
    // @entityClassTableDef(\"@tableName\");\n";

    String tableDef = tableDefTemplate.replace("@entityClass", entityClass)
        .replace("@tableField", entityClass.toUpperCase()).replace("@tableName", tableName);


    // columnsTemplate = " public QueryColumn @property = new QueryColumn(this,
    // \"@columnName\");\n";
    StringBuilder queryColumns = new StringBuilder();
    propertyAndColumns.forEach((property, column) -> queryColumns
        .append(columnsTemplate.replace("@property", camelToUnderline(property).toUpperCase())
            .replace("@columnName", column)));


    // public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};
    StringJoiner allColumns = new StringJoiner(", ");
    propertyAndColumns
        .forEach((property, column) -> allColumns.add(camelToUnderline(property).toUpperCase()));
    String allColumnsString = allColumnsTemplate.replace("@allColumns", allColumns.toString());


    StringJoiner defaultColumnStringJoiner = new StringJoiner(", ");
    defaultColumns.forEach(s -> defaultColumnStringJoiner.add(camelToUnderline(s).toUpperCase()));
    String defaultColumnsString =
        defaultColumnsTemplate.replace("@allColumns", defaultColumnStringJoiner.toString());


    String tableClass = classTemplate.replace("@entityClass", entityClass)
        .replace("@queryColumns", queryColumns).replace("@defaultColumns", defaultColumnsString)
        .replace("@allColumns", allColumnsString);

    return tableDef + tableClass;
  }


  private void genTablesClass(String genBasePath, String genPackageName, String className,
      String classContent) {
    String genContent = classTableTemplate.replace("@package", genPackageName)
        .replace("@classesInfo", classContent);

    Writer writer = null;
    try {
      JavaFileObject sourceFile = filer.createSourceFile(genPackageName + "." + className);
      if (genBasePath == null || genBasePath.trim().length() == 0) {
        writer = sourceFile.openWriter();
        writer.write(genContent);
        writer.flush();

        printMessage(">>>>> joinquery success generate tables class: \n" + sourceFile.toUri());
        return;
      }


      String defaultGenPath = sourceFile.toUri().getPath();

      // 真实的生成代码的目录
      String realPath;

      // 用户配置的路径为绝对路径
      if (isAbsolutePath(genBasePath)) {
        realPath = genBasePath;
      }
      // 配置的是相对路径，那么则以项目根目录为相对路径
      else {
        String projectRootPath = getProjectRootPath(defaultGenPath);
        realPath = new File(projectRootPath, genBasePath).getAbsolutePath();
      }

      // 通过在 test/java 目录下执行编译生成的
      boolean fromTestSource = isFromTestSource(defaultGenPath);
      if (fromTestSource) {
        realPath = new File(realPath, "src/test/java").getAbsolutePath();
      } else {
        realPath = new File(realPath, "src/main/java").getAbsolutePath();
      }

      File genJavaFile =
          new File(realPath, (genPackageName + "." + className).replace(".", "/") + ".java");
      if (!genJavaFile.getParentFile().exists() && !genJavaFile.getParentFile().mkdirs()) {
        System.out.println(">>>>>ERROR: can not mkdirs by mybatis-joinquery processer for: "
            + genJavaFile.getParentFile());
        return;
      }

      writer = new PrintWriter(new FileOutputStream(genJavaFile));
      writer.write(genContent);
      writer.flush();

      printMessage(
          ">>>>> mybatis-joinquery success generate tables class: \n" + genJavaFile.toURI());

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (IOException e) {
        }
      }
    }
  }

  private void printMessage(String message) {
    processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
    System.out.println(message);
  }

  public static String firstCharToLowerCase(String str) {
    char firstChar = str.charAt(0);
    if (firstChar >= 'A' && firstChar <= 'Z') {
      char[] arr = str.toCharArray();
      arr[0] += ('a' - 'A');
      return new String(arr);
    }
    return str;
  }


  private boolean isFromTestSource(String path) {
    return path.contains("test-sources") || path.contains("test-annotations");
  }


  public static boolean isAbsolutePath(String path) {
    return path != null && (path.startsWith("/") || path.indexOf(":") > 0);
  }

  /**
   * 驼峰转下划线
   * 
   * @param string
   * @return
   */
  public static String camelToUnderline(String string) {
    if (string == null || string.trim().length() == 0) {
      return "";
    }
    int len = string.length();
    StringBuilder sb = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char c = string.charAt(i);
      if (Character.isUpperCase(c) && i > 0) {
        sb.append('_');
      }
      sb.append(Character.toLowerCase(c));
    }
    return sb.toString();
  }


  /**
   * 获取项目的根目录，也就是根节点 pom.xml 所在的目录
   *
   * @return
   */
  private String getProjectRootPath(String genFilePath) {
    File file = new File(genFilePath);
    int count = 20;
    return getProjectRootPath(file, count);
  }


  private String getProjectRootPath(File file, int count) {
    if (count <= 0) {
      return null;
    }
    if (file.isFile()) {
      return getProjectRootPath(file.getParentFile(), --count);
    } else {
      if (new File(file, "pom.xml").exists()
          && !new File(file.getParentFile(), "pom.xml").exists()) {
        return file.getAbsolutePath();
      } else {
        return getProjectRootPath(file.getParentFile(), --count);
      }
    }
  }

  protected void handleUncaughtError(Element element, Throwable thrown) {
    StringWriter sw = new StringWriter();
    thrown.printStackTrace(new PrintWriter(sw));

    String reportableStacktrace = sw.toString().replace(System.lineSeparator(), "  ");

    processingEnv.getMessager().printMessage(Kind.ERROR,
        "Internal error in the mapping processor: " + reportableStacktrace, element);
  }

}
