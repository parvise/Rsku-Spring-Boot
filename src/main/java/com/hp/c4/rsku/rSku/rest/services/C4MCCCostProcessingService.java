package com.hp.c4.rsku.rSku.rest.services;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.bean.request.C4CostData;
import com.hp.c4.rsku.rSku.bean.request.TOpexData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.constants.C4RskuLabelConstants;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CmccRateIO;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;

@Service
public class C4MCCCostProcessingService extends C4RskuLabelConstants {

	private static final Logger mLogger = LogManager.getLogger(C4MCCCostProcessingService.class);

	@Autowired
	private C4AggregateCostCalculationService c4AggregateCostCalculationService;

	@Autowired
	private C4CostProcessingService c4CostProcessingService;

	private SQLUtil util = null;

	@Bean
	public void initMcc() {
		try {
			long start = System.currentTimeMillis();
			util = new SQLUtil(DBConstants.C4_DBPOOL_C4PROD_ONSI);
			C4MccPLConversionService.initalizeMccSpl(util);
			long end = System.currentTimeMillis();

			mLogger.info("Mcc Codes initialized completes : " + (end - start) / 1000);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get MCC codes
	 * 
	 * @param prods
	 * @param date
	 * @param desc
	 * @return
	 */
	private Map<Product, List<String>> getMccs(Product[] accessedProducts, String date, DefaultPriceDescriptor desc) {
		Map<Product, List<String>> result = new LinkedHashMap<Product, List<String>>();
		try {
			CmccRateIO io = new CmccRateIO();
			Date dateObj = (new SimpleDateFormat("yyyy/MM/dd")).parse(date);
			String[] mccs = io.getAllMccsByArray(accessedProducts, desc.getPriceCountryCode(),
					desc.getPriceCurrencyCode(), desc.getPriceTermCode(), dateObj);
			for (int i = 0; i < mccs.length; i++) {
				ArrayList<String> list = new ArrayList<String>();
				if (mccs[i] != null) {
					StringTokenizer tokenizer = new StringTokenizer(mccs[i], ",");
					while (tokenizer.hasMoreTokens()) {
						list.add(tokenizer.nextToken());
					}
				}
				if (list.size() > 0 && accessedProducts[i].getMcc() != null)
					accessedProducts[i].setAutoMccAvl(true);
				result.put(accessedProducts[i], list);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}

	public void validateMCCCost(Product[] accessedProducts, com.hp.c4.rsku.rSku.bean.request.RSkuRequest request,
			DefaultPriceDescriptor pdesc) throws Exception {

		Map<Product, List<String>> mccs = new HashMap<Product, List<String>>();

		mLogger.info("Products Without MCC:" + Arrays.toString(accessedProducts));

		mccs = getMccs(accessedProducts, request.getCostDate(), pdesc);

		List<Product> mccProducts = getMccProducts(mccs);

		Set<Product> uniqueProducts = new LinkedHashSet<Product>();

		uniqueProducts.addAll(Arrays.asList(accessedProducts));
		uniqueProducts.addAll(mccProducts);

		// Product[] ids = (Product[]) mccsIds.get(0);

//		Product[] newList = new Product[accessedProducts.length + ids.length];
//		System.arraycopy(accessedProducts, 0, newList, 0, accessedProducts.length);
//		System.arraycopy(ids, 0, newList, accessedProducts.length, ids.length);

		Product[] newList = new Product[uniqueProducts.size()];
		System.arraycopy(uniqueProducts.toArray(), 0, newList, 0, uniqueProducts.size());

		mLogger.info("Products With MCC:" + Arrays.toString(newList));

		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productOpexCostMap = createOpexCostType(request, pdesc,
				newList);

		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCCostMap = createCcosCosType(request, pdesc,
				newList);

		c4CostProcessingService.extratctC4CostElements(newList, productCCostMap, productOpexCostMap);

		mLogger.info("Opex Cost Elements=" + productOpexCostMap);
		mLogger.info("CCost Elements=" + productCCostMap);

	}

	private Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> createOpexCostType(
			com.hp.c4.rsku.rSku.bean.request.RSkuRequest request, DefaultPriceDescriptor pdesc, Product[] newList) {
		String[] c4CostDates = request.getC4CostDates();
		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCostMap = null;
		Map<Character, TPeriodData> periodIdMap = Cache.findTPeriodIds(c4CostDates);
		mLogger.info("C4 PEriod ID's are available" + periodIdMap);

		String[] dateList = new String[] { request.getCostDate() };
		Date[] _dateList = Aperiod.getAscendingDates(dateList);
		try {

			productCostMap = new LinkedHashMap<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>>();
			CmccRateIO io = new CmccRateIO();
			Date dateObj = (new SimpleDateFormat("yyyy/MM/dd")).parse(request.getCostDate());

			for (int i = 0; i < newList.length; i++) {
				String type = createOpexCostFinder(GEO_LEVEL_REGION, pdesc.getGeoCode(), newList[i]);

				if (type.equalsIgnoreCase(OPEXP_TYPE) || type.equalsIgnoreCase(OPEXP_VALUE_TYPE)) {
					// Type = 3 or 4
					// {call c4Output.selectOpExpByW1 (?,?,?,?,?,?,?,?,?)}
					productCostMap.put(newList[i], c4AggregateCostCalculationService
							.getC4OpexValueBasedCostOfSkus(pdesc, newList[i], dateList, periodIdMap, _dateList, type));
				} else {
					// 5 DSCPC etc
					// {call c4Output.selectOpExpByMCC (?,?,?,?,?,?,?,?,?)}
					Set<C4CostData> finalC4CostList = c4AggregateCostCalculationService
							.getC4OpexVolumeBasedCostOfSkus(pdesc, newList[i], dateList, periodIdMap, _dateList, type);
					productCostMap.put(newList[i], finalC4CostList);

					mLogger.info("Test...." + type + ":" + newList[i]);
					Float priceLCP = io.getMccCostByProdId(newList[i].getProdId(), newList[i].getOpt(),
							newList[i].getSpn(), newList[i].getMcc(), pdesc.getPriceCountryCode(),
							pdesc.getPriceCurrencyCode(), pdesc.getPriceTermCode(), dateObj);

					Float rateFOREX = io.getCurrencyRateByDate(pdesc.getPriceCurrencyCode(),
							C4MccPLConversionService.getSpl(newList[i], util), dateObj);

					finalC4CostList
							.add(new TOpexData(newList[i].getProdId().trim(), newList[i].getOpt(), newList[i].getMcc(),
									newList[i].getSpn(), 0, LCP, 0, pdesc.getGeoCode(), pdesc.getRegion().charAt(0),
									dateObj, null, priceLCP, GEO_LEVEL_REGION, null, null, null, null, null, type));
					finalC4CostList
							.add(new TOpexData(newList[i].getProdId().trim(), newList[i].getOpt(), newList[i].getMcc(),
									newList[i].getSpn(), 0, FOREX, 0, pdesc.getGeoCode(), pdesc.getRegion().charAt(0),
									dateObj, null, rateFOREX, GEO_LEVEL_REGION, null, null, null, null, null, type));

					mLogger.info("priceLCP=" + priceLCP + "rateFOREX=" + rateFOREX);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productCostMap;
	}

	private Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> createCcosCosType(
			com.hp.c4.rsku.rSku.bean.request.RSkuRequest request, DefaultPriceDescriptor pdesc, Product[] newList) {
		String[] c4CostDates = request.getC4CostDates();
		Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCostMap = null;
		Map<Character, TPeriodData> periodIdMap = Cache.findTPeriodIds(c4CostDates);
		mLogger.info("C4 PEriod ID's are available" + periodIdMap);

		String[] dateList = new String[] { request.getCostDate() };
		Date[] _dateList = Aperiod.getAscendingDates(dateList);
		try {
			productCostMap = new LinkedHashMap<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>>();

			for (int i = 0; i < newList.length; i++) {
				String type = createCosCostFinder(GEO_LEVEL_REGION, pdesc.getGeoCode(), newList[i]);

				if (type.equalsIgnoreCase(COS_TYPE) || type.equalsIgnoreCase(COS_VALUE_TYPE)) {
					// {call c4Output.selectCOSByW1 (?,?,?,?,?,?,?,?,?)}
					// Type= 0 or 1
					productCostMap.put(newList[i], c4AggregateCostCalculationService.getC4CosValueBasedCostOfSkus(pdesc,
							newList[i], dateList, periodIdMap, _dateList, type));
				} else {
					// {call c4Output.selectCosByMCC (?,?,?,?,?,?)}
					// 2 MCC MATRL %
					productCostMap.put(newList[i], c4AggregateCostCalculationService
							.getC4CosVolumeBasedCostOfSkus(pdesc, newList[i], dateList, periodIdMap, _dateList, type));
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productCostMap;
	}

	private String createCosCostFinder(char geoLevel, String geoCode, Product product) throws SQLException {

		boolean mcc = (product.getMcc() != null) && !product.getMcc().trim().equals("");
		boolean value = false;

		if (mcc)
			value = c4AggregateCostCalculationService.isValueType(product, geoLevel, geoCode);
		if (mcc && value) {
			mLogger.info("CCos Value Type=" + COS_VALUE_TYPE);
			return COS_VALUE_TYPE;
		} else if (mcc && !value) {
			mLogger.info("CCos Volume Type=" + COS_VOLUME_TYPE);
			return COS_VOLUME_TYPE;
		} else {
			mLogger.info("CCos Cos Type=" + COS_TYPE);
			return COS_TYPE;
		}

	}

	private String createOpexCostFinder(char geoLevel, String geoCode, Product product) throws SQLException {

		boolean mcc = (product.getMcc() != null) && !product.getMcc().trim().equals("");
		boolean value = false;

		if (mcc)
			value = c4AggregateCostCalculationService.isValueType(product, geoLevel, geoCode);
		if (mcc && value) {
			mLogger.info("Opex Value Type=" + OPEXP_VALUE_TYPE);
			return OPEXP_VALUE_TYPE;
		} else if (mcc && !value) {
			mLogger.info("Opex Volume Type=" + OPEXP_VOLUME_TYPE);
			return OPEXP_VOLUME_TYPE;
		} else {
			mLogger.info("Opex Cos Type=" + OPEXP_TYPE);
			return OPEXP_TYPE;
		}

	}

	private List<Product> getMccProducts(Map<Product, List<String>> mccs) {
		List<Product> res = new ArrayList<Product>();

		for (Iterator<Product> i = mccs.keySet().iterator(); i.hasNext();) {
			Product prod = (Product) i.next();
			ArrayList<String> list = (ArrayList<String>) mccs.get(prod);
			for (Iterator<String> j = list.iterator(); j.hasNext();) {
				Product id = new Product();
				id.setProdId(prod.getProdId());
				id.setOpt(prod.getOpt());
				id.setSpn(prod.getSpn());
				id.setRequestedMcc(prod.isRequestedMcc());
				id.setMcc((String) j.next());
				id.setAutoMccAvl(prod.isAutoMccAvl());
//				if (id.getMcc() != null)
//					id.setAutoMccAvl(true);

//				if (!isAutoMcc && prod.isAutoMccAvl()) {
//					isAutoMcc = true;
//				}
//				if (!isRequested && prod.isRequestedMcc()) {
//					isRequested = true;
//				}
//				id.setAutoMccAvl(isAutoMcc);
//				id.setRequestedMcc(isRequested);

				res.add(id);
			}
		}

		return res;
	}

}
