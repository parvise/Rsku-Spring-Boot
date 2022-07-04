package com.hp.c4.rsku.rSku.security.server.util;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;

public class SQLUtil extends SQLUtilAbstract {

	private static final Logger mLogger = LogManager.getLogger(SQLUtil.class);

	public SQLUtil(String dbName) throws SQLException {
		super();
		_dbName = dbName;
	}

	public SQLUtil() throws SQLException {
		super();
	}

	public void setDbName(String pDBName) {
		_dbName = pDBName;
	}

	public Connection getConnection(String dbName) throws SQLException {
		_dbName = dbName;
		Connection con = CdbConnectionMgr.getConnectionMgr().getConnection(dbName);
//    if(con != null) {
//    	  mLogger.debug(" ******** Connection opened ============>"+con);
//    }
		return con;
	}

	// public void freeConnection (Connection conn) throws SQLException {
	public void freeConnection() throws SQLException {
		close();
	}

	// public void freeConnection (Connection conn) throws SQLException {
	public void close() {
		super.close();
	}

	//
}
