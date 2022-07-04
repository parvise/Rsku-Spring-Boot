package com.hp.c4.rsku.rSku.security.server.util;

import java.io.File;
import java.security.Security;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.net.ssl.SSLSocket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import netscape.ldap.LDAPAttribute;
import netscape.ldap.LDAPAttributeSet;
import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPException;
import netscape.ldap.LDAPReferralException;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchResults;
import netscape.ldap.LDAPUrl;
import netscape.ldap.factory.JSSESocketFactory;

/**
 * 
 * This class provides methods to authenticate the user with HP LDAP The user
 * will be authenticated against his/her email id and NT password. For
 * authentication, this class uses secure protocol.
 * 
 * The keystore used is from HP intranet site and will be valid through 2012.
 *
 *
 */
public class LDAPUtil {
//	private static final String LDAP_KEYSTORE_FILE	 = "ldap.keystore.file";
//    private static final String LDAP_KEYSTORE_PASSWD = "ldap.keystore.password";
//    private static final String LDAP_SECURE_PORT 	 = "ldap.secure.port";
//    private static final String LDAP_HOST			 = "ldap.host";
//    private static final String LDAP_PORT			 = "ldap.port";
//    private static final String LDAP_BASE			 = "ldap.base";
//    private static final String C4_DASHBOARD_GROUP   = "ldap.c4dashboard.group";
//    private static final String C4_SECURITY_GROUP = "ldap.c4security.group"; //Added by Janaki for having C4 Security Admin app to Group based LDAP validation.

	// Split Work Start - FOR HPE and HPI LDAP Details
	// For HPE LDAP Details
	private static final String LDAP_HPE_KEYSTORE_FILE = "ldap.hpe.keystore.file";
	private static final String LDAP_HPE_KEYSTORE_PASSWD = "ldap.hpe.keystore.password";
	private static final String LDAP_HPE_SECURE_PORT = "ldap.hpe.secure.port";
	private static final String LDAP_HPE_HOST = "ldap.hpe.host";
	private static final String LDAP_HPE_PORT = "ldap.hpe.port";
	private static final String LDAP_HPE_BASE = "ldap.hpe.base";
	private static final String LDAP_HPE_DOMAIN = "ldap.hpe.domain";
	private static final String C4_HPE_DASHBOARD_GROUP = "ldap.hpe.c4dashboard.group";
	private static final String C4_HPE_SECURITY_GROUP = "ldap.hpe.c4security.group";

	// For HPI LDAP Details
	private static final String LDAP_HPI_KEYSTORE_FILE = "ldap.hpi.keystore.file";
	private static final String LDAP_HPI_KEYSTORE_PASSWD = "ldap.hpi.keystore.password";
	private static final String LDAP_HPI_SECURE_PORT = "ldap.hpi.secure.port";
	private static final String LDAP_HPI_HOST = "ldap.hpi.host";
	private static final String LDAP_HPI_PORT = "ldap.hpi.port";
	private static final String LDAP_HPI_BASE = "ldap.hpi.base";
	private static final String LDAP_HPI_DOMAIN = "ldap.hpi.domain";
	private static final String C4_HPI_DASHBOARD_GROUP = "ldap.hpi.c4dashboard.group";
	private static final String C4_HPI_SECURITY_GROUP = "ldap.hpi.c4security.group";

	private static final String CHECK_FLAG = "check.first.flag";

	private static final String KEYSTOREFILE = "keystorefile";
	private static final String KEYSTOREPWD = "keystorefilepwd";
	private static final String SECUREPORT = "secureport";
	private static final String HOST = "host";
	private static final String PORT = "port";
	private static final String BASE = "base";
	private static final String DASHBOARDGROUP = "dashboardgroup";
	private static final String SECURITYGROUP = "securitygroup";
	private static final String SCOPE = "scope";

