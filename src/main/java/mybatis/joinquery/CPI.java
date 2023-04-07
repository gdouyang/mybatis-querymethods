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
package mybatis.joinquery;

import java.util.List;

import mybatis.joinquery.dialect.IDialect;

/**
 * Cross Package Invoke
 * 夸包调用工具类，这么设计的原因，是需要保证 QueryWrapper 方法对于用户的纯净性
 * 而 framework 又可以通过 CPI 来调用 QueryWrapper 的其他方法
 */

public class CPI {

    public static List<QueryTable> getQueryTables(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getQueryTables();
    }

    public static List<QueryColumn> getSelectColumns(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getSelectColumns();
    }

    public static List<Join> getJoins(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getJoins();
    }

    public static List<QueryTable> getJoinTables(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getJoinTables();
    }

    public static QueryCondition getWhereQueryCondition(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getWhereQueryCondition();
    }

    public static List<QueryColumn> getGroupByColumns(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getGroupByColumns();
    }

    public static QueryCondition getHavingQueryCondition(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getHavingQueryCondition();
    }

    public static List<QueryOrderBy> getOrderBys(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getOrderBys();
    }

    public static Integer getLimitOffset(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getLimitOffset();
    }

    public static Integer getLimitRows(JoinQueryWrapper queryWrapper) {
        return queryWrapper.getLimitRows();
    }

    public static String toConditionSql(QueryColumn queryColumn,List<QueryTable> queryTables, IDialect dialect) {
        return queryColumn.toConditionSql(queryTables,dialect);
    }

    public static String toSelectSql(QueryColumn queryColumn,List<QueryTable> queryTables, IDialect dialect) {
        return queryColumn.toSelectSql(queryTables,dialect);
    }

}
