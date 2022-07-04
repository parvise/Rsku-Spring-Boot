package com.hp.c4.rsku.rSku.rest.repo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.pojo.DefaultMotTradingExpense;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Repository
public class C4MasterTablesInitRepo {

	private static Map<String, DefaultMotTradingExpense> defaultMotMap;
	private static Map<String, String> loadAllMotMap;
	private static Map<String, String> loadAllPLsCompnayMap;
	private static Map<String, String> loadAllDeliverMethodsMap;

	private static Map<String, String> theTmpPricetermMapping = null;

	private static Map<String, DefaultPriceDescriptor> defaultPriceDescMap = null;

	private static Map<String, List<MaskElementTypes>> plMaskElements;

	public Map<String, List<MaskElementTypes>> getAllPLMasks() throws C4SecurityException {
		if (plMaskElements == null) {
			plMaskElements = Cache.getAllPLMasks();
		}
		return plMaskElements;
	}

	public Map<String, String> getAllPricetermMapping() throws C4SecurityException {

		if (theTmpPricetermMapping == null) {
			theTmpPricetermMapping = Cache.getAllPricetermMapping();
		}
		return theTmpPricetermMapping;
	}

	public Map<String, DefaultPriceDescriptor> getAllDefaultCntryPriceDescriptors() throws C4SecurityException {

		if (defaultPriceDescMap == null) {
			defaultPriceDescMap = Cache.getAllDefaultCntryPriceDescriptors();
		}
		return defaultPriceDescMap;
	}

	public Map<String, DefaultMotTradingExpense> getDefaultMotExpense() throws C4SecurityException {
		if (defaultMotMap == null) {
			defaultMotMap = Cache.getDefaultMotExpense();
		}
		return defaultMotMap;
	}

	public Map<String, String> loadAllMots() throws C4SecurityException {
		if (loadAllMotMap == null) {
			loadAllMotMap = Cache.loadAllMots();
		}

		return loadAllMotMap;
	}

	public Map<String, String> loadAllPLsToCompanyMap() throws C4SecurityException {
		if (loadAllPLsCompnayMap == null) {
			loadAllPLsCompnayMap = Cache.getPLToCompanyMap();
		}

		return loadAllPLsCompnayMap;
	}

	public Map<String, String> loadAllDeliveryMethods() throws C4SecurityException {
		if (loadAllDeliverMethodsMap == null) {
			loadAllDeliverMethodsMap = Cache.loadAllDeliveryMethods();
		}

		return loadAllDeliverMethodsMap;
	}

}
