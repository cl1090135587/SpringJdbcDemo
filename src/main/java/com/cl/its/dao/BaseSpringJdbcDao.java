package com.cl.its.dao;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javacommon.xsqlbuilder.SafeSqlProcesserFactory;
import javacommon.xsqlbuilder.XsqlBuilder;
import javacommon.xsqlbuilder.XsqlBuilder.XsqlFilterResult;
import javacommon.xsqlbuilder.safesql.DirectReturnSafeSqlProcesser;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.incrementer.AbstractSequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.DB2SequenceMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.OracleSequenceMaxValueIncrementer;

import com.cl.its.bean.IEntityDao;

import cn.org.rapid_framework.beanutils.PropertyUtils;
import cn.org.rapid_framework.jdbc.dialect.Dialect;
import cn.org.rapid_framework.jdbc.sqlgenerator.CacheSqlGenerator;
import cn.org.rapid_framework.jdbc.sqlgenerator.SpringNamedSqlGenerator;
import cn.org.rapid_framework.jdbc.sqlgenerator.SqlGenerator;
import cn.org.rapid_framework.jdbc.sqlgenerator.metadata.Column;
import cn.org.rapid_framework.jdbc.sqlgenerator.metadata.MetadataCreateUtils;
import cn.org.rapid_framework.jdbc.sqlgenerator.metadata.Table;
import cn.org.rapid_framework.jdbc.support.OffsetLimitResultSetExtractor;
import cn.org.rapid_framework.page.Page;
import cn.org.rapid_framework.util.CollectionHelper;
import cn.org.rapid_framework.util.ObjectUtils;
import cn.org.rapid_framework.util.SqlRemoveUtils;

//import com.smartpay.its.framework.dao.IEntityDao;
//import com.smartpay.its.framework.dao.ItsBeanPropertyRowMapper;
//import com.smartpay.its.framework.vo.query.BaseQuery;

/**
 * Spring JDBC数据访问对象基类
 * 
 * @author badqiu
 * 
 */
