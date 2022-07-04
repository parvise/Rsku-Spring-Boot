package com.hp.c4.rsku.rSku.dbio.persistent;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnect {

	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");

		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}
	}

	public Connection doDbConnect(String dbProp) {
		Connection conn = null;
		try {
			System.out.println("Database properties lodaded....... ");

			CdbProperties dbProperties = new CdbProperties(dbProp);

			String dburl1 = dbProperties.getDatabaseURL();
			System.out.println("Connection URL: " + dburl1);
			if (dburl1 == null || dburl1.length() < 1)
				throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database url specified");

			String userName = dbProperties.getUserName();
			if (userName == null || userName.length() < 1)
				throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database user name specified in ");

			String pswd = dbProperties.getPassword();
			if (pswd == null || pswd.length() < 1)
				throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database password specified ");

			String decodePwd = null;
			decodePwd = new String(pswd);

			conn = DriverManager.getConnection(dburl1, userName, decodePwd);

			conn.setAutoCommit(false);
		} catch (Exception e) {
			System.out.println("Error: " + e.toString());
			e.printStackTrace();
			System.exit(1);
		}

		return conn;
	}

	public boolean doDbDisconnect(Connection conn) {
		boolean isDisconnected;

		try {
			conn.close();
			isDisconnected = true;
		} catch (SQLException e) {
			System.out.println("Error: doDbDisconnect. " + e.toString());
			isDisconnected = false;
		}

		return isDisconnected;
	}
}
