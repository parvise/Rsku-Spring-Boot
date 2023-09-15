package com.hp.c4.rsku.rSku.dbio.persistent;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.hp.c4.rsku.rSku.constants.DBConstants;

public class DBProperties {

	private static Properties mEmailerProps;

	private static Map<String, Connection> dbConnections = new HashMap<String, Connection>();
	private static DbConnect conn;

	private DBProperties() {
	}

	static {
		conn = new DbConnect();
		mEmailerProps = new Properties();
		String directoryFilePath = DBConstants.DB_PROPERTIES_FILE;

		FileInputStream in = null;
		try {
			System.out.println("SQL Properties initailized");
			in = new FileInputStream(directoryFilePath);
			mEmailerProps.load(in);
			if (in != null) {
				in.close();
				System.out.println(" ***** In Try block: FileInputStream in is closed.  ");
			}
		} catch (IOException e) {
			System.out.println("Inside exception of Cemailer constructor ---- " + e);
		} finally {

			try {
				if (in != null) {
					in.close();
					in = null;
					System.out.println(" *****  In  block :  BufferedFileReader - tLineReader  is closed");
				}
			} catch (Exception ignore) {
				System.out.println(" Error in the Finally block " + ignore.getMessage());
			}
		}

		Connection c4Onsi = conn.doDbConnect(mEmailerProps.getProperty(DBConstants.C4_DBPOOL_C4PROD_ONSI));
		Connection c4Offi = conn.doDbConnect(mEmailerProps.getProperty(DBConstants.C4_DBPOOL_C4PROD_OFFI));
		Connection gpsnap = conn.doDbConnect(mEmailerProps.getProperty(DBConstants.C4_DBPOOL_GPSNAP_ONSI));
		dbConnections.put(DBConstants.C4_DBPOOL_C4PROD_ONSI, c4Onsi);
		dbConnections.put(DBConstants.C4_DBPOOL_C4PROD_OFFI, c4Offi);
		dbConnections.put(DBConstants.C4_DBPOOL_GPSNAP_ONSI, gpsnap);

		try {
			CdbConnectionMgr.getConnectionMgr().createPool(DBConstants.C4_DBPOOL_C4PROD_ONSI,
					mEmailerProps.getProperty(DBConstants.C4_DBPOOL_C4PROD_ONSI), null);

			Connection con = CdbConnectionMgr.getConnectionMgr().getConnection(DBConstants.C4_DBPOOL_C4PROD_ONSI);

			System.out.println("Welcome" + con);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static Map<String, Connection> getDBConnections() {
		return dbConnections;
	}

	public static void closeDBConnection(String poolName) {
		if (dbConnections != null) {
			System.out.println(
					"Db Pool Name is closed :" + poolName + " : " + conn.doDbDisconnect(dbConnections.get(poolName)));
		}
	}

	public static void closeAllDBConnections() {
		if (dbConnections != null) {
			for (String poolName : dbConnections.keySet()) {
				System.out.println("Db Pool Name is closed :" + poolName + " : "
						+ conn.doDbDisconnect(dbConnections.get(poolName)));
			}
		}
	}
}
