package com.hp.c4.rsku.rSku.rest.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

	public String getNextIncoTermSequence(String delivery) throws C4SecurityException {

		try {
			List<String> _theIncotermSequence = mOTTradingExpenseRepo.loadAllSequenceIncoTermList();
			boolean found = false;
			for (Iterator<String> i = _theIncotermSequence.iterator(); i.hasNext();) {
				Object value = i.next();
				if (found) {
					return (String) value;
				}
				if (value.equals(delivery)) {
					found = true;
				}
			}
			return null;
		} catch (C4SecurityException e) {
			return "C4 Exception Please contact to C4 Admin";
		}

	}
}
