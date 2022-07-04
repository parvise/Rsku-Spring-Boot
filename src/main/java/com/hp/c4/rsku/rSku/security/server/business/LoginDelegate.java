package com.hp.c4.rsku.rSku.security.server.business;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.security.server.dao.LoginDAO;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.LDAPUtil;

/**
 * <p>
 * Title : LoginDelegate.java
 * </p>
 * <p>
 * Description : It receives calls from EJB layer and business processing logic
 * is done here. It calls DAO classes methods which does the actual db
 * connectivity and other stuff.
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * 
 * @author Chetan/Satish/Srini
 * @version v1.0
 * @Since June 2008
 * 
 */
public class LoginDelegate {
	private static final Logger mLogger = LogManager.getLogger(LoginDelegate.class);
	private static LoginDAO logindao = null;

	public LoginDelegate() {
	}

	// This static block initalizes the LDAP Properties file and creates LoginDAO
	// object
	static {
		try {
			logindao = new LoginDAO(DBConstants.C4_DBPOOL_INFOSHU_INFI); // LoginDAO object created which internally
																			// call
			// ObjectDAO which intializes DBPOOL
		} catch (Exception ex) {
			try {
				mLogger.fatal("Exception in logidDAO object creation -----> " + ex.getMessage());
				throw new C4SecurityException(ex.getMessage());
			} catch (Exception cse) {
			}
		}
	}

	/**
	 * Method : authenticate Description : It calls the authenticate() method of
	 * LoginDAO
	 * 
	 * @params String loginId, String passwd, String app_name
	 * @throws {@link C4SecurityException}
	 * @return String
	 */
	public String authenticate(String loginId, String passwd, String app_name) throws C4SecurityException {
		String result = null;
		String userId = null;
		String temp = null;
		String email = null;
		boolean email_auth = false;
		boolean isServiceAccountLevel = false;
		mLogger.info("Calling authenticate method ----> ");
		mLogger.info("loginId --------->" + loginId);
		mLogger.info("app_name -------->" + app_name);
		// mLogger.info("passwd -------->"+passwd);
		try {
			// This wil check whether the loginId is emailId or not.
			// If email then it returns the userID for this email id
			temp = logindao.getAppUserByEmail(loginId);
			mLogger.info("temp ----->" + temp);
			if (temp != null && !temp.equalsIgnoreCase("NO USERS FOUND.")) {
				email = loginId;
				loginId = temp;
			}
			// If loginId is emailId the LDAP authentication happens which returns boolean
			// whether success or failure
			if (email != null && email.length() > 0) {
				mLogger.info("email ------->" + email);
				try {
					app_name = "C4GUI";
					email_auth = (new LDAPUtil(System.getProperties())).authenticateUser(email, passwd);
//					mLogger.info("The Result of LDAP user authentication ---->"+email_auth);
				} catch (Exception iue) {
					throw new C4SecurityException(iue.getMessage());
				}
			}

			mLogger.info("email_auth ------------>" + email_auth);

			// It checks if app name is C4SOAP and Its Not a valid emailId (Its service
			// account starts with $) authenticate Service Account level valid or not
			if (app_name.equalsIgnoreCase("C4RSKU") && !email_auth) {
				isServiceAccountLevel = (new LDAPUtil(System.getProperties())).authenticateServiceAccountLevel(loginId,
						new String(passwd));
				if (!isServiceAccountLevel) {
					result = "INVALID";
					mLogger.info("The Result of Authentication ---------->" + result);
					return result;
				}
			}

			// If Ldap Authentication is sucessful then sessionId is created otherwise
			// loginId and password are validated against the database
			// if the validation is sucessful it will create session else it will return
			// string "INVALID"
			if (email_auth) {
				result = logindao.createSession(loginId, app_name);
			} else {
				String encoded = null;
				if (passwd != null) {
					encoded = passwd;
				}

				if (isServiceAccountLevel) {
					encoded = null;
				}
				mLogger.info("isServiceAccountLevel----> " + isServiceAccountLevel + ": app_name" + app_name);
				result = logindao.authenticate(loginId, encoded, app_name);
			}
			if(isServiceAccountLevel && result.equalsIgnoreCase("INVALID")) {
				result=app_name;
			}
			mLogger.info("The Result of Authentication ---------->" + result);
		} catch (SQLException se) {
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			throw new C4SecurityException(ex.getMessage());
		}
		return result;
	}

