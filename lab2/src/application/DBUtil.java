package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtil {
	private static final String DBURL_STRING = "jdbc:mysql://localhost:3306/hospital_data?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=GMT%2B8";
	private static final String USER_STRING = "root";
	private static final String PWD_STRING = "";
	private static Connection connection;
	private static Statement statement;

	/**
	 * 连接数据库，创建statement
	 * 
	 * @throws SQLException
	 */
	public static void connect() throws SQLException {
		try {
			com.mysql.cj.jdbc.Driver driverOne = new com.mysql.cj.jdbc.Driver();
			DriverManager.registerDriver(driverOne);
			connection = DriverManager.getConnection(DBURL_STRING, USER_STRING, PWD_STRING);
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 执行数据库查询语句
	 * 
	 * @param sqlString
	 * @return
	 * @throws SQLException
	 */
	public static ResultSet executeQuery(String sqlString) throws SQLException {
		try {
			if (statement == null)
				throw new SQLException();
			return statement.executeQuery(sqlString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 执行数据库语句
	 * 
	 * @param sqlString
	 * @return
	 * @throws SQLException
	 */
	public static void execute(String sqlString) throws SQLException {
		try {
			if (statement == null)
				throw new SQLException();
			statement.execute(sqlString);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void connectShutdown() throws SQLException {
		try {
			if (statement != null)
				statement.close();
			if (connection != null)
				connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}
}