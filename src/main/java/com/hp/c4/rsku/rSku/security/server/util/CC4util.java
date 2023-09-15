package com.hp.c4.rsku.rSku.security.server.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;

/**
 * Title : CC4util.java Description : Utility class for loading the properties
 * from property files
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Chetan/Satish/Srini @ version v2.0 @ Since June 2008
 */
public class CC4util {

	/**
	 * It loads properties from a file
	 * 
	 * @param String file
	 * @throws IOException
	 * @throws SQLException
	 * @return void
	 */
	public static void loadProperties(String file) throws IOException, SQLException {
		loadProperties(new FileInputStream(file));
	}

	/**
	 * It loads properties from a file
	 * 
	 * @param String file
	 * @throws IOException
	 * @return void
	 */
	public static void loadPropertyFile(String fileName) throws IOException {
		Properties prop = new Properties();
		Properties sys = System.getProperties();
		prop.load(CC4util.class.getResourceAsStream(fileName));
		Set keyset = prop.keySet();
		Iterator itr = keyset.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			sys.put(key, prop.getProperty(key));
		}
	}

	/**
	 * It loads properties from an InputStream
	 * 
	 * @param InputStream in
	 * @throws IOException ,SQLException
	 * @return void
	 */
	public static void loadProperties(InputStream in) throws IOException, SQLException {
		Properties prop = new Properties();
		Properties sys = System.getProperties();
		prop.load(in);
		Set keyset = prop.keySet();
		Iterator itr = keyset.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			sys.put(key, prop.getProperty(key));
		}
		intializeDbPools(getDBProperties());
	}

	/**
	 * Initializing and DB pool creation
	 * 
	 * @param Properties prop
	 * @throws IOException
	 * @throws SQLException
	 * @return void
	 */
	public static void intializeDbPools(Properties prop) throws IOException, SQLException {
		Set keyset = prop.keySet();
		Iterator itr = keyset.iterator();
		while (itr.hasNext()) {
			String name = (String) itr.next();
			if (name.equalsIgnoreCase(DBConstants.C4_DBPOOL_INFOSHU_INFI))
				CdbConnectionMgr.getConnectionMgr().createPool(name, prop.getProperty(name),null);
		}
	}

	/**
	 * Gets the db properties from property file
	 * 
	 * @return Properties
	 */
	public static Properties getDBProperties() {
		Properties prop = new Properties();
		Set keyset = System.getProperties().keySet();
		Iterator itr = keyset.iterator();
		while (itr.hasNext()) {
			String name = (String) itr.next();
			if (name.startsWith(UtilConstants.DB_PROP)) {
				String value = (String) System.getProperty(name);
			//	String dbname = name.substring(UtilConstants.DB_PROP.length() + 1);
				prop.put(name, value);
			}
		}
		return prop;
	}

	/**
	 * method name : getPropertyDir description : it returns the value of
	 * -DC4_PROPERTIES_DIR argument. if this argument is not set, then returns null.
	 * called from : Cc4Cfg.java[to get c4.properties], CdataBaseConnection.java[to
	 * get c4output.properties]
	 */
	public static String getPropertyDir() {
		String propertyDir = null;
		try {
			propertyDir = System.getProperty("C4_PROPERTIES_DIR");
		} catch (Exception ex) {
			ex.printStackTrace();
			propertyDir = null;
		}
		return propertyDir;
	}// end of getPropertyDir

	/**
	 * This method loads the properties from a file into the system properties. Just
	 * to avoid the initializing the other properties. Added by venu.
	 * 
	 * @param in
	 * @throws IOException
	 * @throws SQLException
	 * @author atthulur
	 */
	public static void loadFileProperties(String propsFile) throws IOException, SQLException {
		Properties prop = new Properties();
		Properties sys = System.getProperties();
		prop.load(new FileInputStream(propsFile));
		Set keyset = prop.keySet();
		Iterator itr = keyset.iterator();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			sys.put(key, prop.getProperty(key));
		}
	}// end of loadFileProperties method.

}// end of class
