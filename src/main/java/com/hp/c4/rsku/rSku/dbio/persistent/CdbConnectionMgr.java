package com.hp.c4.rsku.rSku.dbio.persistent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.rest.epam.api.C4EpamAPIPassword;
import com.hp.c4.rsku.rSku.rest.epam.api.Client;
import com.hp.c4.rsku.rSku.rest.epam.api.Client.APIException;
import com.hp.c4.rsku.rSku.rest.epam.api.json.Json;
import com.hp.c4.rsku.rSku.rest.epam.api.json.JsonObject;

import oracle.jdbc.pool.OracleDataSource;
import sun.misc.BASE64Decoder;

final public class CdbConnectionMgr {
	//

	private static final Logger mLogger = LogManager.getLogger(CdbConnectionMgr.class);
	private static final String INITIAL_LIMIT = "InitialLimit";
	private static final String MAX_LIMIT = "MaxLimit";
	private static final String MIN_LIMIT = "MinLimit";

	// To store the Data Sources
	private Hashtable<String, OracleDataSource> htDataSourceList = new Hashtable<String, OracleDataSource>();

	private static CdbConnectionMgr mgr = new CdbConnectionMgr();

	private CdbConnectionMgr() {
	}

	public static CdbConnectionMgr getConnectionMgr() {
		return mgr;
	}

	public void createPool(String name, String dbprop) throws SQLException, IOException {
		CdbProperties db1 = getProperties(dbprop);

		// Biswa: String name is passed to set as cache name on data source
		OracleDataSource ods1 = getConnectionPool(db1, name);

		// htDbList.put(name,new DbPool(ods1));
		htDataSourceList.put(name, ods1);// stores the data source

		mLogger.info("Pool created successfully for : " + name);
	}

	private CdbProperties getProperties(String prop) throws IOException {
		return new CdbProperties(prop);
	}

	/**
	 * method to be used by the web application since file name cannot be passed as
	 * the resource files are expected to be in the jar directory.
	 */

	// String dbName is passed to identify the cache for different data
	// source
	@SuppressWarnings("restriction")
	private OracleDataSource getConnectionPool(CdbProperties dbProperties, String dbName) throws SQLException {
		Map<String, String> epamAPIMap = new HashMap<String, String>();
		
		String dburl1 = dbProperties.getDatabaseURL();
		if (dburl1 == null || dburl1.length() < 1)
			throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database url specified");

		String userName = dbProperties.getUserName();
		if (userName == null || userName.length() < 1)
			throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database user name specified in ");

		OracleDataSource ods = new OracleDataSource();

		ods.setURL(dburl1);
		ods.setUser(userName);

		String pswd = dbProperties.getPassword();
		mLogger.info("EPam.." + epamAPIMap + ":" + pswd);
		if (pswd == null || pswd.length() < 1) {

			String epamSyetemName = dbProperties.getEpamSystemName();
			if (epamSyetemName == null || epamSyetemName.length() < 1)
				throw new SQLException(
						"CdbConnectionMgr.getConnection(). Error - No database Epam System Name specified in ");

			epamAPIMap.put("EPAM_SYSTEM_NAME", epamSyetemName);
			epamAPIMap.put("EPAM_ACCOUNT_NAME", userName);
			try {
				pswd = C4EpamAPIPassword.getEpamApiPaswword(epamAPIMap);
				if (pswd == null || pswd.length() < 1)
					throw new SQLException(
							"CdbConnectionMgr.getConnection(). Error - No database password specified ");
			} catch (APIException e) {
				mLogger.error("Exception occured at getEpamApiPaswword...."
						+ e.getMessage());
				throw new SQLException(
						"Exception occured at getEpamApiPaswword.... - No database password specified "
								+ e.getMessage());
			}
		} else {
			if (pswd == null || pswd.length() < 1)
				throw new SQLException(
						"CdbConnectionMgr.getConnection(). Error - No database password specified ");

			String encryptedPwd = null;
			byte[] decodeResult;
			try {
				decodeResult = new BASE64Decoder().decodeBuffer(pswd);
				encryptedPwd = new String(decodeResult);
				pswd = encryptedPwd;
			} catch (IOException e) {
				mLogger.error("Error occured at Decode th DB passowrds");
			}

		}
		mLogger.info("DB Password=="+pswd+":"+userName+":"+dburl1);

		ods.setPassword(pswd);

		ods.setImplicitCachingEnabled(true);

		// S2S Changes: ODS Connection Properties
		Properties prop = new Properties();

		Properties cacheProp = new Properties();
		// prop.put (oracle.net.ns.SQLnetDef.TCP_CONNTIMEOUT_STR,"" + (1 * 1000)); // 1
		// second
		ods.setConnectionProperties(prop);
		ods.setLoginTimeout(100);

		// S2S Changes: Connection Cache Properties
		cacheProp.setProperty(MIN_LIMIT, String.valueOf(dbProperties.getMinConnections()));
		cacheProp.setProperty(MAX_LIMIT, String.valueOf(dbProperties.getMaxConnections()));
		cacheProp.setProperty(INITIAL_LIMIT, String.valueOf(dbProperties.getIntialConnections()));

		// Added by Srini on 31-Mar-2009, to set the used-connection-wait-timeout value
		// for the db pool.
		// The value of used-connection-wait-timeout is obtained from the property file.
		if (dbProperties.getUsedConnWaitTimeOut() != null) {
			mLogger.info("*****   used-connection-wait-timeout is present and its value is --> "
					+ dbProperties.getUsedConnWaitTimeOut());
			cacheProp.setProperty("used-connection-wait-timeout", dbProperties.getUsedConnWaitTimeOut());
		}

		ods.setConnectionProperties(cacheProp);// setting the DB properties to data source

		String ons = "";
		if ((dbProperties.getNodeTertiary() != "") && (dbProperties.getNodeQuaternary() != "")) {
			ons = "nodes=" + dbProperties.getNodePrimary() + ":" + dbProperties.getONSPort() + ","
					+ dbProperties.getNodeSecondary() + ":" + dbProperties.getONSPort() + ","
					+ dbProperties.getNodeTertiary() + ":" + dbProperties.getONSPort() + ","
					+ dbProperties.getNodeQuaternary() + ":" + dbProperties.getONSPort();
		} else {
			ons = "nodes=" + dbProperties.getNodePrimary() + ":" + dbProperties.getONSPort() + ","
					+ dbProperties.getNodeSecondary() + ":" + dbProperties.getONSPort();
		}

		mLogger.info("ons string: " + ons);
		// ods.setONSConfiguration(ons); /*setting for ONS event */

		// To avoid duplicate cache name error when application loads the pools multiple
		// times
//       ods.setConnectionCacheName(dbName); // setting Cache name

		return ods;
	}

