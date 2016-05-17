package com.cl.its.dao;

/*
 * Powered By [rapid-framework]
 * Web Site: http://www.rapid-framework.org.cn
 * Google Code: http://code.google.com/p/rapid-framework/
 * Since 2008 - 2013
 */

//package com.smartpay.its.getway.dao;

import cn.org.rapid_framework.page.Page;

import org.springframework.stereotype.Repository;

import com.cl.its.bean.GwTransactionRequest;
import com.cl.its.bean.User;
//import com.smartpay.its.framework.dao.jdbc.BaseSpringJdbcDao;
//import com.smartpay.its.framework.vo.query.BaseQuery;

@Repository
public class GwTransactionRequestDao extends BaseSpringJdbcDao<GwTransactionRequest, Long> {

	// 注意: getSqlGenerator()可以生成基本的：增删改查sql语句, 通过这个父类已经封装了增删改查操作
	// sqlgenerator参考:
	// http://code.google.com/p/rapid-framework/wiki/rapid_sqlgenerator

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private User user;
	public Class<GwTransactionRequest> getEntityClass() {
		return GwTransactionRequest.class;
	}

	public GwTransactionRequest save(GwTransactionRequest entity) {
		String sql = getSqlGenerator().getInsertSql();

		// insertWithAssigned(entity,sql); //手工分配

		// 其它主键生成策略
		insertWithOracleSequence(entity, "seq_GW_TRANSACTION_REQUEST", sql); // oracle
																				// sequence:
		// insertWithDB2Sequence(entity,"seqGW_TRANSACTION_REQUEST",sql); //db2
		// sequence:
		// insertWithIdentity(entity,sql); //for sqlserver and mysql
		// insertWithUUID(entity,sql); //uuid

		return entity;
	}

	/**
	 * 根据查询条件进行分页检索
	 * 
	 * @param query
	 * @return
	 */
	public Page<GwTransactionRequest> findPageByQuery(BaseQuery<GwTransactionRequest> query) {
		// XsqlBuilder syntax,please see
		// http://code.google.com/p/rapid-xsqlbuilder
		// [column]为字符串拼接, {column}为使用占位符. 如username='[username]',偷懒时可以使用字符串拼接
		// [sortColumns] 为PageRequest的属性
		String xSql = new StringBuilder(200).append("select ").append(getSqlGenerator().getColumnsSql("t"))
				.append(" from GW_TRANSACTION_REQUEST t where 1=1 ")
				.append("/~ and t.PAYMENT_ORDER_ID = {paymentOrderId} ~/")
				.append("/~ and t.TRANSACTION_TYPE = {transactionType} ~/")
				.append("/~ and t.CHANNEL_ID = {channelId} ~/").append("/~ and t.CNL_CONN_ID = {cnlConnId} ~/")
				.append("/~ and t.CNL_ORG_ID = {cnlOrgId} ~/").append("/~ and t.CNL_ORDER_NO = {cnlOrderNo} ~/")
				.append("/~ and t.CNL_BATCH_NO = {cnlBatchNo} ~/").append("/~ and t.AMOUNT = {amount} ~/")
				.append("/~ and t.REQ_PLAIN = {reqPlain} ~/").append("/~ and t.REQ_MSG = {reqMsg} ~/")
				.append("/~ and t.REQ_FROM = {reqFrom} ~/").append("/~ and t.REPLY_TO = {replyTo} ~/")
				.append("/~ and t.CREATE_DATETIME >= {createDatetimeBegin} ~/")
				.append("/~ and t.CREATE_DATETIME <= {createDatetimeEnd} ~/").append("/~ order by [sortColumns] ~/")
				.toString();

		return findPageByQuery(xSql, query);
	}

}