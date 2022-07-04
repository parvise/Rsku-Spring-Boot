package com.hp.c4.rsku.rSku.security.server.dao;

import java.sql.SQLException;
import java.sql.Types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CProcedureInfo;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CSPParameter;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;

/**
 * <p>
 * Title : LoginDAO.java
 * </p>
 * <p>
 * Description : It deals with authenticate and validate login from all
 * applications. It Extends ObjectIO class. All db activity is done here.
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Chetan/Satish/Srini @ version v1.0 @ Since June 2008
 */
public class LoginDAO extends ObjectDAO {

	public static final String LOGIN = "login";
	public static final String PASSWD = "passwd";
	public static final String COUNT = "count";
	public static final String SESSION_ID = "session_id";
	public static final String APP_USER_ID = "appuserid";
	public static final String USER_ID = "user_id";
	private String OUT_PARAM = "out_param";
	// For logging info and errors to the log file.
	private static Logger mLogger = LogManager.getLogger(LoginDAO.class);

	/**
	 * Method: LoginDAO (constructor)
	 * 
	 * @param String dbname
	 */
	public LoginDAO(String dbname) {
		super(dbname);
	}

	/**
	 * Method :authenticate Description :This method calls getValidateSpInfo of
	 * LoginDAO
	 * 
	 * @param loginID
	 * @param pass
	 * @param app_name
	 * @return
	 * @throws SQLException
	 * @throws C4SecurityException
	 * @author Chetan/Satish/Srini
	 * @version v2.0
	 * @since June 2008
	 */
	public String authenticate(String loginID, String pass, String app_name) throws SQLException, C4SecurityException {
		return (String) callSp(getValidateSpInfo(loginID, pass, app_name));
	}

	/**
	 * <p>
	 * Method: logout of LoginDAO.java Description: It logs out of the session and
	 * makes the status in t_session as INACT
	 * </p>
	 * 
	 * @param String sessionId
	 * @param String userId
	 * @param String appname
	 * @return String
	 * @throws {@link C4SecurityException}
	 * @throws {@link SQLException}
	 */
	public String logout(String sessionId, String userId, String appname) throws SQLException, C4SecurityException {
		return (String) callSp(getLogoutSpInfo(sessionId, userId, appname));
	}

	public String logout(String sessionId) throws SQLException, C4SecurityException {
		return (String) callSp(getLogoutSpInfo(sessionId));
	}

	/**
	 * Method: getAppUserByEmail Description :This method calls getAppUserIDSpInfo
	 * of LoginDAO
	 * 
	 * @param String loginID
	 * @return String
	 * @throws SQLException
	 * @throws C4SecurityException
	 * @author Chetan/Satish/Srini
	 * @version v2.0
	 * @since June 2008
	 */
	public String getAppUserByEmail(String loginId) throws SQLException, C4SecurityException {
		return (String) callSp(getAppUserIDSpInfo(loginId));
	}

	/**
	 * Method: createSession Description :This method calls getCreateSessionInfo of
	 * LoginDAO
	 * 
	 * @param String loginID
	 * @param String app_name
	 * @return String
	 * @throws SQLException
	 * @throws C4SecurityException
	 * @author Chetan/Satish/Srini
	 * @version v2.0
	 * @since June 2008
	 */

	public String createSession(String loginID, String app_name) throws SQLException, C4SecurityException {
		return (String) callSp(getCreateSessionInfo(loginID, app_name));
	}

	/**
	 * Method: processResult
	 * 
	 * @return Object
	 * @param CProcedureInfo info
	 */
	public Object processResult(CProcedureInfo info) throws SQLException, C4SecurityException {
		return (Object) info.getParamValue(OUT_PARAM);
	}

	/**
	 * Method: getLogoutSpInfo
	 * 
	 * @param String sessionId
	 * @param String userId
	 * @param String appname
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getLogoutSpInfo(String sessionId, String userId, String appname) {
		CSPParameter[] cpa = getAllLogoutParams(sessionId, userId, appname);
		return new CProcedureInfo(UtilConstants.LOGOUT_SP, cpa);
	}

	private CProcedureInfo getLogoutSpInfo(String sessionId) {
		CSPParameter[] cpa = getAllLogoutParams(sessionId);
		return new CProcedureInfo(UtilConstants.SESSIONLOGOUT_SP, cpa);
	}

	/**
	 * Method: getAllLogoutParams
	 * 
	 * @param String sessionId
	 * @param String userId
	 * @param String appname
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllLogoutParams(String sessionId, String userId, String appname) {
		CSPParameter[] cpa = new CSPParameter[4];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "sessionid");
		cpa[0].setValue(sessionId);
		cpa[1] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[1].setValue(userId);
		cpa[2] = new CSPParameter(Types.VARCHAR, true, "APP_NAME");
		cpa[2].setValue(appname);
		cpa[3] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	private CSPParameter[] getAllLogoutParams(String sessionId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "sessionid");
		cpa[0].setValue(sessionId);
		cpa[1] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getCreateSessionInfo Description: This method calls getAllParams of
	 * LoginDAO and CProcedureInfo of CProcedureInfo
	 * 
	 * @param String user
	 * @param String pass
	 * @param String app_name
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getCreateSessionInfo(String user, String app_name) {
		CSPParameter[] cpa = getAllParams(user, app_name);
		return new CProcedureInfo(UtilConstants.CREATE_SESSION_SP, cpa);
	}

	/**
	 * Method: getValidateSpInfo
	 * 
	 * @param String user
	 * @param String pass
	 * @param String app_name
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getValidateSpInfo(String user, String pass, String app_name) {
		CSPParameter[] cpa = getAllParams(user, pass, app_name);
		return new CProcedureInfo(UtilConstants.AUTHENTICATE_SP, cpa);
	}

	/**
	 * Method: getAppUserIDSpInfo
	 * 
	 * @param String user
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getAppUserIDSpInfo(String user) {
		CSPParameter[] cpa = getAllParams(user);
		return new CProcedureInfo(UtilConstants.GET_APP_USER_ID_SP, cpa);
	}

	/**
	 * Method: getAllParams
	 * 
	 * @param String loginID
	 * @param String passwd
	 * @param String app_name
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllParams(String loginID, String passwd, String app_name) {
		CSPParameter[] cpa = new CSPParameter[4];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, LOGIN);
		cpa[0].setValue(loginID);
		cpa[1] = new CSPParameter(Types.VARCHAR, true, PASSWD);
		cpa[1].setValue(passwd);
		cpa[2] = new CSPParameter(Types.VARCHAR, true, "APP_NAME");
		cpa[2].setValue(app_name);
		// OUT_PARAM = SESSION_ID;
		cpa[3] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getAllParams
	 * 
	 * @param String loginID
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllParams(String loginID) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, LOGIN);
		cpa[0].setValue(loginID);
		// OUT_PARAM = APP_USER_ID;
		cpa[1] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getAllParams
	 * 
	 * @param String loginID
	 * @param String passwd
	 * @param String app_name
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllParams(String loginID, String app_name) {
		CSPParameter[] cpa = new CSPParameter[3];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, LOGIN);
		cpa[0].setValue(loginID);
		cpa[1] = new CSPParameter(Types.VARCHAR, true, "APP_NAME");
		cpa[1].setValue(app_name);
		cpa[2] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}
}