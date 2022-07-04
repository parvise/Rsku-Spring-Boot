package com.hp.c4.rsku.rSku.rest.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;
import com.hp.c4.rsku.rSku.rest.repo.PriceDescriptorsRepo;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Service
public class PriceDescriptorService {

	@Autowired
	private PriceDescriptorsRepo priceDescriptorsRepo;

	public Map<String, String> getAllPricetermMapping() {
		try {
			return priceDescriptorsRepo.getAllPricetermMapping();
		} catch (C4SecurityException e) {
			Map<String, String> allPriceDescptrMap = new HashMap<String, String>();
			allPriceDescptrMap.put("C4 Exception", "Please contact to C4 Admin");
			return allPriceDescptrMap;
		}

	}

	public Map<String, DefaultPriceDescriptor> getAllDefaultCntryPriceDescriptors() {

		try {
			return priceDescriptorsRepo.getAllDefaultCntryPriceDescriptors();
		} catch (C4SecurityException e) {
			Map<String, DefaultPriceDescriptor> allPriceDescptrMap = new HashMap<String, DefaultPriceDescriptor>();
			allPriceDescptrMap.put("C4 Exception Please contact to C4 Admin", null);
			return allPriceDescptrMap;
		}
	}
}
