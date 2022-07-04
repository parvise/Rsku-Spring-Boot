package com.hp.c4.rsku.rSku.rest.repo;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;
import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.dbio.persistent.mapping.io.CMaskUtilIO;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Repository
public class PLMaskElementsRepo {

	private static Map<String, List<MaskElementTypes>> plMaskElements;

	private static Map<String, List<MaskElementTypes>> defaultPLMaskElementTypes;

	public Map<String, List<MaskElementTypes>> getAllPLMasks() throws C4SecurityException {
		if (plMaskElements == null) {
			plMaskElements = Cache.getAllPLMasks();
		}
		return plMaskElements;
	}

	public Map<String, List<MaskElementTypes>> getAllDefaultMasElementTypes() throws C4Exception {
		if (defaultPLMaskElementTypes == null) {

			CMaskUtilIO io = new CMaskUtilIO();

			defaultPLMaskElementTypes = io.selectCosElementTypes();
			defaultPLMaskElementTypes.putAll(io.selectOpExpElementTypes());
		}
		return defaultPLMaskElementTypes;
	}
}
