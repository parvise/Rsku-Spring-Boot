package com.hp.c4.rsku.rSku.security.server.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.security.server.dao.User;
import com.hp.c4.rsku.rSku.security.server.dao.UserDAO;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.STRUCT;

/**
 * <p>
 * Title : UserDelegate.java
 * </p>
 * <p>
 * Description : It receives calls from EJB layer and business processing logic
 * is done here. It calls DAO classes methods which does the actual db
 * connectivity and other stuff.
 * </p>
 * *
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * 
 * @author Chetan/Satish/Srini
 * @version v2.0
 * @Since July 2008
 *
 *        Modifications by Srini in June 2009 as a part of C4 CORBA Removal
 *        Project H2 (PSGOTOPC09):- 1. Added the following new methods -
 *        getUserId(String sessionId) getUserGeoLevels(String userId)
 *        getUserProductLines(String userId) getUpdateGeosForUser(String
 *        sessionId) getUpdatePLsForUser(String sessionId)
 *        checkUserGroups(String sessionId, String[] groups)
 */
public class UserDelegate {

	// For logging info and errors to the log file.
	private static Logger mLogger = LogManager.getLogger(UserDelegate.class);
	private static UserDAO userdao = null;
	private static SQLUtil util = null;
	private static java.sql.Connection con = null;

