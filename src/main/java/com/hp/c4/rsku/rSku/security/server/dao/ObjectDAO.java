package com.hp.c4.rsku.rSku.security.server.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CProcedureInfo;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CSPParameter;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

/**
 * <p>
 * Title : ObjectDAO.java
 * </p>
 * <p>
 * Description : This abstract class is extended by all DAO classes.
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Chetan/Satish/Srini @ version v1.0 @ Since June 2008
 */
public abstract class ObjectDAO {

	// For logging info and errors to the log file.
	private static Logger mLogger = LogManager.getLogger(ObjectDAO.class);

	private String dbName = null;

	public ObjectDAO() {
		this(C4SecurityDBConnMgr.C4SECURITY_DATABASE);
	}

	public ObjectDAO(String dbName) {
		this.dbName = dbName;
	}

	/**
	 * Method: processResult (abstract)
	 * 
	 * @param Object type CProcedureInfo
	 * @return Object
	 * @throws C4SecurityException
	 * @throws SQLException
	 */
	public abstract Object processResult(CProcedureInfo info) throws C4SecurityException, SQLException;

	/**
	 * Method: callSp
	 * 
	 * @param Object type CProcedureInfo
	 * @param Object type Connection
	 * @return Object
	 * @throws SQLException
	 * @throws C4SecurityException
	 */
	public Object callSp(CProcedureInfo info, Connection con) throws SQLException, C4SecurityException {
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
		}
	}

	/**
	 * Method: callSp
	 * 
	 * @param Object type CProcedureInfo
	 * @return Object
	 * @throws SQLException
	 * @throws C4SecurityException
	 */
	public Object callSp(CProcedureInfo info) throws SQLException, C4SecurityException {
		Connection con = null;
		CallableStatement cstmt = null;
		try {
			con = CdbConnectionMgr.getConnectionMgr().getConnection(DBConstants.C4_DBPOOL_INFOSHU_INFI);
			cstmt = con.prepareCall(makeStatement(info));
			setValues(cstmt, info);
			cstmt.execute();
			getResults(cstmt, info);
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

	/**
	 * Method: setValues
	 * 
	 * @return void
	 * @param Object type CallableStatement
	 * @param Object type CProcedureInfo
	 * @throws SQLException
	 */
	protected void setValues(CallableStatement cstmt, CProcedureInfo info) throws SQLException {
		int n = info.getParamCount();
		for (int i = 0; i < n; i++) {
			CSPParameter p = info.getParameterAt(i);
			if (p.isParamOut()) {
				cstmt.registerOutParameter(i + 1, p.getDataType());
			} else {
				switch (p.getDataType()) {
				case java.sql.Types.VARCHAR:
				case java.sql.Types.CHAR:
					String vs = (String) p.getValue();
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
					if (dt != null)
						cstmt.setTimestamp(i + 1, new Timestamp(dt.getTime()));
					else
						cstmt.setNull(i + 1, Types.DATE);
					break;
				}
			}
		}
	}

	/**
	 * Method: getResults
	 * 
	 * @return void
	 * @param Object type CallableStatement
	 * @param Object type CProcedureInfo
	 * @throws SQLException
	 */
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

	/**
	 * Method: makeStatement
	 * 
	 * @param Object type CProcedureInfo
	 * @return String
	 */
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
}