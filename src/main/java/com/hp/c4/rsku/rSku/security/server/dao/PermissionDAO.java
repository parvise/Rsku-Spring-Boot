package com.hp.c4.rsku.rSku.security.server.dao;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CProcedureInfo;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CSPParameter;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;
public class PermissionDAO extends ObjectDAO{
	
//	For logging info and errors to the log file.
	  private static Logger mLogger = LogManager.getLogger(PermissionDAO.class);
	  private String OUT_PARAM="out_param";
	  /**
	   * Method:  UserDAO (constructor)
	   * @param String dbname
	   */
	  public PermissionDAO(String dbname) {
		  super(dbname);
	  }
	  
	  
	  /**
	   * Method: createUser
	   * @param User user
	   * @return String
	   * @throws {@link SQLException} , {@link C4SecurityException}
	   */
	  public String getUserId(String sessionId) throws SQLException, C4SecurityException {
		  return (String)callSp(getUserIdSpInfo(sessionId));
	  }
	  
	  /**
	   * Method: searchGroup of GroupDAO
	   * @param String groupName
	   * @return ArrayList
	   * @throws {@link SQLException} , {@link C4SecurityException} 
	   */
	  public String[] getUserGroups(String userId)  throws SQLException, C4SecurityException {
		    return (String[])callSp(getUserGroupsSpInfo(userId));
	  }
	  
	  
	  /**
	   * Method: searchGroup of GroupDAO
	   * @param String groupName
	   * @return ArrayList
	   * @throws {@link SQLException} , {@link C4SecurityException} 
	   */
	  public String[] getAcl(String groupId)  throws SQLException, C4SecurityException {
		  return (String[])callSp(getAclSpInfo(groupId));
	  }
	  
	  /**
	   * Method: searchGroup of GroupDAO
	   * @param String groupName
	   * @return ArrayList
	   * @throws {@link SQLException} , {@link C4SecurityException} 
	   */
	  public Map getPermission(String aclid)  throws SQLException, C4SecurityException {
		  return (Map)callSp(getPermissionSpInfo(aclid));
	  }
	  
	  /**
	   * Method: getCreateUserSpInfo
	   * Description: {@link UtilConstants.CREATE_USER_SP}
	   * @param User user
	   * @return CProcedureInfo
	   */
	  private CProcedureInfo getPermissionSpInfo(String aclid) {
		CSPParameter[] cpa = getPermissionParams(aclid);
		return new CProcedureInfo(UtilConstants.GET_PERMISSION,cpa);
	  }
	  
	  
	  
