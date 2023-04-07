package mybatis.joinquery;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Before;
import org.junit.Test;

import mybatis.joinquery.JoinQueryWrapper;
import mybatis.joinquery.dialect.CommonsDialectImpl;
import mybatis.joinquery.dialect.DbType;
import mybatis.joinquery.dialect.DialectFactory;
import mybatis.joinquery.dialect.IDialect;

public class BaseTest {

	public BaseTest() {
	}

	SqlSessionFactory sqlSessionFactory = null;

	@Before
	public void before() {

		String resource = "joinquery/mybatis-config.xml";
		try {
			InputStream inputStream = Resources.getResourceAsStream(resource);
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
			Collection<MappedStatement> mappedStatements = sqlSessionFactory.getConfiguration().getMappedStatements();
			System.out.println(mappedStatements.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void test() {
		SqlSession session = sqlSessionFactory.openSession();

		try {
			DialectFactory.setHintDbType(DbType.H2);
			session.getConnection().prepareStatement(
					"create table if not exists tb_account (id bigint primary key not null, user_name varchar(32),"
					+ " birthday datetime, sex int, age int, is_normal int, is_delete int);").execute();
			session.getConnection().prepareStatement("insert into tb_account(id, user_name, sex, age, is_normal, is_delete) values("
					+ "1, '长得', 1, 12, 1, 0)").execute();
			
			session.getConnection().prepareStatement(
					"create table if not exists tb_article (id bigint primary key not null, uuid varchar(32), "
					+ "title text, content text, created datetime, modified datetime, account_id int, "
					+ "version int, is_delete int);").execute();
			session.getConnection().prepareStatement("insert into tb_article(id, uuid, title, content, version, is_delete) values("
					+ "1, '1212', '标题', '文章内容', 1, 0)").execute();
			
			AccountMapper mapper = session.getMapper(AccountMapper.class);
			
			JoinQueryWrapper queryWrapper = JoinQueryWrapper.create()
					.select(Tables.ACCOUNT.ID, Tables.ACCOUNT.USER_NAME.as("userName"), Tables.ACCOUNT.AGE, Tables.ACCOUNT.SEX)
					.from(Tables.ACCOUNT)
					.leftJoin(Tables.ARTICLE)
					.on(Tables.ACCOUNT.ID.eq(Tables.ARTICLE.ACCOUNT_ID))
					.where(Tables.ACCOUNT.AGE.in(10, 11, 12));
			IDialect dialect = new CommonsDialectImpl();
			String sql = dialect.forSelectListByQuery(queryWrapper);
			System.out.println(sql);
			List<Account> selectListByQuery = mapper.selectListByQuery(queryWrapper);
			System.out.println(selectListByQuery);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				session.getConnection().prepareStatement("drop table tb_account;").execute();
				session.getConnection().prepareStatement("drop table tb_article;").execute();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			session.commit();
			session.close();
		}
	}

}
