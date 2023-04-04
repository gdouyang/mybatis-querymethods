package mybatis.join;


import static mybatis.join.Tables.ARTICLE;

import org.junit.Test;

import mybatis.join.JoinQueryWrapper;
import mybatis.join.dialect.CommonsDialectImpl;
import mybatis.join.dialect.IDialect;

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
