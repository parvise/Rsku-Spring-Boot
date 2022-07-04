package com.hp.c4.rsku.rSku.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hp.c4.rsku.rSku.rest.services.MOTTradingExpenseService;

@RestController
public class MOTTradingExpenseController {

	@Autowired
	private MOTTradingExpenseService mOTTradingExpenseService;

	@RequestMapping(value = "/defaultMotTradingExp", method = RequestMethod.GET)
	public ResponseEntity<Object> getDefaultMotExpense() {

		Object obj = mOTTradingExpenseService.getDefaultMotExpense();

		return new ResponseEntity<Object>(obj, HttpStatus.OK);

	}
}
