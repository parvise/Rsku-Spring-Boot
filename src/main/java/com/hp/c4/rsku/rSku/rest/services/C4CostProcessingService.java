package com.hp.c4.rsku.rSku.rest.services;

import java.io.File;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.bean.request.C4BaseProduct;
import com.hp.c4.rsku.rSku.bean.request.C4CostData;
import com.hp.c4.rsku.rSku.bean.request.RSkuProduct;
import com.hp.c4.rsku.rSku.bean.request.RSkuRequest;
import com.hp.c4.rsku.rSku.bean.request.TCosData;
import com.hp.c4.rsku.rSku.bean.request.TPeriodData;
import com.hp.c4.rsku.rSku.bean.response.C4Response;
import com.hp.c4.rsku.rSku.bean.response.PLMaskElements;
import com.hp.c4.rsku.rSku.bean.response.ProductCost;
import com.hp.c4.rsku.rSku.bean.response.ProductWithMcc;
import com.hp.c4.rsku.rSku.bean.response.RSkuResponse;
import com.hp.c4.rsku.rSku.bean.response.Response;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.constants.C4RskuLabelConstants;
import com.hp.c4.rsku.rSku.constants.DBConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.PlMappingData;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CSubmitCosIO;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CmccRateIO;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CmccRateIO.RateRange;
import com.hp.c4.rsku.rSku.pojo.DefaultMotTradingExpense;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;
import com.hp.c4.rsku.rSku.pojo.Product;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;
import com.hp.c4.rsku.rSku.security.server.util.SFTPUtil;
import com.hp.c4.rsku.rSku.security.server.util.SQLUtil;
import com.hp.c4.rsku.rSku.security.server.util.UtilConstants;
import com.hp.c4.rsku.rSku.security.server.util.ValidateC4RskuRequest;
import com.hp.c4.rsku.rSku.security.server.util.constatnts.DeliveryMethodConstants;
import com.hp.c4.rsku.rSku.security.server.util.icost.C4LoaderBindingImpl;
import com.hp.c4.rsku.rSku.security.server.util.icost.C4LoaderPortType;
import com.hp.c4.rsku.rSku.security.server.util.icost.CcosCost;
import com.hp.c4.rsku.rSku.security.server.util.icost.Cheader;

@Service
public class C4CostProcessingService extends C4RskuLabelConstants {

	private static final Logger mLogger = LogManager.getLogger(C4CostProcessingService.class);

	@Autowired
	private PLMaskElementsService pLMaskElementsService;

	@Autowired
	private C4AggregateCostCalculationService c4AggregateCostCalculationService;

	@Autowired
	private C4MCCCostProcessingService c4MCCCostProcessingService;

	@Autowired
	private ValidateC4RskuRequest validateC4RskuRequest;

	@Autowired
	private MOTTradingExpenseService mOTTradingExpenseService;

	private static final String WorldWide = "Worldwide";
	private static final String notFound = "NOT_FOUND";

	// The return values from getPermission for either PL or MENU
	public static final int OPERATION_PERMITTED = 1;
	public static final int PERMISSION_DENIED = -2;
	public static final int OPERATION_NOT_PERMITTED = 0;

	private Map<Product, String> accessDeinedInfo = new LinkedHashMap<Product, String>();

	private final static String NOT_IN_HIERARCHY = "product not loaded in hierarchy";
	private final static String BELONGS_TO_PL = "product belongs to PL ";

	private final static String COST_STATUS_COMPLETE = "COMPLETE";
	private final static String COST_STATUS_IN_COMPLETE = "IN-COMPLETE";
	private final static String COST_STATUS_WARNING = "WARNING";
	private final static String COST_STATUS_NOT_FOUND = "NOT-FOUND";

	private final static String COST_ACTION_INSERT = "INSERT";
	private final static String COST_ACTION_UPDATE = "UPDATE";

	// "DAP","DDU","DDP","DAT" (AIR,RAIL,TRUCK,EXPRESS,SEA)
	private final static List<String> airMotExlcudeList = Arrays.asList(new String[] { "DAP", "DDU", "DDP", "DAT" });
	private final static String[] mandatoryElements = { MALAD, MATRL, VWRTY, VRLTY, OVBLC, OFXDC };

	private Response response;
	private RSkuRequest request;

	private DecimalFormat decimalFormat = new DecimalFormat("##.00");

	private Map<Character, TPeriodData> periodIdMap;

	// Static Load once
	private Map<String, List<MaskElementTypes>> allMaskElements;
	private Map<String, List<MaskElementTypes>> defaultCostElementTypes;
	private Map<String, DefaultMotTradingExpense> defaultMotMap;

	private List<String> mandatoryList;

	private static Map<String, SortedSet<RateRange>> _theExchangeRates;
	private static Map<String, String> currencyCodeMap;
	private CmccRateIO mccRateIO;
	private CurrencyRatesDaemon dameon;

	private SQLUtil util = null;

