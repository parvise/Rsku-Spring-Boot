package com.hp.c4.rsku.rSku.security.server.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.bean.request.RSkuRequest;
import com.hp.c4.rsku.rSku.c4.util.Aperiod;
import com.hp.c4.rsku.rSku.c4.util.CperiodDatesWithQueryDateImpl;
import com.hp.c4.rsku.rSku.constants.C4RskuLabelConstants;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.rest.repo.C4MasterTablesInitRepo;
import com.hp.c4.rsku.rSku.rest.services.MOTTradingExpenseService;
import com.hp.c4.rsku.rSku.rest.services.PriceDescriptorService;
import com.hp.c4.rsku.rSku.security.server.util.constatnts.DeliveryMethodConstants;

@Service
public class ValidateC4RskuRequest extends C4RskuLabelConstants {

	private static final Logger mLogger = LogManager.getLogger(ValidateC4RskuRequest.class);

	@Autowired
	private MOTTradingExpenseService motTradingExpenseService;

	@Autowired
	private C4MasterTablesInitRepo c4MasterTablesInitRepo;

	// @Value("${c4.service.account.userName}")
	private String serviceAccountUserName;

	// @Value("${c4.service.account.password}")
	private String serviceAccountPassword;

	@Value("${c4.service.aacount.appName}")
	private String appName;

	private Map<String, DefaultPriceDescriptor> priceDescriptorMap;
	@Autowired
	private PriceDescriptorService priceDescriptorService;

	public Map<String, String> validateRskuRequestFields(RSkuRequest request) throws C4SecurityException {

		Map<String, String> errorMap = new LinkedHashMap<String, String>();
		if (request.getListOfProducts() == null || request.getListOfProducts().length == 0) {
			errorMap.put(BASE_PRODUCTS_KEY, BASE_PRODUCTS_VALUE);
		}

		if (request.getDeliveryMethod() == null || request.getDeliveryMethod().trim().length() == 0) {
			request.setDeliveryMethod("DDP");
		} else {
			if (!checkValidDliveryCode(request.getDeliveryMethod().trim())) {
				errorMap.put(DELIVERY_METHOD_KEY, request.getDeliveryMethod() + DELIVERY_METHOD_VALUE);
			}
		}

		if (request.getMot() == null || request.getMot().trim().length() == 0) {
			request.setMot("AIR");
		} else {
			if (!checkValidMot(request.getMot().trim().toUpperCase())) {
				errorMap.put(MOT_KEY, request.getMot() + MOT_VALUE);
			}
		}

		request.setOutputCurrency("USD");

		String geoCode = request.getCountryCode();
		if (geoCode == null || geoCode.trim() == "") {
			errorMap.put(COUNTRY_KEY, COUNTRY_VALUE);
		} else {
			DefaultPriceDescriptor pdesc = null;
			priceDescriptorMap = priceDescriptorService.getAllDefaultCntryPriceDescriptors();

			for (String country : priceDescriptorMap.keySet()) {
				if (country.equalsIgnoreCase(geoCode.trim())) {
					pdesc = priceDescriptorMap.get(country);
					break;
				} else {
					pdesc = priceDescriptorMap.get(country);
					if (pdesc != null && pdesc.getTwoLetterCntry().equalsIgnoreCase(geoCode.trim())) {
						break;
					}
					pdesc = null;
				}
			}

			if (pdesc == null) {
				errorMap.put(COUNTRY_KEY, INVALID_COUNTRY_VALUE + geoCode);
			} else {
				request.setDefaultPriceDescriptor(pdesc);
			}
		}

		if (request.getCostDate() == null || request.getCostDate().trim() == "") {
			errorMap.put(COST_DATE_KEY, COST_DATE_VALUE);
		} else {
			boolean isException = false;
			try {
				new SimpleDateFormat("yyyy/MM/dd").parse(request.getCostDate().trim());
				if (request.getCostDate().trim().length() != 10) {
					isException = true;
					errorMap.put(COST_DATE_KEY, COST_DATE_FORMAT_VALUE);
				}
			} catch (ParseException e) {
				isException = true;
				errorMap.put(COST_DATE_KEY, COST_DATE_FORMAT_VALUE);
			}
			if (!isException) {
				String[] dateList = new String[] { request.getCostDate().trim() };
				String[] c4CostDates = consolidateC4TPerioddates(dateList);
				if (c4CostDates.length == 0) {
					errorMap.put(COST_DATE_KEY, COST_DATE_FORMAT_VALUE);
				} else {
					request.setC4CostDates(c4CostDates);
				}
			}
		}

		if (request.getRapidSku() == null
				|| (request.getRapidSku() != null
						&& (request.getRapidSku().getPl() == null || request.getRapidSku().getPl().trim() == ""))
				|| (request.getRapidSku().getProduct() != null
						&& (request.getRapidSku().getProduct().getrSkuProd() == null
								|| request.getRapidSku().getProduct().getrSkuProd().trim() == ""))
				|| request.getRapidSku().getProduct() != null
						&& (request.getRapidSku().getProduct().getrSkuDec() == null
								|| request.getRapidSku().getProduct().getrSkuDec().trim() == "")) {
			errorMap.put(RAPID_SKU_KEY, RAPID_SKU_VALUE);
		} else if (request.getRapidSku() != null && request.getRapidSku().getPl() != null) {
			String pl = request.getRapidSku().getPl().trim();
			Map<String, String> plToCompanyMap = c4MasterTablesInitRepo.loadAllPLsToCompanyMap();
			if (plToCompanyMap != null) {
				String tenant = plToCompanyMap.get(pl);
				if (tenant == null) {
					errorMap.put(PL_KEY + pl, PL_INVALID_VALUE);
				} else if (!tenant.equalsIgnoreCase("HPI")) {
					errorMap.put(PL_KEY + pl, PL_INVALID_HPI_VALUE);
				}
			}

		}

		if (request.getConnect() == null
				|| (request.getConnect() != null && (request.getConnect().getUserName().trim() == ""
						|| request.getConnect().getPassword().trim() == ""))) {
			errorMap.put(CONNECT_KEY, CONNECT_VALUE);
		} else {

			serviceAccountUserName = request.getConnect().getUserName().trim();
			serviceAccountPassword = request.getConnect().getPassword().trim();
			// String encoded = new
			// BASE64Encoder().encode(serviceAccountPassword.getBytes());

			mLogger.info("Performing C4 RSKU request Service account Validations initilizing......."
					+ serviceAccountUserName + ":" + PRODUCT_NOT_LOADED);
			String sessionId = null;
			try {
				sessionId = Cache.validateUser(serviceAccountUserName, serviceAccountPassword, appName);
			} catch (C4SecurityException e) {
				mLogger.error("Exception occured at validateUser..." + e.getMessage());
				errorMap.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
			}
			if (sessionId != null && sessionId.equalsIgnoreCase("INVALID")) {
				errorMap.put(AUTH_FAILS_KEY, serviceAccountUserName);
			} else if (sessionId != null && sessionId.equalsIgnoreCase(appName)) {
				mLogger.info(serviceAccountUserName + " is not Associated with" + appName);
				errorMap.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
			}
		}

		return errorMap;

	}

