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
package joinquery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import joinquery.util.ArrayUtil;
import joinquery.util.CollectionUtil;

@SuppressWarnings("serial")
public class JoinQueryWrapper extends BaseQueryWrapper<JoinQueryWrapper> {


    public static JoinQueryWrapper create() {
        return new JoinQueryWrapper();
    }

    public JoinQueryWrapper select(QueryColumn... queryColumns) {
        for (QueryColumn column : queryColumns) {
            if (column != null) {
                addSelectColumn(column);
            }
        }
        return this;
    }


    public JoinQueryWrapper from(TableDef... tableDefs) {
        for (TableDef tableDef : tableDefs) {
            from(new QueryTable(tableDef.getTableName()));
        }
        return this;
    }


    public JoinQueryWrapper from(String... tables) {
        for (String table : tables) {
            from(new QueryTable(table));
        }
        return this;
    }


    public JoinQueryWrapper from(QueryTable... tables) {
        if (CollectionUtil.isEmpty(queryTables)) {
            queryTables = new ArrayList<>();
            queryTables.addAll(Arrays.asList(tables));
        } else {
            for (QueryTable table : tables) {
                boolean contains = false;
                for (QueryTable queryTable : queryTables) {
                    if (queryTable.isSameTable(table)) {
                        contains = true;
                    }
                }
                if (!contains) {
                    queryTables.add(table);
                }
            }
        }
        return this;
    }


    public JoinQueryWrapper from(JoinQueryWrapper queryWrapper) {
        return from(new SelectQueryTable(queryWrapper));
    }


    public JoinQueryWrapper as(String alias) {
        if (CollectionUtil.isEmpty(queryTables)) {
            throw new IllegalArgumentException("query table must not be empty.");
        }
        if (queryTables.size() > 1) {
            throw new IllegalArgumentException("QueryWrapper.as(...) only support 1 table");
        }
        queryTables.get(0).alias = alias;
        return this;
    }


    public JoinQueryWrapper where(QueryCondition queryCondition) {
        this.setWhereQueryCondition(queryCondition);
        return this;
    }

    public JoinQueryWrapper where(String sql) {
        this.setWhereQueryCondition(new StringQueryCondition(sql));
        return this;
    }


    public JoinQueryWrapper where(String sql, Object... params) {
        this.setWhereQueryCondition(new StringQueryCondition(sql, params));
        return this;
    }


    public JoinQueryWrapper where(Map<String, Object> whereConditions) {
        if (whereConditions != null) {
            whereConditions.forEach((s, o) -> and(QueryCondition.create(new QueryColumn(s), o)));
        }
        return this;
    }


