package com.hp.c4.rsku.rSku.rest.services;

import java.sql.SQLException;
import java.util.Map;
import java.util.SortedSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CmccRateIO;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CmccRateIO.RateRange;

// Callable<Map<String, SortedSet<RateRange>>> 
public class CurrencyRatesDaemon implements Runnable {

	private static final Logger mLogger = LogManager.getLogger(C4CostProcessingService.class);
	
	private static Map<String, SortedSet<RateRange>> _theExchangeRates;
	private CmccRateIO mccRateIO;

	public CurrencyRatesDaemon() {
		mccRateIO = new CmccRateIO();
	}

	@Override
	public void run() {
		try {
			long start = System.currentTimeMillis();
			// _theExchangeRates = dameon.getAllRates();
			_theExchangeRates = mccRateIO.getAllRates();

			long end = System.currentTimeMillis();
			mLogger.info("All Currency Rates initialized completes : " + (end - start) / 1000);
		} catch (C4Exception | SQLException e) {
			e.printStackTrace();
		}
	}

	public Map<String, SortedSet<RateRange>> getAllRates() {
		return _theExchangeRates;
	}

}
