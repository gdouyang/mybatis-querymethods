package mybatis.join;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

public interface JoinMapper<T> {
	/**
     * 根据 query 来构建条件查询数据列表
     *
     * @param queryWrapper 查询条件
     * @return 数据列表
     * @see mybatis.join.JoinSqlProvider#selectListByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = JoinSqlProvider.class, method = "selectListByQuery")
    List<T> selectListByQuery(@Param(JoinSqlProvider.QUERY) JoinQueryWrapper joinQueryWrapper);
    
    /**
     * 根据 queryWrapper 来查询数据量
     *
     * @param queryWrapper
     * @return 数据量
     * @see mybatis.join.JoinSqlProvider#selectCountByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = JoinSqlProvider.class, method = "selectCountByQuery")
    long selectCountByQuery(@Param(JoinSqlProvider.QUERY) JoinQueryWrapper joinQueryWrapper);
}
