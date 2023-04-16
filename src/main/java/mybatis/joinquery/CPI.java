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
package mybatis.joinquery;

import java.util.List;

import mybatis.joinquery.dialect.IDialect;

/**
 * Cross Package Invoke 夸包调用工具类，这么设计的原因，是需要保证 QueryWrapper 方法对于用户的纯净性 而 framework 又可以通过 CPI 来调用
 * QueryWrapper 的其他方法
 */

public class CPI {

  public static List<QueryTable> getQueryTables(JoinQueryWrapper queryWrapper) {
    return queryWrapper.getQueryTables();
  }

  public static List<QueryTable> getJoinTables(JoinQueryWrapper queryWrapper) {
    return queryWrapper.getJoinTables();
  }

  public static Integer getLimitOffset(JoinQueryWrapper queryWrapper) {
    return queryWrapper.getLimitOffset();
  }

  public static Integer getLimitRows(JoinQueryWrapper queryWrapper) {
    return queryWrapper.getLimitRows();
  }

  public static StringBuilder getBaseQuerySql(JoinQueryWrapper queryWrapper, IDialect dialect) {
    return queryWrapper.getBaseQuerySql(dialect);
  }

  public static void buildJoinSql(StringBuilder sql, JoinQueryWrapper queryWrapper,
      IDialect dialect) {
    queryWrapper.buildJoinSql(sql, dialect);
  }

  public static void buildWhereSql(StringBuilder sql, JoinQueryWrapper queryWrapper,
      List<QueryTable> queryTables, IDialect dialect) {
    queryWrapper.buildWhereSql(sql, queryTables, dialect);
  }

  public static void buildGroupBySql(StringBuilder sql, JoinQueryWrapper queryWrapper,
      List<QueryTable> queryTables, IDialect dialect) {
    queryWrapper.buildGroupBySql(sql, queryTables, dialect);
  }

  public static void buildHavingSql(StringBuilder sql, JoinQueryWrapper queryWrapper,
      List<QueryTable> queryTables, IDialect dialect) {
    queryWrapper.buildHavingSql(sql, queryTables, dialect);
  }

  public static void buildOrderBySql(StringBuilder sql, JoinQueryWrapper queryWrapper,
      List<QueryTable> queryTables, IDialect dialect) {
    queryWrapper.buildOrderBySql(sql, queryTables, dialect);
  }
}
