package com.cl.test.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cl.its.bean.User;
import com.cl.its.dao.BaseQuery;
import com.cl.its.dao.UserDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class TestDao {
	@Autowired
	private UserDao dao;

	@Test
	public void test_dao() {
		User us = new User("tttt", 666);
		try {
			User save = dao.save(us);
			System.out.println(save);
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Test
	public void test_query() {
		BaseQuery<User> baseQuery = new BaseQuery<User>();
		baseQuery.setFilters(new User("cl", 666));
		List<User> query = dao.findByQuery("select user, name from t_user where age = :age", baseQuery);
		System.out.println(query.toString());
	}
}