package querymethods.mybatisplus;

import org.apache.ibatis.mapping.MappedStatement;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class AbstractMethodImpl extends AbstractMethod {

  private static final long serialVersionUID = 1L;

  protected AbstractMethodImpl() {
    super("");
  }

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
  public String sqlWhereEntityWrapper(boolean newLine, TableInfo table) {

    return super.sqlWhereEntityWrapper(newLine, table);
  }
  
  @Override
  protected String sqlOrderBy(TableInfo tableInfo) {
    return super.sqlOrderBy(tableInfo);
  }

}
