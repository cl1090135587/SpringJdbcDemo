package com.cl.test.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.sql.RowSet;

import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.cl.test.bean.User;

public class JdbcTempleTest {

	private static DriverManagerDataSource ds = new DriverManagerDataSource();
	private static JdbcTemplate jdbcTemplate = new JdbcTemplate();
	private static NamedParameterJdbcTemplate ndJdbcTemplate = new NamedParameterJdbcTemplate(ds);

	@Before
	public void set_database() {
		ds.setDriverClassName("com.mysql.jdbc.Driver");
		ds.setUrl("jdbc:mysql://localhost:3306/test");
		ds.setUsername("root");
		ds.setPassword("cl");

		jdbcTemplate.setDataSource(ds);
	}

	@Test
	public void test_execute_sql() throws ClassNotFoundException {
		String sql = "insert into t_user(name,age) values('cl',23)";
		jdbcTemplate.execute(sql);
	}

	@Test
	public void test_update() {
		String sql = "insert into t_user(name,age) values(?,?)";
		Object[] obj = new Object[] { "cl2", 32 };

		jdbcTemplate.update(sql, obj);
		jdbcTemplate.update(sql, "23", 32);
		jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setString(1, "tt");
				ps.setInt(2, 33);
			}
		});
	}

	@Test
	public void test_get_autoKey() {
		KeyHolder key = new GeneratedKeyHolder();
		final String sql = "insert into t_user(name,age) values(?,?)";
		int update = jdbcTemplate.update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				pstmt.setString(1, "pstmt");
				pstmt.setInt(2, 23);
				return pstmt;
			}
		}, key);
		System.out.println(update);
		System.out.println(key.getKey().intValue());
	}

	@Test
	public void test_batch_insert() {
		final String sql = "insert into t_user(name,age) values(?,?)";
		final List<User> list = new ArrayList<User>();
		for (int i = 0; i < 10; i++) {
			list.add(new User("cl", 23 + i));
		}
		int[] retBath = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				User user = list.get(i);
				ps.setString(1, user.getName());
				ps.setInt(2, user.getAge());
			}

			@Override
			public int getBatchSize() {
				return list.size();
			}
		});
		System.out.println(Arrays.toString(retBath));
	}

	@Test
	public void test_query() {
		String sql = "select name, age from t_user where age > ?";
		final List<User> users = new ArrayList<User>();
		int age = 100;
		jdbcTemplate.query(sql, new Object[] { age }, new RowCallbackHandler() {
			// 当返回结果为空的时候，不会调用processRow，而会执行空逻辑
			// 通过JDBC查询返回ResultSet结果时，JDBC并不会一次性将所有匹配的数据都加载
			// 到JVM中，而是只返回一批次的数据(由JDBC驱动程序决定，如Oracle的JDBC默认返回10条)，当游标超过
			// 数据范围时，JDBC再获取一批数据。
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				User user = new User();
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				users.add(user);
				System.out.println("xx");
			}
		});
		for (User user : users) {
			System.out.println(user);
		}
	}

	@Test
	public void test_query_rowmpper() {
		String sql = "select name, age from t_user where age > ?";
		// final List<User> users = new ArrayList<User>();
		int age = 100;
		List<User> users = jdbcTemplate.query(sql, new Object[] { age }, new RowMapper<User>() {
			// 直接返回一个List的对象，如果结果集非常大的话，可能需要很大的内存。
			@Override
			public User mapRow(ResultSet rs, int rowNum) throws SQLException {
				System.out.println(rowNum);
				User user = new User();
				user.setName(rs.getString("name"));
				user.setAge(rs.getInt("age"));
				return user;
			}
		});
		for (User user : users) {
			System.out.println(user);
		}
	}

	// 单值的获取方法，更方便，如select countrt(*) from xxx
	@Test
	public void test_query_object() throws SQLException {
		String sql = "select name, age from t_user where age > 3";
		// int countRow = jdbcTemplate.queryForInt(sql);
		// System.out.println(countRow);

		// Connection connection =
		// DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
		// Connection nativeConnection =
		// jdbcTemplate.getNativeJdbcExtractor().getNativeConnection(connection);
		// nativeConnection.close();
		SqlRowSet rs = jdbcTemplate.queryForRowSet(sql);
		jdbcTemplate.setMaxRows(10);
		int idx = 0;
		while (rs.next()) {
			System.out.println(++idx + "\t" + rs.getString(1));
		}
	}

	@Test
	public void test_query_namedJdbc() {
		String sql = "select name, age from t_user where age >= :age";
		BeanPropertySqlParameterSource param = new BeanPropertySqlParameterSource(new User("cl", 33));
		SqlRowSet rs = ndJdbcTemplate.queryForRowSet(sql, param);
//		ndJdbcTemplate.query(sql, param, new RowMapper() {
//
//			@Override
//			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
//				// TODO Auto-generated method stub
//				return null;
//			}
//		});
		while (rs.next()) {
			System.out.println(rs.getString("name"));
		}
		MapSqlParameterSource param2 = new MapSqlParameterSource();
		param2.addValue("name", 33);
		SqlRowSet rs2 = ndJdbcTemplate.queryForRowSet(sql, param);
		while (rs2.next()) {
			System.out.println(rs2.getString("name"));
		}

	}
}
