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

import java.util.List;
import joinquery.dialect.IDialect;
import joinquery.util.StringUtil;

/**
 * 操作类型的操作
 * 示例1：and not ( id > 100 and name like %%)
 */
@SuppressWarnings("serial")
public class OperatorQueryCondition extends QueryCondition {
    
    private String operator;
    private QueryCondition child;

    public OperatorQueryCondition(String operator, QueryCondition child) {
        this.operator = operator;
        this.child = child;
    }

    @Override
    public String toSql(List<QueryTable> queryTables, IDialect dialect) {
        StringBuilder sql = new StringBuilder();

        //检测是否生效
        if (checkEffective()) {
            String childSql = child.toSql(queryTables, dialect);
            if (StringUtil.isNotBlank(childSql)) {
                QueryCondition effectiveBefore = getEffectiveBefore();
                if (effectiveBefore != null) {
                    sql.append(effectiveBefore.connector);
                }
                sql.append(operator).append("(").append(childSql).append(")");
            }
        }

        if (this.next != null) {
            return sql + next.toSql(queryTables, dialect);
        }

        return sql.toString();
    }

    @Override
    public Object getValue() {
        return WrapperUtil.getValues(child);
    }
}