	/**
	 * Method : authenticateWithLDAPGroup. Written by Janaki. This method is
	 * included to have the C4 Security Admin app to authenticate users against an
	 * LDAP group Description : It calls the authenticate() method of LoginDAO
	 * 
	 * @params String loginId, String passwd, String app_name
	 * @throws {@link C4SecurityException}
	 * @return String
	 */
	public String authenticateWithLDAPGroup(String loginId, String passwd, String app_name) throws C4SecurityException {
		String result = null;
		String userId = null;
		String temp = null;
		String email = null;
		boolean email_auth = false;
		mLogger.info("Calling authenticateWithLDAPGroup method ----> ");
		mLogger.info("loginId --------->" + loginId);
		mLogger.info("app_name -------->" + app_name);
		// mLogger.info("passwd -------->"+passwd);
		try {
			// This wil check whether the loginId is emailId or not.
			// If email then it returns the userID for this email id
			temp = logindao.getAppUserByEmail(loginId);
			mLogger.info("temp ----->" + temp);
			if (temp != null && !temp.equalsIgnoreCase("NO USERS FOUND.")) {
				email = loginId;
				loginId = temp;
			}
			// If loginId is emailId the LDAP authentication happens which returns boolean
			// whether success or failure
			if (email != null && email.length() > 0) {
				mLogger.info("email ------->" + email);
				try {
					email_auth = (new LDAPUtil(System.getProperties())).authenticateUserAgainstGroup(email, passwd);
					mLogger.info("The Result of LDAP user authentication ---->" + email_auth);
				} catch (Exception iue) {
					mLogger.error("Exception in LDAP user authentication ---->" + iue.getMessage());
					throw new C4SecurityException(iue.getMessage());
				}
			}
			mLogger.info("email_auth ------------>" + email_auth);

			// If Ldap Authentication is sucessful then sessionId is created otherwise
			// loginId and password are validated against the database
			// if the validation is sucessful it will create session else it will return
			// string "INVALID"
			if (email_auth) {
				result = logindao.createSession(loginId, app_name);
			} else {
				String encoded = null;
				if (passwd != null) {

					encoded = passwd;
				}

				mLogger.info(" -----Encoded   ---> " + encoded);

				result = logindao.authenticate(loginId, encoded, app_name);
			}
			mLogger.info("The Result of Authentication ---------->" + result);
		} catch (SQLException se) {
			mLogger.fatal("SQL Problem in Login - " + se.getMessage());
			throw new C4SecurityException(se.getMessage());
		} catch (Exception ex) {
			mLogger.fatal("Exception inside authenticate method - " + ex.getMessage());
			throw new C4SecurityException(ex.getMessage());
		}
		return result;
	}

	/**
	 * <p>
	 * Method: logout of LoginDelegate.java Description: It logs out of the session
	 * and makes the status in t_session as INACT
	 * </p>
	 * 
	 * @param String sessionId
	 * @param String userId
	 * @param String appname
	 * @return String
	 * @throws {@link C4SecurityException}
	 */
	public String logout(String sessionId, String userId, String appname) throws C4SecurityException {
		String result = "";
		try {
			result = logindao.logout(sessionId, userId, appname);
			// mLogger.info("Result of logout - "+result);
		} catch (SQLException sqx) {
			mLogger.fatal("logout of LoginDelegate.java - SQLException:  " + sqx.getMessage());
			throw new C4SecurityException(sqx.getMessage());
		}
		return result;
	}

	public String logout(String sessionId) throws C4SecurityException {
		String result = "";
		try {
			result = logindao.logout(sessionId);
			// mLogger.info("Result of logout - "+result);
		} catch (SQLException sqx) {
			mLogger.fatal("logout of LoginDelegate.java - SQLException:  " + sqx.getMessage());
			throw new C4SecurityException(sqx.getMessage());
		}
		return result;
	}

	public static void main(String args[]) {
	}
}