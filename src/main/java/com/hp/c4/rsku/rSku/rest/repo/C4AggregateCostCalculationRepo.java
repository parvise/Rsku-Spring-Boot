package com.hp.c4.rsku.rSku.rest.repo;

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
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.bean.request.C4CostData;
import com.hp.c4.rsku.rSku.bean.request.TCCosData;
import com.hp.c4.rsku.rSku.bean.request.TCosData;
import com.hp.c4.rsku.rSku.bean.request.TOpexData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.c4.util.EperiodType;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.rest.services.C4MccPLConversionService;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtilAbstract;

@Repository
public class C4AggregateCostCalculationRepo {

	private static final Logger mLogger = LogManager.getLogger(C4AggregateCostCalculationRepo.class);

	private static final String C4_RETRIVE_COS_COST_FOR_SKUS = "{call c4Output.selectCOSByW1 (?,?,?,?,?,?,?,?,?)}";
	private static final String C4_RETRIVE_COS_MCC_COST_FOR_SKUS = "{call c4Output.selectCosByMCC (?,?,?,?,?,?)}";

	private static final String C4_RETRIVE_OPEX_COST_FOR_SKUS = "{call c4Output.selectOpExpByW1 (?,?,?,?,?,?,?,?,?)}";
	private static final String C4_RETRIVE_OPEX_MCC_COST_FOR_SKUS = "{call c4Output.selectOpExpByMCC (?,?,?,?,?,?,?,?,?)}";

	private static final String MCC_CODE_COST_COUNT = "{call c4Output.countCosMcc (?,?,?,?,?,?,?)}";
	private static final String STATUS = "ACTIV";

	private static final char WEEKLY = 'W';
	private static final char MONTHLY = 'M';
	private static final char QUARTERLY = 'Q';

	private SQLUtil util = null;

	@Bean
	public void initSql() {
		try {
			util = new SQLUtil(DBConstants.C4_DBPOOL_C4PROD_ONSI);
		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4CostOfSkus......" + e.getMessage());
		}
	}

	/*
	 * This method is used for get the C4 Cost as per the requested SKU's
	 */

