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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import mybatis.joinquery.dialect.IDialect;
import mybatis.joinquery.util.CollectionUtil;
import mybatis.joinquery.util.StringUtil;

@SuppressWarnings({"serial", "unchecked"})
public class BaseQueryWrapper<T> implements Serializable {

  protected List<QueryTable> queryTables;
  protected String datasource;

  protected List<QueryColumn> selectColumns;
  protected List<Join> joins;
  protected List<QueryTable> joinTables;
  protected QueryCondition whereQueryCondition;
  protected List<QueryColumn> groupByColumns;
  protected QueryCondition havingQueryCondition;
  protected List<QueryOrderBy> orderBys;

  protected Integer limitOffset;
  protected Integer limitRows;

  protected T addSelectColumn(QueryColumn queryColumn) {
    if (selectColumns == null) {
      selectColumns = new LinkedList<>();
    }

    selectColumns.add(queryColumn);
    return (T) this;
  }

  protected T AddJoin(Join join) {
    if (joins == null) {
      joins = new LinkedList<>();
    }
    joins.add(join);
    return (T) this;
  }

  protected T setWhereQueryCondition(QueryCondition queryCondition) {
    if (whereQueryCondition != null) {
      queryCondition.connect(whereQueryCondition, SqlConnector.AND);
    }

    whereQueryCondition = queryCondition;
    return (T) this;
  }


  protected T addWhereQueryCondition(QueryCondition queryCondition, SqlConnector connector) {
    if (queryCondition != null) {
      if (whereQueryCondition == null) {
        whereQueryCondition = queryCondition;
      } else {
        whereQueryCondition.connect(queryCondition, connector);
      }
    }
    return (T) this;
  }

  protected T addGroupByColumns(QueryColumn queryColumn) {
    if (groupByColumns == null) {
      groupByColumns = new LinkedList<>();
    }

    groupByColumns.add(queryColumn);
    return (T) this;
  }

  protected T addHavingQueryCondition(QueryCondition queryCondition, SqlConnector connector) {
    if (havingQueryCondition == null) {
      havingQueryCondition = queryCondition;
    } else {
      havingQueryCondition.connect(queryCondition, connector);
    }
    return (T) this;
  }

  protected T addOrderBy(QueryOrderBy queryOrderBy) {
    if (orderBys == null) {
      orderBys = new LinkedList<>();
    }
    orderBys.add(queryOrderBy);
    return (T) this;
  }


  protected void addJoinTable(QueryTable queryTable) {
    if (joinTables == null) {
      joinTables = new ArrayList<>();
    }
    joinTables.add(queryTable);
  }


  protected List<QueryTable> getQueryTables() {
    return queryTables;
  }

  protected void setQueryTables(List<QueryTable> queryTables) {
    this.queryTables = queryTables;
  }

  protected String getDatasource() {
    return datasource;
  }

  protected void setDatasource(String datasource) {
    this.datasource = datasource;
  }

  protected List<QueryColumn> getSelectColumns() {
    return selectColumns;
  }

  protected void setSelectColumns(List<QueryColumn> selectColumns) {
    this.selectColumns = selectColumns;
  }

  protected List<Join> getJoins() {
    return joins;
  }

  protected void setJoins(List<Join> joins) {
    this.joins = joins;
  }

  protected List<QueryTable> getJoinTables() {
    return joinTables;
  }

  protected void setJoinTables(List<QueryTable> joinTables) {
    this.joinTables = joinTables;
  }

  protected QueryCondition getWhereQueryCondition() {
    return whereQueryCondition;
  }

  protected List<QueryColumn> getGroupByColumns() {
    return groupByColumns;
  }

  protected void setGroupByColumns(List<QueryColumn> groupByColumns) {
    this.groupByColumns = groupByColumns;
  }

  protected QueryCondition getHavingQueryCondition() {
    return havingQueryCondition;
  }

  protected List<QueryOrderBy> getOrderBys() {
    return orderBys;
  }

  protected void setOrderBys(List<QueryOrderBy> orderBys) {
    this.orderBys = orderBys;
  }

  protected Integer getLimitOffset() {
    return limitOffset;
  }

  protected void setLimitOffset(Integer limitOffset) {
    this.limitOffset = limitOffset;
  }

  protected Integer getLimitRows() {
    return limitRows;
  }

  protected void setLimitRows(Integer limitRows) {
    this.limitRows = limitRows;
  }

  protected StringBuilder getBaseQuerySql(IDialect dialect) {
    List<QueryTable> allTables = CollectionUtil.merge(queryTables, joinTables);

    StringBuilder sqlBuilder = new StringBuilder("SELECT ");
    if (selectColumns == null || selectColumns.isEmpty()) {
      sqlBuilder.append("*");
    } else {
      int index = 0;

      for (QueryColumn selectColumn : selectColumns) {
        String selectColumnSql = selectColumn.toSelectSql(allTables, dialect);
        sqlBuilder.append(selectColumnSql);
        if (index != selectColumns.size() - 1) {
          sqlBuilder.append(", ");
        }
        index++;
      }
    }
    sqlBuilder.append(" FROM ")
        .append(StringUtil.join(", ", queryTables, queryTable -> queryTable.toSql(dialect)));

    buildJoinSql(sqlBuilder, dialect);

    return sqlBuilder;
  }

  protected void buildJoinSql(StringBuilder sqlBuilder, IDialect dialect) {
    if (joins != null && !joins.isEmpty()) {
      for (Join join : joins) {
        if (!join.checkEffective()) {
          continue;
        }
        sqlBuilder.append(join.toSql(queryTables, dialect));
      }
    }
  }

  protected void buildWhereSql(StringBuilder sqlBuilder, List<QueryTable> queryTables,
      IDialect dialect) {
    if (whereQueryCondition != null) {
      String whereSql = whereQueryCondition.toSql(queryTables, dialect);
      if (StringUtil.isNotBlank(whereSql)) {
        sqlBuilder.append(" WHERE ").append(whereSql);
      }
    }
  }

  protected void buildGroupBySql(StringBuilder sqlBuilder, List<QueryTable> queryTables,
      IDialect dialect) {
    if (groupByColumns != null && !groupByColumns.isEmpty()) {
      sqlBuilder.append(" GROUP BY ");
      int index = 0;
      for (QueryColumn groupByColumn : groupByColumns) {
        String groupBy = groupByColumn.toConditionSql(queryTables, dialect);
        sqlBuilder.append(groupBy);
        if (index != groupByColumns.size() - 1) {
          sqlBuilder.append(", ");
        }
        index++;
      }
    }
  }

  protected void buildHavingSql(StringBuilder sqlBuilder, List<QueryTable> queryTables,
      IDialect dialect) {
    if (havingQueryCondition != null) {
      havingQueryCondition.index = 100000;
      String havingSql = havingQueryCondition.toSql(queryTables, dialect);
      if (StringUtil.isNotBlank(havingSql)) {
        sqlBuilder.append(" HAVING ").append(havingSql);
      }
    }
  }


  protected void buildOrderBySql(StringBuilder sqlBuilder, List<QueryTable> queryTables,
      IDialect dialect) {
    if (orderBys != null && !orderBys.isEmpty()) {
      sqlBuilder.append(" ORDER BY ");
      int index = 0;
      for (QueryOrderBy orderBy : orderBys) {
        sqlBuilder.append(orderBy.toSql(queryTables, dialect));
        if (index != orderBys.size() - 1) {
          sqlBuilder.append(", ");
        }
        index++;
      }
    }
  }

}
