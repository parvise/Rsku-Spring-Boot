package com.hp.c4.rsku.rSku.rest.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.c4.rsku.rSku.pojo.DefaultMotTradingExpense;
import com.hp.c4.rsku.rSku.rest.repo.MOTTradingExpenseRepo;
import com.hp.c4.rsku.rSku.security.server.util.C4SecurityException;

@Service
public class MOTTradingExpenseService {

	@Autowired
	private MOTTradingExpenseRepo mOTTradingExpenseRepo;

	public Map<String, DefaultMotTradingExpense> getDefaultMotExpense() {

		try {
			return mOTTradingExpenseRepo.getDefaultMotExpense();
		} catch (C4SecurityException e) {
			Map<String, DefaultMotTradingExpense> exception = new HashMap<String, DefaultMotTradingExpense>();
			exception.put("C4 Exception Please contact to C4 Admin", null);
			return exception;
		}

	}

	public Map<String, String> loadAllMOTs() throws C4SecurityException {

		return mOTTradingExpenseRepo.loadAllMots();

	}
}
