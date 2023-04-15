package mybatis.joinquery;


import static mybatis.joinquery.Tables.ARTICLE;
import org.junit.Test;
import mybatis.joinquery.dialect.CommonsDialectImpl;
import mybatis.joinquery.dialect.IDialect;

public class ArticleSqlTester {


    @Test
    public void testSelectSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select()
                .from(ARTICLE);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }


}
