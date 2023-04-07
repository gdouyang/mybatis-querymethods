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


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mybatis.joinquery.util.StringUtil;

class WrapperUtil {


    static String buildAsAlias(String alias) {
        return StringUtil.isBlank(alias) ? "" : " AS " + alias;
    }

    static final Object[] NULL_PARA_ARRAY = new Object[0];

    static Object[] getValues(QueryCondition condition) {
        if (condition == null) {
            return NULL_PARA_ARRAY;
        }

        List<Object> paras = new LinkedList<>();
        getValues(condition, paras);

        return paras.isEmpty() ? NULL_PARA_ARRAY : paras.toArray();
    }


    private static void getValues(QueryCondition condition, List<Object> paras) {
        if (condition == null) {
            return;
        }
        Object value = condition.getValue();
        if (value != null) {
            if (value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                for (Object object : values) {
                    if (object != null && (object.getClass().isArray()
                            || object.getClass() == int[].class
                            || object.getClass() == long[].class
                            || object.getClass() == short[].class
                            || object.getClass() == float[].class
                            || object.getClass() == double[].class)) {
                        for (int i = 0; i < Array.getLength(object); i++) {
                            paras.add(Array.get(object, i));
                        }
                    } else {
                        paras.add(object);
                    }
                }
            } else if (value instanceof JoinQueryWrapper) {
                Object[] valueArray = ((JoinQueryWrapper) value).getValueArray();
                paras.addAll(Arrays.asList(valueArray));
            } else {
                paras.add(value);
            }
        }

        getValues(condition.next, paras);
    }
    
    static Map<String, Object> getValuesMap(QueryCondition condition) {
        if (condition == null) {
            return null;
        }

        Map<String, Object> paras = new LinkedHashMap<>();
        getValuesMap(condition, paras);

        return paras;
    }


    private static void getValuesMap(QueryCondition condition, Map<String, Object> paras) {
        if (condition == null) {
            return;
        }
        Object value = condition.getValue();
        if (value != null) {
            if (value.getClass().isArray()) {
                Object[] values = (Object[]) value;
                for (int idx = 0; idx < values.length; idx++) {
                	Object object = values[idx];
                    if (object != null && (object.getClass().isArray()
                            || object.getClass() == int[].class
                            || object.getClass() == long[].class
                            || object.getClass() == short[].class
                            || object.getClass() == float[].class
                            || object.getClass() == double[].class)) {
                        for (int i = 0; i < Array.getLength(object); i++) {
                            paras.put(condition.index + "" + i, Array.get(object, i));
                        }
                    } else {
                    	paras.put(condition.index + "" + idx, object);
                    }
                }
            } else if (value instanceof JoinQueryWrapper) {
                Map<String, Object> valueArray = ((JoinQueryWrapper) value).getValueMap();
                paras.putAll(valueArray);
            } else {
            	paras.put(condition.index + "", value);
            }
        }

        getValuesMap(condition.next, paras);
    }


    public static String getColumnTableName(List<QueryTable> queryTables, QueryTable queryTable) {
        if (queryTables == null) {
            return "";
        }

        if (queryTables.size() == 1 && queryTables.get(0).isSameTable(queryTable)) {
            return "";
        }

        QueryTable realTable = getRealTable(queryTables, queryTable);
        if (realTable == null) {
            return "";
        }

        return StringUtil.isNotBlank(realTable.alias) ? realTable.alias : realTable.name;
    }

    public static QueryTable getRealTable(List<QueryTable> queryTables, QueryTable queryTable) {
        if (queryTables == null || queryTables.isEmpty()) {
            return queryTable;
        }

        if (queryTable == null && queryTables.size() == 1) {
            return queryTables.get(0);
        }

        for (QueryTable table : queryTables) {
            if (table.isSameTable(queryTable)) {
                return table;
            }
        }
        return queryTable;
    }

}
