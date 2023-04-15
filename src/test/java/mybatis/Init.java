package mybatis;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class Init {

  public SqlSessionFactory sqlSessionFactory = null;
  
  public void setup(String resource) {
    try {
        InputStream inputStream = Resources.getResourceAsStream(resource);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        

        SqlSession session = sqlSessionFactory.openSession();

        try {
            session.getConnection().prepareStatement(
                    "create table if not exists tb_account (id bigint primary key not null, user_name varchar(32),"
                    + " birthday datetime, sex int, age int, is_normal int, is_delete int);").execute();
            session.getConnection().prepareStatement("delete from tb_account;").execute();
            session.getConnection().prepareStatement("insert into tb_account(id, user_name, sex, age, is_normal, is_delete) values("
                    + "1, '长得', 1, 12, 1, 0)").execute();
            
            session.getConnection().prepareStatement(
                    "create table if not exists tb_article (id bigint primary key not null, uuid varchar(32), "
                    + "title text, content text, created datetime, modified datetime, account_id int, "
                    + "version int, is_delete int);").execute();
            session.getConnection().prepareStatement("delete from tb_article;").execute();
            session.getConnection().prepareStatement("insert into tb_article(id, uuid, title, content, version, is_delete) values("
                    + "1, '1212', '标题', '文章内容', 1, 0)").execute();
            
            session.getConnection().prepareStatement(
                "create table if not exists customer (id bigint primary key not null,"
                + " first_name varchar(255),"
                + " last_name varchar(255),"
                + " active tinyint)").execute();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            session.commit();
            session.close();
        }
    
    } catch (IOException e) {
        e.printStackTrace();
    }
  }
}