	@Bean
	public void init() {
		long start = System.currentTimeMillis();
		try {
			util = new SQLUtil(DBConstants.C4_DBPOOL_C4PROD_ONSI);
			allMaskElements = pLMaskElementsService.getAllPLMasks();
			defaultCostElementTypes = pLMaskElementsService.getAllDefaultCosElementTypes();
			defaultMotMap = mOTTradingExpenseService.getDefaultMotExpense();

			mandatoryList = Arrays.asList(mandatoryElements);

			long end = System.currentTimeMillis();
			mLogger.info("All PL Masking Elements & Default (Cost Elements & MOT) initialized completes : "
					+ (end - start) / 1000);

			mccRateIO = new CmccRateIO();

			currencyCodeMap = Cache.getIsoCurrencyCode();

			dameon = new CurrencyRatesDaemon();
			Thread th = new Thread(dameon);
			th.setDaemon(true);
			th.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Object validateRskuRequest(RSkuRequest request, boolean isRsku) throws Exception {
		this.request = request;

		mLogger.info("Performing C4 RSKU request user input Validations.......");

		Map<String, String> errorCodes = new LinkedHashMap<String, String>();
		try {
			errorCodes = validateC4RskuRequest.validateRskuRequestFields(request, isRsku);

			if (errorCodes.size() > 0) {
				mLogger.info("Validation fails refer the response......... " + errorCodes);
				return errorCodes;
			}
		} catch (C4SecurityException e1) {
			errorCodes.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
			mLogger.error("Validation fails refer the response......... " + errorCodes);
			return errorCodes;
		}
		DefaultPriceDescriptor pdesc = null;
		String[] c4CostDates = request.getC4CostDates();
		try {

			String[] dateList = new String[] { request.getCostDate() };

			mLogger.info("Pass the C4 Cost dates validation....Completes" + Arrays.toString(c4CostDates));

			pdesc = request.getDefaultPriceDescriptor();

			mLogger.info("Pass the Price Descriptor validation....Completes" + pdesc);

			periodIdMap = Cache.findTPeriodIds(c4CostDates);
			mLogger.info("C4 PEriod ID's are available" + periodIdMap);

			com.hp.c4.rsku.rSku.pojo.Product[] prodList = getListOfUniqueProducts(request.getListOfProducts());

			String[] pls = getPLMappingData(prodList);

			Product[] accessedProducts = checkAccess(prodList, pls, pdesc);

			mLogger.info("Delivery Method:"
					+ DeliveryMethodConstants.valueOf(request.getDeliveryMethod()).getDeliveryCodeDesc());

			Date[] _dateList = Aperiod.getAscendingDates(dateList);

			Calendar c = Calendar.getInstance();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
			mLogger.info("Cost dates Weekly,Monthly,Qarterly......" + Arrays.toString(c4CostDates));

			if (isRsku) {
				response = new RSkuResponse();
				response.setAccessDeniedProducts(accessDeinedInfo);
				response.setCountryCode(pdesc.getGeoCode());
				response.setCountryDesc(pdesc.getCountry());
				response.setDeliveryMethod(
						DeliveryMethodConstants.valueOf(request.getDeliveryMethod()).getDeliveryCodeDesc());
				response.setMot(request.getMot().toUpperCase());

				extratctRSkuCostElements(accessedProducts, c4AggregateCostCalculationService.getC4CostOfSkus(pdesc,
						accessedProducts, dateList, periodIdMap, _dateList));

				c.setTime(sdf.parse(c4CostDates[2]));
				String quarterDate = sdf1.format(c.getTime());
				mLogger.info("Qarter Date:......." + quarterDate);
				response.setCostDate(quarterDate);

				ProductCost rSku = ((RSkuResponse) response).getRapidSkuCostDetails();

				if (rSku != null) {
					Map<String, Float> costElements = rSku.getSkuCostElements();
					if (costElements != null && costElements.size() > 0) {
						generateImporterProcessFiles(c4CostDates[2]);
						mLogger.info(
								"C4 RSKU Request service........ Completes and Now Cost is available in Response ");
					}
				} else {
					mLogger.info("C4 RSKU Request service........ Completes... Cost is Not found.... ");
				}

			} else {
				response = new C4Response();
				response.setAccessDeniedProducts(accessDeinedInfo);
				response.setCountryCode(pdesc.getGeoCode());
				response.setCountryDesc(pdesc.getCountry());
				response.setDeliveryMethod(
						DeliveryMethodConstants.valueOf(request.getDeliveryMethod()).getDeliveryCodeDesc());
				response.setMot(request.getMot().toUpperCase());

				// Adding MCC Cost retrival Logic
				c.setTime(sdf.parse(c4CostDates[1]));
				String monthDate = sdf1.format(c.getTime());
				mLogger.info("Month Date:......." + monthDate);
				response.setCostDate(monthDate);

				if (accessedProducts.length > 0)
					c4MCCCostProcessingService.validateMCCCost(accessedProducts, request, pdesc);
				// Adding MCC Cost retrival Logic
			}

			if (currencyCodeMap != null && currencyCodeMap.containsKey(request.getOutputCurrency()))
				response.setOutputCurrency(currencyCodeMap.get(request.getOutputCurrency()));

		} catch (C4SecurityException e) {
			mLogger.error("Exception occuret at ......." + e.getMessage());
			errorCodes.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
			mLogger.error("Validation fails refer the response......... " + errorCodes);
			return errorCodes;
		} catch (ParseException e) {
			mLogger.error("Exception occuret at ......." + e.getMessage());
			errorCodes.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
			mLogger.error("Validation fails refer the response......... " + errorCodes);
			return errorCodes;
		}

		return response;

	}

	private String getPLString(String[] arrPL) {
		StringBuffer sbuff = new StringBuffer();
		try {
			if (arrPL != null) {

				for (int i = 0; i < arrPL.length; i++) {
					if (arrPL[i].equalsIgnoreCase("NULL"))// dont add NULL PL
						continue;
					sbuff.append(arrPL[i]);
					sbuff.append(",");
				}
				if (sbuff.length() > 0 && sbuff.charAt(sbuff.length() - 1) == ',')
					sbuff.deleteCharAt(sbuff.length() - 1);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return sbuff.toString();
	}

	private String getStringFromArray(String[] str, String seperator) {
		StringBuffer sbuff = new StringBuffer();
		if (str != null) {
			for (int i = 0; i < str.length; i++) {
				sbuff.append(str[i]);
				if ((i + 1) != str.length)
					sbuff.append(seperator);
			}
		}
		return sbuff.toString();
	}

	/**
	 * This method helps us to Generate the Cost files to proceess the Importer Job
	 * & Helps to insert Rapid SKU details into Database like RAS loader
	 */
	private void generateImporterProcessFiles(String quarterDate) {

		mLogger.info("Generating the Cost files at Staging location started.....");
		C4LoaderPortType port = new C4LoaderBindingImpl();
		Cheader header = new Cheader();
		header.setAuthor(IMPORTER_NOTIFY_MAIL);
		header.setSender(IMPORTER_NOTIFY_MAIL);

		String[] authorizedRegions = Cache.getUpdateGeosForUser();
		String[] userProductLines = Cache.getUpdatePLsForUser();

		header.setPls(getPLString(userProductLines));
		header.setRegions(getStringFromArray(authorizedRegions, ","));
		mLogger.info("Author notificationMailIds :" + IMPORTER_NOTIFICATION_AUTHOR_MAILIDS);
		header.setEmail(IMPORTER_NOTIFICATION_AUTHOR_MAILIDS);
		header.setBatchType(BATCH_TYPE_FULL);
		header.setPeriodType(BATCH_FILE_QUARTER);
		String c4Date = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		try {
			Date date = simpleDateFormat.parse(response.getCostDate());

			c4Date = simpleDateFormat.format(date);

			header.setStartDate(c4Date); // 8/1/2021 12:00:00 AM

			// header.setCreationDate("2002/12/31"); // 9/15/2021
			// header.setEffectiveDate("2003/02/01");// 9/14/2021 12:25:01 PM

			simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			String creationDate;

			creationDate = simpleDateFormat.format(new Date());
			header.setCreationDate(creationDate);
			// at this point now effective date contains the date in importer timezone
		} catch (Exception ex) {
			mLogger.info("Exception occured at generateImporterProcessFiles" + ex.getMessage());
		}

		simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
		String effectiveDate;
		try {
			effectiveDate = simpleDateFormat.format(new Date());
			header.setEffectiveDate(effectiveDate);
			// at this point now effective date contains the date in importer timezone
		} catch (Exception ex) {
			mLogger.info("Exception occured at generateImporterProcessFiles" + ex.getMessage());
		}

		ProductCost rSku = ((RSkuResponse) response).getRapidSkuCostDetails();

		Map<String, Float> costElements = rSku.getSkuCostElements();
		simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		List<CcosCost> list = new ArrayList<CcosCost>();
		mLogger.info("RSU Cost Elements:" + costElements);

		// Additional MOT's removed from RSku Cost which is taking care by Importer job
		List<String> excludeAddMots = Arrays.asList(EXCLUDE_ADD_MOTS.split(","));
		for (String metric : excludeAddMots) {
			if (costElements.containsKey(metric)) {
				// costElements.remove(metric);
			}
		}

		boolean isPartial = false;

		Map<String, CcosCost> rSkuCostElementsDbAvlble = preparePartialDataFile(quarterDate, list, excludeAddMots);
		mLogger.info("Already Cost has Avialble R-SKU's:" + rSkuCostElementsDbAvlble);

		if (rSkuCostElementsDbAvlble.size() > 0) {
			isPartial = true;
			header.setBatchType(BATCH_TYPE_PARTIAL);
		}

		simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CST"));
		// C_F_20211006_

		// int randomNum = random.ints(1,
		// userProductLines.length).findFirst().getAsInt();

		int randomNum = (int) (Math.random() * (userProductLines.length - 1)) + 1;
		mLogger.info("Pickup PL......:::" + userProductLines[randomNum - 1]);

		// build filename prefix & segment
		String sFileNamePrefix = "C_" + (!isPartial ? "F" : "P") + "_" + simpleDateFormat.format(new Date()) + "_01_"
				+ userProductLines[randomNum - 1];

		mLogger.info("R-SKU final File Name =" + sFileNamePrefix);

		String fromFolder = IMPORTER_INPUT_FILE_FROM_LOCATION;
		String fileName = fromFolder + sFileNamePrefix;

		try {
			port.setHeader(fileName, header);
		} catch (RemoteException e) {
			mLogger.info("Exception occured at generateImporterProcessFiles" + e.getMessage());
		}

		for (String element : costElements.keySet()) {
			CcosCost data = null;
			if (rSkuCostElementsDbAvlble.get(element) != null) {
				data = rSkuCostElementsDbAvlble.get(element);
				data.setAction(COST_ACTION_UPDATE);
				data.setCost(costElements.get(element));
			} else {
				data = new CcosCost();
				data.setProductId(rSku.getProdIdBase());
				data.setOpt("");
				data.setSpn("");
				data.setMcc("");
				data.setCountry("");
				data.setRegion("");
				data.setWw("WW");
				data.setElementType(element);
				data.setCost(costElements.get(element));
				data.setPeriodStart(c4Date);
				if (isPartial)
					data.setAction(COST_ACTION_INSERT);
			}

			rSkuCostElementsDbAvlble.put(element, data);
		}

		list.addAll(rSkuCostElementsDbAvlble.values());

		try {
			// C_P is Partial contains Action Codes & C_F is Full feed
			if (isPartial)
				port.setCosPartialData(fileName, list.toArray(new CcosCost[list.size()]), true);
			else
				port.setCosFullData(fileName, list.toArray(new CcosCost[list.size()]), true);
		} catch (RemoteException e) {
			mLogger.info("Exception occured at generateImporterProcessFiles" + e.getMessage());
		}

		// Important Thing is To insert Rapid SKU details into Database
		insertRSkuProductDetailsIntoDb();

		transferFiles(sFileNamePrefix, fromFolder);

	}

	private Map<String, CcosCost> preparePartialDataFile(String quarterDate, List<CcosCost> list,
			List<String> EXCLUDE_ADD_MOTS) {
		ProductCost rSku = ((RSkuResponse) response).getRapidSkuCostDetails();
		String rskuProdId = rSku.getProdIdBase();

		return Cache.findRskuCostAvailable(response.getCostDate(), rskuProdId, list, quarterDate, EXCLUDE_ADD_MOTS);

	}

	private void insertRSkuProductDetailsIntoDb() {
		mLogger.info("Insert/Updating the RSKU product details into C4 DB started......");
		ProductCost rSKUDetails = ((RSkuResponse) response).getRapidSkuCostDetails();
		String productId = rSKUDetails.getProdIdBase();
		String prodDesc = request.getRapidSku().getProduct().getrSkuDec();
		String pl = rSKUDetails.getMaskElements().getPl();
		// Store the Rapid SKU details into Database
		Cache.storeRapidSkuDetails(productId, pl, prodDesc);
		mLogger.info("Insert/Updating the RSKU product details into C4 DB Done......");
	}

	private void transferFiles(String fileName, String fromFolder) {

		mLogger.info("File Transfer though SFT is started........" + IS_DB_CONFIG);
		String stageServer = null;
		String username = null;
		String password = null;
		if (IS_DB_CONFIG.equalsIgnoreCase(Boolean.TRUE.toString())) {

			stageServer = IMPORTER_STAGE_SERVER_HOSTNAME; // "impitg.inc.hpicorp.net";
			username = IMPORTER_STAGE_SERVER_USERNAME;
			password = IMPORTER_STAGE_SERVER_PASSWORD;

		} else {
			CSubmitCosIO io = new CSubmitCosIO(DBConstants.C4_DBPOOL_C4PROD_OFFI);
			try {
				@SuppressWarnings("unchecked")
				HashMap<String, String> map = (HashMap<String, String>) io
						.getHostUserPwd(UtilConstants.USED_FOR_STAGING);

				stageServer = map.get(UtilConstants.HOST).toString();
				username = map.get(UtilConstants.USERNAME).toString();
				password = map.get(UtilConstants.PASSWORD).toString();
			} catch (C4Exception e) {
				mLogger.error("Error occured File Transfer though SFT ........" + e.getMessage());
			}
		}
		String copyWinFrom = fromFolder + fileName + IMPORTER_FILE_XML_EXTENSION;
		String copyWinTo = IMPORTER_INPUT_FILE_TO_LOCATION + fileName + IMPORTER_FILE_XML_EXTENSION;

		mLogger.info("Copy from " + copyWinFrom);
		mLogger.info("Copy To " + copyWinTo);
		File file = null;
		try {
			SFTPUtil sftpUtil = new SFTPUtil(username, password, stageServer);
			try {
				file = new File(copyWinFrom);
				sftpUtil.connect();
				// sftpUtil.changeDirectory(copyWinTo);
				sftpUtil.putFile(copyWinFrom, copyWinTo);
				sftpUtil.disConnect();

			} catch (Exception excp) {
				mLogger.fatal("C4FTPStager.putFile - Exception: " + excp);
			}

			mLogger.info("XML file staging done....");
			file = new File(copyWinFrom);
			boolean ok = file.delete();
			if (ok == true)
				mLogger.info("Deleted temp file " + copyWinFrom);

			copyWinFrom = fromFolder + fileName + IMPORTER_FILE_DAT_EXTENSION;
			copyWinTo = IMPORTER_INPUT_FILE_TO_LOCATION + fileName + IMPORTER_FILE_DAT_EXTENSION;
			sftpUtil = new SFTPUtil(username, password, stageServer);
			try {
				file = new File(copyWinFrom);
				sftpUtil.connect();
				// sftpUtil.changeDirectory(copyWinTo);
				sftpUtil.putFile(copyWinFrom, copyWinTo);
				sftpUtil.disConnect();

			} catch (Exception excp) {
				mLogger.fatal("C4FTPStager.putFile - Exception: " + excp);
			}

			mLogger.info("DAT file staging done....");
			file = new File(copyWinFrom);
			ok = file.delete();
			if (ok == true)
				mLogger.info("Deleted temp file " + copyWinFrom);
		} catch (Exception e1) {
			mLogger.error("Error occured at File Transfer though SFT ........" + e1.getMessage());
		}
		((RSkuResponse) response).setFileName(fileName + IMPORTER_FILE_XML_EXTENSION);
		mLogger.info("File Transfer though SFT done........");
	}

	/**
	 * Extracting the Cost and performing Rollup the Cost to Rsku
	 * 
	 * @param accessedProducts
	 * @param productCostMap
	 */
	private void extratctRSkuCostElements(Product[] accessedProducts,
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
		calculateC4RskuTCost(accessedProducts, allSkuCostElementsMap, sumUpAllSkuElementsCost);

	}

	/**
	 * Extracting the C4 Cost and Calculate Tcost
	 * 
	 * @param accessedProducts
	 * @param productCostMap
	 * @throws Exception
	 */
	public void extratctC4CostElements(Product[] accessedProducts,
			Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCostMap,
			Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productOpexCostMap) throws Exception {
		Map<Product, Map<String, Float>> baseSkuCostElementsMap = new LinkedHashMap<Product, Map<String, Float>>();
		Map<Product, Map<String, Float>> mccSkuCostElementsMap = new LinkedHashMap<Product, Map<String, Float>>();
		mLogger.info("Extract C4 Cost elemnts  is started...... ");

		Set<Product> baseProductsList = new LinkedHashSet<Product>();
		Set<Product> mccProductsList = new LinkedHashSet<Product>();
		for (Product product : accessedProducts) {
			Map<String, Float> baseElemntsCost = baseSkuCostElementsMap.get(product);
			if (baseElemntsCost == null) {
				baseElemntsCost = new HashMap<String, Float>();
			}
			Map<String, Float> mccElemntsCost = mccSkuCostElementsMap.get(product);
			if (mccElemntsCost == null) {
				mccElemntsCost = new HashMap<String, Float>();
			}
			Set<C4CostData> tcosDataList = productCostMap.get(product);
			Set<C4CostData> opexDataList = productOpexCostMap.get(product);

			for (C4CostData tCosData : tcosDataList) {
				if (tCosData.getProdId().equalsIgnoreCase(product.getProdId())) {
					if (tCosData.getElementType() == null)
						baseElemntsCost.put(MATRL, tCosData.getCost());
					else
						baseElemntsCost.put(tCosData.getElementType(), tCosData.getCost());

				}
			}

			for (C4CostData opexData : opexDataList) {
				if (opexData.getProdId().equalsIgnoreCase(product.getProdId())) {
					mccElemntsCost.put(opexData.getElementType(), opexData.getCost());
				}
			}

			if (product.getMcc() == null || product.getMcc().equals("")) {
				baseSkuCostElementsMap.put(product, baseElemntsCost);
				baseProductsList.add(product);
			} else {
				if (baseElemntsCost.size() > 0) {
					mccElemntsCost.putAll(baseElemntsCost);
				}
				mccProductsList.add(product);
				mccSkuCostElementsMap.put(product, mccElemntsCost);
			}
		}

		mLogger.info(baseSkuCostElementsMap);
		mLogger.info(mccSkuCostElementsMap);
		// calculateTCost(accessedProducts, allSkuCostElementsMap, null);

		Product[] baseProduct = baseProductsList.toArray(new Product[0]);
		Product[] mccProduct = mccProductsList.toArray(new Product[0]);
		calculateTCost(baseProduct, baseSkuCostElementsMap, productCostMap);
		calculateMccCost(mccProduct, mccSkuCostElementsMap, productCostMap);

		mLogger.info("Extract C4 Cost elemnts is Done...... ");
	}

	/**
	 * Adding MCC cost to Normal SKU Cost
	 * 
	 * @param accessedProducts
	 * @param mccSkuCostElementsMap
	 * @param productCostMap
	 * @throws Exception
	 */
	public void calculateMccCost(Product[] accessedProducts, Map<Product, Map<String, Float>> mccSkuCostElementsMap,
			Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCostMap) throws Exception {
		mLogger.info("Calculating the C4 MCC Cost started processing.......");
		try {
			List<ProductCost> cost = response.getBaseSkuCostDetails();
			int index = 0;
			List<Product> onlyMccProductsRequest = new ArrayList<Product>();
			Date dateObj = (new SimpleDateFormat("yyyy/MM/dd")).parse(request.getCostDate());
			Map<Product, PlMappingData> plData1 = Cache.getPlMappingInfo(accessedProducts);
			if (accessedProducts.length > 0) {
				for (Product product : accessedProducts) {
					float mccCost = 0.0f;
					Map<String, Float> opexElements = mccSkuCostElementsMap.get(product);
					if (plData1.containsKey(product)) {
						PlMappingData plMappingData = plData1.get(product);
						float forex = getCurrencyRate(dateObj, plMappingData);
						if (forex != 0)
							opexElements.put(OUTPUTFOREX, forex);

						if (opexElements.containsKey(MATRL))
							mccCost = getMccCost(mccCost, opexElements);
						else
							opexElements.clear();
					}

					for (index = 0; index < cost.size(); index++) {
						if (cost.size() > 0 && cost.get(index).getProdIdBase().equalsIgnoreCase(product.getProdId())
								&& !product.isRequestedMcc()) {
							if (cost.get(index).getSkuCostElements().size() > 1 && mccCost == 0.0) {
								cost.get(index).setCostStatus(COST_STATUS_IN_COMPLETE);
							}
							if (!cost.get(index).getCostStatus().equalsIgnoreCase(COST_STATUS_NOT_FOUND))
								// Summing Product Cost + MCC cost
								cost.get(index).settCos(Float
										.parseFloat("" + decimalFormat.format(cost.get(index).gettCos() + mccCost)));
							ProductWithMcc mcc = new ProductWithMcc();
							StringBuffer buf = new StringBuffer();
							buf.append(product.getProdId());
							buf.append(PIPE_SYMBOL);
							buf.append(product.getOpt());
							buf.append(PIPE_SYMBOL);
							buf.append(product.getSpn());
							buf.append(PIPE_SYMBOL);
							buf.append(product.getMcc());
							String sku = buf.toString().replaceAll("null", "");
							mcc.setSku(sku);
							mcc.setMccCostElements(opexElements);
							cost.get(index).setMcc(mcc);
							String spl = C4MccPLConversionService.getSpl(product, util);
							if (spl == null) {
								cost.get(index).setCostStatus(COST_STATUS_IN_COMPLETE);
							}
							mLogger.debug("Special PRODUCT LINE:" + spl);
							// index++;
						} else {
							onlyMccProductsRequest.add(product);
						}
					}

				}
			}

			List<ProductCost> mccSkuCostDetails = new ArrayList<ProductCost>();
			for (Product product : onlyMccProductsRequest) {
				ProductCost productCost = null;
				float mccCost = 0.0f;
				Map<String, Float> opexElements = mccSkuCostElementsMap.get(product);
				if (plData1.containsKey(product)) {
					PlMappingData plMappingData = plData1.get(product);
					PLMaskElements maskElements = new PLMaskElements(plMappingData.get_pl(),
							plMappingData.get_platform(), null);

					float forex = getCurrencyRate(dateObj, plMappingData);
					if (forex != 0)
						opexElements.put(OUTPUTFOREX, forex);
					if (opexElements.containsKey(MATRL))
						mccCost = getMccCost(mccCost, opexElements);
					else
						opexElements.clear();

					String costStatus = COST_STATUS_COMPLETE;
					if (opexElements.size() == 0 && mccCost == 0.0) {
						costStatus = COST_STATUS_NOT_FOUND;
					}
					StringBuffer buf = new StringBuffer();
					buf.append(product.getProdId());
					buf.append(PIPE_SYMBOL);
					buf.append(product.getOpt());
					buf.append(PIPE_SYMBOL);
					buf.append(product.getSpn());
					buf.append(PIPE_SYMBOL);
					buf.append(product.getMcc());
					String sku = buf.toString().replaceAll("null", "");
					Set<C4CostData> costData = productCostMap.get(product);
					Iterator<C4CostData> itr = costData.iterator();
					productCost = new ProductCost(sku, opexElements, costStatus,
							Float.parseFloat(decimalFormat.format(mccCost)), maskElements);
					mccSkuCostDetails.add(productCost);
					while (itr.hasNext()) {
						C4CostData c4CostData = itr.next();
						productCost.setFrequency(String.valueOf(c4CostData.getPeriodType()));
						TPeriodData periodData = periodIdMap.get(c4CostData.getPeriodType());
						if (periodData.getPeriodId() == c4CostData.getPeriodId())
							productCost.setPeriod(String.valueOf(periodData.getStartDate()));
						break;
					}

				}
			}

			mccSkuCostDetails.addAll(cost);
			response.setBaseSkuCostDetails(mccSkuCostDetails);
			mLogger.info("Calculating the C4 MCC Cost started Ends.......");
		} catch (SQLException | ParseException | C4Exception e) {
			e.printStackTrace();
		}

	}

	private Float getCurrencyRate(Date dateObj, PlMappingData plMappingData) throws C4Exception {
		Float forex = null;
		if (request.getOutputCurrency().equalsIgnoreCase(DEFAULT_OUTPUT_CURRENCY)) {
			forex = 1.0f;
		} else {
			if (_theExchangeRates != null) {
				SortedSet<RateRange> theSet = _theExchangeRates
						.get(getCurrencyKey(plMappingData.get_pl(), request.getOutputCurrency()));

				if (theSet == null)
					forex = null;

				for (Iterator<RateRange> i = theSet.iterator(); i.hasNext();) {
					final RateRange theRange = (RateRange) i.next();
					if (theRange.inRange(dateObj)) {
						forex = theRange.getRate();
						break;
					}
				}
			} else {
				forex = mccRateIO.getCurrencyRateOrderByDate(request.getOutputCurrency(), plMappingData.get_pl(),
						dateObj);
			}
			if (forex == null) {
				forex = mccRateIO.getCurrencyRateByDate(request.getOutputCurrency(), plMappingData.get_pl(), dateObj);
			}
		}

		return forex;
	}

	/**
	 * Calculate MCC Cost Apply MCC Cost Formula
	 * 
	 * @param mccCost
	 * @param opexElements
	 * @return
	 */
	// (LCP * (1/FOREX) * (1/100))*MATRL% (1-DSPC/100).
	private float getMccCost(float mccCost, Map<String, Float> opexElements) {
		try {
			if (opexElements.size() > 3) {

				// calculating the cost in local currency
				double matrl = Double.parseDouble("" + (opexElements.get(MATRL) / 100));
				double discount = Double.parseDouble("" + (opexElements.get(DSCPC) / 100));
				double price = Double.parseDouble("" + opexElements.get(LCP));
				double rate = Double.parseDouble("" + opexElements.get(FOREX));

				// calculating the cost in given currency
				// double cost = matrl * price * (1 - discount);

				// calculating the cost in USD
				double usdcost = matrl * price * (1 - discount);
				if (rate != 0)
					usdcost /= rate;

				if (opexElements.containsKey(OUTPUTFOREX)) {
					double outputrate = Double.parseDouble("" + opexElements.get(OUTPUTFOREX));

					mccCost = (float) (usdcost * outputrate);
				} else {
					mccCost = (float) usdcost;
				}

			} else if (opexElements.size() >= 1) {
				float matrl = opexElements.get(MATRL);
				if (opexElements.containsKey(OUTPUTFOREX)) {
					float outputrate = opexElements.get(OUTPUTFOREX);
					mccCost = matrl * outputrate;
				} else {
					mccCost = matrl;
				}

			}
			return mccCost;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 1.0f;
	}

	/**
	 * Calculate BASE C4 TCOS
	 * 
	 * @param accessedProducts
	 * @param allSkuCostElementsMap
	 * @param productCostMap
	 * @throws Exception
	 */
	public void calculateTCost(Product[] accessedProducts, Map<Product, Map<String, Float>> allSkuCostElementsMap,
			Map<com.hp.c4.rsku.rSku.pojo.Product, Set<C4CostData>> productCostMap) throws Exception {

		mLogger.info("Calculating the C4 Total Cost started processing.......");
		try {
			Map<Product, PlMappingData> plData = null;
			if (accessedProducts.length > 0) {
				plData = Cache.getPlMappingInfo(accessedProducts);

				Collection<PlMappingData> plMapData = plData.values();
				int count = 0;
				List<ProductCost> baseSkuCostDetails = response.getBaseSkuCostDetails();
				// Calculating Tcos formula for base SKU's
				if (plData != null)
					for (PlMappingData plMappingData : plMapData) {
						ProductCost cost = validateCostStatus(allSkuCostElementsMap, request, allMaskElements,
								plMappingData);
						baseSkuCostDetails.add(cost);
						Set<C4CostData> costDataList = productCostMap.get(plMappingData.get_prod());
						Iterator<C4CostData> itr = costDataList.iterator();
						while (itr.hasNext()) {
							C4CostData c4CostData = itr.next();
							baseSkuCostDetails.get(count).setFrequency(String.valueOf(c4CostData.getPeriodType()));
							TPeriodData periodData = periodIdMap.get(c4CostData.getPeriodType());
							if (periodData.getPeriodId() == c4CostData.getPeriodId())
								baseSkuCostDetails.get(count).setPeriod(String.valueOf(periodData.getStartDate()));
							break;
						}
						count++;
					}
				response.setBaseSkuCostDetails(baseSkuCostDetails);
			}

			mLogger.info("Calculating the C4 Total Cost Ends .......");
		} catch (SQLException e) {
			mLogger.error("Exception occured at calculateTCost method....." + e.getMessage());
		}
	}

	/**
	 * Calculate C4RSKU TCOS
	 * 
	 * @param accessedProducts
	 * @param allSkuCostElementsMap
	 * @param sumUpAllSkuElementsCost
	 */
	private void calculateC4RskuTCost(Product[] accessedProducts,
			Map<Product, Map<String, Float>> allSkuCostElementsMap, Map<String, Float> sumUpAllSkuElementsCost) {

		mLogger.info("Calculating the C4 Total Cost started processing.......");
		try {

			Map<Product, PlMappingData> plData = Cache.getPlMappingInfo(accessedProducts);
			List<ProductCost> baseSkuCostDetails = response.getBaseSkuCostDetails();
			// Calculating Tcos formula for base SKU's
			for (PlMappingData plMappingData : plData.values()) {
				baseSkuCostDetails
						.add(validateCostStatus(allSkuCostElementsMap, request, allMaskElements, plMappingData));
			}
			response.setBaseSkuCostDetails(baseSkuCostDetails);

			RSkuProduct rskProduct = request.getRapidSku().getProduct();
			Product[] rSKU = getListOfUniqueProducts(new RSkuProduct[] { rskProduct });

			Map<Product, Map<String, Float>> sumUpAllSkuElementsCostMap = new LinkedHashMap<Product, Map<String, Float>>();
			sumUpAllSkuElementsCostMap.put(rSKU[0], sumUpAllSkuElementsCost);
			PlMappingData plMapping = new PlMappingData(rSKU[0], request.getRapidSku().getPl(), null);

			if (sumUpAllSkuElementsCostMap.size() > 0) {
				Map<String, Float> rapidSkuCostElements = sumUpAllSkuElementsCostMap.get(plMapping.get_prod());
				if (rapidSkuCostElements.containsKey(VTRDX)) {
					Float vtrdx = rapidSkuCostElements.get(VTRDX);
					rapidSkuCostElements.put(DDUTX, vtrdx);
					rapidSkuCostElements.put(DDUTR, vtrdx);
					rapidSkuCostElements.put(VTRTR, vtrdx);
				}

				if (rapidSkuCostElements.containsKey(VTRSE)) {
					Float vtrse = rapidSkuCostElements.get(VTRSE);
					rapidSkuCostElements.put(VTRRA, vtrse);
					rapidSkuCostElements.put(DDUSE, vtrse);
					rapidSkuCostElements.put(DDURA, vtrse);
				}
			}

			// Calculating Tcos formula for Rapid SKU's
			ProductCost rapidSkuCostDetails = validateCostStatus(sumUpAllSkuElementsCostMap, request, allMaskElements,
					plMapping);

			((RSkuResponse) response).setRapidSkuCostDetails(rapidSkuCostDetails);

		} catch (SQLException e) {
			mLogger.error("Exception occured at calculateTCost method....." + e.getMessage());
		}
	}

	/**
	 * Adding Cost status COMPLETE,IN-COMPLETE,WARNING
	 * 
	 * @param allSkuCostElementsMap
	 * @param request
	 * @param allMaskElements
	 * @param plMappingData
	 * @return
	 * @throws SQLException
	 */
	private ProductCost validateCostStatus(Map<Product, Map<String, Float>> allSkuCostElementsMap, RSkuRequest request,
			Map<String, List<MaskElementTypes>> allMaskElements, PlMappingData plMappingData) throws SQLException {
		ProductCost productCost = null;
		try {

			if (!airMotExlcudeList.contains(request.getDeliveryMethod())) {
				request.setMot("AIR");
			}

			String deliveryMethod = request.getDeliveryMethod() + "#" + request.getMot();
			DefaultMotTradingExpense defaultMot = defaultMotMap.get(deliveryMethod.toUpperCase());

			List<MaskElementTypes> elementsList = null;

			if (plMappingData.get_platform() == null) {
				elementsList = allMaskElements.get(plMappingData.get_pl());
			} else {
				String theKey = getMaskKey(plMappingData.get_pl(), plMappingData.get_platform());
				elementsList = allMaskElements.get(theKey);
				if (elementsList == null && allMaskElements.containsKey(plMappingData.get_pl())) {
					elementsList = allMaskElements.get(plMappingData.get_pl());
				}
				if (elementsList == null) {
					elementsList = new ArrayList<MaskElementTypes>();
					Collection<List<MaskElementTypes>> list = defaultCostElementTypes.values();
					mLogger.info("This PL doesn't exist Mask Elements capturing default..." + plMappingData);
					for (List<MaskElementTypes> maskElementTypes : list) {
						elementsList.addAll(maskElementTypes);
					}
				}
			}

			List<MaskElementTypes> requiredElementsList = new ArrayList<MaskElementTypes>();
			List<MaskElementTypes> missingElementsList = new ArrayList<MaskElementTypes>();
			List<MaskElementTypes> warningElementsList = new ArrayList<MaskElementTypes>();

			boolean isDeliveryRequired = false;
			boolean isDeliveryAvlble = false;

			Date dateObj = (new SimpleDateFormat("yyyy/MM/dd")).parse(request.getCostDate());

			if (elementsList != null) {

				Map<String, Float> singleSKuElementsCost = allSkuCostElementsMap.get(plMappingData.get_prod());
				Map<String, Float> elementsMap = new LinkedHashMap<String, Float>();
				String costStatus = "";

				if (singleSKuElementsCost.size() > 0)
					for (MaskElementTypes maskElementTypes : elementsList) {

						if (mandatoryList.contains(maskElementTypes.getElementType())
								|| maskElementTypes.getFlag() == MASK_ELEMENTS_REQUIRED) {
							Float elementCost = 0.0f;
							if (maskElementTypes.getFlag() == MASK_ELEMENTS_REQUIRED) {
								if (singleSKuElementsCost.containsKey(maskElementTypes.getElementType())) {
									requiredElementsList.add(maskElementTypes);
									if (maskElementTypes.getElementType().equalsIgnoreCase(VTRDX))
										isDeliveryRequired = true;
								} else {
									missingElementsList.add(maskElementTypes);
								}
							}
							if (singleSKuElementsCost.get(VTRDX) != null)
								isDeliveryAvlble = true;
							if (singleSKuElementsCost.get(maskElementTypes.getElementType()) != null)
								elementCost = singleSKuElementsCost.get(maskElementTypes.getElementType());
							elementsMap.put(maskElementTypes.getElementType(), elementCost);
						} else if ((defaultMot.getDefaultElementType()
								.equalsIgnoreCase(maskElementTypes.getElementType())
								|| singleSKuElementsCost.containsKey(defaultMot.getDefaultElementType()))
								&& !elementsMap.containsKey(DELIVERY)) {
							Float deliveryCost = singleSKuElementsCost.get(defaultMot.getElementType());
							String newDelivery = defaultMot.getDefaultElementType();
							if (deliveryCost == null) {

								if (isDeliveryRequired || isDeliveryAvlble)
									warningElementsList.add(maskElementTypes);
								// we try to find the next most expensive incoterm populated in the DB
								while (true) {
									newDelivery = mOTTradingExpenseService.getNextIncoTermSequence(newDelivery);
									deliveryCost = singleSKuElementsCost.get(newDelivery);
									if (deliveryCost != null || newDelivery == null)
										break;
								}

							}

							if (deliveryCost == null) {
								deliveryCost = new Float(0.0);
							}
							elementsMap.put(DELIVERY, deliveryCost);
						}
					}
				// warningStatus && !inCompleteStatus && !completeStatus &&
				// isAllMasElementsMandatory
				if (warningElementsList.size() > 0) {
					costStatus = COST_STATUS_WARNING;
				} else if (missingElementsList.size() > 0) {
					costStatus = COST_STATUS_IN_COMPLETE;
				} else {
					costStatus = COST_STATUS_COMPLETE;
				}
//			mLogger.info("elementsMap" + elementsMap);

				float forex = getCurrencyRate(dateObj, plMappingData);
				if (forex != 0) {
					elementsMap.put(OUTPUTFOREX, forex);
					singleSKuElementsCost.put(OUTPUTFOREX, forex);
				}
				PLMaskElements maskElements = new PLMaskElements(plMappingData.get_pl(), plMappingData.get_platform(),
						elementsList);

				Float tCOS = 0.0f;
				if (singleSKuElementsCost.size() > 1) {
					tCOS = applyTcosFormula(elementsMap);
				}

				if (singleSKuElementsCost.size() == 1 && tCOS == 0.0) {
					costStatus = COST_STATUS_NOT_FOUND;
					singleSKuElementsCost.clear();
				} else if (singleSKuElementsCost.size() > 1 && !singleSKuElementsCost.containsKey(OUTPUTFOREX)) {
					costStatus = COST_STATUS_IN_COMPLETE;
				}

				productCost = new ProductCost(plMappingData.get_prod().getProdId().toUpperCase(), singleSKuElementsCost,
						costStatus, Float.parseFloat(decimalFormat.format(tCOS)), maskElements);

				// mLogger.info("plMappingData" + plMappingData);
				// mLogger.info("elementsList" + elementsList);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		mLogger.info("C4 Product Cost is Avaliable for the requested SKU's .......");
		return productCost;

	}

	private static String getCurrencyKey(String pPl, String pCurrencyCode) {
		StringBuffer theBuffer = new StringBuffer(pPl);
		theBuffer.append("-");
		theBuffer.append(pCurrencyCode);
		return theBuffer.toString();
	}

	/**
	 * Gets all the required fields for the formula and calculate TCOS
	 */
	private float applyTcosFormula(Map<String, Float> elementsMap) {
		float tcos = 0.0f;
		try {
			double matrl = Double.parseDouble("" + elementsMap.get(MATRL));
			double vwrty = Double.parseDouble("" + elementsMap.get(VWRTY));
			// double deliveryTX =
			// Double.parseDouble(""+((Element)elementsMap.get(delivery)).value);
			if (elementsMap.get(DELIVERY) == null) {
				elementsMap.put(DELIVERY, 0.0f);
			}
			double deliveryTX = Double.parseDouble("" + elementsMap.get(DELIVERY));
			double vrlty = Double.parseDouble("" + elementsMap.get(VRLTY));
			double ovblc = Double.parseDouble("" + elementsMap.get(OVBLC));
			double malad = Double.parseDouble("" + elementsMap.get(MALAD)) / 100;
			double ofxdc = Double.parseDouble("" + elementsMap.get(OFXDC));
			if (elementsMap.containsKey(OUTPUTFOREX)) {
				double outputforex = Double.parseDouble("" + elementsMap.get(OUTPUTFOREX));
				tcos = Float.parseFloat(
						"" + ((matrl * (1 + malad) + vwrty + deliveryTX + vrlty + ovblc + ofxdc) * outputforex));
			} else {
				tcos = Float.parseFloat("" + ((matrl * (1 + malad) + vwrty + deliveryTX + vrlty + ovblc + ofxdc)));
			}
			// mLogger.info("Tcos Calculation=" + elementsMap + ":" + tcos);
			return tcos;
		} catch (Exception e) {
			mLogger.info("Exception occured at Calculate TCOS Formula......" + e);
		}
		return tcos;
	}

	public static String getMaskKey(String pPl, String pPlatform) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(pPl);
		if (pPlatform != null && !pPlatform.equalsIgnoreCase(ARROW_NULL_CHECK)) {
			buffer.append(PIPE_SYMBOL);
			buffer.append(pPlatform);
		}

		return buffer.toString();
	}

	/*
	 * Remove Duplicate SKU's & convert the Request List of SKU's to C4 SKU's which
	 * contains MCC & SPN
	 */
	private com.hp.c4.rsku.rSku.pojo.Product[] getListOfUniqueProducts(Object[] listOfProducts) {
		com.hp.c4.rsku.rSku.pojo.Product[] prodList = new com.hp.c4.rsku.rSku.pojo.Product[listOfProducts.length];
		mLogger.info("Actual SKU's received from Rsku Request:" + listOfProducts.length);
		Set<Product> uniqueSkusFromRskuRequest = new LinkedHashSet<Product>();
		if (listOfProducts.length > 0) {

			int i = 0;
			if (listOfProducts[i] instanceof C4BaseProduct) {
				C4BaseProduct[] listBaseProducts = (C4BaseProduct[]) listOfProducts;
				for (C4BaseProduct product : listBaseProducts) {
					prodList[i] = new com.hp.c4.rsku.rSku.pojo.Product();
					splitProducts(prodList[i], product.getProdIdBase().trim().toUpperCase());
					uniqueSkusFromRskuRequest.add(prodList[i]);
					i++;
				}
			} else if (listOfProducts[i] instanceof RSkuProduct) {
				RSkuProduct[] liRSkuProducts = (RSkuProduct[]) listOfProducts;
				for (RSkuProduct product : liRSkuProducts) {
					prodList[i] = new com.hp.c4.rsku.rSku.pojo.Product();
					splitProducts(prodList[i], product.getrSkuProd().trim().toUpperCase());
					prodList[i].setRSku(true);
					prodList[i].setProdDesc(product.getrSkuDec());
					uniqueSkusFromRskuRequest.add(prodList[i]);
					i++;
				}
			}
		}

		int size = uniqueSkusFromRskuRequest.size();
		com.hp.c4.rsku.rSku.pojo.Product[] uniqueProducts = new com.hp.c4.rsku.rSku.pojo.Product[size];
		System.arraycopy(uniqueSkusFromRskuRequest.toArray(), 0, uniqueProducts, 0, size);
		mLogger.info("Uniques SKU's from RSKU request:" + size);
		return uniqueProducts;
	}

	private void splitProducts(com.hp.c4.rsku.rSku.pojo.Product prodList, String product) {

		String[] splitText = product.split(Pattern.quote(PIPE_SYMBOL), 4);

		prodList.setProdId(splitText[0]);
		if (splitText.length > 1) {
			if (splitText[1] != null && !splitText[1].equalsIgnoreCase(""))
				prodList.setOpt(splitText[1]);
			else
				prodList.setOpt(null);

			if (splitText[2] != null && !splitText[2].equalsIgnoreCase(""))
				prodList.setSpn(splitText[2]);
			else
				prodList.setSpn(null);

			if (splitText[3] != null && !splitText[3].equalsIgnoreCase("")) {
				prodList.setMcc(splitText[3]);
				prodList.setRequestedMcc(true);
			} else
				prodList.setMcc(null);
		}
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

	private String[] getPLMappingData(com.hp.c4.rsku.rSku.pojo.Product[] prodList) {
		mLogger.info("PL Mapping for the Requested Base SKU's");
		try {
			String[] thePLs = new String[prodList.length];
			Map<Product, PlMappingData> data = Cache.getPlMappingInfo(prodList);

			int count = 0;
			Collection<PlMappingData> plMapData = data.values();
			for (PlMappingData plMapping : plMapData) {
				thePLs[count] = plMapping.get_pl();
				count++;
			}
			return thePLs;
		} catch (SQLException e) {
			mLogger.error("Exception occured at ...." + e.getMessage());
		}
		return null;
	}

	/**
	 * Filtering all the Requested SKU's are belongs Valid PL's or Not. Accessdenied
	 * Info will get
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

}
