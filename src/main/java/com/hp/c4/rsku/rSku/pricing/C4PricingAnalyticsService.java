package com.hp.c4.rsku.rSku.pricing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.c4.rsku.rSku.bean.request.TCosData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.CdbConnectionMgr;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.PlMappingData;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.rest.services.C4AggregateCostCalculationService;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtilWithConnection;

@Component
public class C4PricingAnalyticsService {

	private static final Logger mLogger = LogManager.getLogger(C4PricingAnalyticsService.class);

	@Autowired
	private C4AggregateCostCalculationService c4AggregateCostCalculationService;

	private Map<Product, String> accessDeinedInfo = new LinkedHashMap<Product, String>();

	private final String NOT_IN_HIERARCHY = "product not loaded in hierarchy";
	private final String BELONGS_TO_PL = "product belongs to PL ";

	protected static final String WorldWide = "Worldwide";
	private static final String notFound = "NOT_FOUND";

	// The return values from getPermission for either PL or MENU
	public static final int OPERATION_PERMITTED = 1;
	public static final int PERMISSION_DENIED = -2;
	public static final int OPERATION_NOT_PERMITTED = 0;

	private DecimalFormat decimalFormat = new DecimalFormat("##.00");

	public String getAllC4ProductsMonthly() {

		SQLUtilWithConnection util = null;
		ResultSet rs = null;
		PreparedStatement ps = null;

		ResultSet rs1 = null;
		PreparedStatement ps1 = null;
		try {
			Connection theConnection = CdbConnectionMgr.getConnectionMgr()
					.getConnection(DBConstants.C4_DBPOOL_C4PROD_ONSI);

			ps = theConnection.prepareStatement(DBConstants.FIND_SELECT_T_PERIOD_ID);

			ps.setString(1, "01-APR-22");
			rs = ps.executeQuery();
			TPeriodData tperiod = null;
			if (rs.next()) {

				Integer periodId = rs.getInt("PERIOD_ID");
				String periodType = rs.getString("PERIOD_TYPE");
				Date startDate = rs.getDate("START_DATE");
				tperiod = new TPeriodData(periodId, periodType.charAt(0), startDate);
			}

			Map<Character, TPeriodData> periodIdMap = new HashMap<Character, TPeriodData>();
			periodIdMap.put('M', tperiod);
			mLogger.info("C4 PEriod ID's are available" + periodIdMap);

			Map<String, DefaultPriceDescriptor> defaultPriceDescMap = Cache.getAllDefaultCntryPriceDescriptors();

			Set<String> countryNames = defaultPriceDescMap.keySet();
			for (String country : countryNames) {
				DefaultPriceDescriptor geoCodes = defaultPriceDescMap.get(country);
				mLogger.info("Testing...." + tperiod.getPeriodId() + ":" + country);

				long start = System.currentTimeMillis();
				ps1 = theConnection.prepareStatement(DBConstants.FIND_ALL_SKU_COUNTRY_PERIOD_ID);

				ps1.setInt(1, tperiod.getPeriodId());
				ps1.setString(2, geoCodes.getGeoCode());
				rs1 = ps1.executeQuery();

				List<Product> uniqueSkusFromRskuRequest = new ArrayList<Product>();

				while (rs1.next()) {
					Product product = new Product();

					int packId = rs1.getInt("PACK_ID");
					String prodId = rs1.getString("PRODUCT_ID");
					String opt = rs1.getString("OPT");
					String spn = rs1.getString("SPN");
					String mcc = rs1.getString("MCC");

					product.setProdId(prodId);
					product.setSpn(spn);
					product.setOpt(opt);
					product.setMcc(mcc);
					uniqueSkusFromRskuRequest.add(product);
				}

				long end = System.currentTimeMillis();

				long minutes = TimeUnit.MILLISECONDS.toMinutes(end - start);
				mLogger.info("Time Taken for country :" + country + ":filter with Dates =" + tperiod.getStartDate()
						+ "::" + minutes + ":Size =" + uniqueSkusFromRskuRequest.size());

				Date[] _dateList = Aperiod.getAscendingDates(new String[] { "2022/04/01" });

				Product[] prodList =  uniqueSkusFromRskuRequest.toArray(new Product[0]);

				//String[] pls = getPLMappingData(prodList);

				//Product[] accessedProducts = checkAccess(prodList, pls, geoCodes);

				extratctCostElements(prodList,
						c4AggregateCostCalculationService.getC4CostOfSkus(geoCodes,
								prodList, new String[] { "2022/04/01" },
								periodIdMap, _dateList));
			}

		} catch (Exception e) {
			mLogger.error("Exception occured at this method getC4CostOfSkus......" + e.getMessage());
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

		return "Welcom";
	}

	private String[] getPLMappingData(com.hp.c4.rsku.rSku.pojo.Product[] prodList) {
		mLogger.info("PL Mapping for the Requested Base SKU's");
		try {
			String[] thePLs = new String[prodList.length];
			PlMappingData[] data = Cache.getPlMappingInfo(prodList);

			for (int i = 0; i < data.length; i++) { // we populate the result arrya with the values
				thePLs[i] = data[i].get_pl();
			}
			return thePLs;
		} catch (SQLException e) {
			mLogger.error("Exception occured at ...." + e.getMessage());
		}
		return null;
	}

	/**
	 * Filtering all the Requested SKU's are belongs Valid PL's or Not.
	 * 
	 * @param prodList
	 * @param pls
	 * @param pdesc
	 * @return
	 * @throws C4SecurityException
	 */
	private Product[] checkAccess(com.hp.c4.rsku.rSku.pojo.Product[] prodList, String[] pls,
			DefaultPriceDescriptor pdesc) throws C4SecurityException {
		mLogger.info("Checking for SKU's are Valid PL's which belongs to HPI/Not........");
		StringBuffer sbf = null;
		int count = 0;
		for (int i = 0; i < prodList.length; i++) {
			if (!notFound.equals(pls[i]))
				count++;
		}
		boolean[] p = null;
		String[] res = new String[count];
		short[] ret = new short[count * 3];
		p = new boolean[count];
		int[] missedOut = new int[prodList.length - count];
		int missedOutCounter = 0;
		int counter = 0;

		for (int i = 0; i < prodList.length; i++) {
			if (!notFound.equals(pls[i])) {
				sbf = new StringBuffer();
				sbf.append(pls[i]);
				sbf.append("~");
				sbf.append(WorldWide);
				sbf.append("~");
				sbf.append(pdesc.getRegion());
				sbf.append("~");
				sbf.append(pdesc.getCountry());
				res[counter++] = sbf.toString();
//	          res[counter++] = pls[i]+"~"+WorldWide + "~" + geoDesc.region + "~" + geoDesc.country;
			} else {
				missedOut[missedOutCounter++] = i;
			}
		}
		// mLogger.info("Checking The List of PL's" + "::::" + Arrays.toString(res) +
		// "For PriceDesc ::" + pdesc);

		mLogger.info("Getting the Permissions for the Pls statred......");
		ret = Cache.getPermissions(res);
		boolean[] returnResult = null;
		reconcile(ret, p);
		returnResult = new boolean[prodList.length];
		int incretr = 0;
		for (int i = 0; i < prodList.length; i++) {
			boolean missOut = false;
			for (int j = 0; j < missedOutCounter; j++) {
				if (missedOut[j] == i) {
					returnResult[i] = true;
					missOut = true;
					break;
				}
			}
			if (!missOut) {
				returnResult[i] = p[incretr++];
			}
		}

		ArrayList<Integer> deniedIndexes = new ArrayList<Integer>();
		ArrayList<Product> deniedProds = new ArrayList<Product>();
		ArrayList<Product> acceptedProds = new ArrayList<Product>();
		if (accessDeinedInfo != null) {
			accessDeinedInfo.clear();
		}
		for (int i = 0; i < returnResult.length; i++) {
			if (returnResult[i])
				acceptedProds.add(prodList[i]);
			else {
				deniedIndexes.add(new Integer(i));
				deniedProds.add(prodList[i]);

				String PL = pls[i];
				if (PL != null) {
					PL = BELONGS_TO_PL + PL;
				} else {
					PL = NOT_IN_HIERARCHY;
				}
				accessDeinedInfo.put(prodList[i], PL);
			}

		}

		if (deniedProds.size() != 0) {
			int[] indexes = new int[deniedProds.size()];
			for (int i = 0; i < deniedProds.size(); i++) {
				indexes[i] = ((Integer) deniedIndexes.get(i)).intValue();
			}
		}
		mLogger.info("User requested Access Denied Info : " + ":" + accessDeinedInfo);
		mLogger.info("User requested Access Products Info : " + ":" + acceptedProds);
		mLogger.info("Getting the Permissions for the Pls Done......");
		return (Product[]) acceptedProds.toArray(new Product[acceptedProds.size()]);
	}

	private void reconcile(short[] in, boolean[] out) {
		for (int i = 0; i < out.length; i++) {
			int count = 0;
			for (int j = i * 3; count < 3; count++, j++) {
				boolean found = false;
				switch (in[j]) {
				case OPERATION_PERMITTED:
					out[i] = true;
					out[i] = true;
					found = true;
					break;

				case PERMISSION_DENIED:
					out[i] = false;
					out[i] = false;
					found = true;
					break;

				case OPERATION_NOT_PERMITTED:
					out[i] = false;
					out[i] = false;
					break;

				default:
				}
				if (found)
					break;
			}
		}
	}

	/**
	 * Extracting the Cost and performing Rollup the Cost to Rsku
	 * 
	 * @param accessedProducts
	 * @param productCostMap
	 */
	private void extratctCostElements(Product[] accessedProducts,
			Map<com.hp.c4.rsku.rSku.pojo.Product, Set<TCosData>> productCostMap) {
		Map<Product, Map<String, Float>> allSkuCostElementsMap = new LinkedHashMap<Product, Map<String, Float>>();
		mLogger.info("Individual Cost elemnts roll-up is started...... ");

		Map<String, Float> sumUpAllSkuElementsCost = new LinkedHashMap<String, Float>();

		for (Product product : accessedProducts) {
			Map<String, Float> elemntsCost = allSkuCostElementsMap.get(product);
			if (elemntsCost == null) {
				elemntsCost = new HashMap<String, Float>();
			}
			Set<TCosData> tcosDataList = productCostMap.get(product);

			for (TCosData tCosData : tcosDataList) {
				if (tCosData.getProdId().equalsIgnoreCase(product.getProdId())) {
					elemntsCost.put(tCosData.getElementType(), tCosData.getCost());
					Float elementCost = tCosData.getCost();
					if (sumUpAllSkuElementsCost.get(tCosData.getElementType()) != null)
						elementCost += sumUpAllSkuElementsCost.get(tCosData.getElementType());
					sumUpAllSkuElementsCost.put(tCosData.getElementType(),
							Float.parseFloat(decimalFormat.format(elementCost)));
				}
			}

			allSkuCostElementsMap.put(product, elemntsCost);
		}

		mLogger.info("Individual Cost elemnts roll-up is Done...... ");
		mLogger.info(allSkuCostElementsMap);
		mLogger.info(sumUpAllSkuElementsCost);
		// calculateTCost(accessedProducts, allSkuCostElementsMap,
		// sumUpAllSkuElementsCost);

	}
}
