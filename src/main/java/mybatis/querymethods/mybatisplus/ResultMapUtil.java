package mybatis.querymethods.mybatisplus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultFlag;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeHandler;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

import mybatis.querymethods.QueryMethodsException;
import mybatis.querymethods.util.MetaObjectUtil;

public class ResultMapUtil {
  public static final Pattern DELIMITER = Pattern.compile("^[`\\[\"]?(.*?)[`\\]\"]?$");

  private static Map<String, ResultMap> cache = new ConcurrentHashMap<>();

  public static void setResultType(MappedStatement ms, TableInfo tableInfo) {
    List<ResultMap> resultMaps = new ArrayList<ResultMap>();
    resultMaps.add(getResultMap(tableInfo, ms.getConfiguration()));
    MetaObject metaObject = MetaObjectUtil.forObject(ms);
    metaObject.setValue("resultMaps", Collections.unmodifiableList(resultMaps));
  }

  /**
   * 生成当前实体的resultMap对象
   *
   * @param configuration
   * @return
   */
  public static ResultMap getResultMap(TableInfo tableInfo, Configuration configuration) {

    String tableName = tableInfo.getTableName();
    ResultMap resultMap = cache.get(tableName);
    if (null != resultMap) {
      return resultMap;
    }

    List<TableFieldInfo> fieldList = tableInfo.getFieldList();
    if (fieldList == null || fieldList.size() == 0) {
      return null;
    }
    List<ResultMapping> resultMappings = new ArrayList<ResultMapping>();

    // ID
    ResultMapping resultMapping = createResultMapping(configuration, tableInfo.getKeyProperty(),
        tableInfo.getKeyType(), null, null, tableInfo.getKeyColumn(), true);
    resultMappings.add(resultMapping);
    //
    for (TableFieldInfo entityColumn : fieldList) {
      String column = entityColumn.getColumn();
      resultMapping = createResultMapping(configuration, entityColumn.getProperty(),
          entityColumn.getPropertyType(), entityColumn.getJdbcType(), entityColumn.getTypeHandler(),
          column, false);
      resultMappings.add(resultMapping);
    }
    ResultMap.Builder builder = new ResultMap.Builder(configuration, "BaseMapperResultMap",
        tableInfo.getEntityType(), resultMappings, true);
    resultMap = builder.build();
    cache.put(tableName, resultMap);
    return resultMap;
  }

  private static ResultMapping createResultMapping(Configuration configuration, String property,
      Class<?> propertyType, JdbcType jdbcType, Class<? extends TypeHandler<?>> typeHandler,
      String column, boolean isId) {
    // 去掉可能存在的分隔符
    Matcher matcher = DELIMITER.matcher(column);
    if (matcher.find()) {
      column = matcher.group(1);
    }
    ResultMapping.Builder builder =
        new ResultMapping.Builder(configuration, property, column, propertyType);
    if (jdbcType != null) {
      builder.jdbcType(jdbcType);
    }
    if (typeHandler != null) {
      try {
        builder.typeHandler(getInstance(propertyType, typeHandler));
      } catch (Exception e) {
        throw new QueryMethodsException(e);
      }
    }
    List<ResultFlag> flags = new ArrayList<ResultFlag>();
    if (isId) {
      flags.add(ResultFlag.ID);
    }
    builder.flags(flags);
    ResultMapping build = builder.build();
    return build;
  }

  /**
   * 实例化TypeHandler
   * 
   * @param javaTypeClass
   * @param typeHandlerClass
   * @return
   */
  @SuppressWarnings("unchecked")
  public static <T> TypeHandler<T> getInstance(Class<?> javaTypeClass, Class<?> typeHandlerClass) {
    if (javaTypeClass != null) {
      try {
        Constructor<?> c = typeHandlerClass.getConstructor(Class.class);
        return (TypeHandler<T>) c.newInstance(javaTypeClass);
      } catch (NoSuchMethodException ignored) {
        // ignored
      } catch (Exception e) {
        throw new TypeException("Failed invoking constructor for handler " + typeHandlerClass, e);
      }
    }
    try {
      Constructor<?> c = typeHandlerClass.getConstructor();
      return (TypeHandler<T>) c.newInstance();
    } catch (Exception e) {
      throw new TypeException("Unable to find a usable constructor for " + typeHandlerClass, e);
    }
  }
}
