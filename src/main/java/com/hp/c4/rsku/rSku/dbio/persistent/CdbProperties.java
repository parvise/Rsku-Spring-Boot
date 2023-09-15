package com.hp.c4.rsku.rSku.dbio.persistent;

import java.io.IOException;
import java.util.Properties;

/**
 * <p>
 * Title : CdbProperties.java
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ version v1.0 @ Since 2021
 */
public class CdbProperties {

	private static final String USER_NAME = "userName";
	private static final String PASSWORD = "password";
	private static final String DB_URL = "databaseUrl";
	private static final String MAX_CONN = "maximum_connections";
	private static final String MIN_CONN = "minimum_connections";
	private static final String INITIAL_CONN = "initial_connections";
	private static final String NODE_PRI = "node_pri"; // primary RAC node
	private static final String NODE_SEC = "node_sec"; // secondary RAC node
	private static final String NODE_TER = "node_ter"; // tertiary RAC node
	private static final String NODE_QUART = "node_quart"; // quaternary RAC node
	private static final String ONS_PORT = "ons_port"; // ONS port
	private static final String EPAM_SYSTEM_NAME = "epam_system_name"; // EPAM REST-API System Name config

	private static final String USED_CONN_WAIT_TIMEOUT = "used_connection_wait_timeout"; // used-connection-wait-timeout
	private Properties prop = new Properties();

	public CdbProperties(String s) throws IOException {
		s = s.replace(',', '\n');
		prop.load(new java.io.ByteArrayInputStream(s.getBytes()));
	}

	public String getUserName() {
		return prop.getProperty(USER_NAME, "");
	}

	public String getPassword() {
		return prop.getProperty(PASSWORD, "");
	}

	public String getDatabaseURL() {
		return prop.getProperty(DB_URL, "");
	}

	public int getMaxConnections() {
		return Integer.parseInt(prop.getProperty(MAX_CONN, "0"));
	}

	public int getMinConnections() {
		return Integer.parseInt(prop.getProperty(MIN_CONN, "0"));
	}

	public int getIntialConnections() {
		return Integer.parseInt(prop.getProperty(INITIAL_CONN, "0"));
	}

	public String getNodePrimary() {
		return prop.getProperty(NODE_PRI, "");
	}

	public String getNodeSecondary() {
		return prop.getProperty(NODE_SEC, "");
	}

	public String getNodeTertiary() {
		return prop.getProperty(NODE_TER, "");
	}

	public String getNodeQuaternary() {
		return prop.getProperty(NODE_QUART, "");
	}

	public String getONSPort() {
		return prop.getProperty(ONS_PORT, "6200");
	}

	public String getUsedConnWaitTimeOut() {
		return prop.getProperty(USED_CONN_WAIT_TIMEOUT);
	}
	
	public String getEpamSystemName() {
		String hasKey = prop.getProperty(EPAM_SYSTEM_NAME, "");
		hasKey = hasKey.replace('#', ',');
		hasKey = hasKey.replaceAll(" ", "%20");
		return hasKey;
	}
}