	// Active Directory Service account level
	private static final String AD_SERVICE_HOST_URL = "ad.service.host.url";
	private static final String AD_SERVICE_BASE = "ad.service.base";
	private static final String AD_SERVICE_DOMAIN = "ad.service.domain";
	private static final String AD_SERVICE_FILTER_ATTR = "ad.service.filter.attr";
	private static final String AD_SERVICE_ACCOUNT_HPI_HOST_URL = "ad.service.account.hpi.host.url";
	private static final String AD_SERVICE_ACCOUNT_HPI_BASE = "ad.service.account.hpi.base";
	private static final String AD_SERVICE_ACCOUNT_HPI_FILTER_ATTRIBUTE = "ad.service.account.hpi.filter.attribute";
	private static final String AD_SERVICE_ACCOUNT_HPI_DOMAIN = "ad.service.account.hpi.domain";

	// Split Work End

	public static final int LDAP_SCOPE = LDAPConnection.SCOPE_SUB;

	private static Logger _logger = LogManager.getLogger(LDAPUtil.class);

	LDAPConnection ldapConnection = null;
	static Properties ldapProps = new Properties();
	private static HashMap ldapMap = null;

	public LDAPUtil() {
	}

	public LDAPUtil(Properties lprops) throws Exception {
		prepareKeyStoreFile(lprops);
	}

	private static void prepareKeyStoreFile(Properties lprops) throws Exception {
		ldapProps = lprops;
//		String keystorefile = ldapProps.getProperty(LDAP_KEYSTORE_FILE);
		String keystorefile = "";
		File fl = null;
		try {

			keystorefile = ldapProps.getProperty(LDAP_HPI_KEYSTORE_FILE);
			_logger.info("HPI Keystore file=" + keystorefile);
			fl = new File(keystorefile);
			if (!fl.exists()) {
				_logger.info("HPI Keystore file " + keystorefile + " is not accessible");
			} else {
				_logger.info("HPI Keystore file " + keystorefile + " is accessible");
			}
		} catch (Exception e) {
			_logger.error("Exceptioin in LDAPUtil constructor -------->" + e.getMessage());
			throw e;
		}
	}

