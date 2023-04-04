package joinquery.processer;

public class Options {

  private Boolean enable;

  private String genPath;

  private String tablesPackage;

  private String tablesClassName;

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public String getGenPath() {
    return genPath;
  }

  public void setGenPath(String genPath) {
    this.genPath = genPath;
  }

  public String getTablesPackage() {
    return tablesPackage;
  }

  public void setTablesPackage(String tablesPackage) {
    this.tablesPackage = tablesPackage;
  }

  public String getTablesClassName() {
    return tablesClassName;
  }

  public void setTablesClassName(String tablesClassName) {
    this.tablesClassName = tablesClassName;
  }

}
