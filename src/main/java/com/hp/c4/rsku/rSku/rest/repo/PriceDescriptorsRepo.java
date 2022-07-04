package com.hp.c4.rsku.rSku.rest.repo;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Repository
public class PriceDescriptorsRepo {

	private static Map<String, String> theTmpPricetermMapping = null;

	private static Map<String, DefaultPriceDescriptor> defaultPriceDescMap = null;

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
}
