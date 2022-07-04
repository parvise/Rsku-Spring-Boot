package com.hp.c4.rsku.rSku.security.server.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CProcedureInfo;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CSPParameter;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;

/**
 * <p>
 * Title : UserDAO.java
 * </p>
 * <p>
 * Description : It deals with User Maintenance like adduser, update user,
 * delete users. It Extends ObjectIO class. All db activity is done here.
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Chetan/Satish/Srini @ version v2.0 @ Since July 2008
 */
public class UserDAO extends ObjectDAO {

	// For logging info and errors to the log file.
	private static Logger mLogger = LogManager.getLogger(UserDAO.class);
	private String OUT_PARAM = "out_param";
	private String OUT_PARAM1 = "out_param1";

	/**
	 * Method: UserDAO (constructor)
	 * 
	 * @param String dbname
	 */
	public UserDAO(String dbname) {
		super(dbname);
	}

	/**
	 * Method: createUser
	 * 
	 * @param User user
	 * @return String
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public String createUser(User user) throws SQLException, C4SecurityException {
		return (String) callSp(getCreateUserSpInfo(user));
	}

	/**
	 * Method: getUserId
	 * 
	 * @param String sessionId
	 * @return String userId
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String getUserId(String sessionId) throws SQLException, C4SecurityException {
		return (String) callSp(getUserIdSpInfo(sessionId));
	}

	/**
	 * Method: getUserGeoLevels
	 * 
	 * @param String userId
	 * @return String[] UserGeoLevels
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUserGeoLevels(String userId) throws SQLException, C4SecurityException {
		return (String[]) callSp(getUserGeoLevelsSpInfo(userId));
	}

	/**
	 * Method: getUserProductLines
	 * 
	 * @param String userId
	 * @return String[] userProductLines
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUserProductLines(String userId) throws SQLException, C4SecurityException {
		return (String[]) callSp(getUserProductLinesSpInfo(userId));
	}

	/**
	 * Method: getUpdateGeosForUser
	 * 
	 * @param String sessionId
	 * @return String[] UpdateGeos for the logged-in user
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUpdateGeosForUser(String sessionId) throws SQLException, C4SecurityException {
		return (String[]) callSp(getUpdateGeosForUserSpInfo(getUserId(sessionId)));
	}

	/**
	 * Method: getUpdatePLsForUser
	 * 
	 * @param String sessionId
	 * @return String[] UpdatePLs for the logged-in user
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUpdatePLsForUser(String sessionId) throws SQLException, C4SecurityException {
		return (String[]) callSp(getUpdatePLsForUserSpInfo(getUserId(sessionId)));
	}

	/**
	 * Method: getAllGroups
	 * 
	 * @return ArrayList
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public ArrayList getAllGroups() throws SQLException, C4SecurityException {
		return (ArrayList) callSp(getAllGroupSpInfo());
	}

	/**
	 * Method: getAppList
	 * 
	 * @return ArrayList
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public ArrayList getAppList() throws SQLException, C4SecurityException {
		return (ArrayList) callSp(getAppListSpInfo());
	}

	/**
	 * Method: getAllUserList
	 * 
	 * @return ArrayList
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public ArrayList getAllUserList() throws SQLException, C4SecurityException {
		return (ArrayList) callSp(getAllUserListSpInfo());
	}

	/**
	 * Method: searchUser
	 * 
	 * @param userId
	 * @return ArrayList
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public ArrayList searchUser(String userId) throws SQLException, C4SecurityException {
		return (ArrayList) callSp(getsearchUserSpInfo(userId));
	}

	/**
	 * Method: searchUserDetails
	 * 
	 * @param userId
	 * @return User
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public User searchUserDetails(String userId) throws SQLException, C4SecurityException {
		return (User) callSp(getSearchUserDetailsSpInfo(userId));
	}

	/**
	 * Method: updateUser Description:
	 * 
	 * @param User user
	 * @return User[]
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public String updateUser(User user) throws SQLException, C4SecurityException {

		return (String) callSp(getUpdateUserSpInfo(user));
	}

	/**
	 * Method: searchUser
	 * 
	 * @param String userId
	 * @param String emailId
	 * @return User[]
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public User[] searchUser(String userId, String emailId) throws SQLException, C4SecurityException {
		return (User[]) callSp(getSearchUserSpInfo(userId, emailId));
	}

	/**
	 * Method: deleteUsers
	 * 
	 * @param String[] userIds
	 * @return User[]
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public User[] deleteUsers(String[] userIds) throws SQLException, C4SecurityException {
		return (User[]) callSp(getDeleteUsersSpInfo(userIds));
	}

	/**
	 * Method: getCreateUserSpInfo Description: {@link UtilConstants.CREATE_USER_SP}
	 * 
	 * @param User user
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getCreateUserSpInfo(User user) {
		CSPParameter[] cpa = getCreateUserParams(user);
		return new CProcedureInfo(UtilConstants.CREATE_USER_SP, cpa);
	}

	/**
	 * Method: getUserIdSpInfo Description: {@link UtilConstants.GET_USER_ID}
	 * 
	 * @param String sessionId
	 * @return CProcedureInfo
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CProcedureInfo getUserIdSpInfo(String sessionId) {
		CSPParameter[] cpa = getUserIdParams(sessionId);
		return new CProcedureInfo(UtilConstants.GET_USERID, cpa);
	}

	/**
	 * Method: getUserGeoLevelsSpInfo Description:
	 * {@link UtilConstants.GET_USER_GEO_LEVELS}
	 * 
	 * @param String userId
	 * @return CProcedureInfo
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CProcedureInfo getUserGeoLevelsSpInfo(String userId) {
		CSPParameter[] cpa = getUserGeoLevelsParams(userId);
		return new CProcedureInfo(UtilConstants.GET_USER_GEO_LEVELS, cpa);
	}

	/**
	 * Method: getUserProductLinesSpInfo Description:
	 * {@link UtilConstants.GET_USER_PROD_LINES}
	 * 
	 * @param String userId
	 * @return CProcedureInfo
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CProcedureInfo getUserProductLinesSpInfo(String userId) {
		CSPParameter[] cpa = getUserProductLinesParams(userId);
		return new CProcedureInfo(UtilConstants.GET_USER_PROD_LINES, cpa);
	}

	/**
	 * Method: getUpdateGeosForUserSpInfo Description:
	 * {@link UtilConstants.GET_USER_UPDATE_GEOS}
	 * 
	 * @param String sessionId
	 * @return CProcedureInfo
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CProcedureInfo getUpdateGeosForUserSpInfo(String sessionId) {
		CSPParameter[] cpa = getUpdateGeosForUserParams(sessionId);
		return new CProcedureInfo(UtilConstants.GET_USER_UPDATE_GEOS, cpa);
	}

	/**
	 * Method: getUpdatePLsForUserSpInfo Description:
	 * {@link UtilConstants.GET_USER_UPDATE_PLS}
	 * 
	 * @param String sessionId
	 * @return CProcedureInfo
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CProcedureInfo getUpdatePLsForUserSpInfo(String sessionId) {
		CSPParameter[] cpa = getUpdatePLsForUserParams(sessionId);
		return new CProcedureInfo(UtilConstants.GET_USER_UPDATE_PLS, cpa);
	}

	/**
	 * Method: getAllGroupSpInfo Description: {@link UtilConstants.GET_ALL_GROUPS}
	 * 
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getAllGroupSpInfo() {
		CSPParameter[] cpa = getAllGroupsParams();
		return new CProcedureInfo(UtilConstants.GET_ALL_GROUPS, cpa);
	}

	/**
	 * Method: getAppListSpInfo Description: {@link UtilConstants.GET_APP_LIST}
	 * 
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getAppListSpInfo() {
		CSPParameter[] cpa = getAppListParams();
		return new CProcedureInfo(UtilConstants.GET_APP_LIST, cpa);
	}

	/**
	 * Method: getAllUserListSpInfo Description: {@link UtilConstants.GET_APP_LIST}
	 * 
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getAllUserListSpInfo() {
		CSPParameter[] cpa = getAllUserListParams();
		return new CProcedureInfo(UtilConstants.GET_USER_LIST, cpa);
	}

	/**
	 * Method: getsearchUserSpInfo Description:
	 * {@link UtilConstants.SEARCH_USER_DETAILS_SP}
	 * 
	 * @param String userId
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getsearchUserSpInfo(String userId) {
		CSPParameter[] cpa = getsearchUserParams(userId);
		return new CProcedureInfo(UtilConstants.SEARCH_USER_DETAILS_SP, cpa);
	}

	/**
	 * Method: getSearchUserDetailsSpInfo Description:
	 * {@link UtilConstants.SEARCH_USER_GROUP_DETAILS_SP}
	 * 
	 * @param String userId
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getSearchUserDetailsSpInfo(String userId) {
		CSPParameter[] cpa = getsearchUserGroupParams(userId);
		return new CProcedureInfo(UtilConstants.SEARCH_USER_GROUP_DETAILS_SP, cpa);
	}

	/**
	 * Method: getSearchUserSpInfo Description:
	 * {@link UtilConstants.SEARCH_USER_DETAILS_SP}
	 * 
	 * @param String userId
	 * @param String emailId
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getSearchUserSpInfo(String userId, String emailId) {
		CSPParameter[] cpa = getSearchParams(userId, emailId);
		return new CProcedureInfo(UtilConstants.SEARCH_USER_DETAILS_SP, cpa);
	}

	/**
	 * Method: getUpdateUserSpInfo Description:
	 * {@link UtilConstants.UPDATE_USER_LOGIN_SP}
	 * 
	 * @param User user
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getUpdateUserSpInfo(User user) {
		CSPParameter[] cpa = getUpdateUserParams(user);

		return new CProcedureInfo(UtilConstants.UPDATE_USER_SP, cpa);
	}

	/**
	 * Method: getDeleteUsersSpInfo Description:
	 * {@link UtilConstants.DELETE_USER_LOGIN_SP}
	 * 
	 * @param String[] userIds
	 * @return CProcedureInfo
	 */
	private CProcedureInfo getDeleteUsersSpInfo(String[] userIds) {
		CSPParameter[] cpa = getDeleteUsersParams(userIds);
		return new CProcedureInfo(UtilConstants.DELETE_USER_LOGIN_SP, cpa);
	}