	  /**
	   * Method: getCreateUserParams
	   * @param User user
	   * @return CSPParameter[]
	   */
	  private CSPParameter[] getPermissionParams(String aclid) {
		CSPParameter[] cpa = new CSPParameter[2];
	    cpa[0] = new CSPParameter(Types.VARCHAR,true,"aclid");
	    cpa[0].setValue(aclid);
	    cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR,false,OUT_PARAM);
	    return cpa;
	  }
	  
	  /**
	   * Method: getCreateUserSpInfo
	   * Description: {@link UtilConstants.CREATE_USER_SP}
	   * @param User user
	   * @return CProcedureInfo
	   */
	  private CProcedureInfo getUserIdSpInfo(String sessionId) {
		  CSPParameter[] cpa = getUserIdParams(sessionId);
		return new CProcedureInfo(UtilConstants.GET_USERID,cpa);
	  }
	  
	  
	  
	  /**
	   * Method: getCreateUserParams
	   * @param User user
	   * @return CSPParameter[]
	   */
	  private CSPParameter[] getUserIdParams(String sessionId) {
		 CSPParameter[] cpa = new CSPParameter[2];
		 cpa[0] = new CSPParameter(Types.VARCHAR,true,"sessionid_");
	    cpa[0].setValue(sessionId);
	    cpa[1] = new CSPParameter(Types.VARCHAR,false,OUT_PARAM);
	    return cpa;
	  }
	  
	  
	  
	  
	  /**
	   * Method: getCreateUserSpInfo
	   * Description: {@link UtilConstants.CREATE_USER_SP}
	   * @param User user
	   * @return CProcedureInfo
	   */
	  private CProcedureInfo getUserGroupsSpInfo(String userId) {
		  CSPParameter[] cpa = getUserGroupsParams(userId);
		return new CProcedureInfo(UtilConstants.GET_USER_GROUPS,cpa);
	  }
	  
	  
	  
	  /**
	   * Method: getCreateUserSpInfo
	   * Description: {@link UtilConstants.CREATE_USER_SP}
	   * @param User user
	   * @return CProcedureInfo
	   */
	  private CProcedureInfo getAclSpInfo(String groupId) {
		CSPParameter[] cpa = getAclParams(groupId);
		return new CProcedureInfo(UtilConstants.GET_ACL,cpa);
	  }
	  
	  
	  /**
	   * Method: getCreateUserParams
	   * @param User user
	   * @return CSPParameter[]
	   */
	  private CSPParameter[] getAclParams(String groupId) {
		 CSPParameter[] cpa = new CSPParameter[2];
	    cpa[0] = new CSPParameter(Types.VARCHAR,true,"groupId");
	    cpa[0].setValue(groupId);
	    cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR,false,OUT_PARAM);
	    return cpa;
	  }
	  
	  
	  /**
	   * Method: getCreateUserParams
	   * @param User user
	   * @return CSPParameter[]
	   */
	  private CSPParameter[] getUserGroupsParams(String userId) {
		 CSPParameter[] cpa = new CSPParameter[2];
		 cpa[0] = new CSPParameter(Types.VARCHAR,true,"userid_");
	    cpa[0].setValue(userId);
	    cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR,false,OUT_PARAM);
	    return cpa;
	  }
	  
	  
	  /**
	   * Method: processResult of GroupDAO
	   * @param CProcedureInfo info
	   * @return Object
	   * @throws {@link SQLException} , {@link C4SecurityException} 
	   */
	  public Object processResult(CProcedureInfo info) throws SQLException, C4SecurityException
	  {
		  
		  	
		  	String gname = null;
		  	String aclid=null;
		   if (info.getName().equals(UtilConstants.GET_USER_GROUPS)){
			 // mLogger.info("SP info  2222- "+UtilConstants.GET_USER_GROUPS);
			  ArrayList groupidList=null;
			  ResultSet rs = null;
			  try {
				  groupidList=new ArrayList();
			  rs= (ResultSet) info.getParamValue(OUT_PARAM);
		
			  while(rs.next()){	   
				
				  gname=rs.getString("groupid_");
				  groupidList.add(gname);
				  
				  }
				  
			  } catch(SQLException se) {
				  mLogger.fatal("SQLException in GET_ALL_GROUPS of processResult - "+se.getMessage());
				  throw new C4SecurityException(se.getMessage());
			  } catch(Exception e) {
				  mLogger.fatal("General Exception in GET_ALL_GROUPS of processResult - "+e.getMessage());
				  throw new C4SecurityException(e.getMessage());
			  }

			  return (String[])groupidList.toArray(new String[0]);
			  
		  }
		   else if (info.getName().equals(UtilConstants.GET_ACL))
		   {
			 // mLogger.info("SP info 3333- "+UtilConstants.GET_ACL);
			  ArrayList aclidList=null;
			  ResultSet rs = null;
			 
			  try {
				  aclidList=new ArrayList();
			  rs= (ResultSet) info.getParamValue(OUT_PARAM);
			  while(rs.next()){
				  aclid=rs.getString("aclid_");
				  aclidList.add(aclid);
				  }
				  
			  } catch(SQLException se) {
				  mLogger.fatal("SQLException in GET_ALL_GROUPS of processResult - "+se.getMessage());
				  throw new C4SecurityException(se.getMessage());
			  } catch(Exception e) {
				  mLogger.fatal("General Exception in GET_ALL_GROUPS of processResult - "+e.getMessage());
				  throw new C4SecurityException(e.getMessage());
			  }
			  return (String[])aclidList.toArray(new String[0]);
			  
		  }
		   else if (info.getName().equals(UtilConstants.GET_PERMISSION)){
			//  mLogger.info("SP info 4444- "+UtilConstants.GET_PERMISSION);
			  Map ht = new HashMap();
			  ResultSet rs = null;
			 
			  try {
			  rs= (ResultSet) info.getParamValue(OUT_PARAM);
			  while(rs.next()){	    	
				  char sign1 = rs.getString(1).charAt(0);
			      String groupId = rs.getString(2);
			      String permissionType = rs.getString(3);
			      //char sign2 = rs.getString(4).charAt(0);
			      boolean granted = (sign1 == '+');
			      PermissionData pd = new PermissionData(permissionType,granted);
			      ArrayList list = (ArrayList)ht.get(groupId);
			       if (list == null) {
			        list = new ArrayList();
			         ht.put(groupId,list);
			      }
			      list.add(pd);
				  }
				  
			  } catch(SQLException se) {
				  mLogger.fatal("SQLException in GET_ALL_GROUPS of processResult - "+se.getMessage());
				  throw new C4SecurityException(se.getMessage());
			  } catch(Exception e) {
				  mLogger.fatal("General Exception in GET_ALL_GROUPS of processResult - "+e.getMessage());
				  throw new C4SecurityException(e.getMessage());
			  }
			  return ht;
			
			  
		  }
		  
		  else {
			  mLogger.info("SP info 5555- ");
			  return (Object)info.getParamValue(OUT_PARAM);
		  }
	
	  }
	  
	  /**
	   * Method: getPermissions of PermissionDAO
	   * @param SQLUtil util
	   * @param String sessionId
	   * @param String permissionType 
	   * @param oracle.sql.ARRAY PLS_ARRAY
	   * @return String
	   * @throws {@link SQLException} , {@link C4SecurityException}
	   */
      // By Srini - Modified this method as a part of CORBA Removal H2 Project
	  public Map getPermissions(SQLUtil util,String sessionId, String permissionType, oracle.sql.ARRAY ACLPERMISSION_ARRAY)
	  throws C4SecurityException,SQLException {
		mLogger.info("Test 1234); "+sessionId+" : "+permissionType+" : ");
		Map mp = new HashMap();
		ResultSet rs = null;
		util.setSQL("{call PERMISSION.getpermissions(?,?,?,?)}");
		mLogger.info("Callling {call PERMISSION.getpermissions(?,?,?,?)}");
		util.setString(1, sessionId);
		util.setObject(2, permissionType);
		util.setObject(3, ACLPERMISSION_ARRAY);
		util.registerOutParameter(4,oracle.jdbc.OracleTypes.CURSOR);
//		mLogger.info("Inside getPermissions(..) -- executing the SP {call PERMISSION.getpermissions(?,?,?,?)}  ..........");
		util.executeQuery();
		rs = (ResultSet)util.getObject(4);
		String aclDescription = null, aclPermission = null;
		String tempAclDescription = null;
		if(rs != null) {
			while(rs.next()) {
				aclDescription = rs.getString(1);
				aclPermission = rs.getString(2);
//				mLogger.debug("aclDescription ----------->"+aclDescription+"   ,   aclPermission  ----------------->"+aclPermission);
				if(mp.containsKey(aclDescription)) {
					tempAclDescription = (String)mp.get(aclDescription);
					if(tempAclDescription.equals("1") && aclPermission.equals("-2")) {
						mp.put(aclDescription, aclPermission);
					}
				} else {
					mp.put(aclDescription, aclPermission);
				}
			}
		}
		if(rs != null)
			rs.close();
		
		return mp;
	  }
}


class PermissionData {
	private String action;
	private boolean granted;


	  public PermissionData(String action, boolean granted) {
	    this.granted = granted;
	    this.action = action;
	  }

	  public String getPermission() {
	    return action;
	  }

	  public boolean isGranted() {
	    return granted;
	  }

	  public String toString() {
	    return action + " = " + granted;
	   }
}
