/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mybatis.join.processer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.Table;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import org.apache.ibatis.type.UnknownTypeHandler;

public class QueryEntityProcesser extends AbstractProcessor {

    private static final List<String> defaultSupportColumnTypes = Arrays.asList(
            int.class.getName(), Integer.class.getName(),
            short.class.getName(), Short.class.getName(),
            long.class.getName(), Long.class.getName(),
            float.class.getName(), Float.class.getName(),
            double.class.getName(), Double.class.getName(),
            boolean.class.getName(), Boolean.class.getName(),
            Date.class.getName(), java.sql.Date.class.getName(), Time.class.getName(), Timestamp.class.getName(),
            Instant.class.getName(), LocalDate.class.getName(), LocalDateTime.class.getName(), LocalTime.class.getName(),
            OffsetDateTime.class.getName(), OffsetTime.class.getName(), ZonedDateTime.class.getName(),
            Year.class.getName(), Month.class.getName(), YearMonth.class.getName(), JapaneseDate.class.getName(),
            byte[].class.getName(), Byte[].class.getName(),
            BigInteger.class.getName(), BigDecimal.class.getName(),
            char.class.getName(), String.class.getName(), Character.class.getName()
    );


    private static final String classTableTemplate = "package @package;\n" +
            "\n" +
            "import mybatis.join.QueryColumn;\n" +
            "import mybatis.join.TableDef;\n" +
            "\n" +
            "// Auto generate by mybatis-flex, do not modify it.\n" +
            "public class Tables {\n" +
            "@classesInfo" +
            "}\n";

    private static final String tableDefTemplate = "\n\n    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";


    private static final String classTemplate = "\n" +
            "    public static class @entityClassTableDef extends TableDef {\n" +
            "\n" +
            "@queryColumns" +
            "@defaultColumns" +
            "@allColumns" +
            "\n" +
            "        public @entityClassTableDef(String tableName) {\n" +
            "            super(tableName);\n" +
            "        }\n" +
            "    }\n";


    private static final String columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";

    private static final String defaultColumnsTemplate = "\n        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{@allColumns};\n";
    private static final String allColumnsTemplate = "        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};\n\n";

    private Filer filer;
//    private Elements elementUtils;
//    private Types typeUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.filer = processingEnvironment.getFiler();
//        this.elementUtils = processingEnvironment.getElementUtils();
//        this.typeUtils = processingEnvironment.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (!roundEnv.processingOver()) {

            JoinProps props = new JoinProps("mybatis-join.properties");

            String enable = props.getProperties().getProperty("processer.enable", "");
            if ("false".equalsIgnoreCase(enable)) {
                return true;
            }
            String genPath = props.getProperties().getProperty("processer.genPath", "");
            String genTablesPackage = props.getProperties().getProperty("processer.tablesPackage");
            String className = props.getProperties().getProperty("processer.tablesClassName", "Tables");

            StringBuilder guessPackage = new StringBuilder();


