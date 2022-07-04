package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;

public class CSubmitCosIO extends ObjectIO {
	public static final String CURSOR = "CURSOR";
	// used for cos/opex staging file generation
	public static final String SP_SELECT_IMPORT_FILENAMES = "c4maint.selectImportFileNames";
	public static final String SP_SELECT_IMPORT_FILENAMES_BYDATE = "c4maint.selectImportFileNamesByDate";
	public static final String SP_INSERT_IMPORT_FILENAMES = "c4maint.insertImportFileNames";
	public static final String SP_DELETE_IMPORT_FILENAMES = "c4maint.deleteImportFileNames";
	public static final String SP_SELECT_HOST_USER_PWD = "c4maint.selectHostUserPwd";

	public CSubmitCosIO() {

	}

	public CSubmitCosIO(String dbPoolName) {
		super(dbPoolName);
	}

	/**
	 * getImportFileNames() description : gets the relevant file names from
	 * t_files_for_importer of offline db. called from : c4manager.util.C4LoaderUtil
	 * 
	 * @param fileNamePrefix
	 * @param fileNameSuffix
	 * @returns ArrayList
	 * @throws C4Exception
	 */
	public Object getImportFileNames(String fileNamePrefix, String fileNameSuffix) throws C4Exception {
		try {
			return callSp(getImportFileProcedureInfo(SP_SELECT_IMPORT_FILENAMES, fileNamePrefix, fileNameSuffix));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	/**
	 * getImportFileProcedureInfo()
	 * 
	 * @param name
	 * @param fileNamePrefix
	 * @param fileNameSuffix called from getImportFileNames()
	 */
	private CProcedureInfo getImportFileProcedureInfo(String name, String fileNamePrefix, String fileNameSuffix) {
		CSPParameter[] cpa = new CSPParameter[3];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "fileNamePrefixIn");
		cpa[0].setValue(fileNamePrefix);
		cpa[1] = new CSPParameter(Types.VARCHAR, true, "fileNameSuffixIn");
		cpa[1].setValue(fileNameSuffix);
		cpa[2] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	/**
	 * getImportFileNamesByDate() description : gets the relevant file names from
	 * t_files_for_importer of offline db, with creationDate < sysdate-hourIn.
	 * called from : c4manager.util.C4LoaderUtil
	 * 
	 * @param hourIn
	 * @returns ArrayList
	 * @throws C4Exception
	 */
	public Object getImportFileNamesByDate(int hourIn) throws C4Exception {
		try {
			return callSp(getImportFileProcedureInfo(SP_SELECT_IMPORT_FILENAMES_BYDATE, hourIn));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	/**
	 * getImportFileProcedureInfo()
	 * 
	 * @param SP     name
	 * @param hourIn called from getImportFileNamesByDate()
	 */
	private CProcedureInfo getImportFileProcedureInfo(String name, int hourIn) {

		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.INTEGER, true, "hourIn");
		cpa[0].setValue(new Integer(hourIn));
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	/**
	 * insertImportFileName() description : inserts filname into
	 * t_files_for_importer of offline db. called from : c4manager.util.C4LoaderUtil
	 * 
	 * @param fileName
	 * @throws C4Exception
	 */
	public void insertImportFileName(String fileName) throws C4Exception {
		try {
			this.callSp(insDelImportFileProcedureInfo(SP_INSERT_IMPORT_FILENAMES, fileName));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	/**
	 * deleteImportFileName() description : deletes filname from
	 * t_files_for_importer of offline db. called from : c4manager.util.C4FTPStager
	 * 
	 * @param fileName
	 * @throws C4Exception
	 */
	public void deleteImportFileName(String fileName) throws C4Exception {
		try {
			this.callSp(insDelImportFileProcedureInfo(SP_DELETE_IMPORT_FILENAMES, fileName));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	/**
	 * insDelImportFileProcedureInfo()
	 * 
	 * @param name
	 * @param fileNameIn called from insertImportFileName()
	 */
	private CProcedureInfo insDelImportFileProcedureInfo(String name, String fileNameIn) {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "fileNameIn");
		cpa[0].setValue(fileNameIn);
		return new CProcedureInfo(name, cpa);
	}

	/**
	 * getHostUserPwd()
	 * 
	 * @param used_for
	 * @return HashMap containing HOST, USERNAME, PASSWORD values gets the row from
	 *         offline db for given value USED_FOR. USER_FOR contains "STAGING" as
	 *         one value.
	 * @throws C4Exception
	 */
	public Object getHostUserPwd(String used_for) throws C4Exception {
		try {
			return callSp(getHostUserPwdProcedureInfo(SP_SELECT_HOST_USER_PWD, used_for));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	/**
	 * getHostUserPwdProcedureInfo() called from getUserPwd description : creates
	 * the object with input and output parameteres for SP "selectHostUserPwd"
	 * 
	 * @param name
	 * @param used_for
	 * @return
	 */
	private CProcedureInfo getHostUserPwdProcedureInfo(String name, String used_for) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, "UsedForIn");
		cpa[0].setValue(new String(used_for));
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	/**
	 * processResult() common procedure for all, to return results of the SP.
	 * 
	 * @param info
	 * @return
	 * @throws java.sql.SQLException
	 * @throws com.hp.pcp.c4.util.C4Exception
	 */
	public Object processResult(CProcedureInfo info) throws java.sql.SQLException, C4Exception {
		try {
			int count_check = 0;
			ResultSet rs = (ResultSet) info.getParamValue(CURSOR);
			if (info.getName().equals(SP_SELECT_IMPORT_FILENAMES)
					|| info.getName().equals(SP_SELECT_IMPORT_FILENAMES_BYDATE)) {
				ArrayList al = new ArrayList();
				while (rs.next())
					al.add(rs.getString("filename"));
				return al;
			} else if (info.getName().equals(SP_INSERT_IMPORT_FILENAMES)) {
				return "";
			} else if (info.getName().equals(SP_SELECT_HOST_USER_PWD)) {
				HashMap map = new HashMap();
				if (rs.next()) {
					map.put(UtilConstants.HOST, rs.getString(UtilConstants.HOST));
					map.put(UtilConstants.USERNAME, rs.getString(UtilConstants.USERNAME));
					map.put(UtilConstants.PASSWORD, rs.getString(UtilConstants.PASSWORD));
				}
				return map;
			}

		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
		return null;
	}

}// END OF CLASS CBatchIO