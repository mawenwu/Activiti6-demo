package com.base;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.commons.PageSupport;

@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的分页对象
	 * 
	 * @param queryString
	 *            jql查询语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @param start
	 *            获取的结果集起始位置
	 * @param maxRows
	 *            获取的最大结果集总数
	 * @return 已分好页的分页结果对象
	 */
	public <X> PageSupport<X> findPageSupportByJQL(String hql, Map<String, ?> params, final int start,
			final int maxRows);

	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的分页对象
	 * 
	 * @param cls
	 *            实体类
	 * @param cons_index
	 *            构造函数索引
	 * @param sql
	 *            sql查询语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @param start
	 *            获取的结果集起始位置
	 * @param maxRows
	 *            获取的最大结果集总数
	 * @return 已分好页的分页结果对象
	 */
	public <X> PageSupport<X> findPageSupportBySQL(Class cls, int cons_index, String sql, Map<String, ?> params,
			final int start, final int maxRows);

	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的分页对象
	 * 
	 * @param queryString
	 *            jql查询语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @param start
	 *            获取的结果集起始位置
	 * @param maxRows
	 *            获取的最大结果集总数
	 * @return 已分好页的分页结果对象
	 */
	public <X> PageSupport<X> findPageSupportByJQLGroupBy(String jql, Map<String, ?> params, final int start,
			final int maxRows);

	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回唯一满足条件的实体对象
	 * <p>
	 * [注]:如果查询结果集中有多个实体满足条件,则抛出运行期异常.
	 * 
	 * @param params
	 *            查询条件字段-值MAP
	 * 
	 * @return 成功找到的唯一结果对象
	 */
	public <X> X findObjectByJql(String queryJQL, final Map<String, ?> params);

	/**
	 *  
	 * 根据给定字段过滤条件进行查询,返回唯一满足条件的实体对象
	 * <p>
	 * [注]:如果查询结果集中有多个实体满足条件,则抛出运行期异常.
	 * 
	 * @param params
	 *            查询条件字段-值MAP
	 * 
	 * @return 成功找到的唯一结果对象
	 */
	public <X> X findObjectBySql(String querySQL, final Map<String, ?> params);

	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回所有满足条件的实体集合
	 * 
	 * @param queryJQL
	 *            查询字符串
	 * @param params
	 *            查询条件字段-值MAP
	 * @return 查询结果
	 */
	public <X> List<X> findListByJQL(String queryJQL, final Map<String, ?> params);

	/**
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的List
	 * 
	 * @Title: findListBySql
	 * @param cls
	 *            class
	 * @param cons_index
	 *            构造函数索引
	 * @param sql
	 *            sql语句
	 * @param params
	 *            参数
	 * @return List<?> 返回类型
	 */
	public <X> List<X> findListBySql(Class cls, int cons_index, String sql, Map<String, ?> params);

	/**
	 * 批量更新数据
	 * 
	 * @param updateJQL
	 *            更新语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @return 查询结果
	 */
	public void bathUpdateByJql(final String updateJQL, final Map<String, ?> params);

	/**
	 * 从数据库里批量删除实体对象
	 * 
	 * @param objectsToRemove
	 *            待删除的对象集合
	 * @throws DataAccessException
	 *             当删除数据库中相应记录发生异常时抛出
	 */
	public void batchRemove(final List<?> objectsToRemove) throws DataAccessException;
}
