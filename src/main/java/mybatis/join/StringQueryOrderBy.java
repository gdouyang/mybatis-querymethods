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
package mybatis.join;


import java.util.List;

import mybatis.join.dialect.IDialect;
import mybatis.join.util.SqlUtil;

/**
 * 排序字段
 */
@SuppressWarnings("serial")
public class StringQueryOrderBy extends QueryOrderBy {

    private String orderBy;

    public StringQueryOrderBy(String orderBy) {
        SqlUtil.keepOrderBySqlSafely(orderBy);
        this.orderBy = orderBy;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        return orderBy;
    }
}
