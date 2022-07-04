/**
 * <p>
 * File			:  com.hp.pcp.c4.security.server.business.PermissionDelegate.java
 * Description	:  It contains getPermissions() method which is used to
 * 				   get PL & C4 Manager level access permissions
 * 				
 * Copyright	:  Copyright (c) 2001
 * Company		:  Hewlett Packard
 * @author		:  Srini / Chetan  
 * @version 	:  1.0
 * 
 * Modifications by Srini in June 2009 as a part of C4 CORBA Removal Project H2 (PSGOTOPC09):-
 * 1. Modified the method getPermission(),by adding two more parameters String accessType, String permissionType
 *    so that PL & Menu level access is accompanied by the same method.
 *
 *</p>
 */

package com.hp.c4.rsku.rSku.security.server.business;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.security.server.dao.PermissionDAO;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;

import EDU.oswego.cs.dl.util.concurrent.ReadWriteLock;
import EDU.oswego.cs.dl.util.concurrent.WriterPreferenceReadWriteLock;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.STRUCT;

public class PermissionDelegate {

	private static Logger mLogger = LogManager.getLogger(PermissionDelegate.class);
	private PermissionDAO permissiondao = null;

	private ReadWriteLock rwlock = new WriterPreferenceReadWriteLock();

	// Constructor
	public PermissionDelegate() throws C4SecurityException {
		try {
			permissiondao = new PermissionDAO(UtilConstants.DB_POOL_NAME);
		} catch (Exception e) {
			throw new C4SecurityException(e.getMessage());
		}
	}

