package com.cl.its.dao;

//package com.smartpay.its.framework.vo.query;

import cn.org.rapid_framework.page.PageRequest;

public class BaseQuery<T> extends PageRequest<T> implements java.io.Serializable {
	private static final long serialVersionUID = -360860474471966681L;
	public static final int DEFAULT_PAGE_SIZE = 10;

	static {
		System.out.println("BaseQuery.DEFAULT_PAGE_SIZE=" + DEFAULT_PAGE_SIZE);
	}

	public BaseQuery() {
		setPageSize(DEFAULT_PAGE_SIZE);
	}

}
