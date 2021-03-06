package querymethods.mybatisplus;

import java.util.HashMap;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;

public class MPTableInfo {

  private Map<String, String> map = new HashMap<>();

  public MPTableInfo() {

  }

  public MPTableInfo(TableInfo tableInfo) {
    if (tableInfo != null) {
      this.addPropertyColumn(tableInfo.getKeyProperty(), tableInfo.getKeyColumn());
      if (tableInfo.getFieldList() != null && !tableInfo.getFieldList().isEmpty()) {
        for (TableFieldInfo fieldInfo : tableInfo.getFieldList()) {
          this.addPropertyColumn(fieldInfo.getProperty(), fieldInfo.getColumn());
        }
      }
    }
  }

  public String getColumnByProperty(String property) {
    return this.map.get(property);
  }

  public void addPropertyColumn(String property, String column) {
    this.map.put(property, column);
  }

}
