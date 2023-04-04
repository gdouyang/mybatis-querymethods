package joinquery;

// Auto generate by mybatis-flex, do not modify it.
public class Tables {


    public static final AccountTableDef ACCOUNT = new AccountTableDef("tb_account");

    public static class AccountTableDef extends TableDef {

        public QueryColumn ID = new QueryColumn(this, "id");
        public QueryColumn USER_NAME = new QueryColumn(this, "user_name");
        public QueryColumn BIRTHDAY = new QueryColumn(this, "birthday");
        public QueryColumn SEX = new QueryColumn(this, "sex");
        public QueryColumn AGE = new QueryColumn(this, "age");
        public QueryColumn IS_NORMAL = new QueryColumn(this, "is_normal");
        public QueryColumn IS_DELETE = new QueryColumn(this, "is_delete");

        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, USER_NAME, BIRTHDAY, SEX, AGE, IS_NORMAL};
        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{ID, USER_NAME, BIRTHDAY, SEX, AGE, IS_NORMAL, IS_DELETE};


        public AccountTableDef(String tableName) {
            super(tableName);
        }
    }


    public static final ArticleTableDef ARTICLE = new ArticleTableDef("tb_article");

    public static class ArticleTableDef extends TableDef {

        public QueryColumn ID = new QueryColumn(this, "id");
        public QueryColumn UUID = new QueryColumn(this, "uuid");
        public QueryColumn ACCOUNT_ID = new QueryColumn(this, "account_id");
        public QueryColumn TITLE = new QueryColumn(this, "title");
        public QueryColumn CONTENT = new QueryColumn(this, "content");
        public QueryColumn CREATED = new QueryColumn(this, "created");
        public QueryColumn MODIFIED = new QueryColumn(this, "modified");
        public QueryColumn IS_DELETE = new QueryColumn(this, "is_delete");
        public QueryColumn VERSION = new QueryColumn(this, "version");

        public QueryColumn[] DEFAULT_COLUMNS = new QueryColumn[]{ID, UUID, ACCOUNT_ID, TITLE, CREATED, MODIFIED, VERSION};
        public QueryColumn[] ALL_COLUMNS = new QueryColumn[]{ID, UUID, ACCOUNT_ID, TITLE, CONTENT, CREATED, MODIFIED, IS_DELETE, VERSION};


        public ArticleTableDef(String tableName) {
            super(tableName);
        }
    }
}
