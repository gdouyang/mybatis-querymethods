package querymethods.mybatisplus;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.ibatis.mapping.MappedStatement;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class AbstractMethodImpl extends AbstractMethod {

  @Override
  public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass,
      TableInfo tableInfo) {
    return null;
  }

  @Override
  public String sqlLogicSet(TableInfo table) {

    return super.sqlLogicSet(table);
  }

  @Override
  public String sqlSet(boolean logic, boolean ew, TableInfo table, boolean judgeAliasNull,
      String alias, String prefix) {

    return super.sqlSet(logic, ew, table, judgeAliasNull, alias, prefix);
  }

  @Override
  public String sqlComment() {

    return super.sqlComment();
  }

  @Override
  protected String sqlFirst() {

    return super.sqlFirst();
  }

  @Override
  public String sqlSelectColumns(TableInfo table, boolean queryWrapper) {

    return super.sqlSelectColumns(table, queryWrapper);
  }

  @Override
  public String sqlCount() {

    return super.sqlCount();
  }

  @Override
  public String sqlSelectObjsColumns(TableInfo table) {

    return super.sqlSelectObjsColumns(table);
  }

  @Override
  public String sqlWhereByMap(TableInfo table) {

    return super.sqlWhereByMap(table);
  }

  @Override
  public String sqlWhereEntityWrapper(boolean newLine, TableInfo table) {

    return super.sqlWhereEntityWrapper(newLine, table);
  }

  @Override
  public String filterTableFieldInfo(List<TableFieldInfo> fieldList,
      Predicate<TableFieldInfo> predicate, Function<TableFieldInfo, String> function,
      String joiningVal) {

    return super.filterTableFieldInfo(fieldList, predicate, function, joiningVal);
  }

  @Override
  public String optlockVersion(TableInfo tableInfo) {

    return super.optlockVersion(tableInfo);
  }


}