	static {
		try {
			userdao = new UserDAO(UtilConstants.DB_POOL_NAME);
		} catch (Exception e) {
			try {
				throw new C4SecurityException(e.getMessage());
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * Method : maintainUser()
	 * 
	 * @param User   user
	 * @param String action
	 * @return String
	 * @throws {@link C4SecurityException}
	 */
	public String maintainUser(User user, String action) throws C4SecurityException {
		String result = null;
		try {
			result = createUpdateUser(user, action);
			mLogger.info("Result of maintainUser - " + result);
		} catch (Exception ex) {
			mLogger.error("General exception in User Creation/Updation - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return result;
	}

	/**
	 * Method: searchUser
	 * 
	 * @param userId
	 * @return ArrayList
	 * @throws {@link C4SecurityException}
	 */
	public ArrayList searchUser(String userId) throws C4SecurityException {
		ArrayList userDetails = null;
		try {
			userDetails = userdao.searchUser(userId);
			if (userDetails != null)
				mLogger.info("User List size: " + userDetails.size());
		} catch (SQLException se) {
			mLogger.error("SQL Problem in SearchUser - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in SearchUser - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userDetails;
	}

	/**
	 * Method: searchUserDetails
	 * 
	 * @param userId
	 * @return User
	 * @throws {@link C4SecurityException}
	 */
	public User searchUserDetails(String userId) throws C4SecurityException {
		User usr = null;
		try {
			usr = userdao.searchUserDetails(userId);
			mLogger.info("User Id: " + usr.getUserId());
		} catch (SQLException se) {
			mLogger.error("SQL Problem in searchUserDetails - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in searchUserDetails - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return usr;
	}

	/**
	 * Method : getAllGroups() Description : It will return all the availiable
	 * groups from DB
	 * 
	 * @return ArrayList
	 * @throws {@link C4SecurityException}
	 */
	public ArrayList getAllGroups() throws C4SecurityException {
		ArrayList groupsList = null;
		try {
			groupsList = userdao.getAllGroups();
			if (groupsList != null)
				mLogger.info("Groups List size: " + groupsList.size());
		} catch (SQLException se) {
			mLogger.error("SQL Problem in getAllGroups - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in getAllGroups - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return groupsList;
	}

	/**
	 * Method : getAppList() Description : It will return all the availiable
	 * Application Names from DB
	 * 
	 * @return ArrayList
	 * @throws {@link C4SecurityException}
	 */
	public ArrayList getAppList() throws C4SecurityException {
		ArrayList appLists = null;
		try {
			appLists = userdao.getAppList();
			if (appLists != null)
				mLogger.info("Apps List size: " + appLists.size());
		} catch (SQLException se) {
			mLogger.error("SQL Problem in getAppList - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in getAppList - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return appLists;
	}

	/**
	 * Method : getAllUserList() Description : It will return all the availiable
	 * User Names and all other details of all Users from DB
	 * 
	 * @return ArrayList
	 * @throws {@link C4SecurityException}
	 */
	public ArrayList getAllUserList() throws C4SecurityException {
		ArrayList userLists = null;
		try {
			userLists = userdao.getAllUserList();
			System.out.println(userLists.toString());
			if (userLists != null)
				mLogger.info("User List size: " + userLists.size());
		} catch (SQLException se) {
			mLogger.error("SQL Problem in getAllUserList - " + se.getCause());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in getAllUserList - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userLists;
	}

	/**
	 * Method: createUpdateUser of UserDelegate
	 * 
	 * @param User   user
	 * @param String action
	 * @return String
	 * @throws C4SecurityException
	 */
	public String createUpdateUser(User user, String action) throws C4SecurityException {
		String result = null;
		String[] gpids = null;
		try {
			util = new SQLUtil();
			con = util.getConnection();
			Object[] cos_attr = null;
			Vector cos = new Vector();
			int oracleId = CharacterSet.US7ASCII_CHARSET;
			gpids = user.getAssocGpids();
			CharacterSet cset = CharacterSet.make(oracleId);

			oracle.sql.StructDescriptor cosDesc = oracle.sql.StructDescriptor.createDescriptor("GROUPID_OBJECT", con);
			cos_attr = new Object[1];
			if (gpids != null) {
				for (int i = 0; i < gpids.length; i++) {
					cos_attr[0] = new CHAR(gpids[i], cset);
					cos.add(new STRUCT(cosDesc, con, cos_attr));
				}
			}
			ArrayDescriptor desc1 = ArrayDescriptor.createDescriptor("GROUPID_ARRAY", con);
			STRUCT[] struct1 = (STRUCT[]) cos.toArray(new STRUCT[0]);
			ARRAY new_array1 = new ARRAY(desc1, con, struct1);
			result = userdao.createUpdateUser(util, user, new_array1, action);
			mLogger.info("Result of User creation/updation - " + result);
			cos.removeAllElements();
		} catch (SQLException se) {
			mLogger.error("SQL Problem in createUpdateUser - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in createUpdateUser - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
				if (util != null)
					util.close();
			} catch (SQLException sqe) {
				throw new C4SecurityException(sqe.getMessage());
			}
		}
		return result;
	}

	/**
	 * Method : getUserId Description : Retrieve userId of the logged-in user.
	 * 
	 * @param String sessionId
	 * @return String userId
	 * @throws {@link C4SecurityException}
	 */
//By Srini - Added this method as a part of CORBA Removal H2 Project  
	public String getUserId(String sessionId) throws C4SecurityException {
		String result = null;
		try {
			result = userdao.getUserId(sessionId);
//		  mLogger.info("The Result - userId --->"+result);
		} catch (Exception ex) {
			mLogger.error("General exception in getUserId - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return result;
	}

	/**
	 * Method : getUserGeoLevels Description : gets the GeoLevels for the logged-in
	 * user.
	 * 
	 * @param String userId
	 * @return String[] userGeoLevels
	 * @throws {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUserGeoLevels(String userId) throws C4SecurityException {
		String[] userGeoLevels = null;
		try {
			userGeoLevels = userdao.getUserGeoLevels(userId);
//		  mLogger.info("The Result - userGeoLevels[] --->"+userGeoLevels);
		} catch (Exception ex) {
			mLogger.error("General exception in getUserId - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userGeoLevels;
	}

	/**
	 * Method : getUserProductLines Description : gets the Product Lines for the
	 * logged-in user.
	 * 
	 * @param String userId
	 * @return String[] userProductLines
	 * @throws {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUserProductLines(String userId) throws C4SecurityException {
		String[] userProductLines = null;
		try {
			userProductLines = userdao.getUserProductLines(userId);
//		  mLogger.info("The Result - userProductLines[] --->"+userProductLines);
		} catch (Exception ex) {
			mLogger.error("General exception in getting userProductLines - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userProductLines;
	}

	/**
	 * Method : getUpdateGeosForUser Description : gets UpdateGeos for the logged-in
	 * user
	 * 
	 * @param String sessionId
	 * @return String[] UpdateGeos for the logged-in user
	 * @throws {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUpdateGeosForUser(String sessionId) throws C4SecurityException {
		String[] userUpdateGeos = null;
		try {
			userUpdateGeos = userdao.getUpdateGeosForUser(sessionId);
// 		  mLogger.info("The Result - getUpdateGeosForUser[] --->"+userUpdateGeos);
		} catch (Exception ex) {
			mLogger.error("General exception in getting getUpdateGeosForUser - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userUpdateGeos;
	}

	/**
	 * Method : getUpdatePLsForUser Description : gets Update PLs for the logged-in
	 * user
	 * 
	 * @param String sessionId
	 * @return String[] UpdatePLs for the logged-in user
	 * @throws {@link C4SecurityException}
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public String[] getUpdatePLsForUser(String sessionId) throws C4SecurityException {
		String[] userProductLines = null;
		SQLUtil util1 = null;
		try {
			util1 = new SQLUtil(DBConstants.C4_DBPOOL_INFOSHU_INFI);
			userProductLines = userdao.getUpdatePLsForUser(sessionId);

//  		  if(userProductLines != null)
//  			  mLogger.info("The Result - getUpdatePLsForUser[] size --->"+userProductLines.length);
		} catch (Exception ex) {
			mLogger.error("General exception in getting getUpdatePLsForUser - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return userProductLines;
	}

	/**
	 * Method: checkUserGroups of UserDelegate
	 * 
	 * @param String   userId
	 * @param String[] groups
	 * @return short[] - list of values whether user is associated or not
	 * @throws C4SecurityException
	 */
	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public short[] checkUserGroups(String userId, String[] groups) throws C4SecurityException {
		mLogger.info("Entry of checkUserGroups  ..........>");
		mLogger.info("Parameters: userId ---->" + userId);
		mLogger.info("			 groups[] ---->" + Arrays.toString(groups));
		short result[] = null;
		try {
			util = new SQLUtil();
			con = util.getConnection();
			Object[] cos_attr = null;
			Vector cos = new Vector();
			int oracleId = CharacterSet.US7ASCII_CHARSET;
			CharacterSet cset = CharacterSet.make(oracleId);

			oracle.sql.StructDescriptor cosDesc = oracle.sql.StructDescriptor.createDescriptor("GROUPID_OBJECT", con);
			cos_attr = new Object[1];
			if (groups != null) {
				result = new short[groups.length];
				for (int i = 0; i < groups.length; i++) {
					cos_attr[0] = new CHAR(groups[i], cset);
					cos.add(new STRUCT(cosDesc, con, cos_attr));
				}
			}
			ArrayDescriptor desc1 = ArrayDescriptor.createDescriptor("GROUPID_ARRAY", con);
			STRUCT[] struct1 = (STRUCT[]) cos.toArray(new STRUCT[0]);
			ARRAY new_array1 = new ARRAY(desc1, con, struct1);
			ArrayList retGroups = userdao.checkUserGroups(util, userId, new_array1);
			if (groups != null) {
				result = new short[groups.length];
				for (int i = 0; i < groups.length; i++) {
					if (retGroups.contains(groups[i])) {
						result[i] = 1;
					} else {
						result[i] = 0;
					}
// 	        	mLogger.debug("result["+i+"] ----->"+result[i]);
				}
			}
			cos.removeAllElements();
		} catch (SQLException se) {
			mLogger.error("SQL Problem in checkUserGroups - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.error("General exception in checkUserGroups - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		} finally {
			try {
				if (con != null)
					con.close();
				if (util != null)
					util.close();
			} catch (SQLException sqe) {
				throw new C4SecurityException(sqe.getMessage());
			}
		}
		mLogger.info("Exit of checkUserGroups  <...........");
		return result;
	}

	public static void main(String[] args) {

	}
}