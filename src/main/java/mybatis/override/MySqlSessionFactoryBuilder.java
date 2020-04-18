package mybatis.override;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.ibatis.exceptions.ExceptionFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class MySqlSessionFactoryBuilder extends SqlSessionFactoryBuilder {

	@Override
	public SqlSessionFactory build(InputStream inputStream, String environment, Properties properties) {
		try {
			MyXMLConfigBuilder parser = new MyXMLConfigBuilder(inputStream, environment, properties);
			return build(parser.parse());
		} catch (Exception e) {
			throw ExceptionFactory.wrapException("Error building SqlSession.", e);
		} finally {
			ErrorContext.instance().reset();
			try {
				inputStream.close();
			} catch (IOException e) {
				// Intentionally ignore. Prefer previous error.
			}
		}
	}
}
