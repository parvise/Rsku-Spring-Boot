package com.hp.c4.rsku.rSku.rest.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;

public class C4MccPLConversionService {

	private static final Logger mLogger = LogManager.getLogger(C4MccPLConversionService.class);

	private static String SP_MCC_PL_CONV_HPI = "{call c4sfmccplconvhpsplit.selectMccPLConvHPI(?)}";
	private static String SP_PROD_LINE_SFM_HPI = "{call c4sfmccplconvhpsplit.selectProdLineSalesForceMapHPI(?)}";
	private static final String queryStr = "{ call c4Output.selectProductHierByW1 (?,?,?,?)}";

	private static Map<String, CmccPlConv> mccMapHPI;
	private static Map<String, String> mProdLineSalesForceMapHPI;

	/**
	 * Retrieves product line and sales force mapping information from
	 * <code>T_PROD_SALES_FORCE_MAP</code> table contained within a HashMap
	 * 
	 * @param util
	 * @return
	 * @throws Exception
	 */
	private static Map<String, CmccPlConv> getMccPLConvHPI(SQLUtil util) throws Exception {

		ResultSet rs = null;
		int index = 1;
		Map<String, CmccPlConv> mccMapHPI = null;
		try {

			util.setSQL(SP_MCC_PL_CONV_HPI);
			util.registerOutParameter(index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();

			rs = (ResultSet) util.getObject(index);

			if (rs != null) {
				mccMapHPI = new TreeMap<String, CmccPlConv>();
				while (rs.next()) {
					mccMapHPI.put(rs.getString("C4_KEY"), new CmccPlConv(rs.getString("ORDER_SUBTYPE"),
							rs.getString("MCC_MNEMONIC"), rs.getString("GPL"), rs.getString("SPL")));
				}
			}

			return mccMapHPI;

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (util != null) {
					util.subclose();
				}
			} catch (Exception te) {
				te.printStackTrace();
				new Exception();
			}
		}
	}

