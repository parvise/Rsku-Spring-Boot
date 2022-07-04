package com.hp.c4.rsku.rSku.security.server.util;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLUtilWithConnection extends SQLUtil {
	private Connection _sharedConnection = null;

	public SQLUtilWithConnection(String dbName) throws SQLException {
		super(dbName);
	}

	public SQLUtilWithConnection(Connection pConnection) throws SQLException {
		_sharedConnection = pConnection;
	}

	public Connection getConnection(String dbName) throws SQLException {
		if (_sharedConnection != null)
			return _sharedConnection;
		return super.getConnection(dbName);
	}

	// public void freeConnection (Connection conn) throws SQLException {
	public void freeConnection() throws SQLException {
		if (_sharedConnection == null)
			super.freeConnection();
	}

	// public void freeConnection (Connection conn) throws SQLException {
	public void close() {
		if (_sharedConnection == null)
			super.close();
	}

	public void setTrace() {
	}
}
