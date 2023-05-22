package mybatis.joinquery;

import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Before;
import org.junit.Test;
import mybatis.Init;
import mybatis.joinquery.util.JoinQueryUtil;

public class JoinQueryTest {

  SqlSessionFactory sqlSessionFactory = null;

  Init init = new Init();

  @Before
  public void before() {
    String resource = "joinquery/mybatis-config.xml";
    init.setup(resource);
    sqlSessionFactory = init.sqlSessionFactory;
  }

  @Test
  public void test() {
    SqlSession session = sqlSessionFactory.openSession();

    try {
      AccountMapper mapper = session.getMapper(AccountMapper.class);
      {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
            .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME.as("userName"), Tables.ACCOUNT.AGE,
                Tables.ACCOUNT.SEX)
            .from(Tables.ACCOUNT).leftJoin(Tables.ARTICLE)
            .on(Tables.ACCOUNT.ID.eq(Tables.ARTICLE.ACCOUNT_ID))
            .where(Tables.ACCOUNT.AGE.in(10, 11, 12)).having(Tables.ACCOUNT.AGE.between(18, 25))
            .where(Tables.ACCOUNT.AGE.in(10, 11, 12));
        
        List<Account> selectListByQuery = mapper.selectListByJoinQuery(queryWrapper);
        System.out.println("result: " + selectListByQuery);
      }
      
      {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
            .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME.as("userName"), Tables.ACCOUNT.AGE,
                Tables.ACCOUNT.SEX)
            .from(Tables.ACCOUNT);
        List<Account> selectListByQuery = mapper.selectListByJoinQuery(queryWrapper);
//        List<Account> selectListByQuery = JoinQueryUtil.queryList(session, query1, Account.class);
        System.out.println("result1: " + selectListByQuery);
      }
      {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
            .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME.as("userName"), Tables.ACCOUNT.AGE,
                Tables.ACCOUNT.SEX)
            .from(Tables.ACCOUNT)
            .where(Tables.ACCOUNT.USER_NAME.like("长"));
        List<Account> selectListByQuery = mapper.selectListByJoinQuery(queryWrapper);
        System.out.println("result1: " + selectListByQuery);
      }
      {
        JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
            .select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME.as("userName"), Tables.ACCOUNT.AGE,
                Tables.ACCOUNT.SEX)
            .from(Tables.ACCOUNT);
        int count = JoinQueryUtil.count(session, queryWrapper);
        System.out.println("count: " + count);
      }
    } finally {
      session.close();
    }
  }

}
