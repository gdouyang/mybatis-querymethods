package mybatis.joinquery;


import static mybatis.joinquery.QueryFunctions.avg;
import static mybatis.joinquery.QueryFunctions.count;
import static mybatis.joinquery.QueryFunctions.distinct;
import static mybatis.joinquery.QueryFunctions.exist;
import static mybatis.joinquery.QueryFunctions.max;
import static mybatis.joinquery.QueryFunctions.noCondition;
import static mybatis.joinquery.QueryFunctions.notExist;
import static mybatis.joinquery.QueryFunctions.select;
import static mybatis.joinquery.QueryFunctions.selectOne;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import mybatis.joinquery.dialect.CommonsDialectImpl;
import mybatis.joinquery.dialect.DialectFactory;
import mybatis.joinquery.dialect.IDialect;
import mybatis.joinquery.dialect.KeywordWrap;
import mybatis.joinquery.dialect.LimitOffsetProcesser;

public class AccountSqlTest {


    @Test
    public void testSelectSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.AGE.as("a"))
                .from(Tables.ACCOUNT);

        IDialect dialect = DialectFactory.getDialect();
        String sql = dialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT `age` AS `a` FROM `tb_account`", sql);
    }

    @Test
    public void testSelectColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME)
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT `id`, `user_name` FROM `tb_account`", sql);
    }

    @Test
    public void testSelect1ColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME,
                        Tables.ARTICLE.ID.as("articleId"), Tables.ARTICLE.TITLE)
                .from(Tables.ACCOUNT.as("a"), Tables.ARTICLE.as("b"))
                .where(Tables.ACCOUNT.ID.eq(Tables.ARTICLE.ACCOUNT_ID));

        IDialect dialect = new CommonsDialectImpl(KeywordWrap.NONE, LimitOffsetProcesser.MYSQL);
        String sql = dialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT a.id, a.user_name, b.id AS articleId, b.title FROM tb_account AS a, tb_article AS b WHERE a.id = b.account_id", sql);
    }

    @Test
    public void testSelectColumnsAndFunctionsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME, max(Tables.ACCOUNT.BIRTHDAY), avg(Tables.ACCOUNT.SEX).as("sex_avg"))
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT `id`, `user_name`, MAX(`birthday`), AVG(`sex`) AS sex_avg FROM `tb_account`", sql);
    }


    @Test
    public void testSelectAllColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ALL_COLUMNS)
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(query);
        System.out.println(sql);
        Assert.assertEquals("SELECT `id`, `user_name`, `birthday`, `sex`, `age`, `is_normal`, `is_delete` FROM `tb_account`", sql);
    }


    @Test
    public void testSelectCountSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectCountSql(queryWrapper);
        System.out.println(sql);
        Assert.assertEquals("SELECT COUNT(*) FROM `tb_account` WHERE `id` >=  #{p0}  AND `user_name` LIKE  #{p1} ", sql);
    }


    @Test
    public void testWhereSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `id` >=  #{p0}  AND `user_name` LIKE  #{p1} ", sql);
    }


    @Test
    public void testWhereCond1Sql() {
        boolean flag = false;
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100).when(flag))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `user_name` LIKE  #{p1} ", sql);

        Object[] valueArray = queryWrapper.getValueArray();
        System.out.println(Arrays.toString(valueArray));
    }


    @Test
    public void testWhereCond2Sql() {
        boolean flag = false;
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(flag ? Tables.ACCOUNT.ID.ge(100) : noCondition())
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        System.out.println(sql);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `user_name` LIKE  #{p1} ", sql);

        Object[] valueArray = queryWrapper.getValueArray();
        System.out.println(Arrays.toString(valueArray));
    }


    @Test
    public void testWhereExistSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(
                        exist(
                                selectOne().from(Tables.ARTICLE).as("a").where(Tables.ARTICLE.ID.ge(100))
                        )
                );

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `id` >=  #{p0}  AND  EXIST (SELECT 1 FROM `tb_article` AS `a` WHERE `id` >=  #{p0} )", sql);
        System.out.println(sql);
    }


    @Test
    public void testWhereAndOrSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(Tables.ACCOUNT.SEX.eq(1).or(Tables.ACCOUNT.SEX.eq(2)))
                .or(Tables.ACCOUNT.AGE.in(18, 19, 20).or(Tables.ACCOUNT.USER_NAME.like("michael")));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `id` >=  #{p0}  AND (`sex` =  #{p0}  OR `sex` =  #{p1} ) OR (`age` IN ( #{p00} , #{p01} , #{p02} ) OR `user_name` LIKE  #{p1} )", sql);
        System.out.println(sql);
    }

    @Test
    public void testWhereSelectSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(
                        select(Tables.ARTICLE.ACCOUNT_ID).from(Tables.ARTICLE).where(Tables.ARTICLE.ID.ge(100))
                ));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` WHERE `id` >= (SELECT `account_id` FROM `tb_article` WHERE `id` >=  #{p0} )", sql);
        System.out.println(sql);
    }

    @Test
    public void testGroupSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .groupBy(Tables.ACCOUNT.USER_NAME);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` GROUP BY `user_name`", sql);
        System.out.println(sql);
    }

    @Test
    public void testHavingSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .groupBy(Tables.ACCOUNT.USER_NAME)
                .having(Tables.ACCOUNT.AGE.between(18, 25));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` GROUP BY `user_name` HAVING `age` BETWEEN  #{p100000} AND  #{p100001} ", sql);
        System.out.println(sql);
    }

    @Test
    public void testJoinSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .leftJoin(Tables.ARTICLE).on(Tables.ACCOUNT.ID.eq(Tables.ARTICLE.ACCOUNT_ID))
                .where(Tables.ACCOUNT.AGE.ge(10));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` LEFT JOIN `tb_article` ON `tb_account`.`id` = `tb_article`.`account_id` WHERE `tb_account`.`age` >=  #{p0} ", sql);
        System.out.println(sql);
    }

    @Test
    public void testOrderBySql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .orderBy(Tables.ACCOUNT.AGE.asc(), Tables.ACCOUNT.USER_NAME.desc().nullsLast());

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.buildSelectSql(queryWrapper);
        Assert.assertEquals("SELECT * FROM `tb_account` ORDER BY `age` ASC, `user_name` DESC NULLS LAST", sql);
        System.out.println(sql);
    }


    @Test
    public void testLimitOffset() {

        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .orderBy(Tables.ACCOUNT.ID.desc())
                .limit(10)
                .offset(20);

        IDialect dialect1 = new CommonsDialectImpl();
        String sql1 = dialect1.buildSelectSql(queryWrapper);
        System.out.println(sql1);
        Assert.assertEquals("SELECT * FROM `tb_account` ORDER BY `id` DESC LIMIT 20, 10", sql1);

        IDialect dialect2 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.ORACLE);
        String sql2 = dialect2.buildSelectSql(queryWrapper);
        System.out.println(sql2);
        Assert.assertEquals("SELECT * FROM (SELECT TEMP_DATAS.*, ROWNUM RN FROM (SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC) TEMP_DATAS WHERE  ROWNUM <=30) WHERE RN >20", sql2);

        IDialect dialect3 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DB2);
        String sql3 = dialect3.buildSelectSql(queryWrapper);
        System.out.println(sql3);
        Assert.assertEquals("SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC OFFSET 20  ROWS FETCH NEXT 10 ROWS ONLY", sql3);

        IDialect dialect4 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.POSTGRESQL);
        String sql4 = dialect4.buildSelectSql(queryWrapper);
        System.out.println(sql4);
        Assert.assertEquals("SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC LIMIT 20 OFFSET 10", sql4);

        IDialect dialect5 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.INFORMIX);
        String sql5 = dialect5.buildSelectSql(queryWrapper);
        System.out.println(sql5);
        Assert.assertEquals("SELECT SKIP 20 FIRST 10 * FROM \"tb_account\" ORDER BY \"id\" DESC", sql5);

        IDialect dialect6 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.SYBASE);
        String sql6 = dialect6.buildSelectSql(queryWrapper);
        System.out.println(sql6);
        Assert.assertEquals("SELECT TOP 10 START AT 21 * FROM \"tb_account\" ORDER BY \"id\" DESC", sql6);

        IDialect dialect7 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.FIREBIRD);
        String sql7 = dialect7.buildSelectSql(queryWrapper);
        System.out.println(sql7);
        Assert.assertEquals("SELECT * FROM \"tb_account\" ORDER BY \"id\" DESC ROWS 20 TO 30", sql7);
    }


    @Test
    public void testrSelectLimitSql() {
      boolean condition = true;
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select(Tables.ACCOUNT.ALL_COLUMNS)
                .select(Tables.ARTICLE.ID.as("article_id"))
                .select(distinct(Tables.ARTICLE.ID))
                .select(max(Tables.ACCOUNT.SEX))
                .select(count(distinct(Tables.ARTICLE.ID)))
                .from(Tables.ACCOUNT).as("a1")
//                .leftJoin(newWrapper().select().from(ARTICLE).where(Tables.ARTICLE.ID.ge(100))).as("aaa")
                .leftJoin(Tables.ARTICLE).as("b1")
                .on(Tables.ARTICLE.ACCOUNT_ID.eq(Tables.ACCOUNT.ID))
                .where(Tables.ACCOUNT.ID.ge(select(Tables.ARTICLE.ID).from(Tables.ARTICLE).as("cc").where(Tables.ARTICLE.ID.eq(111))))
                .and((condition ? noCondition() : Tables.ARTICLE.ID.ge(22211)).and(Tables.ACCOUNT.ID.eq(10011)).when(false))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"))
                .and(Tables.ARTICLE.ID.in(select(Tables.ARTICLE.ID).from("aaa")))
                .and(
                        notExist(
                                selectOne().from("aaa").where(Tables.ARTICLE.ID.ge(333))
                        )
                )
                .groupBy(Tables.ACCOUNT.ID).having(Tables.ARTICLE.ID.ge(0))
//                .and("bbb.id > ?",100)
                .orderBy(Tables.ACCOUNT.ID.desc())
                .limit(10, 10);

        String mysqlSql = new CommonsDialectImpl().buildSelectSql(queryWrapper);
        System.out.println(">>>>> mysql: \n" + mysqlSql);
        Assert.assertEquals("SELECT `a1`.`id`, `a1`.`user_name`, `a1`.`birthday`, `a1`.`sex`, `a1`.`age`, `a1`.`is_normal`, `a1`.`is_delete`, `b1`.`id` AS `article_id`,  DISTINCT `b1`.`id`, MAX(`a1`.`sex`), COUNT( DISTINCT `b1`.`id`) FROM `tb_account` AS `a1` LEFT JOIN `tb_article` AS `b1` ON `b1`.`account_id` = `a1`.`id` WHERE `a1`.`id` >= (SELECT `id` FROM `tb_article` AS `cc` WHERE `id` =  #{p0} ) AND `a1`.`user_name` LIKE  #{p0}  AND `b1`.`id` IN (SELECT `tb_article`.`id` FROM `aaa`) AND  NOT EXIST (SELECT 1 FROM `aaa` WHERE `tb_article`.`id` >=  #{p0} ) GROUP BY `a1`.`id` HAVING `b1`.`id` >=  #{p10000}  ORDER BY `a1`.`id` DESC LIMIT 10, 10", mysqlSql);
        System.out.println(">>>>> mysql: \n" + Arrays.toString(queryWrapper.getValueArray()));

//        String oracleSql = new OracleDialect().buildSelectSql(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> oracle: " + oracleSql);
//
//        String informixSql = new InformixDialect().buildSelectSql(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> informix: " + informixSql);
    }

}
