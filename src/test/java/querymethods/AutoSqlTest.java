package querymethods;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

/**
 * 
 * @author OYGD
 *
 */
public class AutoSqlTest extends BaseTest {
	@Test
	public void test() {
		SqlSession session = sqlSessionFactory.openSession();

		try {
			BlogMapper mapper = session.getMapper(BlogMapper.class);

			Integer id = 1;
			mapper.deleteByPrimaryKey(id);
			
			Blog b = new Blog();
			b.setId(id);
			b.setFirstName("OY");
			b.setLastName("GD");
			mapper.insert1(b);

			Blog blog = mapper.findByIdOrFirstName(id, "OY1");
			assert blog != null;

			Integer countById = mapper.countById(id);
			assert countById != null;
			
			blog = mapper.findByIdAndFirstName(b.getId(), b.getFirstName());
			assert blog != null; 
			
		} finally {
			session.commit();
			session.close();
		}
	}

}
