package com.hp.c4.rsku.rSku.rest.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hp.c4.rsku.rSku.bean.request.RSkuRequest;
import com.hp.c4.rsku.rSku.pricing.C4PricingAnalyticsService;
import com.hp.c4.rsku.rSku.rest.services.C4CostProcessingService;

@RestController
public class RSkuRequestController {

	private static final Logger mLogger = LogManager.getLogger(RSkuRequestController.class);

	@Autowired
	private C4CostProcessingService validateC4RskuRequestService;

	@Autowired
	private C4PricingAnalyticsService c4PricingAnalyticsService;

	@RequestMapping(value = "/getC4RskuCost", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<Object> getC4RskuCost(@RequestBody RSkuRequest request) throws Exception {

		long start = System.currentTimeMillis();
		mLogger.info("Accessing C4 RSKU Request service........ ");

		Object obj = validateC4RskuRequestService.validateRskuRequest(request, true);
		long end = System.currentTimeMillis();
		mLogger.info("################################ SERVICE ENDS ################ " + (end - start) / 1000);

		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getC4ExplicitMccsCost", produces = "application/json", method = RequestMethod.POST)
	public ResponseEntity<Object> getC4ExplicitMccsCost(@RequestBody RSkuRequest request) throws Exception {

		long start = System.currentTimeMillis();
		mLogger.info("Accessing C4 RSKU Request service........ ");

		Object obj = validateC4RskuRequestService.validateRskuRequest(request, false);
		long end = System.currentTimeMillis();
		mLogger.info("################################ SERVICE ENDS ################ " + (end - start) / 1000);

		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}

	@RequestMapping(value = "/getPricingAnalytics", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Object> getPricingAnalytics() {

		mLogger.info("Accessing Get Pricing Analytics service........ ");

		c4PricingAnalyticsService.getAllC4ProductsMonthly();

		return new ResponseEntity<Object>("SUccess", HttpStatus.OK);
	}
	
	@GetMapping("/welcome")
	public String welcome() {
		return "Welcome to Rsku Boot";
	}
}
