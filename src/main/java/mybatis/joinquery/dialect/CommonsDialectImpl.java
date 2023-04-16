/**
 * Copyright (c) 2022-2023, Mybatis-Flex (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package mybatis.joinquery.dialect;

import java.util.List;
import mybatis.joinquery.CPI;
import mybatis.joinquery.JoinQueryWrapper;
import mybatis.joinquery.QueryTable;
import mybatis.joinquery.util.CollectionUtil;
import mybatis.joinquery.util.StringUtil;

/**
 * 通用的方言设计，其他方言可以继承于当前 CommonsDialectImpl 创建或获取方言请参考
 * {@link mybatis.joinquery.dialect.DialectFactory}
 */
public class CommonsDialectImpl implements IDialect {

  protected KeywordWrap keywordWrap = KeywordWrap.BACKQUOTE;
  private LimitOffsetProcesser limitOffsetProcesser = LimitOffsetProcesser.MYSQL;

  public CommonsDialectImpl() {}

  public CommonsDialectImpl(LimitOffsetProcesser limitOffsetProcesser) {
    this.limitOffsetProcesser = limitOffsetProcesser;
  }

  public CommonsDialectImpl(KeywordWrap keywordWrap, LimitOffsetProcesser limitOffsetProcesser) {
    this.keywordWrap = keywordWrap;
    this.limitOffsetProcesser = limitOffsetProcesser;
  }

  @Override
  public String wrap(String keyword) {
    return keywordWrap.wrap(keyword);
  }

  @Override
  public String forSelectListByQuery(JoinQueryWrapper queryWrapper) {
    return buildSelectSql(queryWrapper);
  }


  @Override
  public String forSelectCountByQuery(JoinQueryWrapper queryWrapper) {
    return buildSelectCountSql(queryWrapper);
  }

  //////////// build query sql///////
  @Override
  public String buildSelectSql(JoinQueryWrapper queryWrapper) {
    List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
    List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
    List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

    StringBuilder sqlBuilder = CPI.getBaseQuerySql(queryWrapper, this);

    CPI.buildWhereSql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildGroupBySql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildHavingSql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildOrderBySql(sqlBuilder, queryWrapper, allTables, this);

    Integer limitRows = CPI.getLimitRows(queryWrapper);
    Integer limitOffset = CPI.getLimitOffset(queryWrapper);
    if (limitRows != null || limitOffset != null) {
      sqlBuilder = buildLimitOffsetSql(sqlBuilder, queryWrapper, limitRows, limitOffset);
    }

    return sqlBuilder.toString();
  }


  @Override
  public String buildSelectCountSql(JoinQueryWrapper queryWrapper) {
    List<QueryTable> queryTables = CPI.getQueryTables(queryWrapper);
    List<QueryTable> joinTables = CPI.getJoinTables(queryWrapper);
    List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

    // ignore selectColumns
    StringBuilder sqlBuilder = new StringBuilder("SELECT COUNT(*) FROM ");
    sqlBuilder.append(StringUtil.join(", ", queryTables, queryTable -> queryTable.toSql(this)));


    CPI.buildWhereSql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildGroupBySql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildHavingSql(sqlBuilder, queryWrapper, allTables, this);
    CPI.buildOrderBySql(sqlBuilder, queryWrapper, allTables, this);

    // ignore orderBy and limit
    // buildOrderBySql(sqlBuilder, queryWrapper);
    // buildLimitSql(sqlBuilder, queryWrapper);

    return sqlBuilder.toString();
  }

  /**
   * 构建 limit 和 offset 的参数
   */
  protected StringBuilder buildLimitOffsetSql(StringBuilder sqlBuilder,
      JoinQueryWrapper queryWrapper, Integer limitRows, Integer limitOffset) {
    return limitOffsetProcesser.process(sqlBuilder, queryWrapper, limitRows, limitOffset);
  }

  protected String buildQuestion(int count, boolean withBrackets) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count; i++) {
      sb.append("?");
      if (i != count - 1) {
        sb.append(", ");
      }
    }
    return withBrackets ? "(" + sb + ")" : sb.toString();
  }


}
