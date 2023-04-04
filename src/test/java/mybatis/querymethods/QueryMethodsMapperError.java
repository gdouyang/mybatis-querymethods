package mybatis.querymethods;

import org.apache.ibatis.annotations.Select;

public interface QueryMethodsMapperError {

  @Select("")
  Integer countById1(Integer id);

}
