package com.hp.c4.rsku.rSku.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hp.c4.rsku.rSku.rest.services.PLMaskElementsService;

@RestController
public class PLMaskElementsController {

	@Autowired
	private PLMaskElementsService pLMaskElementsService;

	@RequestMapping(value = "/getAllPLMasks", produces = "application/json", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllPLMasks() {

		Object obj = pLMaskElementsService.getAllPLMasks();

		return new ResponseEntity<Object>(obj, HttpStatus.OK);

	}
}
