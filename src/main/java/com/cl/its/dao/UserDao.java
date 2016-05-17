package com.cl.its.dao;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cl.its.bean.User;

@Repository
public class UserDao extends BaseSpringJdbcDao<User, Long> {

	@Override
	public User save(User entity) throws DataAccessException {
		String sql = getSqlGenerator().getInsertSql();

		// insertWithAssigned(entity,sql); //手工分配

		// 其它主键生成策略
		// insertWithOracleSequence(entity, "seq_GW_TRANSACTION_REQUEST", sql);
		// // oracle
		// sequence:
		// insertWithDB2Sequence(entity,"seqGW_TRANSACTION_REQUEST",sql); //db2
		// sequence:
		insertWithIdentity(entity, sql); // for sqlserver and mysql
		// insertWithUUID(entity,sql); //uuid

		return entity;
	}

	@Override
	public Class<User> getEntityClass() {
		return User.class;
	}
}
