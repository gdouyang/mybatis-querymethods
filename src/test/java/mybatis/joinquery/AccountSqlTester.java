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

import org.junit.Test;

import mybatis.joinquery.JoinQueryWrapper;
import mybatis.joinquery.dialect.CommonsDialectImpl;
import mybatis.joinquery.dialect.IDialect;
import mybatis.joinquery.dialect.KeywordWrap;
import mybatis.joinquery.dialect.LimitOffsetProcesser;

public class AccountSqlTester {


    @Test
    public void testSelectSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select()
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelectColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME)
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelect1ColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME,
                        Tables.ARTICLE.ID.as("articleId"), Tables.ARTICLE.TITLE)
                .from(Tables.ACCOUNT.as("a"), Tables.ARTICLE.as("b"))
                .where(Tables.ACCOUNT.ID.eq(Tables.ARTICLE.ACCOUNT_ID));

        IDialect dialect = new CommonsDialectImpl(KeywordWrap.NONE, LimitOffsetProcesser.MYSQL);
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }

    @Test
    public void testSelectColumnsAndFunctionsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME, max(Tables.ACCOUNT.BIRTHDAY), avg(Tables.ACCOUNT.SEX).as("sex_avg"))
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }


    @Test
    public void testSelectAllColumnsSql() {
        JoinQueryWrapper query = new JoinQueryWrapper()
                .select(Tables.ACCOUNT.ALL_COLUMNS)
                .from(Tables.ACCOUNT);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(query);
        System.out.println(sql);
    }


    @Test
    public void testSelectCountSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectCountByQuery(queryWrapper);
        System.out.println(sql);
    }


    @Test
    public void testWhereSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .where(Tables.ACCOUNT.ID.ge(100))
                .and(Tables.ACCOUNT.USER_NAME.like("michael"));

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
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
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);

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
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);

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
        String sql = dialect.forSelectListByQuery(queryWrapper);
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
        String sql = dialect.forSelectListByQuery(queryWrapper);
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
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testGroupSql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .groupBy(Tables.ACCOUNT.USER_NAME);

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
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
        String sql = dialect.forSelectListByQuery(queryWrapper);
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
        String sql = dialect.forSelectListByQuery(queryWrapper);
        System.out.println(sql);
    }

    @Test
    public void testOrderBySql() {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
                .select()
                .from(Tables.ACCOUNT)
                .orderBy(Tables.ACCOUNT.AGE.asc(), Tables.ACCOUNT.USER_NAME.desc().nullsLast());

        IDialect dialect = new CommonsDialectImpl();
        String sql = dialect.forSelectListByQuery(queryWrapper);
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

        IDialect dialect2 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.ORACLE);
        String sql2 = dialect2.buildSelectSql(queryWrapper);
        System.out.println(sql2);

        IDialect dialect3 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.DB2);
        String sql3 = dialect3.buildSelectSql(queryWrapper);
        System.out.println(sql3);

        IDialect dialect4 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.POSTGRESQL);
        String sql4 = dialect4.buildSelectSql(queryWrapper);
        System.out.println(sql4);

        IDialect dialect5 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.INFORMIX);
        String sql5 = dialect5.buildSelectSql(queryWrapper);
        System.out.println(sql5);

        IDialect dialect6 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.SYBASE);
        String sql6 = dialect6.buildSelectSql(queryWrapper);
        System.out.println(sql6);


        IDialect dialect7 = new CommonsDialectImpl(KeywordWrap.DOUBLE_QUOTATION, LimitOffsetProcesser.FIREBIRD);
        String sql7 = dialect7.buildSelectSql(queryWrapper);
        System.out.println(sql7);
    }


    @Test
    public void testrSelectLimitSql() {
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
                .and((true ? noCondition() : Tables.ARTICLE.ID.ge(22211)).and(Tables.ACCOUNT.ID.eq(10011)).when(false))
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

        String mysqlSql = new CommonsDialectImpl().forSelectListByQuery(queryWrapper);
        System.out.println(">>>>> mysql: \n" + mysqlSql);
        System.out.println(">>>>> mysql: \n" + Arrays.toString(queryWrapper.getValueArray()));

//        String oracleSql = new OracleDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> oracle: " + oracleSql);
//
//        String informixSql = new InformixDialect().forSelectListByQuery(CPI.getQueryTable(queryWrapper).getName(), queryWrapper);
//        System.out.println(">>>>> informix: " + informixSql);
    }

}
