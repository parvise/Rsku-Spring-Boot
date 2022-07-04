package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;

/**
 * Title: C4 Description: C4 Copyright: Copyright (c) 2001 Company: HP
 * 
 * @author
 * @version 1.0
 */

public class CMaskUtilIO extends ObjectIO {
	/**
	 * in/out parameters for sps used by element mask
	 */

	public static final String CURSOR = "CURSOR";

	/**
	 * stored procedures to be called.
	 */
	public static final String SELECT_OP = "c4maint.selectOpExpElementTypes";
	public static final String SELECT_COS = "c4maint.selectCosElementTypes";
	public static final String PRODUCTLINE = "productline";
//public static final String SELECT_PL = "c4maint.selectDistinctPLs";
	public static final String SELECT_PL = "c4maint.selectPL";
//  public static final String SELECT_PLATFORM = "c4maint.selectMaskPlatform";
	public static final String SELECT_PLATFORM = "c4maint.selectPlatformByPL";
	public static final String SELECT_FT = "c4maint.selectFieldTypes";

	public CMaskUtilIO() {
		super(DBConstants.C4_DBPOOL_C4PROD_ONSI);
	}

	public Map<String, List<MaskElementTypes>> selectCosElementTypes() throws C4Exception {
		try {
			return (Map) callSp(getSelectProcedureInfo(SELECT_COS));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Map<String, List<MaskElementTypes>> selectOpExpElementTypes() throws C4Exception {
		try {
			return (Map) callSp(getSelectProcedureInfo(SELECT_OP));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Map selectProductLines() throws C4Exception {
		try {
			return (Map) callSp(getSelectProcedureInfo(SELECT_PL));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Map selectPlatforms(String productLine) throws C4Exception {
		try {
			return (Map) callSp(getSelectProcedureInfo(SELECT_PLATFORM, productLine));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Map selectFieldTypes() throws C4Exception {
		try {
			return (Map) callSp(getSelectProcedureInfo(SELECT_FT));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Object processResult(CProcedureInfo info) throws com.hp.c4.rsku.rSku.c4.util.C4Exception {
		try {
			ResultSet rs = (ResultSet) info.getParamValue(CURSOR);
			if (info.getName().equals(SELECT_OP) || info.getName().equals(SELECT_COS)) {
				Map<String, List<MaskElementTypes>> t = new TreeMap<String, List<MaskElementTypes>>();
				String type = "";
				List<MaskElementTypes> elementList = new ArrayList<MaskElementTypes>();
				if (info.getName().equals(SELECT_OP)) {
					type = "OPEX";
				} else if (info.getName().equals(SELECT_COS)) {
					type = "COS";
				}
				while (rs.next()) {
					elementList.add(new MaskElementTypes(rs.getString("TYPE"), 'N', rs.getString("FIELD_TYPE")));
				}
				t.put(type, elementList);
				return t;
			} else if (info.getName().equals(SELECT_PL)) {
				// ArrayList a = new ArrayList();
				Map t = new TreeMap();
				while (rs.next()) {
					// a.add(rs.getString("PROD_LINE"));
					t.put(rs.getString("PROD_LINE"), rs.getString("PROD_LINE_DESC"));
				}
				return t;
			} else if (info.getName().equals(SELECT_PLATFORM)) {
				// ArrayList a = new ArrayList();
				Map t = new TreeMap();
				while (rs.next()) {
					// a.add(rs.getString("PLATFORM"));
					t.put(rs.getString("PLATFORM"), rs.getString("PLATFORM_DESC"));
				}
				return t;
			} else if (info.getName().equals(SELECT_FT)) {
				Map map = new TreeMap();
				while (rs.next()) {
					map.put(rs.getString("TYPE"), rs.getString("DESCRIPTION"));
				}
				return map;
			}
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
		return null;
	}

	/*
	 * Stored procedure parameters to be obtained from the xml file sooon, right now
	 * little ugly?? - viswa
	 */
	private CProcedureInfo getSelectProcedureInfo(String name) {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	private CProcedureInfo getSelectProcedureInfo(String name, String productLine) {
		CSPParameter[] cpa = new CSPParameter[2];
		cpa[0] = new CSPParameter(Types.VARCHAR, true, PRODUCTLINE);
		cpa[0].setValue(productLine);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

}