package edu.fudan.se.undergraduate.opration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	private final static String url = "jdbc:mysql://localhost:3306/crowdservice?user=root&password=";

	public static Connection connect() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection conn = DriverManager.getConnection(url);

		return conn;
	}
}
