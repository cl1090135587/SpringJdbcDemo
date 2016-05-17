package com.cl.test.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author cl
 *
 */
public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int age;
	private Date createTime;

	public User() {

	}

	public User(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", age=" + age + ", createTime="
				+ createTime + "]";
	}
}
