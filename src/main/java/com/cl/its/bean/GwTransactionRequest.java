package com.cl.its.bean;

/*
 * Powered By [rapid-framework]
 * Web Site: http://www.rapid-framework.org.cn
 * Google Code: http://code.google.com/p/rapid-framework/
 * Since 2008 - 2013
 */

//package com.smartpay.its.getway.model;

import org.hibernate.validator.constraints.*;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import cn.org.rapid_framework.util.DateConvertUtils;

import javax.persistence.*;

public class GwTransactionRequest extends BaseEntity implements java.io.Serializable {
	private static final long serialVersionUID = 5454155825314635342L;

	// alias
	public static final String TABLE_ALIAS = "交易请求日志表";
	public static final String ALIAS_REQUEST_ID = "交易请求id<seq>";
	public static final String ALIAS_PAYMENT_ORDER_ID = "支付流水号";
	public static final String ALIAS_TRANSACTION_TYPE = "交易类型";
	public static final String ALIAS_CHANNEL_ID = "渠道id标识";
	public static final String ALIAS_CNL_CONN_ID = "连接id标识";
	public static final String ALIAS_CNL_ORG_ID = "渠道机构号";
	public static final String ALIAS_CNL_ORDER_NO = "渠道流水号";
	public static final String ALIAS_CNL_BATCH_NO = "渠道批次号";
	public static final String ALIAS_CNL_DATETIME = "渠道交易时间";
	public static final String ALIAS_AMOUNT = "支付金额";
	public static final String ALIAS_REQ_PLAIN = "报文明文";
	public static final String ALIAS_REQ_MSG = "请求报文";
	public static final String ALIAS_REQ_FROM = "请求来源";
	public static final String ALIAS_REPLY_TO = "回复请求的地址";
	public static final String ALIAS_CREATE_DATETIME = "订单创建时间";

	// date formats
	public static final String FORMAT_CREATE_DATETIME = DATE_FORMAT;

	// 可以直接使用: @Length(max=50,message="用户名长度不能大于50")显示错误消息
	// columns START
	/**
	 * 交易请求id<seq> db_column: REQUEST_ID
	 */

	private Long requestId;
	/**
	 * 支付流水号 db_column: PAYMENT_ORDER_ID
	 */

	private Long paymentOrderId;
	/**
	 * 交易类型 db_column: TRANSACTION_TYPE
	 */

	private Integer transactionType;
	/**
	 * 渠道id标识 db_column: CHANNEL_ID
	 */

	private Integer channelId;
	/**
	 * 连接id标识 db_column: CNL_CONN_ID
	 */

	private Integer cnlConnId;
	/**
	 * 渠道机构号 db_column: CNL_ORG_ID
	 */
	@Length(max = 16)
	private String cnlOrgId;
	/**
	 * 渠道流水号 db_column: CNL_ORDER_NO
	 */
	@Length(max = 32)
	private String cnlOrderNo;
	/**
	 * 渠道批次号 db_column: CNL_BATCH_NO
	 */
	@Length(max = 32)
	private String cnlBatchNo;
	/**
	 * 订单创建时间 db_column: CNL_DATETIME
	 */

	private java.util.Date cnlDatetime;
	/**
	 * 支付金额 db_column: AMOUNT
	 */

	private Long amount;
	/**
	 * 报文明文 db_column: REQ_PLAIN
	 */
	@Length(max = 2048)
	private String reqPlain;
	/**
	 * 请求报文 db_column: REQ_MSG
	 */
	@Length(max = 2048)
	private String reqMsg;
	/**
	 * 请求来源 db_column: REQ_FROM
	 */
	@Length(max = 100)
	private String reqFrom;
	/**
	 * 回复请求的地址 db_column: REPLY_TO
	 */
	@Length(max = 100)
	private String replyTo;
	/**
	 * 订单创建时间 db_column: CREATE_DATETIME
	 */

	private java.util.Date createDatetime;

	// columns END

	// 注意： spring_jdbc的MetadataCreateUtils.fromTable(Entity.class) 可以读取JPA
	// annotation的标注信息
	// 现支持 @Id,@Column,@Table标注

	public GwTransactionRequest() {
	}

