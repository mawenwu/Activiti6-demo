package com.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.util.CollectionUtils;

import com.commons.PageSupport;
import com.commons.SQLUtils;

public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T,ID>
        implements BaseRepository<T,ID> {

    private final EntityManager entityManager;
    protected int batchSize;
   
	public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
        this.batchSize = 10;
    }
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
	 * @param maxRow
	 *            获取的最大结果集总数
	 * @return 已分好页的分页结果对象
	 */
	@SuppressWarnings({ "unchecked" })
	@Override
	public <X> PageSupport<X> findPageSupportByJQL(String queryString, Map<String, ?> params, int start, int maxRows) {
		int tmpStart = start >= 0 ? start : 0;
		int tmpMaxRows = maxRows >= 0 ? maxRows : 0;
		String countJql = null; 
		// 查询记录总数 
		countJql = SQLUtils.buildCountSQL(queryString);
		Long count = (Long) findObjectByJql(countJql, params);
		
		if (count == null || count <= 0) {
			return new PageSupport<X>(new ArrayList<X>(0), 0, tmpMaxRows,
					tmpStart);
		}
		List<X> objectList = createQueryWithNameParam(queryString, params)
				.setFirstResult(tmpStart).setMaxResults(tmpMaxRows)
				.getResultList();
		
		int pageNo = tmpStart / tmpMaxRows;
		int pageSize = tmpMaxRows;
		PageRequest pageRequest = new PageRequest(pageNo, pageSize, null);
		
		Page<X> page = new PageImpl<X>(objectList,pageRequest,count);
		return new PageSupport<X>(page.getContent(), page.getTotalElements());
	}
	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的分页对象
	 * @param cls 
	 *            实体类
	 * @param  cons_index
	 *            构造函数索引
	 * @param queryString
	 *            sql查询语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @param start
	 *            获取的结果集起始位置
	 * @param maxRows
	 *            获取的最大结果集总数
	 * @return 已分好页的分页结果对象
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <X> PageSupport<X> findPageSupportBySQL(Class cls,int cons_index,String sql, Map<String, ?> params, int start, int maxRows) {
		int tmpMaxRows = maxRows >= 0 ? maxRows : 0;
		int tmpStart = start >= 0 ? start : 0;

		// 查询记录总数
		StringBuffer countSql = new StringBuffer();
		countSql.append("select count(*) from (").append(sql).append(")");
		Long count = Long.valueOf(String.valueOf(findObjectBySql(
				countSql.toString(), params)));

		if (count == null || count <= 0) {
			return new PageSupport<X>(new ArrayList<X>(0), 0, tmpMaxRows,
					tmpStart);
		}
		List objectList = createNativeQueryWithNameParam(sql, params)
				.setFirstResult(tmpStart).setMaxResults(tmpMaxRows)
				.getResultList();
		try {
			for (int i = 0; i < objectList.size(); i++) {
				objectList.set(i,newInstance(cls.getName(),(Object[]) objectList.get(i),cons_index));
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new PageSupport<X>(null, count.intValue(), tmpMaxRows, tmpStart);
		}
		
		int pageNo = tmpStart / tmpMaxRows;
		int pageSize = tmpMaxRows;
		PageRequest pageRequest = new PageRequest(pageNo, pageSize, null);
		
		Page<X> page = new PageImpl<X>(objectList,pageRequest,count);
		return new PageSupport<X>(page.getContent(), page.getTotalElements());
	}
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
	@SuppressWarnings("unchecked")
	@Override
	public <X> PageSupport<X> findPageSupportByJQLGroupBy(String jql, Map<String, ?> params, int start, int maxRows) {
		int tmpMaxRows = maxRows >= 0 ? maxRows : 0;
		int tmpStart = start >= 0 ? start : 0;
		// 查询记录总数 
		List<Object[]> ObList = null ;
		ObList = findListByJQL(jql, params);
		int count = ObList.size();

		if (count == 0) {
			return new PageSupport<X>(new ArrayList<X>(0), 0, tmpMaxRows, tmpStart);
		}
		List<X> objectList = createQueryWithNameParam(jql, params)
				.setFirstResult(tmpStart).setMaxResults(tmpMaxRows)
				.getResultList();

		int pageNo = tmpStart / tmpMaxRows;
		int pageSize = tmpMaxRows;
		PageRequest pageRequest = new PageRequest(pageNo, pageSize, null);
		
		Page<X> page = new PageImpl<X>(objectList,pageRequest,count);
		return new PageSupport<X>(page.getContent(), page.getTotalElements());
	}
	
	/**
	 * 
	 * 根据给定字段过滤条件进行查询,返回唯一满足条件的实体对象
	 * <p>
	 * [注]:如果查询结果集中有多个实体满足条件,则抛出运行期异常.
	 * @param params
	 *            查询条件字段-值MAP
	 * 
	 * @return 成功找到的唯一结果对象
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <X> X findObjectByJql(String queryJQL, final Map<String, ?> params) {
		Query query = this.createQueryWithNameParam(queryJQL, params);
		return (X) query.getSingleResult();
	}
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
	@SuppressWarnings("unchecked")
	@Override
	public <X> X findObjectBySql(String querySQL, final Map<String, ?> params) {

		Query query = this.createNativeQueryWithNameParam(querySQL, params);

		return (X) query.getSingleResult();
	}
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
	@SuppressWarnings("unchecked")
	@Override
	public <X> List<X> findListByJQL(String queryJQL,
			final Map<String, ?> params) {

		Query query = this.createQueryWithNameParam(queryJQL, params);

		return (List<X>) query.getResultList();
	}
	/**
	 * 根据给定字段过滤条件进行查询,返回给定范围内满足条件的List
	 * @Title: findListBySql
	 * @param cls 
	 *        class
	 * @param cons_index 
	 *        构造函数索引
	 * @param sql
	 *        sql语句
	 * @param params
	 *        参数
	 * @return List<?> 
	 *        返回类型
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <X> List<X> findListBySql(Class cls,int cons_index, String sql,
			Map<String, ?> params) {
		List objectList = createNativeQueryWithNameParam(sql, params)
				.getResultList();
		try {
			for (int i = 0; i < objectList.size(); i++) {
				objectList.set(i,newInstance(cls.getName(),(Object[]) objectList.get(i),cons_index));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return objectList;
	}
	/**
	 * 
	 * 从数据库里批量删除实体对象
	 * 
	 * @param objectsToRemove
	 *            待删除的对象集合
	 * @throws DataAccessException
	 *             当删除数据库中相应记录发生异常时抛出
	 */
	@Override
	public void batchRemove(final List<?> objectsToRemove)
			throws DataAccessException {
		if (CollectionUtils.isEmpty(objectsToRemove)) {
			return;
		}
		int max = objectsToRemove.size();
		for (int i = 0; i < max; i++) {
			entityManager.remove(objectsToRemove.get(i));
			if ((i != 0 && i % batchSize == 0) || i == max - 1) {
				entityManager.flush();
				entityManager.clear();
			}
		}
	}
	/**
	 * 
	 * 批量更新数据
	 * @param updateJQL
	 *            更新语句
	 * @param params
	 *            查询条件字段-值MAP
	 * @return 查询结果
	 */
	@Override
	public void bathUpdateByJql(final String updateJQL,
			final Map<String, ?> params) {
		Query query = this.createQueryWithNameParam(updateJQL, params);
		query.executeUpdate();
	}
	/**
	 * 根据查询JQL语句与命名参数列表创建Query对象，JQL中参数按名称绑定
	 * @param values
	 *            参数Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Query createQueryWithNameParam(final String queryJQL,
			final Map<String, ?> values) {
		Query query = entityManager.createQuery(queryJQL);
		if (values != null) {
			Iterator it = values.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
						.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query;
	}
	/**
	 * 根据查询SQL语句与命名参数列表创建Query对象，SQL中参数按名称绑定
	 * 
	 * @param values
	 *            参数Map
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Query createNativeQueryWithNameParam(final String querySQL,
			final Map<String, ?> values) {
		Query query = entityManager.createNativeQuery(querySQL);
		if (values != null) {
			Iterator it = values.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) it
						.next();
				query.setParameter(entry.getKey(), entry.getValue());
			}
		}
		return query;
	}
	/**
	 * 反射实体类
	 * @param className
	 * @param args
	 * @param constructor_index
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	private Object newInstance(String className, Object[] args, int constructor_index) throws Exception {
		Class newoneClass = Class.forName(className);
		java.lang.reflect.Constructor cons =newoneClass.getConstructors()[constructor_index];
		Object[] params = new Object[cons.getParameterTypes().length];
		for(int i=0;i<params.length;i++){
			params[i]=args[i];
		}
		return cons.newInstance(params);
	}
}
