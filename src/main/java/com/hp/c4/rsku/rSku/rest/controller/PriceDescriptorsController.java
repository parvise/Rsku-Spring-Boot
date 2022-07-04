package com.hp.c4.rsku.rSku.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hp.c4.rsku.rSku.rest.services.PriceDescriptorService;

@RestController
public class PriceDescriptorsController {

	@Autowired
	private PriceDescriptorService priceDescriptorService;

	@RequestMapping(value = "/allPriceDescriptors", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllPricetermMapping() {

		Object obj = priceDescriptorService.getAllPricetermMapping();

		return new ResponseEntity<Object>(obj, HttpStatus.OK);

	}

	@RequestMapping(value = "/getAllDefaultCntryPriceDescriptors", method = RequestMethod.GET)
	public ResponseEntity<Object> getAllDefaultCntryPriceDescriptors() {

		Object obj = priceDescriptorService.getAllDefaultCntryPriceDescriptors();

		return new ResponseEntity<Object>(obj, HttpStatus.OK);
	}
}