	public GwTransactionRequest(Long requestId) {
		this.requestId = requestId;
	}

	@Id
	public Long getRequestId() {
		return this.requestId;
	}

	public void setRequestId(Long value) {
		this.requestId = value;
	}

	public Long getPaymentOrderId() {
		return this.paymentOrderId;
	}

	public void setPaymentOrderId(Long value) {
		this.paymentOrderId = value;
	}

	public Integer getTransactionType() {
		return this.transactionType;
	}

	public void setTransactionType(Integer value) {
		this.transactionType = value;
	}

	public Integer getChannelId() {
		return this.channelId;
	}

	public void setChannelId(Integer value) {
		this.channelId = value;
	}

	public Integer getCnlConnId() {
		return this.cnlConnId;
	}

	public void setCnlConnId(Integer value) {
		this.cnlConnId = value;
	}

	public String getCnlOrgId() {
		return this.cnlOrgId;
	}

	public void setCnlOrgId(String value) {
		this.cnlOrgId = value;
	}

	public String getCnlOrderNo() {
		return this.cnlOrderNo;
	}

	public void setCnlOrderNo(String value) {
		this.cnlOrderNo = value;
	}

	public String getCnlBatchNo() {
		return this.cnlBatchNo;
	}

	public void setCnlBatchNo(String value) {
		this.cnlBatchNo = value;
	}

	@Transient
	public String getCnlDatetimeString() {
		return DateConvertUtils.format(getCnlDatetime(), FORMAT_CREATE_DATETIME);
	}

	public void setCnlDatetimeString(String value) {
		setCnlDatetime(DateConvertUtils.parse(value, FORMAT_CREATE_DATETIME, java.util.Date.class));
	}

	public java.util.Date getCnlDatetime() {
		return cnlDatetime;
	}

	public void setCnlDatetime(java.util.Date cnlDatetime) {
		this.cnlDatetime = cnlDatetime;
	}

	public Long getAmount() {
		return this.amount;
	}

	public void setAmount(Long value) {
		this.amount = value;
	}

	public String getReqPlain() {
		return this.reqPlain;
	}

	public void setReqPlain(String value) {
		this.reqPlain = value;
	}

	public String getReqMsg() {
		return this.reqMsg;
	}

	public void setReqMsg(String value) {
		this.reqMsg = value;
	}

	public String getReqFrom() {
		return this.reqFrom;
	}

	public void setReqFrom(String value) {
		this.reqFrom = value;
	}

	public String getReplyTo() {
		return this.replyTo;
	}

	public void setReplyTo(String value) {
		this.replyTo = value;
	}

	@Transient
	public String getCreateDatetimeString() {
		return DateConvertUtils.format(getCreateDatetime(), FORMAT_CREATE_DATETIME);
	}

	public void setCreateDatetimeString(String value) {
		setCreateDatetime(DateConvertUtils.parse(value, FORMAT_CREATE_DATETIME,
				java.util.Date.class));
	}

	public java.util.Date getCreateDatetime() {
		return this.createDatetime;
	}

	public void setCreateDatetime(java.util.Date value) {
		this.createDatetime = value;
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
				.append("RequestId", getRequestId()).append("PaymentOrderId", getPaymentOrderId())
				.append("TransactionType", getTransactionType())
				.append("ChannelId", getChannelId()).append("CnlConnId", getCnlConnId())
				.append("CnlOrgId", getCnlOrgId()).append("CnlOrderNo", getCnlOrderNo())
				.append("CnlBatchNo", getCnlBatchNo()).append("CnlDatetime", getCnlDatetime())
				.append("Amount", getAmount()).append("ReqPlain", getReqPlain())
				.append("ReqMsg", getReqMsg()).append("ReqFrom", getReqFrom())
				.append("ReplyTo", getReplyTo()).append("CreateDatetime", getCreateDatetime())
				.toString();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getRequestId()).toHashCode();
	}

	public boolean equals(Object obj) {
		if (obj instanceof GwTransactionRequest == false)
			return false;
		if (this == obj)
			return true;
		GwTransactionRequest other = (GwTransactionRequest) obj;
		return new EqualsBuilder().append(getRequestId(), other.getRequestId()).isEquals();
	}
}
