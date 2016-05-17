package com.cl.its.bean;

//package com.smartpay.its.framework.dao;

import java.io.Serializable;
import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cl.its.dao.BaseQuery;

import cn.org.rapid_framework.page.Page;

//import com.smartpay.its.framework.vo.query.BaseQuery;

/**
 * 实体数据访问对象接口
 * 
 * @author Angi WANG
 */
public interface IEntityDao<E, PK extends Serializable> {

	/**
	 * 插入实体
	 * 
	 * @param entity
	 *            需要插入的实体对象
	 * @return 返回插入之后的实体对象，已回写自增长或sequence之类数据库生成的值
	 * @throws DataAccessException
	 */
	public E save(E entity) throws DataAccessException;

	/**
	 * 根据主键删除实体
	 * 
	 * @param pk
	 *            主键，单主键或者复合主键
	 * @return
	 * @throws DataAccessException
	 */
	public int deleteByPk(PK pk) throws DataAccessException;

	/**
	 * 根据SQL和实体属性删除数据
	 * 
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public int deleteByEntity(String sql, E entity) throws DataAccessException;

	/**
	 * 根据实体属性删除数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
//	public int deleteByEntity(E entity) throws DataAccessException;

	/**
	 * 根据SQL和查询条件删除数据
	 * 
	 * @param namedSql
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public int deleteByQuery(String sql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据查询条件删除数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
//	public int deleteByQuery(BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据主键更新数据，支持单主键或者复合主键
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public int updateByPk(E entity) throws DataAccessException;

	/**
	 * 根据SQL和实体属性更新数据
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public int updateByEntity(String sql, E entity) throws DataAccessException;

	/**
	 * 根据实体属性更新数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
//	public int updateByEntity(E entity) throws DataAccessException;

	/**
	 * 根据带命名参数的SQL和查询条件更新数据
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public int updateByQuery(String sql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据查询条件更新数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
//	public int updateByQuery(BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据主键检查是否插入还是更新实体数据
	 * 
	 * @param entity
	 * @throws DataAccessException
	 */
	public void saveOrUpdateByPk(E entity) throws DataAccessException;

	/**
	 * 根据主键查询实体数据
	 * 
	 * @param id
	 * @return
	 * @throws DataAccessException
	 */
	public E getByPk(PK id) throws DataAccessException;

	/**
	 * 根据SQL和实体属性查询一个实体数据
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 */
	public E getByEntity(String sql, E entity) throws DataAccessException;

	/**
	 * 根据实体属性查询一个实体数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 */
//	public E getByEntity(E entity) throws DataAccessException;

	/**
	 * 根据SQL和查询条件获得一个实体数据
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 */
	public E getByQuery(String sql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据查询条件获得一个实体数据，不包含主键属性（动态SQL实现）
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 */
//	public E getByQuery(BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据SQL和实体属性查询全部实体数据
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public List<E> findByEntity(String sql, E entity) throws DataAccessException;

	/**
	 * 根据实体属性查询全部实体数据（动态SQL实现）
	 * 
	 * @return
	 * @throws DataAccessException
	 */
//	public List<E> findByEntity(E entity) throws DataAccessException;

	/**
	 * 根据SQL和实体属性查询全部实体数据
	 * 
	 * @param namedSql
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public List<E> findByQuery(String sql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据查询条件检索全部实体数据（动态SQL实现）
	 * 
	 * @return
	 * @throws DataAccessException
	 */
//	public List<E> findByQuery(BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据SQL和查询条件进行分页检索
	 * 
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public Page<E> findPageByNamedQuery(String namedSql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据查询条件进行分页检索（动态SQL实现）
	 * 
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
//	public Page<E> findPageByQuery(BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据SQL（不可排序）和查询条件求取记录总数
	 * 
	 * @param sql
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
	public int countByEntity(String sql, E entity) throws DataAccessException;

	/**
	 * 根据SQL（不可排序）和查询条件求取记录总数（动态SQL实现）
	 * 
	 * @param sql
	 * @param entity
	 * @return
	 * @throws DataAccessException
	 */
//	public int countByEntity(E entity) throws DataAccessException;

	/**
	 * 根据SQL（不可排序）和查询条件求取记录总数
	 * 
	 * @param sql
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
	public int countByQuery(String sql, BaseQuery<E> query) throws DataAccessException;

	/**
	 * 根据SQL（不可排序）和查询条件求取记录总数（动态SQL实现）
	 * 
	 * @param sql
	 * @param query
	 * @return
	 * @throws DataAccessException
	 */
//	public int countByQuery(BaseQuery<E> query) throws DataAccessException;

}
