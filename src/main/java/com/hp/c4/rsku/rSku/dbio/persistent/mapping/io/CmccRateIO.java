package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.sql.Connection;
import java.sql.ResultSet;
/**
 * Title:        C4
 * Description:  C4
 * Copyright:    Copyright (c) 2001
 * Company:      HP
 * @author
 * @version 1.0
 */
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.DATE;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class CmccRateIO extends ObjectIO {

	private static final String SP_ALLMCCS = "c4mcc.getAllMccs";
	private static final String SP_MCCCOST = "c4mcc.getMccCost";
	private static final String SP_ALL_MCC_CODES = "c4mcc.getAllMccCodes";
	private static final String SP_ALL_MCC_COST = "c4mcc.getMccAllCosts";
	private static final String SP_CURR = "c4mcc.getCurrencyRateByDate";
	private static final String SP_CURR_ORDER_BY_DATE = "c4mcc.getCurrencyRateOrderByDate";
	private static final String SP_ALL_RATES = "c4mcc.getAllCurrencyRate";
	private static final String CURSOR = "CURSOR";
	private static final String SP_SOAP_ALL_MCC_CODES = "{call c4mcc.getAllMccCodes_SOAP  (?,?, ?)}";

	private final String oracleDate = "yyyy-MM-dd hh:mm:ss";

	public CmccRateIO() {
		super(DBConstants.C4_DBPOOL_GPSNAP_ONSI);
	}

	public ArrayList getAllMccsByProdId(String prodId, String opt, String spn, String ctryCode, String currCode,
			String prcTerm, java.util.Date date) throws C4Exception {
		try {
			return (ArrayList) callSp(getSelectProcedureInfo(SP_ALLMCCS, CprodToGPSY.createGPSYID(prodId, opt, spn),
					ctryCode, currCode, prcTerm, date));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public ArrayList getAllMccsByGpsyId(String gpsyId, String ctryCode, String currCode, String prcTerm,
			java.util.Date date) throws C4Exception {
		try {
			return (ArrayList) callSp(getSelectProcedureInfo(SP_ALLMCCS, gpsyId, ctryCode, currCode, prcTerm, date));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public String[] getAllMccsByArray(Product prodID[], String ctryCode, String currCode, String prcTerm,
			java.util.Date date) throws C4Exception {
		SQLUtil util = null;
		Connection con = null;
		long startTime, endTime;
		startTime = (new java.util.Date()).getTime();
		int oracleId = CharacterSet.US7ASCII_CHARSET;
		CharacterSet cset = CharacterSet.make(oracleId);
		String dateStr = (new SimpleDateFormat(oracleDate)).format(date);
		Map<String, String[]> retMap = new HashMap<String, String[]>();
		String[] resultDesc = null;
		try {
			util = new SQLUtil(DBConstants.C4_DBPOOL_GPSNAP_ONSI);
			con = util.getConnection();
			util.setSQL("{call c4mcc.getAllMccCodes  (?,?)} ");

//	prodId    VARCHAR2(20),	ctryCode  VARCHAR2(20),	currCode  VARCHAR2(2),	prcTerm   VARCHAR2(2),	startDate DATE
			StructDescriptor packDesc = StructDescriptor.createDescriptor("PROD_LIST_OBJ", con);
			ArrayList packIds = new ArrayList();
			for (int i = 0; i < prodID.length; i++) {
				Object[] mcc_attr = new Object[5];
				mcc_attr[0] = new CHAR(
						CprodToGPSY.createGPSYID(prodID[i].getProdId(), prodID[i].getOpt(), prodID[i].getSpn()).trim(),
						cset);
				mcc_attr[1] = new CHAR(ctryCode.trim(), cset);
				mcc_attr[2] = new CHAR(currCode.trim(), cset);
				mcc_attr[3] = new CHAR(prcTerm.trim(), cset);
				mcc_attr[4] = new DATE(dateStr.trim());
				packIds.add(new STRUCT(packDesc, con, mcc_attr));
				// System.out.println(mcc_attr[0] + " "+ mcc_attr[1] + " " + mcc_attr[2] + " " +
				// mcc_attr[3] + " " + ((DATE)mcc_attr[4]).dateValue() );

			}
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor("PROD_ARRAY", con);
			ARRAY new_array = new ARRAY(desc, con, packIds.toArray(new STRUCT[0]));

			util.setObject(1, new_array);

			util.registerOutParameter(2, oracle.jdbc.OracleTypes.ARRAY, "RESULT_ARRAY");
			util.execute();
			ARRAY result_array = util.getARRAY(2);
			resultDesc = (String[]) result_array.getArray();
			retMap.put(dateStr, resultDesc);
			resultDesc = (String[]) retMap.get(dateStr);
			// for(int o=0; o< resultDesc.length; o++)
			// System.out.println(resultDesc[o]);

//      endTime = (new java.util.Date()).getTime();
//      System.out.println("SQL time taken:\t" + (endTime - startTime));

		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		} finally {
			try {
				if (util != null)
					util.close();
			} catch (Exception ignore) {
			}
		}
		return resultDesc;
	}

	public Float getMccCostByProdId(String prodId, String opt, String spn, String mccIn, String ctryCode,
			String currCode, String prcTerm, java.util.Date date) throws C4Exception {

		try {
			return (Float) callSp(getSelectProcedureInfo(SP_MCCCOST, CprodToGPSY.createGPSYID(prodId, opt, spn), mccIn,
					ctryCode, currCode, prcTerm, date));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Float getMccCostByGpsyId(String gpsyId, String mccIn, String ctryCode, String currCode, String prcTerm,
			java.util.Date date) throws C4Exception {
		try {
			return (Float) callSp(getSelectProcedureInfo(SP_MCCCOST, gpsyId, mccIn, ctryCode, currCode, prcTerm, date));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Float getCurrencyRateByDate(String currCode, String prodLine, Date startDate) throws C4Exception {
		try {
			return (Float) callSp(getSelectCurrProcedureInfo(SP_CURR, currCode, prodLine, startDate));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	public Float getCurrencyRateOrderByDate(String currCode, String prodLine, Date startDate) throws C4Exception {
		try {
			return (Float) callSp(getSelectCurrProcedureInfo(SP_CURR_ORDER_BY_DATE, currCode, prodLine, startDate));
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	public Map<String, SortedSet<RateRange>> getAllRates() throws C4Exception, SQLException {
		return (Map<String, SortedSet<RateRange>>) callSp(getAllRatesProcedureInfo());
	}

	public Object processResult(CProcedureInfo info) throws java.sql.SQLException, C4Exception {

		ResultSet rs = (ResultSet) info.getParamValue(CURSOR);
		if (info.getName().equals(SP_ALLMCCS)) {
			List<String> al = new ArrayList<String>();
			while (rs.next())
				al.add(rs.getString("MCC_CD"));
			return al;
		} else if (info.getName().equals(SP_MCCCOST)) {
			Float retValue = 0.0f;
			while (rs.next())
				retValue = new Float(rs.getFloat("MCC_LOC_CURR_AMT"));
			return retValue;
		} else if (info.getName().equals(SP_CURR)) {
			Float retValue = 0.0f;
			while (rs.next())
				retValue = new Float(rs.getFloat("RATE"));

			return retValue;
		} else if (info.getName().equals(SP_CURR_ORDER_BY_DATE)) {
			Float retValue = 0.0f;
			if (rs.next())
				retValue = new Float(rs.getFloat("RATE"));

			return retValue;
		} else if (info.getName().equals(SP_ALL_RATES)) {
			Map<String, SortedSet<RateRange>> theValues = new HashMap<String, SortedSet<RateRange>>();
			while (rs.next()) {
				final String thePL = rs.getString("PL_CD");
				final String theCode1 = rs.getString("CURR_CD");
				final String theCode2 = rs.getString("ISO_CURR_CD");
				final Date theStartDate = rs.getDate("START_EFF_DT");
				final Date theEndDate = rs.getDate("END_EFF_DT");
				final Float theRate = new Float(rs.getFloat("RATE"));

				final String theKey1 = getKey(thePL, theCode1);

				SortedSet<RateRange> theSet = theValues.get(theKey1);
				if (theSet == null) {
					theSet = new TreeSet<RateRange>();
					theValues.put(theKey1, theSet);
					theValues.put(getKey(thePL, theCode2), theSet);
				}
				theSet.add(new RateRange(theStartDate, theEndDate, theRate));
			}
			return theValues;
		}
		return null;
	}

	private String getKey(String pPl, String pCurrencyCode) {
		StringBuffer theBuffer = new StringBuffer(pPl);
		theBuffer.append("-");
		theBuffer.append(pCurrencyCode);
		return theBuffer.toString();
	}

	private CProcedureInfo getSelectProcedureInfo(String name, String gpsyId, String ctryCode, String currCode,
			String prcTerm, java.util.Date date) {
		CSPParameter[] cpa = new CSPParameter[6];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "GPSY");
		cpa[0].setValue(gpsyId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "CTRY_CODE");
		cpa[1].setValue(ctryCode);
		cpa[2] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "CURR_CODE");
		cpa[2].setValue(currCode);
		cpa[3] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "PRC_TERM");
		cpa[3].setValue(prcTerm);
		cpa[4] = new CSPParameter(oracle.jdbc.OracleTypes.DATE, true, "START_EFF_DATE");
		cpa[4].setValue(date);
		cpa[5] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	private CProcedureInfo getSelectProcedureInfo(String name, String gpsyId, String mccIn, String ctryCode,
			String currCode, String prcTerm, java.util.Date date) {
		CSPParameter[] cpa = new CSPParameter[7];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "GPSY");
		cpa[0].setValue(gpsyId);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "MCC");
		cpa[1].setValue(mccIn);
		cpa[2] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "CTRY_CODE");
		cpa[2].setValue(ctryCode);
		cpa[3] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "CURR_CODE");
		cpa[3].setValue(currCode);
		cpa[4] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "PRC_TERM");
		cpa[4].setValue(prcTerm);
		cpa[5] = new CSPParameter(oracle.jdbc.OracleTypes.DATE, true, "START_EFF_DATE");
		cpa[5].setValue(date);
		cpa[6] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	private CProcedureInfo getAllRatesProcedureInfo() {
		CSPParameter[] cpa = new CSPParameter[1];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(SP_ALL_RATES, cpa);
	}

	private CProcedureInfo getSelectCurrProcedureInfo(String name, String currCode, String prodLine, Date startDate) {
		CSPParameter[] cpa = new CSPParameter[4];
		cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "CURR_CODE");
		cpa[0].setValue(currCode);
		cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "PROD_LINE");
		cpa[1].setValue(prodLine);
		cpa[2] = new CSPParameter(oracle.jdbc.OracleTypes.DATE, true, "START_DATE");
		cpa[2].setValue(startDate);
		cpa[3] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
		return new CProcedureInfo(name, cpa);
	}

	public String[] getAllMccsByArray_SOAP(Product prodID[], String ctryCode, String currCode, String prcTerm,
			Date[] dates) throws C4Exception {
		SQLUtil util = null;
		Connection con = null;
		int oracleId = CharacterSet.US7ASCII_CHARSET;
		CharacterSet cset = CharacterSet.make(oracleId);

		String[] resultDesc = null;
		try {
			util = new SQLUtil(DBConstants.C4_DBPOOL_GPSNAP_ONSI);
			con = util.getConnection();

			util.setSQL(SP_SOAP_ALL_MCC_CODES);
			// util.setSQL("{call getAllMccCodes_SOAP (?,?, ?)} ");

			StructDescriptor packDesc = StructDescriptor.createDescriptor("PROD_LIST_OBJ", con);
			ArrayList packIds = new ArrayList();
			for (int i = 0; i < prodID.length; i++) {
				Object[] mcc_attr = new Object[5];
				mcc_attr[0] = new CHAR(
						CprodToGPSY.createGPSYID(prodID[i].getProdId(), prodID[i].getOpt(), prodID[i].getSpn()).trim(),
						cset);
				mcc_attr[1] = new CHAR(ctryCode.trim(), cset);
				mcc_attr[2] = new CHAR(currCode.trim(), cset);
				mcc_attr[3] = new CHAR(prcTerm.trim(), cset);
				mcc_attr[4] = null;
				packIds.add(new STRUCT(packDesc, con, mcc_attr));
			}
			ArrayDescriptor desc = ArrayDescriptor.createDescriptor("PROD_ARRAY", con);
			ARRAY new_array = new ARRAY(desc, con, packIds.toArray(new STRUCT[0]));

			util.setObject(1, new_array);

			// For dates..

			StructDescriptor dateDesc = StructDescriptor.createDescriptor("DATE_LIST_OBJ", con);
			ArrayList dtes = new ArrayList();
			for (int i = 0; i < dates.length; i++) {
				Object[] dt_attr = new Object[1];
				String dateStr = (new SimpleDateFormat(oracleDate)).format(dates[i]);
				dt_attr[0] = new DATE(dateStr);
				dtes.add(new STRUCT(dateDesc, con, dt_attr));
			}
			ArrayDescriptor dt_desc = ArrayDescriptor.createDescriptor("DATE_ARRAY", con);
			ARRAY date_array = new ARRAY(dt_desc, con, dtes.toArray(new STRUCT[0]));

			util.setObject(2, date_array);
			////////////////////////////

			util.registerOutParameter(3, oracle.jdbc.OracleTypes.ARRAY, "RESULT_ARRAY_SOAP");
			util.execute();
			ARRAY result_array = util.getARRAY(3);
			resultDesc = (String[]) result_array.getArray();
		} catch (SQLException se) {
			se.printStackTrace();
			throw new C4Exception(se.getMessage());
		} finally {
			try {
				if (util != null)
					util.close();
			} catch (Exception ignore) {
			}
		}
		return resultDesc;
	}

	public static class RateRange implements Comparable {
		Date _start = null;
		Date _end = null;
		Float _rate = null;

		RateRange(Date pStartEffectiveDate, Date pEndEffectiveDate, Float pRate) {
			_start = pStartEffectiveDate;
			_end = pEndEffectiveDate;
			_rate = pRate;
		}

		public Date getStartEffectiveDate() {
			return _start;
		}

		public Date getEndEffectiveDate() {
			return _end;
		}

		public Float getRate() {
			return _rate;
		}

		public int compareTo(Object pOther) {
			final RateRange theRange = (RateRange) pOther;
			return _start.compareTo(theRange.getStartEffectiveDate()) * -1;
		}

		public boolean inRange(Date pDate) {
			final int theCompare = _start.compareTo(pDate);
			if (_end == null)
				return theCompare <= 0;

			return theCompare <= 0 && _end.compareTo(pDate) >= 0;
		}

		public String toString() {
			StringBuffer theBuffer = new StringBuffer("[");
			theBuffer.append(_start);
			theBuffer.append(",");
			theBuffer.append(_end);
			theBuffer.append(",");
			theBuffer.append(_rate);
			theBuffer.append("]");
			return theBuffer.toString();
		}
	}
}
