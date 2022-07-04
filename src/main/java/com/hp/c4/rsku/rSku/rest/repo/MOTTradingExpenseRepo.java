package com.hp.c4.rsku.rSku.rest.repo;

import java.util.Map;

import org.springframework.stereotype.Repository;

import com.hp.c4.rsku.rSku.dbio.persistent.cache.Cache;
import com.hp.c4.rsku.rSku.pojo.DefaultMotTradingExpense;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Repository
public class MOTTradingExpenseRepo {

	private static Map<String, DefaultMotTradingExpense> defaultMotMap;
	private static Map<String, String> loadAllMotMap;

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
}
