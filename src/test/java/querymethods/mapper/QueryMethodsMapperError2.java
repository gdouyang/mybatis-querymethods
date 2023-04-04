package querymethods.mapper;

import org.apache.ibatis.annotations.Select;

public interface QueryMethodsMapperError2 {

  @Select("")
  Integer findId1ById(Integer id);

}