    public JoinQueryWrapper and(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, SqlConnector.AND);
    }

    public JoinQueryWrapper and(String sql) {
        this.addWhereQueryCondition(new StringQueryCondition(sql), SqlConnector.AND);
        return this;
    }


    public JoinQueryWrapper and(String sql, Object... params) {
        this.addWhereQueryCondition(new StringQueryCondition(sql, params), SqlConnector.AND);
        return this;
    }


    public JoinQueryWrapper or(QueryCondition queryCondition) {
        return addWhereQueryCondition(queryCondition, SqlConnector.OR);
    }


    public Joiner<JoinQueryWrapper> leftJoin(String table) {
        return joining(Join.TYPE_LEFT, table, true);
    }


    public Joiner<JoinQueryWrapper> leftJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_LEFT, table, condition);
    }

    public Joiner<JoinQueryWrapper> leftJoin(TableDef table) {
        return joining(Join.TYPE_LEFT, table.getTableName(), true);
    }


    public Joiner<JoinQueryWrapper> leftJoinIf(TableDef table, boolean condition) {
        return joining(Join.TYPE_LEFT, table.getTableName(), condition);
    }

    public Joiner<JoinQueryWrapper> leftJoin(JoinQueryWrapper table) {
        return joining(Join.TYPE_LEFT, table, true);
    }

    public Joiner<JoinQueryWrapper> leftJoinIf(JoinQueryWrapper table, boolean condition) {
        return joining(Join.TYPE_LEFT, table, condition);
    }

    public Joiner<JoinQueryWrapper> rightJoin(String table) {
        return joining(Join.TYPE_RIGHT, table, true);
    }

    public Joiner<JoinQueryWrapper> rightJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_RIGHT, table, condition);
    }

    public Joiner<JoinQueryWrapper> rightJoin(JoinQueryWrapper table) {
        return joining(Join.TYPE_RIGHT, table, true);
    }

    public Joiner<JoinQueryWrapper> rightJoinIf(JoinQueryWrapper table, boolean condition) {
        return joining(Join.TYPE_RIGHT, table, condition);
    }

    public Joiner<JoinQueryWrapper> innerJoin(String table) {
        return joining(Join.TYPE_INNER, table, true);
    }

    public Joiner<JoinQueryWrapper> innerJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_INNER, table, condition);
    }

    public Joiner<JoinQueryWrapper> innerJoin(TableDef table) {
        return innerJoinIf(table, true);
    }

    public Joiner<JoinQueryWrapper> innerJoinIf(TableDef table, boolean condition) {
        return joining(Join.TYPE_INNER, table.getTableName(), condition);
    }

    public Joiner<JoinQueryWrapper> innerJoin(JoinQueryWrapper table) {
        return joining(Join.TYPE_INNER, table, true);
    }

    public Joiner<JoinQueryWrapper> innerJoinIf(JoinQueryWrapper table, boolean condition) {
        return joining(Join.TYPE_INNER, table, condition);
    }

    public Joiner<JoinQueryWrapper> fullJoin(String table) {
        return joining(Join.TYPE_FULL, table, true);
    }

    public Joiner<JoinQueryWrapper> fullJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_FULL, table, condition);
    }

    public Joiner<JoinQueryWrapper> fullJoin(JoinQueryWrapper table) {
        return joining(Join.TYPE_FULL, table, true);
    }

    public Joiner<JoinQueryWrapper> fullJoinIf(JoinQueryWrapper table, boolean condition) {
        return joining(Join.TYPE_FULL, table, condition);
    }

    public Joiner<JoinQueryWrapper> crossJoin(String table) {
        return joining(Join.TYPE_CROSS, table, true);
    }

    public Joiner<JoinQueryWrapper> crossJoinIf(String table, boolean condition) {
        return joining(Join.TYPE_CROSS, table, condition);
    }

    public Joiner<JoinQueryWrapper> crossJoin(JoinQueryWrapper table) {
        return joining(Join.TYPE_CROSS, table, true);
    }

    public Joiner<JoinQueryWrapper> crossJoinIf(JoinQueryWrapper table, boolean condition) {
        return joining(Join.TYPE_CROSS, table, condition);
    }


    protected Joiner<JoinQueryWrapper> joining(String type, String table, boolean condition) {
        Join join = new Join(type, table, condition);
        addJoinTable(join.getQueryTable());
        return new Joiner<>(AddJoin(join), join);
    }

    protected Joiner<JoinQueryWrapper> joining(String type, JoinQueryWrapper queryWrapper, boolean condition) {
        Join join = new Join(type, queryWrapper, condition);
        addJoinTable(join.getQueryTable());
        return new Joiner<>(AddJoin(join), join);
    }


    public JoinQueryWrapper groupBy(String name) {
        addGroupByColumns(new QueryColumn(name));
        return this;
    }

    public JoinQueryWrapper groupBy(String... names) {
        for (String name : names) {
            groupBy(name);
        }
        return this;
    }

    public JoinQueryWrapper groupBy(QueryColumn column) {
        addGroupByColumns(column);
        return this;
    }

    public JoinQueryWrapper groupBy(QueryColumn... columns) {
        for (QueryColumn column : columns) {
            groupBy(column);
        }
        return this;
    }


    public JoinQueryWrapper having(QueryCondition queryCondition) {
        addHavingQueryCondition(queryCondition, SqlConnector.AND);
        return this;
    }

    public JoinQueryWrapper orderBy(QueryOrderBy... orderBys) {
        for (QueryOrderBy queryOrderBy : orderBys) {
            addOrderBy(queryOrderBy);
        }
        return this;
    }

    public JoinQueryWrapper orderBy(String... orderBys) {
        for (String queryOrderBy : orderBys) {
            addOrderBy(new StringQueryOrderBy(queryOrderBy));
        }
        return this;
    }


    public JoinQueryWrapper limit(Integer rows) {
        setLimitRows(rows);
        return this;
    }

    public JoinQueryWrapper offset(Integer offset) {
        setLimitOffset(offset);
        return this;
    }

    public JoinQueryWrapper limit(Integer offset, Integer rows) {
        setLimitOffset(offset);
        setLimitRows(rows);
        return this;
    }

    public JoinQueryWrapper datasource(String datasource) {
        setDatasource(datasource);
        return this;
    }

    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    public Object[] getValueArray() {
        Object[] whereValues = WrapperUtil.getValues(whereQueryCondition);
        Object[] havingValues = WrapperUtil.getValues(havingQueryCondition);
        return ArrayUtil.concat(whereValues, havingValues);
    }
    
    /**
     * 获取 queryWrapper 的参数
     * 在构建 sql 的时候，需要保证 where 在 having 的前面
     */
    public Map<String, Object> getValueMap() {
    	Map<String, Object> whereValues = WrapperUtil.getValuesMap(whereQueryCondition);
    	Map<String, Object> havingValues = WrapperUtil.getValuesMap(havingQueryCondition);
    	if (havingValues != null) {
    		whereValues.putAll(havingValues);
    	}
        return whereValues;
    }

}