	/**
	 * Method: getCreateUserParams
	 * 
	 * @param User user
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getCreateUserParams(User user) {
		CSPParameter[] cpa = new CSPParameter[10];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(user.getUserId());
		cpa[1] = new CSPParameter(Types.VARCHAR, true, "appname");
		cpa[1].setValue(user.getAppName());
		cpa[2] = new CSPParameter(Types.VARCHAR, true, "pswd");
		cpa[2].setValue(user.getPasswd());
		cpa[3] = new CSPParameter(Types.VARCHAR, true, "fullname");
		cpa[3].setValue(user.getLastName() + " " + user.getFirstName());
		cpa[4] = new CSPParameter(Types.VARCHAR, true, "email");
		cpa[4].setValue(user.getEmailAddress());
		cpa[5] = new CSPParameter(Types.VARCHAR, true, "usertype");
		cpa[5].setValue(user.getUserType());
		cpa[6] = new CSPParameter(Types.VARCHAR, true, "status");
		cpa[6].setValue(user.getStatus());
		cpa[7] = new CSPParameter(Types.VARCHAR, true, "expiredate");
		cpa[7].setValue(user.getExpiryDate());
		cpa[8] = new CSPParameter(Types.VARCHAR, true, "grouparray");
		cpa[8].setValue(user.getAssociatedGroups());
		cpa[9] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getUserIdParams
	 * 
	 * @param String sessionId
	 * @return CSPParameter[]
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CSPParameter[] getUserIdParams(String sessionId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "sessionId");
		cpa[0].setValue(sessionId);
		cpa[1] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getAllGroupsParams
	 * 
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllGroupsParams() {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getAppListParams
	 * 
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAppListParams() {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getAllUserListParams
	 * 
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getAllUserListParams() {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getsearchUserParams
	 * 
	 * @param String userId
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getsearchUserParams(String userId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(userId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getUserGeoLevelsParams
	 * 
	 * @param String userId
	 * @return CSPParameter[]
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CSPParameter[] getUserGeoLevelsParams(String userId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(userId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getUserProductLinesParams
	 * 
	 * @param String userId
	 * @return CSPParameter[]
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CSPParameter[] getUserProductLinesParams(String userId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(userId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getUpdateGeosForUserParams
	 * 
	 * @param String sessionId
	 * @return CSPParameter[]
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CSPParameter[] getUpdateGeosForUserParams(String sessionId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(sessionId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getUpdatePLsForUserParams
	 * 
	 * @param String sessionId
	 * @return CSPParameter[]
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	private CSPParameter[] getUpdatePLsForUserParams(String sessionId) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "sessionid");
		cpa[0].setValue(sessionId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getsearchUserGroupParams
	 * 
	 * @param String userId
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getsearchUserGroupParams(String userId) {
		CSPParameter[] cpa = new CSPParameter[3];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(userId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM);
		cpa[2] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, OUT_PARAM1);
		return cpa;
	}

	/**
	 * Method: getUpdateUserParams
	 * 
	 * @param User user
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getUpdateUserParams(User user) {
		CSPParameter[] cpa = new CSPParameter[10];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "userid");
		cpa[0].setValue(user.getUserId());
		cpa[1] = new CSPParameter(Types.VARCHAR, true, "appname");
		cpa[1].setValue(user.getAppName());
		cpa[2] = new CSPParameter(Types.VARCHAR, true, "pswd");
		cpa[2].setValue(user.getPasswd());
		cpa[3] = new CSPParameter(Types.VARCHAR, true, "fullname");
		cpa[3].setValue(user.getLastName() + " " + user.getFirstName());
		cpa[4] = new CSPParameter(Types.VARCHAR, true, "email");
		cpa[4].setValue(user.getEmailAddress());
		cpa[5] = new CSPParameter(Types.VARCHAR, true, "usertype");
		cpa[5].setValue(user.getUserType());
		cpa[6] = new CSPParameter(Types.VARCHAR, true, "status");
		cpa[6].setValue(user.getStatus());
		cpa[7] = new CSPParameter(Types.VARCHAR, true, "expiredate");
		cpa[7].setValue(user.getExpiryDate());
		cpa[8] = new CSPParameter(Types.VARCHAR, true, "grouparray");
		cpa[8].setValue(user.getAssociatedGroups());
		cpa[9] = new CSPParameter(Types.VARCHAR, false, OUT_PARAM);
		return cpa;
	}

	/**
	 * Method: getDeleteUsersParams Description:
	 * 
	 * @param String[] userIds
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getDeleteUsersParams(String[] userIds) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.ARRAY, true, "ARRAY");
		cpa[0].setValue(userIds);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.ARRAY, false, "ARRAY");
		return cpa;
	}

	/**
	 * Method: getSearchParams
	 * 
	 * @param String userId
	 * @param String emailId
	 * @return CSPParameter[]
	 */
	private CSPParameter[] getSearchParams(String userId, String emailId) {
		CSPParameter[] cpa = new CSPParameter[3];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "USERID");
		cpa[0].setValue(userId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "EMAILID");
		cpa[1].setValue(emailId);
		cpa[2] = new CSPParameter(Types.ARRAY, false, "ARRAY");
		return cpa;
	}

	/**
	 * Method: createUpdateUser of UserDAO
	 * 
	 * @param SQLUtil          util
	 * @param User             user
	 * @param String           action
	 * @param oracle.sql.ARRAY ACL_ARRAY
	 * @return String
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public String createUpdateUser(SQLUtil util, User user, oracle.sql.ARRAY GPID_ARRAY, String action)
			throws C4SecurityException, SQLException {
		if (action.equalsIgnoreCase("CREATE")) {
			// mLogger.info("Calling the SP USERMAINTENANCE.insertusers(?,?,?,?,?,?,?,?,?,?)
			// ");
			util.setSQL("{call USERMAINTENANCE.insertusers(?,?,?,?,?,?,?,?,?,?) }");
		} else if (action.equalsIgnoreCase("UPDATE")) {
			// mLogger.info("Calling the SP USERMAINTENANCE.updateUser(?,?,?,?,?,?,?,?,?,?)
			// ");
			util.setSQL("{call USERMAINTENANCE.updateUser(?,?,?,?,?,?,?,?,?,?) }");
		}
		util.setString(1, user.getUserId());
		util.setString(2, user.getAppName());
		util.setString(3, user.getPasswd());
		util.setString(4, user.getLastName() + "~" + user.getFirstName());
		util.setString(5, user.getEmailAddress());
		util.setString(6, user.getUserType());
		util.setString(7, user.getStatus());
		if (user.getExpiryDate() != null)
			util.setDate(8, user.getExpiryDate().getTime());
		else
			util.setDate(8, null);

		util.setObject(9, GPID_ARRAY);
		util.registerOutParameter(10, oracle.jdbc.OracleTypes.VARCHAR);
		util.execute();
		String status = (String) util.getObject(10);
		mLogger.info("Result of User Creation - " + status);
		if (status.equalsIgnoreCase("FAILURE"))
			throw new C4SecurityException("Data insertion Failed");
		return status;
	}

	/**
	 * Method: checkUserGroups of UserDAO
	 * 
	 * @param String           userId
	 * @param String[]         groups
	 * @param oracle.sql.ARRAY ACL_ARRAY
	 * @return short[] - list of vales whether user is associated or not
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public ArrayList checkUserGroups(SQLUtil util, String userId, oracle.sql.ARRAY GPID_ARRAY)
			throws SQLException, C4SecurityException {
		ArrayList result = new ArrayList();
		util.setSQL("{call USERMAINTENANCE.checkusergroups(?,?,?) }");
		util.setString(1, userId);
		util.setObject(2, GPID_ARRAY);
		util.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
//	mLogger.info("Calling the SP USERMAINTENANCE.checkusergroups(?,?,?) ----->");
		util.execute();
		ResultSet rs = (ResultSet) util.getObject(3);
		while (rs.next()) {
			result.add(rs.getString(1));
		}
		if (rs != null)
			rs.close();

		return result;
	}

	/**
	 * Method: processResult
	 * 
	 * @return Object
	 * @param CProcedureInfo info
	 * @throws {@link SQLException} , {@link C4SecurityException}
	 */
	public Object processResult(CProcedureInfo info) throws SQLException, C4SecurityException {
		User userdetails = null;
		ResultSet rs = null;
		String gname = null;
		if (info.getName().equals(UtilConstants.CREATE_USER_SP) || info.getName().equals(UtilConstants.UPDATE_USER_SP)
				|| info.getName().equals(UtilConstants.GET_USERID))
			return info.getParamValue(OUT_PARAM);

		rs = (ResultSet) info.getParamValue(OUT_PARAM);

		if (info.getName().equals(UtilConstants.GET_ALL_GROUPS)) {
			ArrayList gnames = null;
			Group gpbean = null;
			try {
				gnames = new ArrayList();
				while (rs.next()) {
					gpbean = new Group();
					gpbean.setGroupId(String.valueOf(rs.getInt("GROUPID_")));
					gpbean.setGroupName(rs.getString("NAME_"));
					gpbean.setGroupDescription(rs.getString("DESCRIPTION_"));
					gnames.add(gpbean);
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.error("SQL Exception in processResult for GET_ALL_GROUPS: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_ALL_GROUPS: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			return gnames;
		}

		if (info.getName().equals(UtilConstants.GET_APP_LIST)) {
			ArrayList appNames = null;
			try {
				appNames = new ArrayList();
				String appName = null;
				while (rs.next()) {
					appName = rs.getString("appname_");
					appNames.add(appName);
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.info("SQL Exception in processResult for GET_APP_LIST: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_APP_LIST: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			// mLogger.info("App Names size: "+appNames.size());
			return appNames;
		}

		if (info.getName().equals(UtilConstants.SEARCH_USER_DETAILS_SP)
				|| info.getName().equals(UtilConstants.GET_USER_LIST)) {
			ArrayList usersList = null;
			try {
				String fname = "", lname = "";
				usersList = new ArrayList();
				while (rs.next()) {
					userdetails = new User();
					userdetails.setUserId(rs.getString("USERID_"));
					String fullName = rs.getString("FULLNAME_");
					if (fullName != null) {
						StringTokenizer stk = new StringTokenizer(fullName, "~");
						if (fullName.indexOf("~") != -1) {
							if (stk.hasMoreTokens())
								lname = stk.nextToken();
							if (stk.hasMoreTokens())
								fname = stk.nextToken();
						} else {
							if (fullName.indexOf(" ") != -1) {
								lname = fullName.substring(0, fullName.indexOf(" "));
								fname = fullName.substring(fullName.indexOf(" "), fullName.length());
							} else {
								fname = fullName;
								lname = "";
							}
						}
					}
					userdetails.setFirstName(fname);
					userdetails.setLastName(lname);

					userdetails.setEmailAddress(rs.getString("EMAIL_ADDRESS"));
					userdetails.setAppName(rs.getString("APPNAME_"));
					userdetails.setStatus(rs.getString("PARTYSTATUS_"));
					usersList.add(userdetails);
				}
				if (rs != null)
					rs.close();

			} catch (SQLException se) {
				mLogger.error("SQL Exception in processResult for SEARCH_USER_DETAILS_SP: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for SEARCH_USER_DETAILS_SP: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			// mLogger.info("User List size: "+usersList.size());
			return usersList;
		}
		if (info.getName().equals(UtilConstants.SEARCH_USER_GROUP_DETAILS_SP)) {
			User usr = null;
			String fullName = "", fname = "", lname = "";
			java.sql.Date crdt = null, expdt = null, lmdt = null;
			Calendar excal = null, crcal = null, lmcal = null;
			ArrayList gnames = null;
			ResultSet rs1 = null;
			try {
				usr = new User();
				rs1 = (ResultSet) info.getParamValue(OUT_PARAM1);
				gnames = new ArrayList();
				SimpleDateFormat dtfmt = new SimpleDateFormat("MM/dd/yyyy");
				SimpleDateFormat tmfmt = new SimpleDateFormat("HH:mm:ss aaa");
				int cnt = 1;
				while (rs.next()) {
					usr.setUserId(rs.getString("USERID_"));
					fullName = rs.getString("FULLNAME_");
					if (fullName != null) {
						StringTokenizer stk = new StringTokenizer(fullName, "~");
						if (fullName.indexOf("~") != -1) {
							if (stk.hasMoreTokens())
								lname = stk.nextToken();
							if (stk.hasMoreTokens())
								fname = stk.nextToken();
						} else {
							if (fullName.indexOf(" ") != -1) {
								lname = fullName.substring(0, fullName.indexOf(" "));
								fname = fullName.substring(fullName.indexOf(" "), fullName.length());
							} else {
								fname = fullName;
								lname = "";
							}
						}
					}
					usr.setFirstName(fname);
					usr.setLastName(lname);
					usr.setEmailAddress(rs.getString("EMAIL_ADDRESS"));
					usr.setAppName(rs.getString("APPNAME_"));
					usr.setPasswd(rs.getString("PSWD_"));

					crdt = rs.getDate("CREATEDDATE_");
					if (crdt != null) {
						crcal = Calendar.getInstance();
						crcal.setTime(crdt);
						usr.setCreationDate(crcal);
					}
					expdt = rs.getDate("EXPIREDATE_");
					if (expdt != null) {
						excal = Calendar.getInstance();
						excal.setTime(expdt);
						usr.setExpiryDate(excal);
					}
					lmdt = rs.getDate("LASTMODDATE_");
					if (lmdt != null) {
						lmcal = Calendar.getInstance();
						lmcal.setTime(lmdt);
						usr.setLastModDate(lmcal);
					}
					usr.setStatus(rs.getString("PARTYSTATUS_"));
					usr.setUserType(rs.getString("USERTYPE_"));
					usr.setAction("update");
				}
				if (rs != null)
					rs.close();

				while (rs1.next()) {
					gnames.add(rs1.getString("groupid_") + "~" + rs1.getString("name_"));
				}
				if (rs1 != null)
					rs1.close();

				String gpnms = null;
				for (int i = 0; i < gnames.size(); i++) {
					if (i == 0)
						gpnms = (String) gnames.get(i);
					else
						gpnms = gpnms + "," + (String) gnames.get(i);
				}
				usr.setAssociatedGroups(gpnms);
			} catch (SQLException sqe) {
				mLogger.error("SQL Exception in processResult for SEARCH_USER_GROUP_DETAILS_SP: " + sqe.getMessage());
				throw new C4SecurityException(sqe.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for SEARCH_USER_GROUP_DETAILS_SP: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			// mLogger.info("User Id - "+usr.getUserId());
			return usr;
		}

		// By Srini - Added as a part of CORBA Removal H2 Project
		if (info.getName().equals(UtilConstants.GET_USER_GEO_LEVELS)) {
			ArrayList userGeoLevels = null;
			try {
				userGeoLevels = new ArrayList();
				while (rs.next()) {
					userGeoLevels.add(rs.getString(1));
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.info("SQL Exception in processResult for GET_USER_GEO_LEVELS: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_USER_GEO_LEVELS: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			return (String[]) userGeoLevels.toArray(new String[0]);
		}

		// By Srini - Added as a part of CORBA Removal H2 Project
		if (info.getName().equals(UtilConstants.GET_USER_PROD_LINES)) {
			ArrayList userProdLines = null;
			try {
				userProdLines = new ArrayList();
				while (rs.next()) {
					userProdLines.add(rs.getString(1));
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.info("SQL Exception in processResult for GET_USER_PROD_LINES: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_USER_PROD_LINES: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			return (String[]) userProdLines.toArray(new String[0]);
		}

		// By Srini - Added as a part of CORBA Removal H2 Project
		if (info.getName().equals(UtilConstants.GET_USER_UPDATE_GEOS)) {
			ArrayList userUpdateGeos = null;
			try {
				userUpdateGeos = new ArrayList();
				while (rs.next()) {
					userUpdateGeos.add(rs.getString(1));
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.info("SQL Exception in processResult for GET_USER_UPDATE_GEOS: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_USER_UPDATE_GEOS: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			return (String[]) userUpdateGeos.toArray(new String[0]);
		}

		// By Srini - Added as a part of CORBA Removal H2 Project
		if (info.getName().equals(UtilConstants.GET_USER_UPDATE_PLS)) {
			ArrayList userUpdatePLs = null;
			try {
				userUpdatePLs = new ArrayList();
				while (rs.next()) {
					userUpdatePLs.add(rs.getString(1));
				}
				if (rs != null)
					rs.close();
			} catch (SQLException se) {
				mLogger.info("SQL Exception in processResult for GET_USER_UPDATE_PLS: " + se.getMessage());
				throw new C4SecurityException(se.getMessage());
			} catch (Exception e) {
				mLogger.error("General Exception in processResult for GET_USER_UPDATE_PLS: " + e.getMessage());
				throw new C4SecurityException(e.getMessage());
			}
			return (String[]) userUpdatePLs.toArray(new String[0]);
		}

		if (rs != null)
			rs.close();

		return info.getParamValue(OUT_PARAM);
	}

}