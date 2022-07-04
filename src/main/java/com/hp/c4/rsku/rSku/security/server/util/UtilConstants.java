package com.hp.c4.rsku.rSku.security.server.util;

/**
 * <p>
 * Title : UtilConstants.java Description : This file includes String constants
 * used from java files in the package com.hp.pcp.c4.security.server
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Chetan/Satish/Srini @ version v2.0 @ Since June 2008
 */
public class UtilConstants {
//	public static final String USER_DETAILS="USER_DETAILS";
	public static final short OPERATION_PERMITTED = 1;
	public static final short OPERATION_NOT_PERMITTED = 0;
	public static final short RESOURCE_NOT_FOUND = -1;
	public static final short PERMISSION_DENIED = -2;
	public static final short UNKNOWN_ERROR = -3;

	//used in COS/OPEX related files
	public static final String HEADER_EXTENSION = ".xml";
	
	public static final String USED_FOR_STAGING = "STAGING";
	public static final String HOST = "HOST";
	public static final String USERNAME = "USERNAME";
	public static final String PASSWORD = "PASSWORD";

//	public static final String LOG4J_PROPERTIES_FILE="C:\\C4\\c4code\\config\\c4outputlog4j.properties";
//	public static final String DB_PROPERTIES_FILE = "C:\\C4\\c4code\\config\\c4securitydb.properties";
//	public static final String C4_SECURITY_PROPERTIES_FILE = "C:\\C4\\c4code\\config\\c4LDAP.properties";


//	public static final String LOG4J_PROPERTIES_FILE="C:\\Security_Code\\C4Security\\server\\config\\c4outputlog4j.properties";
//	 public static final String DB_PROPERTIES_FILE = "C:\\Security_Code\\C4Security\\server\\config\\c4securitydb.properties";
//	 public static final String C4_SECURITY_PROPERTIES_FILE = "C:\\Security_Code\\C4Security\\server\\config\\c4LDAP.properties";

	public static final String AUTHENTICATE_SP = "C4LOGINVALIDATE.validateLogin";
	public static final String LOGOUT_SP = "C4LOGINVALIDATE.inactivatesession";
	public static final String SESSIONLOGOUT_SP = "C4LOGINVALIDATE.sessionlogout";
	public static final String CREATE_SESSION_SP = "C4LOGINVALIDATE.createsession";
	public static final String GET_APP_USER_ID_SP = "C4LOGINVALIDATE.getappuserid";
	public static final String CREATE_APPLICATION_SP = "appmaintenance.createapplication";
	public static final String UPDATE_APPLICATION_SP = "appmaintenance.updateapplication";
	public static final String SEARCH_APPLICATION_SP = "appmaintenance.getappdetails";
	public static final String CREATE_USER_SP = "USERMAINTENANCE.insertusers";

	// By Srini - Added this method as a part of CORBA Removal H2 Project
	public static final String GET_USER_GEO_LEVELS = "USERMAINTENANCE.getusergeolevels"; // new SP to be written
	public static final String GET_USER_PROD_LINES = "USERMAINTENANCE.getuserproductlines"; // new SP to be written
	public static final String CHECK_USER_GROUPS = "USERMAINTENANCE.checkusergroups"; // new SP to be written
	public static final String GET_USER_UPDATE_GEOS = "USERMAINTENANCE.getuserupdategeos"; // new SP to be written
	public static final String GET_USER_UPDATE_PLS = "USERMAINTENANCE.getuserupdatepls"; // new SP to be written

	public static final String UPDATE_USER_SP = "USERMAINTENANCE.updateUser";
	public static final String UPDATE_USER_LOGIN_SP = "C4LOGINVALIDATE.updateUser";
	public static final String SEARCH_USER_DETAILS_SP = "USERMAINTENANCE.searchuser";
	public static final String SEARCH_GROUPS_SP = "groupmaintenance.getgroupdetails";
	public static final String GET_GROUPDETAILSBY_USERID = "USERMAINTENANCE.getallgroupdetailsbyuser";
	public static final String DELETE_GROUPS_SP = "groupmaintenance.deletegroup";
	public static final String REMOVE_ACL_ASSOC_SP = "groupmaintenance.removeassociation";

	public static final String SEARCH_USER_GROUP_DETAILS_SP = "USERMAINTENANCE.getusergroupdetails";
	public static final String DELETE_USER_LOGIN_SP = "C4LOGINVALIDATE.deleteUsers";
	public static final String DB_POOL_NAME = "c4security";
	public static final String DB_PROP = "c4.dbPool";
	public static final String INVALID_USER_MSG = "Invalid User Id/password.";
	public static final String INVALID_GROUP_MEMBER = "Not a valid group member.";
	public static final String GET_ALL_GROUPS = "USERMAINTENANCE.getallgroupdetails";
	public static final String GET_APP_LIST = "appmaintenance.getallapps";
	// public static final String GET_PL_LISTS= "aclmaintenance.getpllist";
	public static final String GET_ALLACLBY_GROUP = "searchby.getaclsbygroup";
	public static final String GET_ALLACLBY_ACL = "aclmaintenance.getallaclsbyname";
	public static final String GET_ALLACLBY_USER = "searchby.getaclsbyusername";
	public static final String GET_ALL_ACL = "aclmaintenance.getallacls";
	public static final String GET_ALL_MENU_ACL = "aclmaintenance.getmenulist";
	public static final String GET_ASSOCIATED_ACL = "groupmaintenance.associatedacls";
	public static final String CREATE_ACL = "aclmaintenance.createacl";
	public static final String UPDATE_ACL = "aclmaintenance.updateacl";
	public static final String DELETE_ACL = "aclmaintenance.deleteacls";

	public static final String GET_PL_LISTS = "aclmaintenance.getpllist";
	public static final String CREATE_PL = "aclmaintenance.addproductline";
	public static final String UPDATE_PL = "aclmaintenance.addproductline";

	public static final String GET_USERID = "C4LOGINVALIDATE.getuserid";
	public static final String GET_USER_LIST = "usermaintenance.getallusers";
	public static final String GET_USER_GROUPS = "PERMISSION.getusergroups";
	public static final String GET_ACL = "PERMISSION.getaclsbygroupid";
	public static final String GET_PERMISSION = "PERMISSION.getpermissions";
	public static final String C4NAME_ACL_MASK = "CpartyTreeScreenControl::loadNodes()::Mask::";

}