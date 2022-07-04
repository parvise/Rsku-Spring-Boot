package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;

/**
 * Title: C4 Description: C4 Copyright: Copyright (c) 2001 Company: HP
 * 
 * @author viswa
 * @version 1.0
 */

public abstract class ObjectIO {

	private String dbName;
	public static final int IMPORTER = 1;
	public static final int OUTPUT_SERVICE = 2;
	public static final int UPDATE_SERVICE = 3;
	public static final int BROWSER = 4;
	public static final int C4_MAINTENANCE = 5;
	public static final int DATAMART = 6;
	public static final int C4_REPORT = 7;
	public static final int OTHER = 8;
	private static boolean intialized = false;
	private static int application = OTHER;

	// ramesh - start
	public enum AppNames {
		EDW, EDW_HPI, EDW_HPE, EDW_KAI, OPTIMUS, OPTIMUS_HPI, OPTIMUS_HPE, OPTIMUS_KAI, TERA, TERA_HPI, TERA_HPE,
		TERA_KAI, EDM, EDM_HPI, EDM_HPE, EDM_KAI;
	}

	public static Map<String, String> appNameToProcNameMap = new HashMap<String, String>();
	// ramesh - end

	private static final Logger mLogger = LogManager.getLogger(ObjectIO.class);

	public synchronized static void setApplicationType(int apptype) {
		// if already intialized throw exception, can happen only once
		if (intialized)
			throw new IllegalStateException("Application Type already Intialized");
		if (apptype < IMPORTER || apptype > OTHER)
			apptype = OTHER;
		application = apptype;
		// System.out.println("Intialized app type and the value is="+ application);
		intialized = true;
	}

	public ObjectIO() {
		this(getDBName(application));
	}

	public static String getDBName(int appType) {

		return null;
	}

	public ObjectIO(String dbName) {
		setDatabaseName(dbName);
	}

	public void setDatabaseName(String dbName) {
		this.dbName = dbName;
	}

	public String getDatabaseName() {
		return this.dbName;
	}

	public int getApplicationType() {
		return application;
	}

	/**
	 * process results
	 */
	public abstract Object processResult(CProcedureInfo info) throws C4Exception, SQLException;

	public Object callSp(CProcedureInfo info, Connection con) throws SQLException, C4Exception {

		CallableStatement cstmt = null;
		try {
			cstmt = con.prepareCall(makeStatement(info));
			// set all in paramas and register all out params.
			setValues(cstmt, info);
			// execute the query
			cstmt.execute();
			// read results back to the procinfo.
			getResults(cstmt, info);
			return processResult(info);
		} finally {
			try {
				if (cstmt != null)
					cstmt.close();
			} catch (SQLException se) {
			}
			// try { if (con != null) con.close(); } catch(SQLException se1) { }
		}
	}

	public Object callSp(CProcedureInfo info) throws SQLException, C4Exception {
		// System.out.println(" in side call sp");
		Connection con = null;
		CallableStatement cstmt = null;
		try {

			con = CdbConnectionMgr.getConnectionMgr().getConnection(this.dbName);
			// System.out.println(" DB name :"+this.dbName);
			// System.out.println(" in side ObjectIo:callSP(CProcedureInfo) ");
			// System.out.println(" makeStatement(info) "+ makeStatement(info));
			cstmt = con.prepareCall(makeStatement(info));

			// set all in paramas and register all out params.
			setValues(cstmt, info);
			// execute the query
			cstmt.execute();
			// read results back to the procinfo.
			// System.out.println(" after excuting SP ");
			getResults(cstmt, info);
			// System.out.println(" after getResults( "+cstmt+" ,info)");
			return processResult(info);
		} catch (SQLException se) {
			se.printStackTrace();
			// re throw exception
			throw se;
		} finally {
			try {
				if (cstmt != null)
					cstmt.close();
			} catch (SQLException se) {
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException se1) {
			}
		}
	}

	protected void setValues(CallableStatement cstmt, CProcedureInfo info) throws SQLException {
		int n = info.getParamCount();
		// System.out.println(" the paramter count for sp is "+n);
		for (int i = 0; i < n; i++) {
			CSPParameter p = info.getParameterAt(i);
			if (p.isParamOut()) {
				cstmt.registerOutParameter(i + 1, p.getDataType());
			} else {
				switch (p.getDataType()) {
				case java.sql.Types.VARCHAR:
				case java.sql.Types.CHAR:
					String vs = (String) p.getValue();
					// System.out.println(" the string value is in DBIO setValues"+vs);
					if (vs != null)
						cstmt.setString(i + 1, vs);
					else
						cstmt.setNull(i + 1, Types.VARCHAR);
					break;
				case java.sql.Types.INTEGER:
					if (p.getValue() != null) {
						int vi = ((Integer) p.getValue()).intValue();
						cstmt.setInt(i + 1, vi);
					} else
						cstmt.setNull(i + 1, Types.INTEGER);

					break;
				case java.sql.Types.FLOAT:
					if (p.getValue() != null) {
						float vi = ((Float) p.getValue()).floatValue();
						cstmt.setFloat(i + 1, vi);
					} else
						cstmt.setNull(i + 1, Types.FLOAT);
					break;
				case java.sql.Types.DATE:
					java.util.Date dt = (java.util.Date) p.getValue();
					if (dt != null) {
						cstmt.setTimestamp(i + 1, new Timestamp(dt.getTime()));
					} else {
						cstmt.setNull(i + 1, Types.DATE);
					}
					break;
				}
			}
		}
	}

	private void getResults(CallableStatement cstmt, CProcedureInfo info) throws SQLException {
		int n = info.getParamCount();
		for (int i = 0; i < n; i++) {
			CSPParameter p = info.getParameterAt(i);
			if (p.isParamOut()) {
				if (p.getDataType() == Types.INTEGER) {
					p.setValue(new Integer(cstmt.getInt(i + 1)));
				} else {
					Object obj = cstmt.getObject(i + 1);
					p.setValue(obj);
				}
			}
		}

	}

	private String makeStatement(CProcedureInfo info) {
		int count = info.getParamCount();
		StringBuffer buf = new StringBuffer();
		buf.append("{ call " + info.getName() + " ( ");
		for (int i = 0; i < count; i++) {
			if (i != 0)
				buf.append(",");
			buf.append("?");
		}
		buf.append(")}");
		return buf.toString();
	}

	protected String decodeNull(String s) {

		if ("<null>".equals(s))
			return "";
		else
			return s;
	}

	protected String encodeNull(String s) {

		if (s == null || s.equals(""))
			return "<null>";
		else
			return s;
	}

	// MBP: A new method returnStatement. To be called from extract.PLExtractThread
	public Object returnStatement(CProcedureInfo info, Connection con) throws SQLException {

		CallableStatement cstmt = null;

		try {

			cstmt = con.prepareCall(makeStatement(info));
			setValues(cstmt, info);

		} catch (SQLException sqlex) {

			mLogger.error("ObjectIO.returnStatement: SQLException in getting connection: " + sqlex);
			throw sqlex;

		}

		return cstmt;
	}// MBP

	// MBP: A new method returnConnection object for the database C4ONS. This method
	// will be called from extract.PLExtractThread
	public Connection returnConnection() throws SQLException {

		Connection con = null;
		try {
			con = CdbConnectionMgr.getConnectionMgr().getConnection(this.dbName);
		} catch (SQLException sqlex) {

			mLogger.error("ObjectIO.returnConnection: SQLException in getting connection" + sqlex);
			throw sqlex;

		}
		return con;
	}// MBP
}