	public boolean authenticateUser(String userName, String password) throws C4LDAPInvalidUserException {
		String host = null, base = null, user_dn = null, user_filter = null, keystorefile = null, chkFlag = null;
		int port = 0, scope = 0;
		boolean isValidUser = false;
		LDAPConnection ldapCon = null;
		String getAttrs[] = null;
		javax.net.ssl.SSLSocket ssSocket = null;
		LDAPSearchConstraints constr = null;
		String[] tmpArr = new String[2];
		try {
			user_filter = "mail=" + userName;
			getAttrs = new String[] { "cn", "sn", "givenname", "hprole", "hpdepartmentcode", "employeenumber",
					"telephoneNumber", "nonhpuniqueid" };

			// Split Work Start
			chkFlag = (String) ldapProps.getProperty(CHECK_FLAG);
			if (ldapMap == null) {
				ldapMap = getLDAPMap();
			}
			_logger.info(" **** ldapMap in authenticateUser() ====  " + ldapMap+":"+password+":"+userName);

			tmpArr = new String[] { "HPI" };
			for (int i = 0; i < tmpArr.length; i++) {
				HashMap tmpMap = null;
				try {
					Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
					System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
					_logger.info("Java version -> " + System.getProperty("java.version"));
					_logger.info("Default TSL suuports check: 1 -> " + System.getProperty("https.protocols"));
					JSSESocketFactory jsseFactory = new JSSESocketFactory(null);
					System.setProperty("https.protocols", "TLSv1.2");

					tmpMap = (HashMap) ldapMap.get(tmpArr[i]);

					host = (String) tmpMap.get(HOST);
					port = Integer.parseInt((String) tmpMap.get(SECUREPORT));
					base = (String) tmpMap.get(BASE);
					scope = Integer.parseInt((String) tmpMap.get(SCOPE));
					keystorefile = (String) tmpMap.get(KEYSTOREFILE);

					_logger.info(" HOST : " + host);
					_logger.info(" PORT : " + port);
					_logger.info(" BASE : " + base);
					_logger.info(" SCOPE : " + scope);
					_logger.info(" keystorefile : " + keystorefile);
					// Split Work End

					System.setProperty("javax.net.ssl.trustStore", keystorefile);
					ssSocket = (SSLSocket) jsseFactory.makeSocket(host, port);

					_logger.info(" ***** Making host connection *******");
					ldapCon = new LDAPConnection(jsseFactory);

					ldapCon.setConnectTimeout(3600);
					constr = new LDAPSearchConstraints();
					constr.setMaxResults(0);
					constr.setServerTimeLimit(0);

					_logger.info(" ***** Connecting......... *******");
					ldapCon.connect(host, port, "uid=" + userName + ",ou=People," + base, password);
					isValidUser = true;
					_logger.info(" ***** Connected to LDAP Server  *******");
					_logger.info("TSL suuports check: 2 -> " + System.getProperty("https.protocols"));

					// Perform search - Get user DN and other attributes from the directory based on
					// value in user_filter
					_logger.info("Retrieving user's information from the directory ........");

					LDAPSearchResults res = ldapCon.search(base, scope, user_filter, getAttrs, false);
					_logger.info("The Result - LDAPSearchResults ------>" + res);

					if (res != null && res.hasMoreElements()) {
						user_dn = ((LDAPEntry) res.next()).getDN();
					}

					boolean isGMember = false;
					if (user_dn != null) {
						isValidUser = true;
						_logger.info(userName + " is a valid user, and the userDN: ----->" + user_dn);
					} else {
						_logger.info("Invalid User");
						isValidUser = false;
					}

				

				} catch (LDAPException lde) {
					isValidUser = false;
					_logger.error("LDAPException --------->" + lde.getMessage());
					lde.printStackTrace();
					int ec = lde.getLDAPResultCode();
					switch (ec) {
					case LDAPException.INVALID_CREDENTIALS:
						_logger.warn("Invalid Credentials for user ------>" + user_dn);

					default:
						_logger.warn("Unknown Exception for user ----->" + user_dn);
					}
					// return false;
				} catch (Exception e) {
					isValidUser = false;
					_logger.error("Exception in authenticateUser() ------>" + e.getMessage());
					e.printStackTrace();
					// return false;
				}
				if (isValidUser) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			_logger.error(" Exception in authenticatation : " + ex.getMessage());
		} finally {
			try {
				if (ldapCon != null) {
					ldapCon.disconnect();
					_logger.info("Logged out of LDAP Server.");
				}
			} catch (LDAPException e) {
				e.printStackTrace();
			}
		}
		return isValidUser;
	}

	public boolean isGroupMember(LDAPConnection ld, String groupCN, String userMailId) throws Exception {
		boolean result = false;
		HashMap tmpMap = null;
		try {
			// Split Work Start - Respective LDAP will be called based on the login email
			// domain.
			ldapMap = getLDAPMap();
			String chkFlag = (String) ldapProps.getProperty(CHECK_FLAG);
			if (chkFlag != null && chkFlag.equalsIgnoreCase("HPI")) {
				tmpMap = (HashMap) ldapMap.get(chkFlag);
			}

			_logger.info(" **** ldapMap in isGroupMember() ====  " + tmpMap);
			// Split Work End
			String base = "ou=Groups," + (String) tmpMap.get(BASE);
			String[] attrs = new String[4];
			attrs[0] = "member";

			String filter = "cn=" + groupCN;
			String memberUID = "uid=" + userMailId + ",ou=People," + (String) tmpMap.get(BASE);

			_logger.info("Filter=" + filter);
			_logger.info("memberUID=" + memberUID);
			LDAPSearchResults res = ld.search(base, LDAPConnection.SCOPE_ONE, filter, null, false);

			/* Loop on results until finished */
			while (res.hasMoreElements()) {
				LDAPEntry findEntry = null;
				try {
					findEntry = res.next();
				} catch (LDAPReferralException e) {
					LDAPUrl refUrls[] = e.getURLs();
					for (int i = 0; i < refUrls.length; i++) {
						System.out.println("\t" + refUrls[i].getUrl());
					}
					continue;
				} catch (LDAPException e) {
					System.out.println("Error: " + e.toString());
					continue;
				}

				LDAPAttributeSet findAttrs = findEntry.getAttributeSet();
				LDAPAttribute memberAttribute = findEntry.getAttribute("member");
				String[] attValueArray = memberAttribute.getStringValueArray();
				for (int i = 0; i < attValueArray.length; i++) {
					String uid = attValueArray[i];
					_logger.info("member[" + i + "]=" + uid);
					if (uid.equalsIgnoreCase(memberUID)) {
						_logger.info(memberUID + " is a valid group member.");
						return true;
					} else {
						_logger.info(memberUID + " is not a valid group member.");
					}
				}
			}
		} catch (LDAPException e) {
			_logger.error("Error: " + e.toString());
		}
		return result;
	}

	public boolean authenticateUserAgainstGroup(String userName, String password) throws C4LDAPInvalidUserException {
		LDAPConnection connection = null, secureConn = null;
		String userFilter = null, userDN = null, password1 = null, errorStr = "";
		LDAPSearchResults res = null;
		String[] attrs = null, tmpArr = null;
		boolean isValidUser = false;
		try {
			tmpArr = new String[2];
			String chkFlag = (String) ldapProps.getProperty(CHECK_FLAG);
			_logger.info(" *** LDAP flag : " + chkFlag);
			if (ldapMap == null) {
				ldapMap = getLDAPMap();
			}
			if (chkFlag != null && chkFlag.equalsIgnoreCase("HPI")) {
				tmpArr = new String[] { "HPI" };
			} else if (chkFlag != null && chkFlag.equalsIgnoreCase("HPE")) {
				tmpArr = new String[] { "HPE", "HPI" };
			} else {
				tmpArr = new String[] { "HPE", "HPI" };
			}
			for (int i = 0; i < tmpArr.length; i++) {
				HashMap tmpMap = null;
				try {
					_logger.info(" *** Checking " + tmpArr[i] + " LDAP ...... ");
					tmpMap = (HashMap) ldapMap.get(tmpArr[i]);

					userFilter = "mail=" + userName;
					attrs = new String[] { new String("dn") };
					password1 = password;

					connection = this.getLdapConnection(userName, tmpArr[i]);
					_logger.info(" *** LDAPConnection in authenticateUserAgainstGroup()  === " + connection);

					res = connection.search((String) tmpMap.get(BASE), Integer.parseInt((String) tmpMap.get(SCOPE)),
							userFilter, attrs, false);
					if (res.hasMoreElements()) {
						LDAPEntry findEntry = (LDAPEntry) res.next();
						userDN = findEntry.getDN();
					}
					_logger.info(" *** UserDN = " + userDN);
					isValidUser = true;

					if (userDN == null) {
						isValidUser = false;
						errorStr = C4LDAPInvalidUserException.INVALID_USER_MSG;
						// throw new
						// C4LDAPInvalidUserException(C4LDAPInvalidUserException.INVALID_USER_MSG);
					} else {
						secureConn = this.getSecureLdapConnection(userName, tmpArr[i]);
						try {
							secureConn.connect(3, (String) tmpMap.get(HOST),
									Integer.parseInt((String) tmpMap.get(SECUREPORT)), userDN, password1);
							_logger.info("Authentication is Success and " + userName + " is a valid user. userDN = "
									+ userDN);
							boolean isGMember = isGroupMember(secureConn, (String) tmpMap.get(SECURITYGROUP), userName);

							if (isGMember) {
								isValidUser = true;
								_logger.info(userName + " is a valid " + (String) tmpMap.get(SECURITYGROUP)
										+ " group member.");
								// return true;
							} else {
								isValidUser = false;
								_logger.info(userName + " is not a valid " + (String) tmpMap.get(SECURITYGROUP)
										+ " group member.");
								errorStr = C4LDAPInvalidUserException.INVALID_GROUP_MEMBER;
								// throw new
								// C4LDAPInvalidUserException(C4LDAPInvalidUserException.INVALID_GROUP_MEMBER);
							}
						} catch (LDAPException lde) {
							isValidUser = false;
							lde.printStackTrace();
							int ec = lde.getLDAPResultCode();
							switch (ec) {
							case LDAPException.INVALID_CREDENTIALS:
								errorStr = "Invalid Credentials for user " + userDN;
								_logger.warn("Invalid Credentials for user " + userDN);
								// return false;
							default:
								errorStr = "Unknown Exception for user " + userDN;
								_logger.warn("Unknown Exception for user " + userDN);
								// return false;
							}
						} catch (Exception e) {
							isValidUser = false;
							errorStr = "Unknown Exception :  " + e.getMessage();
							e.printStackTrace();
							// return false;
						}
					}
				} catch (LDAPException ldep) {
					isValidUser = false;
					errorStr = "LDAPException :  " + ldep.getMessage();
					ldep.printStackTrace();
				} catch (Exception e) {
					isValidUser = false;
					errorStr = "Exception :  " + e.getMessage();
					e.printStackTrace();
				}
				if (isValidUser) {
					break;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (connection != null && connection.isConnected())
					this.closeConnection(connection);
				if (secureConn != null && secureConn.isConnected())
					this.closeConnection(secureConn);
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		}
		// return false;
		return isValidUser;

	}// end authenticateUserAgainstGroup

	/**
	 * This method creates a non-secure LDAP connection to the host and port
	 * configured in ldap configuration file. <br>
	 * 
	 * @return LDAPConnection
	 * @throws Exception
	 */
	private LDAPConnection getLdapConnection(String user, String companyCD) throws Exception {
		// Split Work Start - Respective LDAP will be called based on the login email
		// domain.
		LDAPConnection ldapConnection = null;
		boolean isValidUser = false;
		HashMap tmpMap = null;
		try {
			if (ldapMap == null) {
				ldapMap = getLDAPMap();
			}
			tmpMap = (HashMap) ldapMap.get(companyCD);
			_logger.info(" *** " + companyCD + " LDAP Details Map : " + tmpMap);
			ldapConnection = new LDAPConnection();
			ldapConnection.connect((String) tmpMap.get(HOST), Integer.parseInt((String) tmpMap.get(PORT)));
			_logger.info(" **** Connected to LDAP :  " + (String) tmpMap.get(HOST) + " at Port : "
					+ (String) tmpMap.get(PORT));
		} catch (LDAPException ldep) {
			_logger.error(" LDAPException in getLdapConnection()  :   " + ldep.getMessage());
			ldep.printStackTrace();
		} catch (Exception e) {
			_logger.error(" Exception in getLdapConnection()  :   " + e.getMessage());
			e.printStackTrace();
		}
		// Split Work End
		return ldapConnection;
	}

	/**
	 * This method closes the LDAP connection safely. <br>
	 * 
	 * @param ldapConnection
	 * @throws Exception
	 */
	private void closeConnection(LDAPConnection ldapConnection) throws Exception {
		try {
			ldapConnection.disconnect();
		} catch (LDAPException lde) {
			_logger.warn("Exception during LDAP connection closure: " + lde.getMessage(), lde);
		}
	}// end closeConnection

	/**
	 * This method creates a secure LDAP connection to the host and secure port
	 * configured in the ldap configuration file. <br>
	 * This method uses keystore to make a secure connection. The keystore file path
	 * and password has to be configured in the ldap configuration file. <br>
	 * If the keystore is not available, the same can be downloaded from HP intranet
	 * site. <br>
	 * 
	 * @return LDAPConnection
	 * @throws Exception
	 */
	private LDAPConnection getSecureLdapConnection(String user, String companyCD) throws Exception {
		// Split Work Start - Respective LDAP will be called based on the login email
		// domain.
		LDAPConnection ldapConnection = null;
		boolean isValidUser = false;
		HashMap tmpMap = null;
		JSSESocketFactory jsseFactory = null;
		javax.net.ssl.SSLSocket ssSocket = null;
		try {
			if (ldapMap == null) {
				ldapMap = getLDAPMap();
			}
			tmpMap = (HashMap) ldapMap.get(companyCD);
			_logger.info(" *** " + companyCD + " LDAP Details Map : " + tmpMap);

			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
			System.setProperty("javax.net.ssl.trustStore", (String) tmpMap.get(KEYSTOREFILE));
			// System.setProperty("javax.net.ssl.keyStore",(String)tmpMap.get(KEYSTOREFILE));
			// System.setProperty("javax.net.ssl.keyStorePassword",(String)tmpMap.get(KEYSTOREPWD));
			jsseFactory = new JSSESocketFactory(null);
			System.setProperty("https.protocols", "TLSv1.2");
			_logger.info("TSL suuports check: 544 -> " + System.getProperty("https.protocols"));
			ssSocket = (SSLSocket) jsseFactory.makeSocket((String) tmpMap.get(HOST),
					Integer.parseInt((String) tmpMap.get(SECUREPORT)));
			_logger.info("TSL suuports check: 544 -> " + System.getProperty("https.protocols"));
			ldapConnection = new LDAPConnection(jsseFactory);
			ldapConnection.setConnectTimeout(1800);
			_logger.info(" **** LDAPConnection to Secure " + companyCD + " LDAP :  " + (String) tmpMap.get(HOST)
					+ " at Port : " + (String) tmpMap.get(SECUREPORT));
		} catch (LDAPException ldep) {
			_logger.error(" LDAPException in getSecureLdapConnection() :  " + ldep.getMessage());
			ldep.printStackTrace();
		} catch (Exception e) {
			_logger.error(" Exception in getSecureLdapConnection() :  " + e.getMessage());
			e.printStackTrace();
		}
		// Split Work End.
		return ldapConnection;
	}

	public static void main(String[] args) {

	}

	// By Srini - C4 Maintenance (C4 Input Cos Screen enhancement. Check for Valid
	// Email) - Start
	public boolean validateEmailAddress(String sea) {
		// Split Work Start - Respective LDAP will be called based on the login email
		// domain.
		_logger.info("validateEmailAddress calling ::: " + sea);
		LDAPConnection ld = null;
		boolean isValidUser = false;
		HashMap tmpMap = null;
		String user_filter = null;
		String[] tmpArr = new String[2];
		String chkFlag = (String) ldapProps.getProperty(CHECK_FLAG);
		_logger.info(" *** LDAP flag : " + chkFlag);
		try {
			if (ldapMap == null) {
				ldapMap = getLDAPMap();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (chkFlag != null && chkFlag.equalsIgnoreCase("HPI")) {
			tmpArr = new String[] { "HPI" };
		} else if (chkFlag != null && chkFlag.equalsIgnoreCase("HPE")) {
			tmpArr = new String[] { "HPE", "HPI" };
		} else {
			tmpArr = new String[] { "HPE", "HPI" };
		}
		for (int i = 0; i < tmpArr.length; i++) {
			tmpMap = (HashMap) ldapMap.get(tmpArr[i]);
			try {
				_logger.info(" *** Checking " + tmpArr[i] + " LDAP ...... ");

				ld = getSecureLdapConnection(sea, tmpArr[i]);
				_logger.info("Got the LDAP Connection <Ramesh Babu Y>:" + ld);

				// LDAP settings/filters
				user_filter = "mail=" + sea;
				LDAPSearchConstraints ldSearchConst = new LDAPSearchConstraints();
				ldSearchConst.setServerTimeLimit(180);
				ldSearchConst.setTimeLimit(60000);
				ld.setSearchConstraints(ldSearchConst);

				Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
				String host = (String) tmpMap.get(HOST);
				int port = Integer.parseInt((String) tmpMap.get(SECUREPORT));
				String base = (String) tmpMap.get(BASE);
				int scope = Integer.parseInt((String) tmpMap.get(SCOPE));

				String user_dn = null;
				String reqAttrs[] = { "cn", "sn", "givenname", "hprole", "hpdepartmentcode", "employeenumber",
						"telephoneNumber", "nonhpuniqueid" };
				ld.connect(host, port);
				_logger.info(" **** Connected to Secure " + tmpArr[i] + " LDAP :  " + (String) tmpMap.get(HOST)
						+ " at Port : " + (String) tmpMap.get(SECUREPORT));
				// Get the user DN, in order to authenticate
				LDAPSearchResults res = ld.search(base, scope, user_filter, reqAttrs, false);
				if (res.hasMoreElements()) {
					LDAPEntry findEntry = (LDAPEntry) res.next();
					user_dn = findEntry.getDN();
					_logger.info(" ======== user_dn ====== " + user_dn);
					if (user_dn.contains(sea)) {
						isValidUser = true;
//		            	return true;
					} else {
						isValidUser = false;
//		            	return false;
					}
				}
			} catch (LDAPException le) {
				isValidUser = false;
//		            return false;
			} catch (Exception e) {
				isValidUser = false;
//		            return false;
			}
			if (isValidUser) {
				break;
			}
		}
		if (ld != null) {
			try {
				ld.disconnect();
			} catch (LDAPException e) {
			}
		}
//	      return false;
		return isValidUser;
	}
	// By Srini - C4 Maintenance (C4 Input Cos Screen enhancement. Check for Valid
	// Email) - End

	// Split Work Start - Getting Ldap MAP
	private HashMap getLDAPMap() throws Exception {
		HashMap ldapDetails = new HashMap();
		HashMap ldapMapHPI = new HashMap();

		ldapMapHPI.put(KEYSTOREFILE, ldapProps.getProperty(LDAP_HPI_KEYSTORE_FILE));
		ldapMapHPI.put(KEYSTOREPWD, ldapProps.getProperty(LDAP_HPI_KEYSTORE_PASSWD));
		ldapMapHPI.put(SECUREPORT, ldapProps.getProperty(LDAP_HPI_SECURE_PORT));
		ldapMapHPI.put(HOST, ldapProps.getProperty(LDAP_HPI_HOST));
		ldapMapHPI.put(PORT, ldapProps.getProperty(LDAP_HPI_PORT));
		ldapMapHPI.put(BASE, ldapProps.getProperty(LDAP_HPI_BASE));
		ldapMapHPI.put(SECURITYGROUP, ldapProps.getProperty(C4_HPI_SECURITY_GROUP));
		ldapMapHPI.put(SCOPE, "" + LDAP_SCOPE);

		ldapDetails.put("HPI", ldapMapHPI);

		return ldapDetails;
	}

	/**
	 * 
	 * @param userId
	 * @param password
	 * @return
	 */
	public boolean authenticateServiceAccountLevel(String userId, String password) {
		boolean isValidUser = false;
		String userIdFilter = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapProps.getProperty(AD_SERVICE_ACCOUNT_HPI_HOST_URL));
		// env.put(Context.SECURITY_AUTHENTICATION, "simple");
		userIdFilter = ldapProps.getProperty(AD_SERVICE_ACCOUNT_HPI_FILTER_ATTRIBUTE) + "=" + userId
				+ ldapProps.getProperty(AD_SERVICE_ACCOUNT_HPI_DOMAIN) + ","
				+ ldapProps.getProperty(AD_SERVICE_ACCOUNT_HPI_BASE);
		env.put(Context.SECURITY_PRINCIPAL, userIdFilter);

		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			LdapContext ctx = new InitialLdapContext(env, null);
			isValidUser = true;

			_logger.info("Service account level authentication is Scucess---->" + userIdFilter);

		} catch (NamingException e) {
			isValidUser = false;
			_logger.error("LDAPException --------->" + e.getMessage());
		}

		return isValidUser;

	}
	// Split Work End
}
