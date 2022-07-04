package com.hp.c4.rsku.rSku.rest.repo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.bean.request.TCosData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.c4.util.EperiodType;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtilWithConnection;

@Repository
public class C4AggregateCostCalculationRepo {

	private static final Logger mLogger = LogManager.getLogger(C4AggregateCostCalculationRepo.class);

	private final String C4_RETRIVE_COST_FOR_SKUS = "{call c4Output.selectCOSByW1 (?,?,?,?,?,?,?,?,?)}";

	/*
	 * This method is used for get the C4 Cost as per the requested SKU's
	 */

	public Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> getC4CostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product[] prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList) throws Exception {

		mLogger.info("Extracting the C4 cost for Valid Sku's ..................");

		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> productCostMap = new LinkedHashMap<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>>();

		Date firstPeriod, lastPeriod, lastPeriodW;
		
		SQLUtilWithConnection util = null;
		ResultSet rs = null;
		try {
			Connection theConnection = CdbConnectionMgr.getConnectionMgr()
					.getConnection(DBConstants.C4_DBPOOL_C4PROD_ONSI);
			firstPeriod = Aperiod.getPeriodStartDateForLessNearest(dateList[0]);
			lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY);
			lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			// System.out.println("544 firstPeriod::1:" + firstPeriod + ":" + lastPeriod);

			String _status = "ACTIV";
			String geoCode = pdesc.getGeoCode();

			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;

			util = new SQLUtilWithConnection(theConnection);

			for (int i = 0; i < prodList.length; i++) {
				util.setSQL(C4_RETRIVE_COST_FOR_SKUS);
				mLogger.info("Input Params::: " + prodList[i] + ":" + geoCode + ":" + _status + ":" + firstPeriod + ":"
						+ lastPeriod);
				int index = 1;
				util.setString(index,
						prodList[i].getProdId() != null && prodList[i].getProdId().trim().length() > 0
								? prodList[i].getProdId().trim()
								: null);
				util.setString(++index,
						prodList[i].getOpt() != null && prodList[i].getOpt().trim().length() > 0
								? prodList[i].getOpt().trim()
								: null);
				util.setString(++index,
						prodList[i].getMcc() != null && prodList[i].getMcc().trim().length() > 0
								? prodList[i].getMcc().trim()
								: null);
				util.setString(++index,
						prodList[i].getSpn() != null && prodList[i].getSpn().trim().length() > 0
								? prodList[i].getSpn().trim()
								: null);
				util.setString(++index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);
				util.setString(++index, _status);
				util.setDate(++index, firstPeriod);
				util.setDate(++index, lastPeriod);

				util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);
				util.execute();
				rs = (ResultSet) util.getObject(index);
				char periodType = ' ';

				Set<TCosData> finalC4CostList = new LinkedHashSet<TCosData>();

				List<TCosData> weeklyList = new ArrayList<TCosData>();
				List<TCosData> monthlyList = new ArrayList<TCosData>();
				List<TCosData> quarterlyList = new ArrayList<TCosData>();
				while (rs.next()) {
					periodType = rs.getString("PERIOD_TYPE").trim().charAt(0);

					if (periodType == 'W') {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							weeklyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"),
									rs.getString("PERIOD_TYPE").trim().charAt(0),
									new java.util.Date(rs.getDate("START_DATE").getTime())));
						}
					} else if (periodType == 'M') {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							monthlyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"),
									rs.getString("PERIOD_TYPE").trim().charAt(0),
									new java.util.Date(rs.getDate("START_DATE").getTime())));
						}
					} else if (periodType == 'Q') {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							quarterlyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"),
									rs.getString("PERIOD_TYPE").trim().charAt(0),
									new java.util.Date(rs.getDate("START_DATE").getTime())));
						}
					}

				}

				if (weeklyList.size() > 0) {
					finalC4CostList.addAll(weeklyList);
				} else if (monthlyList.size() > 0) {
					finalC4CostList.addAll(monthlyList);
				} else if (quarterlyList.size() > 0) {
					finalC4CostList.addAll(quarterlyList);
				}

				productCostMap.put(prodList[i], finalC4CostList);

			}
		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4CostOfSkus......" + e.getMessage());
			throw e;
		} finally {
			if (util != null)
				util.close();
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		mLogger.info("Cost retival from DB is done....." + productCostMap.size());
		//mLogger.info(productCostMap);
		return productCostMap;
	}
}
