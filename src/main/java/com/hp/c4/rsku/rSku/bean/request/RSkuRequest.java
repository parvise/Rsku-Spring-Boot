package com.hp.c4.rsku.rSku.bean.request;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hp.c4.rsku.rSku.pojo.DefaultPriceDescriptor;

@JsonIgnoreProperties(value = { "defaultPriceDescriptor", "c4CostDates" })
public class RSkuRequest {

	private C4BaseProduct[] listOfProducts;

	private String costDate;
	private String countryCode;

	private String deliveryMethod; // By Default Delivery Duty PAID DDP we consider in C4

	private String mot; // MOT By default Air we consider in C4

	private String outputCurrency; // By default USD has Output Currency In C4

	private PlMapping rapidSku;

	private DefaultPriceDescriptor defaultPriceDescriptor;
	private String c4CostDates[];

	private Connect connect;

	public C4BaseProduct[] getListOfProducts() {
		return listOfProducts;
	}

	public void setListOfProducts(C4BaseProduct[] listOfProducts) {
		this.listOfProducts = listOfProducts;
	}

	public String getCostDate() {
		if (costDate == null)
			return null;
		return costDate.trim();
	}

	public void setCostDate(String costDate) {
		this.costDate = costDate;
	}

	public String getCountryCode() {
		if (countryCode == null)
			return null;
		return countryCode.trim();
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	public String getOutputCurrency() {
		return outputCurrency;
	}

	public void setOutputCurrency(String outputCurrency) {
		this.outputCurrency = outputCurrency;
	}

	public PlMapping getRapidSku() {
		return rapidSku;
	}

	public void setRapidSku(PlMapping rapidSku) {
		this.rapidSku = rapidSku;
	}

	public DefaultPriceDescriptor getDefaultPriceDescriptor() {
		return defaultPriceDescriptor;
	}

	public void setDefaultPriceDescriptor(DefaultPriceDescriptor defaultPriceDescriptor) {
		this.defaultPriceDescriptor = defaultPriceDescriptor;
	}

	public String[] getC4CostDates() {
		return c4CostDates;
	}

	public void setC4CostDates(String[] c4CostDates) {
		this.c4CostDates = c4CostDates;
	}

	public Connect getConnect() {
		return connect;
	}

	public void setConnect(Connect connect) {
		this.connect = connect;
	}

	@Override
	public String toString() {
		return "RSkuRequest [listOfProducts=" + Arrays.toString(listOfProducts) + ", costDate=" + costDate
				+ ", countryCode=" + countryCode + ", deliveryMethod=" + deliveryMethod + ", mot=" + mot
				+ ", outputCurrency=" + outputCurrency + "]";
	}

}