	// DDP,DAP etc
	private boolean checkValidDliveryCode(String incoTermCode) throws C4SecurityException {
		Map<String, String> deliveryMap = c4MasterTablesInitRepo.loadAllDeliveryMethods();

		for (DeliveryMethodConstants deliverMethod : DeliveryMethodConstants.values()) {
			if (deliveryMap.containsKey(deliverMethod.getTradingExpCode())
					&& incoTermCode.equalsIgnoreCase(deliverMethod.getIncoTermCode())) {
				return true;

			}
		}

		return false;

	}

	private boolean checkValidMot(String mot) throws C4SecurityException {
		Map<String, String> motMap = motTradingExpenseService.loadAllMOTs();

		if (motMap.containsKey(mot)) {
			return true;

		}

		return false;

	}

	/**
	 * Convert the Cost Date to C4 dates(W,M,Q)
	 * 
	 * @param dateList
	 * @return
	 */
	private static String[] consolidateC4TPerioddates(String[] dateList) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		// yyyy/MM/dd

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");

		Calendar c = Calendar.getInstance();
		String[] c4CostDates = null;

		mLogger.info("Consolidate the Cost Dates into Weekly,Monthly & Quarterly....");
		CperiodDatesWithQueryDateImpl[] impl = Aperiod.getPeriodStartDates(dateList);
		for (CperiodDatesWithQueryDateImpl cperiodDatesWithQueryDateImpl : impl) {
			mLogger.info("Test:" + cperiodDatesWithQueryDateImpl.queryDate + ":"
					+ Arrays.toString(cperiodDatesWithQueryDateImpl.startOnDates));
			c4CostDates = cperiodDatesWithQueryDateImpl.startOnDates;

			try {
				c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[0]));

				// System.out.println(sdf.format(c.getTime()));
				c4CostDates[0] = sdf.format(c.getTime());

				c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[1]));

				// System.out.println(sdf.format(c.getTime()));
				c4CostDates[1] = sdf.format(c.getTime());

				c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[2]));

				// System.out.println(sdf.format(c.getTime()));
				c4CostDates[2] = sdf.format(c.getTime());
			} catch (ParseException e) {
				mLogger.error("Exception occured at Parsing C4 dates" + e.getMessage());
			}

		}

		mLogger.info("C4 Dates are avialble...." + Arrays.toString(c4CostDates));
		return c4CostDates;
	}

}
