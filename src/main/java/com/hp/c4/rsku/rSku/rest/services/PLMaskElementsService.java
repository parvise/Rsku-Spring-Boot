package com.hp.c4.rsku.rSku.rest.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;
import com.hp.c4.rsku.rSku.rest.repo.PLMaskElementsRepo;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Service
public class PLMaskElementsService {

	@Autowired
	private PLMaskElementsRepo pLMaskElementsRepo;

	public Map<String, List<MaskElementTypes>> getAllPLMasks() {

		try {
			return pLMaskElementsRepo.getAllPLMasks();
		} catch (C4SecurityException e) {
			Map<String, List<MaskElementTypes>> exception = new HashMap<String, List<MaskElementTypes>>();
			exception.put("C4 Exception Please contact to C4 Admin", null);
			return exception;
		}
	}

	public Map<String, List<MaskElementTypes>> getAllDefaultMasElementTypes() {

		try {
			return pLMaskElementsRepo.getAllDefaultMasElementTypes();
		} catch (C4Exception e) {
			Map<String, List<MaskElementTypes>> exception = new HashMap<String, List<MaskElementTypes>>();
			exception.put("C4 Exception Please contact to C4 Admin", null);
			return exception;
		}
	}
}