	/**
	 * <p>
	 * Method : getPermissions Descriptioon : PL & Menu level access permission is
	 * obtained.
	 * 
	 * @param String   sessionId
	 * @param String[] pls
	 * @param String   accessType
	 * @param String   permissionType
	 * @return Map - permissions ( key is pl or menu , and the value is 0 or 1 or
	 *         -2)
	 * @throws C4SecurityException
	 * 
	 *                             Modifications by Srini in June 2009 as a part of
	 *                             C4 CORBA Removal Project H2 (PSGOTOPC09):- 1.
	 *                             Modified this method by adding two more
	 *                             parameters String accessType, String
	 *                             permissionType so that PL & Menu level access is
	 *                             accompanied by the same method.
	 * 
	 *                             </p>
	 */
	public short[] getPermissions(String sessionId, String[] pls, String accessType, String permissionType)
			throws C4SecurityException {
		mLogger.info("Entry of getPermissions(...)  --------------->");
		// mLogger.info(":----> " + sessionId + " : " + Arrays.toString(pls) + " : " +
		// accessType + " : " + permissionType);
		short[] resultPerms = null;
		Map permMap = null;
		ArrayDescriptor desc1 = null;
		Object[] cos_attr = null;
		Vector cos = null;
		int oracleId = 0;
		CharacterSet cset = null;
		oracle.sql.StructDescriptor cosDesc = null;
		SQLUtil util = null;
		java.sql.Connection con = null;
		String[] plsreghier = null;

		try {// This try is used to handle thread related exceptions
			rwlock.readLock().acquire();
			// mLogger.info("Thread Is processing hence this method will be
			// locked"+Thread.currentThread().getName());

			try {
				StringTokenizer hier = null;
				String hierstr = null;
				ArrayList acls = new ArrayList();
				Map regionsMap = new HashMap();
				Map countriesMap = new HashMap();
				countriesMap.put("United States", "USA");
				countriesMap.put("United Kingdom", "UK");
				regionsMap.put("Worldwide", "WW");
				regionsMap.put("AP", "AP");// Asia Pacific
				regionsMap.put("NA", "NA");// North America
				regionsMap.put("LA", "LA");// Latin America
				regionsMap.put("EU", "EU");// Europe
				for (int i = 0; i < pls.length; i++) {
					hierstr = pls[i];
					if (accessType.equalsIgnoreCase("PL")) {
						hier = new StringTokenizer(hierstr, "~");
						String pl = hier.nextToken();
						acls.add(pl + " " + (String) regionsMap.get(hier.nextToken()));
						acls.add(pl + " " + (String) regionsMap.get(hier.nextToken()));
						String tempCountry = hier.nextToken();
						if (tempCountry.indexOf("United States") != -1 || tempCountry.indexOf("United Kingdom") != -1)
							acls.add(pl + " " + (String) countriesMap.get(tempCountry));
						else
							acls.add(pl + " " + tempCountry);

					} else if (accessType.equalsIgnoreCase("MENU")) {
						acls.add("C4GUI:" + pls[i]);
					}
					if (accessType.equalsIgnoreCase("MASK")) {
						acls.add(pls[i]);
					}
				}

				plsreghier = new String[acls.size()];
				for (int j = 0; j < acls.size(); j++) {
					plsreghier[j] = (String) acls.get(j);
//					  mLogger.debug("The PLs ["+j+"] ---->"+plsreghier[j]);					  
				}

				if (plsreghier == null)
					return null;

				cos = new Vector();
				oracleId = CharacterSet.US7ASCII_CHARSET;
				cset = CharacterSet.make(oracleId);
				util = new SQLUtil(DBConstants.C4_DBPOOL_INFOSHU_INFI);
				con = util.getConnection();

				cosDesc = oracle.sql.StructDescriptor.createDescriptor("ACLPERMISSION_OBJECT", con);

				cos_attr = new Object[1];
				for (int i = 0; i < plsreghier.length; i++) {
					cos_attr[0] = new CHAR(plsreghier[i], cset);
					cos.add(new STRUCT(cosDesc, con, cos_attr));
				}

				desc1 = ArrayDescriptor.createDescriptor("ACLPERMISSION_ARRAY", con);
				STRUCT[] struct1 = (STRUCT[]) cos.toArray(new STRUCT[0]);
				ARRAY new_array1 = new ARRAY(desc1, con, struct1);
				permMap = permissiondao.getPermissions(util, sessionId, permissionType, new_array1);
				mLogger.info("The result of getPermissions - Map ------->" + permMap);

				if (permMap != null && permMap.size() > 0) {
					resultPerms = new short[plsreghier.length];
					for (int i = 0; i < plsreghier.length; i++) {
						if ((String) permMap.get(plsreghier[i]) != null)
							resultPerms[i] = Short.parseShort((String) permMap.get(plsreghier[i]));
						else
							resultPerms[i] = Short.parseShort("0");

//						  mLogger.debug("The Result - resultPerms["+i+"] --->"+resultPerms[i]);						  
					}
				} else {
					resultPerms = new short[plsreghier.length];
					for (int i = 0; i < plsreghier.length; i++) {
						resultPerms[i] = Short.parseShort("0");
//						  mLogger.debug("The Result - resultPerms[] --->"+resultPerms[i]);
					}
				}
				cos.removeAllElements();
			} // End of the try 2 block
			catch (SQLException se) {
				if (pls != null)
					mLogger.info("ACLs List Before ----------->" + pls.length);

				if (plsreghier != null)
					mLogger.info("ACLs List Before ----------->" + plsreghier.length);

				mLogger.fatal("========SQLException in getting Permissions ==>" + se.getMessage());
				se.printStackTrace();
			} catch (Exception ex) {
				mLogger.fatal("========Exception in getting Permissions ==>" + ex.getMessage());
				ex.printStackTrace();
			} finally {
				try {
					util.close();
				} catch (Exception ex) {
				}
			}
		} catch (InterruptedException ie) {
			mLogger.fatal("Interupted Exception in the Thread --->" + ie.getMessage());
			ie.printStackTrace();
		} finally {
			try {
				rwlock.readLock().release();
			} catch (Exception ie) {
				mLogger.fatal("========Thread Exception in ReadWriteLock ==>" + ie.getMessage());
				ie.printStackTrace();
			}
		}
		// mLogger.info("Exit of getPermissions(...) <---------------" +
		// Arrays.toString(resultPerms));
		return resultPerms;
	}

	public static void main(String[] args) {
	}
}