		private OracleDataSource getDbCache(String dbName) throws Exception {
		OracleDataSource dataSource = (OracleDataSource) htDataSourceList.get(dbName);
		// RBY - Start
		try {

			dataSource.setImplicitCachingEnabled(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// RBY - End
		// dataSource.setLoginTimeout(MAX_WAIT_TIME_FOR_CONNECTION);
		return dataSource;
	}

	public String getDBURL(String dbName) throws Exception {
		// return getDbProperties(dbName).getDatabaseURL();
		return null;
	}

	/**
	 * method parses oracle's dburl to find the SID stupid assumption that only
	 * oracle's thin driver will be used !
	 */
	public static String getSID(String dburl) {
		int s = dburl.lastIndexOf(":");
		int e = dburl.length();
		return dburl.substring(s + 1, e).trim();
	}

	/**
	 * mehtod parses oracle's dburl to find the host name stupid assumption that
	 * only oracle's thin driver will be used ! is there any better way?? viswa
	 */
	public static String getHost(String dburl) {
		int s = dburl.indexOf("@");
		int e = dburl.indexOf(":", s);
		return dburl.substring(s + 1, e);
	}

	public synchronized Connection getConnection(String dbName) throws SQLException {
		Connection c = null;

		try {
			// RBY - Start
			OracleDataSource dataStrore = getDbCache(dbName);
			while (c == null) {
				c = dataStrore.getConnection();
			}
		} catch (Exception ce) { // this case is pool does not existe in our list
			throw new SQLException(ce.getMessage()+":dbName="+dbName);
		}

		return c;
	}

	public synchronized void freeConnection(Connection conn) throws SQLException {
		if (conn != null) {
			conn.close();
			conn = null;
		}
	}

	private void closeConnectionPool(String dbName) throws SQLException {
		try {
			OracleDataSource ds = (OracleDataSource) htDataSourceList.get(dbName);
			// ds.close();
			ds.cleanup();
			htDataSourceList.remove(dbName);
		} catch (Exception ce) {
			throw new SQLException(ce.getMessage());
		}
	}

	public synchronized void closeConnectionPool() throws SQLException {
		// Enumeration enum0 = htDbList.keys();
		Enumeration<String> enum0 = htDataSourceList.keys();
		while (enum0.hasMoreElements()) {
			String dbName = (String) enum0.nextElement();
			closeConnectionPool(dbName);
		}
	}

	public String[] getConnectionPoolList() throws SQLException {
		Enumeration<String> enum0 = htDataSourceList.keys();
		List<String> list = new ArrayList<String>();
		while (enum0.hasMoreElements())
			list.add((String) enum0.nextElement());

		return (String[]) list.toArray(new String[0]);

	}
	//
}
