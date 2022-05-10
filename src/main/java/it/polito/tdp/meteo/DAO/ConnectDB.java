package it.polito.tdp.meteo.DAO;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;

public class ConnectDB {

	static private HikariDataSource ds = null;
	// check user e password
	static private String url = "jdbc:mysql://localhost:3306/meteo";

	public static Connection getConnection() {

		if (ds == null) {
			ds = new HikariDataSource();
			ds.setJdbcUrl(url);
			ds.setUsername("root");
			ds.setPassword("Nuvola20!");
		}

		try {
			return ds.getConnection();

		} catch (SQLException e) {

			e.printStackTrace();
			return null;
		}
	}

}