            StringBuilder tablesContent = new StringBuilder();
            roundEnv.getElementsAnnotatedWith(Table.class).forEach((Consumer<Element>) entityClassElement -> {

                Table table = entityClassElement.getAnnotation(Table.class);

                //init genPackage
                if ((genTablesPackage == null || genTablesPackage.trim().length() == 0)
                        && guessPackage.length() == 0) {
                    String entityClassName = entityClassElement.toString();
                    if (!entityClassName.contains(".")) {
                        guessPackage.append("table");// = "table";
                    } else {
                        guessPackage.append(entityClassName.substring(0, entityClassName.lastIndexOf(".")) + ".table");
                    }
                }

                String tableName = table != null && table.name().trim().length() != 0
                        ? table.name()
                        : firstCharToLowerCase(entityClassElement.getSimpleName().toString());


                Map<String, String> propertyAndColumns = new LinkedHashMap<>();
                List<String> defaultColumns = new ArrayList<>();

                TypeElement classElement = (TypeElement) entityClassElement;
                for (Element fieldElement : classElement.getEnclosedElements()) {

                    //all fields
                    if (ElementKind.FIELD == fieldElement.getKind()) {

                        TypeMirror typeMirror = fieldElement.asType();

                        Column column = fieldElement.getAnnotation(Column.class);
                        Transient t = fieldElement.getAnnotation(Transient.class);
                        if (t == null) {
                            continue;
                        }

                        //获取 typeHandlerClass 的名称，通过 column.typeHandler() 获取会抛出异常：MirroredTypeException:
                        //参考 https://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
                        final String[] typeHandlerClass = {""};
                        List<? extends AnnotationMirror> annotationMirrors = fieldElement.getAnnotationMirrors();
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                            annotationMirror.getElementValues().forEach((BiConsumer<ExecutableElement, AnnotationValue>) (executableElement, annotationValue) -> {
                                if (executableElement.getSimpleName().toString().equals("typeHandler")) {
                                    typeHandlerClass[0] = annotationValue.toString();
                                }
                            });
                        }

                        //未配置 typeHandler 的情况下，只支持基本数据类型，不支持比如 list set 或者自定义的类等
                        if ((column == null || typeHandlerClass[0].equals(UnknownTypeHandler.class.getName()))
                                && !defaultSupportColumnTypes.contains(typeMirror.toString())) {
                            continue;
                        }


                        String columnName = column != null && column.name().trim().length() > 0 ? column.name() : camelToUnderline(fieldElement.toString());
                        propertyAndColumns.put(fieldElement.toString(), columnName);

                        if (column == null) {
                            defaultColumns.add(columnName);
                        }
                    }
                }

                String entityClassName = entityClassElement.getSimpleName().toString();
                tablesContent.append(buildTablesClass(entityClassName, tableName, propertyAndColumns, defaultColumns));

            });

            if (tablesContent.length() > 0) {
                String realGenPackage = genTablesPackage == null || genTablesPackage.trim().length() == 0 ? guessPackage.toString() : genTablesPackage;
                genTablesClass(genPath, realGenPackage, className, tablesContent.toString());
            }

        }

        return false;
    }


    private String buildTablesClass(String entityClass, String tableName, Map<String, String> propertyAndColumns, List<String> defaultColumns) {

        // tableDefTemplate = "    public static final @entityClassTableDef @tableField = new @entityClassTableDef(\"@tableName\");\n";

        String tableDef = tableDefTemplate.replace("@entityClass", entityClass)
                .replace("@tableField", entityClass.toUpperCase())
                .replace("@tableName", tableName);


        //columnsTemplate = "        public QueryColumn @property = new QueryColumn(this, \"@columnName\");\n";
        StringBuilder queryColumns = new StringBuilder();
        propertyAndColumns.forEach((property, column) ->
                queryColumns.append(columnsTemplate
                        .replace("@property", camelToUnderline(property).toUpperCase())
                        .replace("@columnName", column)
                ));


//        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{@allColumns};
        StringJoiner allColumns = new StringJoiner(", ");
        propertyAndColumns.forEach((property, column) -> allColumns.add(camelToUnderline(property).toUpperCase()));
        String allColumnsString = allColumnsTemplate.replace("@allColumns", allColumns.toString());


        StringJoiner defaultColumnStringJoiner = new StringJoiner(", ");
        defaultColumns.forEach(s -> defaultColumnStringJoiner.add(camelToUnderline(s).toUpperCase()));
        String defaultColumnsString = defaultColumnsTemplate.replace("@allColumns", defaultColumnStringJoiner.toString());


//        classTemplate = "\n" +
//                "    public static class @entityClassTableDef extends TableDef {\n" +
//                "\n" +
//                "@queryColumns" +
//                "@allColumns" +
//                "\n" +
//                "        public @entityClassTableDef(String tableName) {\n" +
//                "            super(tableName);\n" +
//                "        }\n" +
//                "    }\n";

        String tableClass = classTemplate.replace("@entityClass", entityClass)
                .replace("@queryColumns", queryColumns)
                .replace("@defaultColumns", defaultColumnsString)
                .replace("@allColumns", allColumnsString);

        return tableDef + tableClass;
    }


    private void genTablesClass(String genBasePath, String genPackageName, String className, String classContent) {
        String genContent = classTableTemplate.replace("@package", genPackageName)
                .replace("@classesInfo", classContent);

        Writer writer = null;
        try {
            JavaFileObject sourceFile = filer.createSourceFile(genPackageName + "." + className);
            if (genBasePath == null || genBasePath.trim().length() == 0) {
                writer = sourceFile.openWriter();
                writer.write(genContent);
                writer.flush();

                printMessage(">>>>> mybatis-flex success generate tables class: \n" + sourceFile.toUri());
                return;
            }


            String defaultGenPath = sourceFile.toUri().getPath();

            //真实的生成代码的目录
            String realPath;

            //用户配置的路径为绝对路径
            if (isAbsolutePath(genBasePath)) {
                realPath = genBasePath;
            }
            //配置的是相对路径，那么则以项目根目录为相对路径
            else {
                String projectRootPath = getProjectRootPath(defaultGenPath);
                realPath = new File(projectRootPath, genBasePath).getAbsolutePath();
            }

            //通过在 test/java 目录下执行编译生成的
            boolean fromTestSource = isFromTestSource(defaultGenPath);
            if (fromTestSource) {
                realPath = new File(realPath, "src/test/java").getAbsolutePath();
            } else {
                realPath = new File(realPath, "src/main/java").getAbsolutePath();
            }

            File genJavaFile = new File(realPath, (genPackageName + "." + className).replace(".", "/") + ".java");
            if (!genJavaFile.getParentFile().exists() && !genJavaFile.getParentFile().mkdirs()) {
                System.out.println(">>>>>ERROR: can not mkdirs by mybatis-flex processer for: " + genJavaFile.getParentFile());
                return;
            }

            writer = new PrintWriter(new FileOutputStream(genJavaFile));
            writer.write(genContent);
            writer.flush();

            printMessage(">>>>> mybatis-flex success generate tables class: \n" + genJavaFile.toURI());

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


    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(Table.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private boolean isFromTestSource(String path) {
        return path.contains("test-sources") || path.contains("test-annotations");
    }


    public static boolean isAbsolutePath(String path) {
        return path != null && (path.startsWith("/") || path.indexOf(":") > 0);
    }

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
            if (new File(file, "pom.xml").exists() && !new File(file.getParentFile(), "pom.xml").exists()) {
                return file.getAbsolutePath();
            } else {
                return getProjectRootPath(file.getParentFile(), --count);
            }
        }
    }

}