public abstract class BaseSpringJdbcDao<E, PK extends Serializable> extends NamedParameterJdbcDaoSupport implements
		IEntityDao<E, PK> {

	// protected final Log log = LogFactory.getLog(getClass());

	static final String LIMIT_PLACEHOLDER = ":__limit";
	static final String OFFSET_PLACEHOLDER = ":__offset";

	// 根据table对象可以创建生成增删改查的sql的工具,在线参考:
	// http://code.google.com/p/rapid-framework/wiki/rapid_sqlgenerator
	Table table = MetadataCreateUtils.createTable(getEntityClass());
	SqlGenerator sqlGenerator = new CacheSqlGenerator(new SpringNamedSqlGenerator(table));

	public abstract Class<E> getEntityClass();

	// 用于分页的dialect,在线参考:
	// http://code.google.com/p/rapid-framework/wiki/rapid_dialect
	private Dialect dialect;

	public void setDialect(Dialect dialect) {
		this.dialect = dialect;
	}

	protected void checkDaoConfig() {
		super.checkDaoConfig();
		if (dialect == null)
			throw new IllegalStateException("'dialect' property must be not null");
		// log.info("use jdbc dialect:" + dialect);
		System.out.println("use jdbc dialect:" + dialect);
	}

	// insert with start
	/**
	 * 适用sqlserver,mysql 自动生成主键
	 */
	protected void insertWithGeneratedKey(E entity, String insertSql) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity), keyHolder);
		setIdentifierProperty(entity, keyHolder.getKey().longValue());
	}

	protected void insertWithIdentity(E entity, String insertSql) {
		insertWithGeneratedKey(entity, insertSql);
	}

	protected void insertWithAutoIncrement(E entity, String insertSql) {
		insertWithIdentity(entity, insertSql);
	}

	protected void insertWithSequence(E entity, AbstractSequenceMaxValueIncrementer sequenceIncrementer,
			String insertSql) {
		Long id = sequenceIncrementer.nextLongValue();
		setIdentifierProperty(entity, id);
		getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}

	protected void insertWithDB2Sequence(E entity, String sequenceName, String insertSql) {
		insertWithSequence(entity, new DB2SequenceMaxValueIncrementer(getDataSource(), sequenceName), insertSql);
	}

	protected void insertWithOracleSequence(E entity, String sequenceName, String insertSql) {
		insertWithSequence(entity, new OracleSequenceMaxValueIncrementer(getDataSource(), sequenceName), insertSql);
	}

	protected void insertWithUUID(E entity, String insertSql) {
		String uuid = UUID.randomUUID().toString().replace("-", "");
		setIdentifierProperty(entity, uuid);
		getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}

	/**
	 * 手工分配ID插入
	 * 
	 * @param entity
	 * @param insertSql
	 */
	protected void insertWithAssigned(E entity, String insertSql) {
		getNamedParameterJdbcTemplate().update(insertSql, new BeanPropertySqlParameterSource(entity));
	}

	private void setIdentifierProperty(E entity, Object id) {
		try {
			BeanUtils.setProperty(entity, getIdentifierPropertyName(), id);
		} catch (Exception e) {
			throw new IllegalStateException("cannot set property value:" + id + " on entityClass:" + entity.getClass()
					+ " by propertyName:" + getIdentifierPropertyName(), e);
		}
	}

	// insert with end

	public int deleteByPk(PK pk) {
		if (getSqlGenerator().getTable().getPrimaryKeyCount() > 1) {
			return getNamedParameterJdbcTemplate().update(getSqlGenerator().getDeleteByPkSql(),
					new BeanPropertySqlParameterSource(pk));
		} else if (getSqlGenerator().getTable().getPrimaryKeyCount() == 1) {
			return getJdbcTemplate().update(getSqlGenerator().getDeleteByPkSql(), pk);
		} else {
			throw new IllegalStateException("not found primary key on table:"
					+ getSqlGenerator().getTable().getTableName());
		}
	}

	/**
	 * 根据带命名参数的SQL和实体属性删除数据
	 * <p>
	 * 
	 * Example:<br>
	 * delete from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public int deleteByEntity(String namedSql, E entity) {
		return getNamedParameterJdbcTemplate().update(namedSql, new BeanPropertySqlParameterSource(entity));
	}

	/**
	 * 根据带命名参数的SQL和查询条件删除数据
	 * 
	 * Example:<br>
	 * delete from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public int deleteByQuery(String namedSql, BaseQuery<E> query) {
		return getNamedParameterJdbcTemplate().update(namedSql, new BeanPropertySqlParameterSource(query));
	}

	public int updateByPk(E entity) {
		return getNamedParameterJdbcTemplate().update(getSqlGenerator().getUpdateByPkSql(),
				new BeanPropertySqlParameterSource(entity));
	}

	/**
	 * 根据带命名参数的SQL和实体属性更新数据
	 * 
	 * Example:<br>
	 * update mb_mcht_info t set t.mcht_address = :mchtAddress where t.mcht_name
	 * = :mchtName
	 */
	public int updateByEntity(String namedSql, E entity) {
		return getNamedParameterJdbcTemplate().update(namedSql, new BeanPropertySqlParameterSource(entity));
	}

	/**
	 * 根据带命名参数的SQL和查询条件更新数据
	 * 
	 * Example:<br>
	 * update mb_mcht_info t set t.mcht_address = :mchtAddress where t.mcht_name
	 * = :mchtName
	 */
	public int updateByQuery(String namedSql, BaseQuery<E> query) {
		return getNamedParameterJdbcTemplate().update(namedSql, new BeanPropertySqlParameterSource(query));
	}

	public void saveOrUpdateByPk(E entity) {
		Object id = getIdentifierPropertyValue(entity);
		if (ObjectUtils.isNullOrEmptyString(id)) {
			save(entity);
		} else {
			updateByPk(entity);
		}
	}

	@SuppressWarnings("unchecked")
	public E getByPk(PK pk) {
		List<E> list = null;
		if (getSqlGenerator().getTable().getPrimaryKeyCount() > 1) {
			list = getNamedParameterJdbcTemplate().query(getSqlGenerator().getSelectByPkSql(),
					new BeanPropertySqlParameterSource(pk), new ItsBeanPropertyRowMapper<E>(getEntityClass()));
		} else if (getSqlGenerator().getTable().getPrimaryKeyCount() == 1) {
			list = getJdbcTemplate().query(getSqlGenerator().getSelectByPkSql(),
					ParameterizedBeanPropertyRowMapper.newInstance(getEntityClass()), pk);
		} else {
			throw new IllegalStateException("not found primary key on table:"
					+ getSqlGenerator().getTable().getTableName());
		}
		return (E) CollectionHelper.findSingleObject(list);
	}

	/**
	 * 根据带命名参数的SQL和实体属性查询一个实体数据
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public E getByEntity(String namedSql, E entity) throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForObject(namedSql, new BeanPropertySqlParameterSource(entity),
				ItsBeanPropertyRowMapper.newInstance(getEntityClass()));
	}

	/**
	 * 根据带命名参数的SQL和查询条件获得一个实体数据
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public E getByQuery(String namedSql, BaseQuery<E> query) throws DataAccessException {
		return getNamedParameterJdbcTemplate().queryForObject(namedSql, new BeanPropertySqlParameterSource(query),
				ItsBeanPropertyRowMapper.newInstance(getEntityClass()));
	}

	/**
	 * 根据带命名参数的SQL和实体属性查询全部实体数据
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public List<E> findByEntity(String namedSql, E entity) throws DataAccessException {
		return getNamedParameterJdbcTemplate().query(namedSql, new BeanPropertySqlParameterSource(entity),
				ItsBeanPropertyRowMapper.newInstance(getEntityClass()));
	}

	/**
	 * 根据带命名参数的SQL和实体属性查询全部实体数据
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public List<E> findByQuery(String namedSql, BaseQuery<E> query) throws DataAccessException {
		return getNamedParameterJdbcTemplate().query(namedSql, new BeanPropertySqlParameterSource(query),
				ItsBeanPropertyRowMapper.newInstance(getEntityClass()));
	}

	/**
	 * 根据带命名参数的SQL和查询条件进行分页检索
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public Page<E> findPageByNamedQuery(String namedSql, BaseQuery<E> baseQuery) {
		return pageQuery(namedSql, "select count(*) " + SqlRemoveUtils.removeSelect(namedSql), baseQuery,
				new ItsBeanPropertyRowMapper<E>(getEntityClass()));
	}

	/**
	 * 根据xSql和查询条件进行分页检索
	 * 
	 * Example:<br>
	 * select t.* from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	protected Page<E> findPageByQuery(String xSql, BaseQuery<E> baseQuery) {
		return pageXQuery(xSql, baseQuery, new ItsBeanPropertyRowMapper<E>(getEntityClass()), "select count(*) ");
	}

	private Page<E> pageXQuery(String xSql, BaseQuery<E> baseQuery, RowMapper<E> rowMapper,
			String countQuerySelectPrefix) {
		String xCountSql = countQuerySelectPrefix + SqlRemoveUtils.removeSelect(xSql);

		Map<String, String> otherFilters = new HashMap<String, String>();
		otherFilters.put("sortColumns", baseQuery.getSortColumns());
		// 混合使用otherFilters与baseQuery.getFilters()为一个filters使用
		XsqlFilterResult queryXsqlResult = getXsqlBuilder().generateHql(xSql, otherFilters, baseQuery);
		String namedSql = queryXsqlResult.getXsql();

		XsqlFilterResult countQueryXsqlResult = getXsqlBuilder().generateHql(xCountSql, baseQuery);
		String namedCountSql = SqlRemoveUtils.removeOrders(countQueryXsqlResult.getXsql());

		return pageQuery(namedSql, namedCountSql, baseQuery, rowMapper);
	}

	@SuppressWarnings("unchecked")
	private Page<E> pageQuery(String namedSql, String namedCountSql, final BaseQuery<E> baseQuery,
			RowMapper<E> rowMapper) {
		final int totalCount = queryTotalCount(namedCountSql, baseQuery);

		// Map acceptedFilters = queryXsqlResult.getAcceptedFilters();
		int pageSize = baseQuery.getPageSize();
		int pageNumber = baseQuery.getPageNumber();
		return pageQuery(namedSql, cn.org.rapid_framework.beanutils.PropertyUtils.describe(baseQuery), totalCount,
				pageSize, pageNumber, rowMapper);
	}

	private int queryTotalCount(String namedCountSql, Object filtersObject) {
		final int totalCount = getNamedParameterJdbcTemplate().queryForInt(namedCountSql,
				new BeanPropertySqlParameterSource(filtersObject));
		return totalCount;
	}

	private Page<E> pageQuery(String sql, Map<String, Object> paramMap, final int totalCount, int pageSize,
			int pageNumber, RowMapper<E> rowMapper) {
		if (totalCount <= 0) {
			return new Page<E>(pageNumber, pageSize, 0);
		}
		Page<E> page = new Page<E>(pageNumber, pageSize, totalCount);
		List<E> list = pageQuery(sql, paramMap, page.getFirstResult(), pageSize, rowMapper);
		page.setResult(list);
		return page;
	}

	@SuppressWarnings("unchecked")
	private List<E> pageQuery(String sql, final Map<String, Object> paramMap, int startRow, int pageSize,
			final RowMapper<E> rowMapper) {
		// 支持limit查询
		if (dialect.supportsLimit()) {
			paramMap.put(LIMIT_PLACEHOLDER.substring(1), pageSize);

			// 支持limit及offset.则完全使用数据库分页
			if (dialect.supportsLimitOffset()) {
				paramMap.put(OFFSET_PLACEHOLDER.substring(1), startRow);
				sql = dialect.getLimitString(sql, startRow, OFFSET_PLACEHOLDER, pageSize, LIMIT_PLACEHOLDER);
				startRow = 0;
			} else {
				// 不支持offset,则在后面查询中使用游标配合limit分页
				sql = dialect.getLimitString(sql, 0, null, pageSize, LIMIT_PLACEHOLDER);
			}

			pageSize = Integer.MAX_VALUE;
		}
		return (List<E>) getNamedParameterJdbcTemplate().query(sql, paramMap,
				new OffsetLimitResultSetExtractor(startRow, pageSize, rowMapper));
	}

	/**
	 * 根据带命名参数的SQL（不可排序）和实体属性求取记录总数
	 * <p>
	 * 
	 * Example:<br>
	 * select count(*) from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public int countByEntity(String namedSql, E entity) {
		return getNamedParameterJdbcTemplate().queryForInt(namedSql, new BeanPropertySqlParameterSource(entity));
	}

	/**
	 * 根据带命名参数的SQL（不可排序）和查询条件求取记录总数
	 * <p>
	 * 
	 * Example:<br>
	 * select count(*) from mb_mcht_info t where t.mcht_name = :mchtName
	 */
	public int countByQuery(String namedSql, BaseQuery<E> query) {
		return getNamedParameterJdbcTemplate().queryForInt(namedSql, new BeanPropertySqlParameterSource(query));
	}

	/**
	 * 得到生成增删改查的sql生成工具
	 * 
	 * @return
	 */
	public SqlGenerator getSqlGenerator() {
		return sqlGenerator;
	}

	public String getIdentifierPropertyName() {
		List<Column> primaryKeyColumns = getSqlGenerator().getTable().getPrimaryKeyColumns();
		if (primaryKeyColumns.isEmpty()) {
			throw new IllegalStateException("not found primary key on table:"
					+ getSqlGenerator().getTable().getTableName());
		}
		return primaryKeyColumns.get(0).getPropertyName();
	}

	public Object getIdentifierPropertyValue(Object entity) {
		try {
			return PropertyUtils.getProperty(entity, getIdentifierPropertyName());
		} catch (Exception e) {
			throw new IllegalStateException("cannot get property value on entityClass:" + entity.getClass()
					+ " by propertyName:" + getIdentifierPropertyName(), e);
		}
	}

	public XsqlBuilder getXsqlBuilder() {
		XsqlBuilder builder = new XsqlBuilder(SafeSqlProcesserFactory.getOracle());
		if (builder.getSafeSqlProcesser().getClass() == DirectReturnSafeSqlProcesser.class) {
			System.err
					.println("BaseSpringJdbcDao.getXsqlBuilder(): 故意报错,你未开启Sql安全过滤,单引号等转义字符在拼接sql时需要转义,不然会导致Sql注入攻击的安全问题，请修改源码使用new XsqlBuilder(SafeSqlProcesserFactory.getDataBaseName())开启安全过滤");
		}
		return builder;
	}

	// begin 动态SQL查询

	// @Override
	// public int deleteByEntity(E entity) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int deleteByQuery(BaseQuery<E> query) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int updateByEntity(E entity) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int updateByQuery(BaseQuery<E> query) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public E getByEntity(E entity) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public E getByQuery(BaseQuery<E> query) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public List<E> findByEntity(E entity) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public List<E> findByQuery(BaseQuery<E> query) throws DataAccessException
	// {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public Page<E> findPageByQuery(BaseQuery<E> query) throws
	// DataAccessException {
	// // TODO Auto-generated method stub
	// return null;
	// }
	//
	// @Override
	// public int countByEntity(E entity) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }
	//
	// @Override
	// public int countByQuery(BaseQuery<E> query) throws DataAccessException {
	// // TODO Auto-generated method stub
	// return 0;
	// }

	// end 动态SQL

}
