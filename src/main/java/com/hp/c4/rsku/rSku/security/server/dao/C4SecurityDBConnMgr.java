package com.hp.c4.rsku.rSku.security.server.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.CdbProperties;

import oracle.jdbc.pool.OracleDataSource;

/**
 * <p>
 * Title : CdbConnectionMgr.java
 * </p>
 * <p>
 * Description : This class actually deals with db connectivity,pool
 * creation,etc.
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * 
 * @author Biswa @ version v2.0 @ Since June 2008
 */
final public class C4SecurityDBConnMgr {

	private static Logger mLogger = LogManager.getLogger(C4SecurityDBConnMgr.class);
	private static final String INITIAL_LIMIT = "InitialLimit";
	private static final String MAX_LIMIT = "MaxLimit";
	private static final String MIN_LIMIT = "MinLimit";
	public static final String C4SECURITY_DATABASE = "c4Security";

	private Hashtable htDbList = new Hashtable();
	private Hashtable htDataSourceList = new Hashtable();
	private static C4SecurityDBConnMgr mgr = new C4SecurityDBConnMgr();
	private OracleDataSource ods = null;

	/**
	 * Constructor
	 */
	private C4SecurityDBConnMgr() {
	}

	/**
	 * Method: getConnectionMgr
	 * 
	 * @return static CdbConnectionMgr
	 */
	public static C4SecurityDBConnMgr getConnectionMgr() {
		return mgr;
	}

	/**
	 * Method: getProperties
	 * 
	 * @param String prop
	 * @return CdbProperties
	 * @throws IOException
	 */
	private CdbProperties getProperties(String prop) throws IOException {
		return new CdbProperties(prop);
	}

	/**
	 * Method: createPool
	 * 
	 * @param String name
	 * @param String dbprop
	 * @throws SQLException
	 * @throws IOException
	 * @return void
	 */
	public void createPool(String name, String dbprop) throws SQLException, IOException {
		CdbProperties db1 = getProperties(dbprop);
		ods = getConnectionPool(db1, name);
	}

	/**
	 * <p>
	 * Method: getConnectionPool
	 * </p>
	 * 
	 * @param CdbProperties dbProperties
	 * @param String        dbName
	 * @return OracleDataSource
	 * @throws SQLException
	 */
	private OracleDataSource getConnectionPool(CdbProperties dbProperties, String dbName) throws SQLException {
		String dburl1 = dbProperties.getDatabaseURL();
		if (dburl1 == null || dburl1.length() < 1)
			throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database url specified");

		String userName = dbProperties.getUserName();
		if (userName == null || userName.length() < 1)
			throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database user name specified in ");

		String pswd = dbProperties.getPassword();
		if (pswd == null || pswd.length() < 1)
			throw new SQLException("CdbConnectionMgr.getConnection(). Error - No database password specified ");

		OracleDataSource ods = new OracleDataSource();

		// mLogger.info("Encrypted password is C4SecurityDBConnMgr " + pswd);

		String decodePwd = null;
		decodePwd = new String(pswd);

		// mLogger.info("Decrypted password is C4SecurityDBConnMgr " + decodePwd);

		ods.setURL(dburl1);
		ods.setUser(userName);
		ods.setPassword(decodePwd);

		ods.setImplicitCachingEnabled(true);

		// S2S Changes: ODS Connection Properties
		Properties prop = new Properties();

		Properties cacheProp = new Properties();
		// prop.put (oracle.net.ns.SQLnetDef.TCP_CONNTIMEOUT_STR,"" + (1 * 1000)); // 1
		// second
		ods.setConnectionProperties(prop);
		ods.setLoginTimeout(1);

		cacheProp.setProperty(MIN_LIMIT, String.valueOf(dbProperties.getMinConnections()));
		cacheProp.setProperty(MAX_LIMIT, String.valueOf(dbProperties.getMaxConnections()));
		cacheProp.setProperty(INITIAL_LIMIT, String.valueOf(dbProperties.getIntialConnections()));

		// Added by Srini on 31-Mar-2009, to set the used-connection-wait-timeout value
		// for the db pool.
		// The value of used-connection-wait-timeout is obtained from the property file.
		if (dbProperties.getUsedConnWaitTimeOut() != null) {
			System.out.println("*****   used-connection-wait-timeout is present and its value is --> "
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
		System.out.println(ons);
		// ods.setONSConfiguration(ons); // setting for ONS event
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
			throw new SQLException(ce.getMessage());
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
		Enumeration enum0 = htDataSourceList.keys();
		while (enum0.hasMoreElements()) {
			String dbName = (String) enum0.nextElement();
			closeConnectionPool(dbName);
		}
	}

	public String[] getConnectionPoolList() throws SQLException {
		Enumeration enum0 = htDataSourceList.keys();
		List list = new ArrayList();
		while (enum0.hasMoreElements())
			list.add((String) enum0.nextElement());

		return (String[]) list.toArray(new String[0]);

	}

}
