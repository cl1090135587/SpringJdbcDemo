package com.cl.test.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.DataSource;

import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.object.MappingSqlQuery;

import com.cl.test.bean.User;

public class OODao {
	private static DriverManagerDataSource ds = new DriverManagerDataSource();
	// private static JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private UserQuery userQuery;

	public OODao() {
		init();
	}

	public void init() {
		this.userQuery = new UserQuery(ds);
	}

	public User getUser() {
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/test");
		ds.setUsername("root");
		ds.setPassword("cl");
		return userQuery.findObject(99);
	}

	private class UserQuery extends MappingSqlQuery<User> {
		private final static String SQL_ = "select name,age from t_user where age > ?";

		public UserQuery(DataSource ds) {
			super(ds, SQL_);
			declareParameter(new SqlParameter(Types.INTEGER));
			compile();
		}

		@Override
		protected User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setName(rs.getString("name"));
			user.setAge(rs.getInt("age"));
			return user;
		}
	}

	public static void main(String[] args) {
		OODao dao = new OODao();
		User user = dao.getUser();
		System.out.println(user);
	}
}
