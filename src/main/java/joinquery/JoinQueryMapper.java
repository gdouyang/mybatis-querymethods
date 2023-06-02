package joinquery;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.builder.annotation.ProviderContext;

public interface JoinQueryMapper<T> {
	/**
     * 根据 query 来构建条件查询数据列表
     *
     * @param queryWrapper 查询条件
     * @return 数据列表
     * @see joinquery.JoinQuerySqlProvider#selectListByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = JoinQuerySqlProvider.class, method = "selectListByQuery")
    List<T> selectListByJoinQuery(@Param(JoinQuerySqlProvider.QUERY) JoinQueryWrapper joinQueryWrapper);
    
    /**
     * 根据 queryWrapper 来查询数据量
     *
     * @param queryWrapper
     * @return 数据量
     * @see joinquery.JoinQuerySqlProvider#selectCountByQuery(Map, ProviderContext)
     */
    @SelectProvider(type = JoinQuerySqlProvider.class, method = "selectCountByQuery")
    long selectCountByJoinQuery(@Param(JoinQuerySqlProvider.QUERY) JoinQueryWrapper joinQueryWrapper);
}