	public Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> getC4CostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product[] prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList) throws Exception {

		mLogger.info("Extracting the C4 cost for Valid Sku's ..................");

		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> productCostMap = new LinkedHashMap<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>>();

		Date firstPeriod, lastPeriod, lastPeriodW;

		ResultSet rs = null;
		try {
			firstPeriod = Aperiod.getPeriodStartDateForLessNearest(dateList[0]);
			lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY);
			lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			// System.out.println("544 firstPeriod::1:" + firstPeriod + ":" + lastPeriod);

			String geoCode = pdesc.getGeoCode();

			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;

			for (int i = 0; i < prodList.length; i++) {
				util.setSQL(C4_RETRIVE_COS_COST_FOR_SKUS);
				// mLogger.info("Input Params::: " + prodList[i] + ":" + geoCode + ":" + _status
				// + ":" + firstPeriod + ":" + lastPeriod);
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
				util.setString(++index, STATUS);
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

					if (periodType == WEEKLY) {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							weeklyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
									new java.util.Date(rs.getDate("START_DATE").getTime())));
						}
					} else if (periodType == MONTHLY) {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							monthlyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
									new java.util.Date(rs.getDate("START_DATE").getTime())));
						}
					} else if (periodType == QUARTERLY) {
						int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
						if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
							quarterlyList.add(new TCosData(prodList[i].getProdId().trim(), prodList[i].getOpt(),
									prodList[i].getMcc(), prodList[i].getSpn(), rs.getInt("PACK_ID"),
									rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
									rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
									rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
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
		// mLogger.info(productCostMap);
		return productCostMap;
	}

	/*
	 * This method is used for get the C4 Cost as per the requested SKU's
	 */

	public Set<C4CostData> getC4CosValueBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product prodList, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		mLogger.info("Extracting the C4 cost for Valid Sku's ..................");

		Set<C4CostData> finalC4CostList = null;

		Date firstPeriod, lastPeriod, lastPeriodW;

		ResultSet rs = null;
		try {
			firstPeriod = Aperiod.getPeriodStartDateForLessNearest(dateList[0]);
			lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY);
			lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			// System.out.println("544 firstPeriod::1:" + firstPeriod + ":" + lastPeriod);

			String geoCode = pdesc.getGeoCode();

			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;

			// {call c4Output.selectCOSByW1 (?,?,?,?,?,?,?,?,?)}
			util.setSQL(C4_RETRIVE_COS_COST_FOR_SKUS);
			// mLogger.info("Input Params::: " + prodList + ":" + geoCode + ":" + STATUS +
			// ":" + firstPeriod + ":" + lastPeriod);
			int index = 1;
			util.setString(index,
					prodList.getProdId() != null && prodList.getProdId().trim().length() > 0
							? prodList.getProdId().trim()
							: null);
			util.setString(++index,
					prodList.getOpt() != null && prodList.getOpt().trim().length() > 0 ? prodList.getOpt().trim()
							: null);
			util.setString(++index,
					prodList.getMcc() != null && prodList.getMcc().trim().length() > 0 ? prodList.getMcc().trim()
							: null);
			util.setString(++index,
					prodList.getSpn() != null && prodList.getSpn().trim().length() > 0 ? prodList.getSpn().trim()
							: null);
			util.setString(++index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);
			util.setString(++index, STATUS);
			util.setDate(++index, firstPeriod);
			util.setDate(++index, lastPeriod);

			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();
			rs = (ResultSet) util.getObject(index);
			char periodType = ' ';

			finalC4CostList = new LinkedHashSet<C4CostData>();

			List<TCCosData> weeklyList = new ArrayList<TCCosData>();
			List<TCCosData> monthlyList = new ArrayList<TCCosData>();
			List<TCCosData> quarterlyList = new ArrayList<TCCosData>();
			while (rs.next()) {
				periodType = rs.getString("PERIOD_TYPE").trim().charAt(0);

				if (periodType == WEEKLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						weeklyList.add(new TCCosData(prodList.getProdId().trim(), prodList.getOpt(), prodList.getMcc(),
								prodList.getSpn(), rs.getInt("PACK_ID"), rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
					}
				} else if (periodType == MONTHLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						monthlyList.add(new TCCosData(prodList.getProdId().trim(), prodList.getOpt(), prodList.getMcc(),
								prodList.getSpn(), rs.getInt("PACK_ID"), rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
					}
				} else if (periodType == QUARTERLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						quarterlyList.add(new TCCosData(prodList.getProdId().trim(), prodList.getOpt(),
								prodList.getMcc(), prodList.getSpn(), rs.getInt("PACK_ID"),
								rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
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
			mLogger.info("CCos Finder: Value Based Cost Elements Size = " + finalC4CostList.size());

		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4CosValueBasedCostOfSkus......" + e.getMessage());
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

		// mLogger.info(productCostMap);
		return finalC4CostList;
	}

	public Set<C4CostData> getC4CosVolumeBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product productID, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {
		String spl = null;
		ResultSet rs = null;
		int index = 1;

		Set<C4CostData> finalC4CostList = null;

		Date firstPeriod, lastPeriod, lastPeriodW;
		mLogger.debug("getC4CosVolumeBasedCostOfSkus------> " + C4_RETRIVE_COS_MCC_COST_FOR_SKUS + ":" + productID);
		try {

			String geoCode = pdesc.getGeoCode();

			firstPeriod = Aperiod.getPeriodStartDateForLessNearest(dateList[0]);
			lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY);
			lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;

			spl = C4MccPLConversionService.getSpl(productID, util);

			util.setSQL(C4_RETRIVE_COS_MCC_COST_FOR_SKUS);
			util.setString(index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);

			util.setString(++index, STATUS);
			util.setDate(++index, firstPeriod);
			util.setDate(++index, lastPeriod);
			util.setString(++index, spl);
			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();

			rs = (ResultSet) util.getObject(index);
			char periodType = ' ';

			finalC4CostList = new LinkedHashSet<C4CostData>();

			List<TCCosData> weeklyList = new ArrayList<TCCosData>();
			List<TCCosData> monthlyList = new ArrayList<TCCosData>();
			List<TCCosData> quarterlyList = new ArrayList<TCCosData>();

			while (rs.next()) {
				periodType = rs.getString("PERIOD_TYPE").trim().charAt(0);

				if (periodType == WEEKLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						weeklyList.add(new TCCosData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(),
								(productID.getProdId().trim() + "|" + productID.getOpt() + "|" + productID.getSpn())
										.hashCode(),
								rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
					}

				} else if (periodType == MONTHLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						monthlyList.add(new TCCosData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(),
								(productID.getProdId().trim() + "|" + productID.getOpt() + "|" + productID.getSpn())
										.hashCode(),
								rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
					}
				} else if (periodType == QUARTERLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						quarterlyList.add(new TCCosData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(),
								(productID.getProdId().trim() + "|" + productID.getOpt() + "|" + productID.getSpn())
										.hashCode(),
								rs.getString("ELEMENT_TYPE"), rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()), type));
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
			mLogger.info("CCos Finder: Volume Based Cost Elements SIze = " + finalC4CostList.size());
		} catch (SQLException e) {
			mLogger.error("Exception occured at this method getC4CosVolumeBasedCostOfSkus......" + e.getMessage());
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
		// mLogger.info(productCostMap);
		return finalC4CostList;
	}

	public Set<C4CostData> getC4OpexVolumeBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product productID, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		String spl = null;
		ResultSet rs = null;
		int index = 1;
		Set<C4CostData> finalC4CostList = null;
		mLogger.debug("getC4OpexVolumeBasedCostOfSkus----> " + C4_RETRIVE_OPEX_MCC_COST_FOR_SKUS + ":" + productID);
		try {

			spl = C4MccPLConversionService.getSpl(productID, util);

			Date firstPeriodW = Aperiod.getPeriodStartDate(_dateList[0], EperiodType.WEEKLY),
					firstPeriod = Aperiod.getPeriodStartDate(_dateList[0], EperiodType.QUARTERLY),
					lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY),
					lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			if (firstPeriodW.before(firstPeriod))
				firstPeriod = firstPeriodW; // over
			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;

			mLogger.info("getC4OpexVolumeBasedCostOfSkus" + firstPeriod + ":" + lastPeriod);

			String geoCode = pdesc.getGeoCode();

			util.setSQL(C4_RETRIVE_OPEX_MCC_COST_FOR_SKUS);

			util.setString(index,
					productID.getProdId() != null && productID.getProdId().trim().length() > 0
							? productID.getProdId().trim()
							: null);
			util.setString(++index,
					productID.getOpt() != null && productID.getOpt().trim().length() > 0 ? productID.getOpt().trim()
							: null);
			util.setString(++index,
					productID.getSpn() != null && productID.getSpn().trim().length() > 0 ? productID.getSpn().trim()
							: null);
			util.setString(++index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);
			util.setString(++index, STATUS);
			util.setDate(++index, firstPeriod);
			util.setDate(++index, lastPeriod);
			util.setString(++index, spl);
			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);
			util.execute();

			rs = (ResultSet) util.getObject(index);

			int packId = 0;
			char periodType = ' ';

			finalC4CostList = new LinkedHashSet<C4CostData>();

			List<TOpexData> weeklyList = new ArrayList<TOpexData>();
			List<TOpexData> monthlyList = new ArrayList<TOpexData>();
			List<TOpexData> quarterlyList = new ArrayList<TOpexData>();

			while (rs.next()) {
				periodType = rs.getString("PERIOD_TYPE").trim().charAt(0);
				packId = rs.getInt("PACK_ID");
				if (periodType == WEEKLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						weeklyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}

//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":" + rs.getString("PROD_LINE")
//							+ ":" + rs.getString("PLATFORM") + ":" + rs.getString("SUB_PLATFORM") + ":"
//							+ rs.getString("SELLING_MODEL"));
				} else if (periodType == MONTHLY) {

					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						monthlyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}
//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":" + rs.getString("PROD_LINE")
//							+ ":" + rs.getString("PLATFORM") + ":" + rs.getString("SUB_PLATFORM") + ":"
//							+ rs.getString("SELLING_MODEL"));
				} else if (periodType == QUARTERLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						quarterlyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"), periodType,
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}
//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":" + rs.getString("PROD_LINE")
//							+ ":" + rs.getString("PLATFORM") + ":" + rs.getString("SUB_PLATFORM") + ":"
//							+ rs.getString("SELLING_MODEL"));
				}

			} // for
			if (weeklyList.size() > 0) {
				finalC4CostList.addAll(weeklyList);
			} else if (monthlyList.size() > 0) {
				finalC4CostList.addAll(monthlyList);
			} else if (quarterlyList.size() > 0) {
				finalC4CostList.addAll(quarterlyList);
			}
			mLogger.info("Opex Finder: Volume Based Cost Elements Size =" + finalC4CostList.size());

		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4OpexVolumeBasedCostOfSkus......" + e.getMessage());
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
		return finalC4CostList;
	}

	public Set<C4CostData> getC4OpexValueBasedCostOfSkus(DefaultPriceDescriptor pdesc,
			com.hp.c4.rsku.rSku.pojo.Product productID, String[] dateList, Map<Character, TPeriodData> periodIdMap,
			Date[] _dateList, String type) throws Exception {

		ResultSet rs = null;
		int index = 1;
		Date firstPeriod, lastPeriod, lastPeriodW, firstPeriodW;
		Set<C4CostData> finalC4CostList = null;
		mLogger.debug("getC4OpexValueBasedCostOfSkus----> " + C4_RETRIVE_OPEX_COST_FOR_SKUS + ":" + productID);
		try {

			firstPeriodW = Aperiod.getPeriodStartDate(_dateList[0], EperiodType.WEEKLY);
			firstPeriod = Aperiod.getPeriodStartDate(_dateList[0], EperiodType.QUARTERLY);
			lastPeriod = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.MONTHLY);
			lastPeriodW = Aperiod.getPeriodStartDate(_dateList[_dateList.length - 1], EperiodType.WEEKLY);

			// MPO R2: MOT: Period consolidation bug. Modified by Janaki
			if (firstPeriodW.before(firstPeriod))
				firstPeriod = firstPeriodW; // over
			if (lastPeriodW.after(lastPeriod))
				lastPeriod = lastPeriodW;
			// mLogger.info("getC4OpexValueBasedCostOfSkus" + firstPeriod + ":" +
			// lastPeriod);

			String geoCode = pdesc.getGeoCode();

			util.setSQL(C4_RETRIVE_OPEX_COST_FOR_SKUS);

			util.setString(index,
					productID.getProdId() != null && productID.getProdId().trim().length() > 0
							? productID.getProdId().trim()
							: null);
			util.setString(++index,
					productID.getOpt() != null && productID.getOpt().trim().length() > 0 ? productID.getOpt().trim()
							: null);
			util.setString(++index,
					productID.getMcc() != null && productID.getMcc().trim().length() > 0 ? productID.getMcc().trim()
							: null);
			util.setString(++index,
					productID.getSpn() != null && productID.getSpn().trim().length() > 0 ? productID.getSpn().trim()
							: null);
			util.setString(++index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);
			util.setString(++index, STATUS);
			util.setDate(++index, firstPeriod);
			util.setDate(++index, lastPeriod);

			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.CURSOR);

			util.execute();
			rs = (ResultSet) util.getObject(index);

			char periodType = ' ';
			int packId = 0;

			finalC4CostList = new LinkedHashSet<C4CostData>();

			List<TOpexData> weeklyList = new ArrayList<TOpexData>();
			List<TOpexData> monthlyList = new ArrayList<TOpexData>();
			List<TOpexData> quarterlyList = new ArrayList<TOpexData>();

			while (rs.next()) {
				periodType = rs.getString("PERIOD_TYPE").trim().charAt(0);
				if (periodType == WEEKLY) {

					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						weeklyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}

//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")));
				} else if (periodType == MONTHLY) {

					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						monthlyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}
//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")));
				} else if (periodType == QUARTERLY) {
					int periodId = ((TPeriodData) periodIdMap.get(periodType)).getPeriodId();
					if (periodIdMap.containsKey(periodType) && periodId == rs.getInt("PERIOD_ID")) {
						quarterlyList.add(new TOpexData(productID.getProdId().trim(), productID.getOpt(),
								productID.getMcc(), productID.getSpn(), packId, rs.getString("ELEMENT_TYPE"),
								rs.getInt("PERIOD_ID"), rs.getString("GEO_CODE"),
								rs.getString("GEO_LEVEL").trim().charAt(0), rs.getTimestamp("TIMESTAMP"),
								rs.getString("GBATCH_ID"), rs.getFloat("COST"),
								rs.getString("PERIOD_TYPE").trim().charAt(0),
								new java.util.Date(rs.getDate("START_DATE").getTime()),
								SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")),
								SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")),
								SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")), type));
					}
//					System.out.println(productID.getProdId() + ":" + productID.getOpt() + ":" + productID.getMcc() + ":"
//							+ productID.getSpn() + ":" + packId + ":" + rs.getString("ELEMENT_TYPE") + ":"
//							+ rs.getInt("PERIOD_ID") + ":" + rs.getString("GEO_CODE") + ":"
//							+ rs.getString("GEO_LEVEL").trim().charAt(0) + ":"
//							+ new java.util.Date(rs.getDate("TIMESTAMP").getTime()) + ":" + rs.getString("GBATCH_ID")
//							+ ":" + rs.getFloat("COST") + ":" + periodType + ":"
//							+ new java.util.Date(rs.getDate("START_DATE").getTime()) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PROD_LINE")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SUB_PLATFORM")) + ":"
//							+ SQLUtilAbstract.checkNullString(rs.getString("SELLING_MODEL")));
				}

			} // for
			if (weeklyList.size() > 0) {
				finalC4CostList.addAll(weeklyList);
			} else if (monthlyList.size() > 0) {
				finalC4CostList.addAll(monthlyList);
			} else if (quarterlyList.size() > 0) {
				finalC4CostList.addAll(quarterlyList);
			}
			mLogger.info("Opex Finder: Value Based Cost Elements Size =" + finalC4CostList.size());

		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4OpexValueBasedCostOfSkus......" + e.getMessage());
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
		return finalC4CostList;
	}

	public boolean isValueType(Product packId, char geoLevel, String geoCode) throws SQLException {
		util.setSQL(MCC_CODE_COST_COUNT);
		int index = 1, n = 0;
		try {
			util.setString(index,
					packId.getProdId() != null && packId.getProdId().trim().length() > 0 ? packId.getProdId().trim()
							: null);
			util.setString(++index,
					packId.getOpt() != null && packId.getOpt().trim().length() > 0 ? packId.getOpt().trim() : null);
			util.setString(++index,
					packId.getMcc() != null && packId.getMcc().trim().length() > 0 ? packId.getMcc().trim() : null);
			util.setString(++index,
					packId.getSpn() != null && packId.getSpn().trim().length() > 0 ? packId.getSpn().trim() : null);
			util.setString(++index, geoCode != null && geoCode.trim().length() > 0 ? geoCode.trim() : null);
			util.setString(++index, STATUS);
			util.registerOutParameter(++index, oracle.jdbc.OracleTypes.INTEGER);
			util.execute();
			n = util.getInt(index);
		} finally {
			if (util != null)
				util.subclose();
		}

		return (n > 0);
	}

}
