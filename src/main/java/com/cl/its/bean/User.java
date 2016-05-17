package com.cl.its.bean;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Table(name = "t_user")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private int age;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	public User() {

	}

	public User(String name, int age) {
		this.name = name;
		this.age = age;
	}

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}