	/**
	 * Retrieves the T_PROD_SALES_FORCE_MAP_HPI (HP Separation)
	 * 
	 * @param util
	 * @return
	 * @throws Exception
	 */
	private static Map<String, String> getProdLineSalesForceMapHPI(SQLUtil util) throws Exception {
		ResultSet rs = null;
		HashMap<String, String> tProdLineSalesForceMap = null;
		int index = 1;
		try {
			// build product line to primary sales force mapping into memory
			util.setSQL(SP_PROD_LINE_SFM_HPI);

			util.registerOutParameter(index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();

			rs = (ResultSet) util.getObject(index);
			if (rs != null) {
				tProdLineSalesForceMap = new HashMap<String, String>();
				while (rs.next()) {
					tProdLineSalesForceMap.put(rs.getString("PL_CODE"), rs.getString("PSF"));
				}
			}
			return tProdLineSalesForceMap;

		} finally {
			try {
				if (rs != null) {
					rs.close();
					rs = null;
				}
				if (util != null) {
					util.subclose();
				}
			} catch (Exception te) {
				// te.printStackTrace();
				new Exception("Exception from CmccData.getProdLineSalesForceMapHPI with message: " + te.getMessage());
			}
		}

	}

	public static void initalizeMccSpl(SQLUtil util) throws Exception {
		if (mccMapHPI == null)
			mccMapHPI = getMccPLConvHPI(util);
		if (mProdLineSalesForceMapHPI == null)
			mProdLineSalesForceMapHPI = getProdLineSalesForceMapHPI(util);
	}

	public static String getSpl(com.hp.c4.rsku.rSku.pojo.Product prodData, SQLUtil util) throws Exception {
		String spl = null;
		String gpl = null;
		ResultSet rs = null;
		util.setSQL(queryStr);
		// prodData.dump();
		int index = 1;
		try {
			util.setString(index,
					prodData.getProdId() != null && prodData.getProdId().trim().length() > 0
							? prodData.getProdId().trim()
							: null);
			util.setString(++index,
					prodData.getOpt() != null && prodData.getOpt().trim().length() > 0 ? prodData.getOpt().trim()
							: null);
			util.setString(++index,
					prodData.getSpn() != null && prodData.getSpn().trim().length() > 0 ? prodData.getSpn().trim()
							: null);

			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();

			rs = (ResultSet) util.getObject(index);

			while (rs.next()) {
				gpl = rs.getString(1);
			}
		} catch (SQLException se) {
			se.printStackTrace();
			throw se;
		} finally {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				util.subclose();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gpl != null) {
			String[] plArray = new String[1];
			plArray[0] = gpl;
			Map<String, String> plCompanyMap = Cache.getPLToCompanyMap();
			spl = getSPLByGPLWithTC(prodData.getMcc(), gpl, plCompanyMap.get(gpl));
		}

		return spl;
	}

	private static String getSPLByGPLWithTC(String mcc, String gpl, String tenantCd) throws Exception {
		mLogger.info("getSPLByGPLWithTC::" + mcc + "|" + gpl + "|" + tenantCd);
		String c4Key = null;
		String spl = null;
		for (int i = 1; i <= 5; i++) {
			c4Key = makeKeyWithTC(gpl, mcc, i, tenantCd); // creates different keys
			if (c4Key != null)
				spl = getSPLWithTC(c4Key, tenantCd);

			if (spl != null)
				return spl; // got the spl
		} // for loop
		return null;
	}

	private static String makeKeyWithTC(String gpl, String mcc, int rule, String tenantCd) {

		String c4Key = null;
		mLogger.info("makeKeyWithTC:" + gpl + ":" + mcc + ":" + rule + ":" + tenantCd);

		if (mcc != null && mcc.length() > 2)
			mcc = mcc.substring(0, 2);

		if (rule == 1) {
			c4Key = gpl.trim() + "|" + mcc.trim() + "*"; // GPL|MCC*
		}
		if (rule == 2) {
			c4Key = "*|" + mcc.trim() + "*"; // *|MCC*
		}
		if (rule == 3) {
			c4Key = gpl.trim() + "|*"; // GPL|*
		}
		if (rule == 4) {
			c4Key = "*|*"; // *|*
		}
		mLogger.info("C4Key:" + c4Key);
		// prepend sales force to the old c4key - TS
		// keep it defaulted to 53 though...
		String tSalesForce = "53";
		Map<String, String> SFMapToUse = null;
		String mapUsed = "HPQ"; // HP Separation

		// HP Separation: Narrow down on the correct Product Sales Force Map using the
		// compnay code
		if (tenantCd != null && tenantCd.trim().length() > 0)
			tenantCd = "HPI";

		if (tenantCd.equalsIgnoreCase("HPI")) {

			SFMapToUse = mProdLineSalesForceMapHPI;
			mapUsed = "HPI";
		}

		// HP Separation: End

		// Orig Pre-HP Separation
		// if(mProdLineSalesForceMap.containsKey(gpl))
		// tSalesForce = (String)mProdLineSalesForceMap.get(gpl);

		// HP Separation
		mLogger.info("SF Map Used: " + mapUsed + ":SFMapToUse : " + SFMapToUse + ": gpl" + gpl);
		if (SFMapToUse.containsKey(gpl))
			tSalesForce = (String) SFMapToUse.get(gpl);

		mLogger.info("SF corresponding to GPL: " + tSalesForce == null ? "Null" : tSalesForce + "for GPL: " + gpl);

		if (tSalesForce == null) {
			mLogger.info("Sales Force in SF Map used was null. So, going back to the combined Sales Force Map.");
			tSalesForce = (String) mProdLineSalesForceMapHPI.get(gpl);
		}
		mLogger.info("C4Key into T_MCC_CONV_PL: " + tSalesForce == null ? "Null" : (tSalesForce + "|" + c4Key));
		// HP Separation: End
//	    mLogger("KEY=====" + tSalesForce + "|" + c4Key);

		return tSalesForce + "|" + c4Key;
	}

	private static String getSPLWithTC(String c4Key, String tenantCd) {
		Map<String, CmccPlConv> mapToIterate = null;
		String mapUsed = "HPQ";
		if (tenantCd != null && tenantCd.trim().length() > 0) {
			if (tenantCd.equalsIgnoreCase("HPI")) {
				if (mccMapHPI != null && mccMapHPI.size() > 0) {
					mapToIterate = mccMapHPI;
					mapUsed = "HPI";
				}
			}
		}

		mLogger.info("MCC SPL table used: " + mapUsed);

		Iterator<String> itr = mapToIterate.keySet().iterator();
		CmccPlConv mccPl;
		while (itr.hasNext()) {
			if (((String) itr.next()).equals(c4Key)) {
				mccPl = (CmccPlConv) mapToIterate.get(c4Key);
				// HP Separation: Debugging
				mLogger.info("SPL for the C4Key" + c4Key + " is " + mccPl.getSPL());
				// HP Separation: End
				return mccPl.getSPL();
			}
		}
		return null;
	}

}

/* This class stores the t_mcc_pl_conv table values */

class CmccPlConv {

	private String _sp, _order_subtype, _mcc_m, _gpl, _spl;

	public CmccPlConv() {
	}

	public CmccPlConv(String ost, String mcc, String gpl, String spl) {

		_order_subtype = ost.trim().length() > 0 ? ost : null;
		_mcc_m = mcc.trim().length() > 0 ? mcc : null;
		_gpl = gpl.trim().length() > 0 ? gpl : null;
		_spl = spl.trim().length() > 0 ? spl : null;
	}

	/* Sales Force */
	public String getSP() {
		return _sp;
	}
	/* Order SubType */

	public String getOrderSubType() {
		return _order_subtype;
	}
	/* MCC -Mnemonic */

	public String getMcc() {
		return _mcc_m;
	}
	/* Goods Product Line */

	public String getGPL() {
		return _gpl;
	}
	/* Support Product Line */

	public String getSPL() {
		return _spl;
	}

}
