package joinquery;

import java.io.Serializable;

@SuppressWarnings("serial")
public class TableDef implements Serializable {

    private String tableName;

    public TableDef(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    public QueryTable as(String alias) {
        return new QueryTable(tableName, alias);
    }


}
