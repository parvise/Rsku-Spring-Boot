package com.hp.c4.rsku.rSku.bean.response;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.hp.c4.rsku.rSku.pojo.Product;

public class RSkuResponse {

	private String costDate;
	private String countryCode;
	private String countryDesc;
	private String deliveryMethod;
	private String fileName;
	private String mot;
	private String outputCurrency;

	private ProductCost[] baseSkuCostDetails;
	private ProductCost rapidSkuCostDetails;
	private Map<Product, String> accessDeniedProducts = new LinkedHashMap<Product, String>();

	public String getCostDate() {
		return costDate;
	}

	public void setCostDate(String costDate) {
		this.costDate = costDate;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCountryDesc() {
		return countryDesc;
	}

	public void setCountryDesc(String countryDesc) {
		this.countryDesc = countryDesc;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}
	
	

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
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

	public ProductCost[] getBaseSkuCostDetails() {
		return baseSkuCostDetails;
	}

	public void setBaseSkuCostDetails(ProductCost[] baseSkuCostDetails) {
		this.baseSkuCostDetails = baseSkuCostDetails;
	}

	public ProductCost getRapidSkuCostDetails() {
		return rapidSkuCostDetails;
	}

	public void setRapidSkuCostDetails(ProductCost rapidSkuCostDetails) {
		this.rapidSkuCostDetails = rapidSkuCostDetails;
	}

	public Map<Product, String> getAccessDeniedProducts() {
		return accessDeniedProducts;
	}

	public void setAccessDeniedProducts(Map<Product, String> accessDeniedProducts) {
		this.accessDeniedProducts = accessDeniedProducts;
	}

	@Override
	public String toString() {
		return "RSkuResponse [costDate=" + costDate + ", countryCode=" + countryCode + ", countryDesc=" + countryDesc
				+ ", deliveryMethod=" + deliveryMethod + ", mot=" + mot + ", outputCurrency=" + outputCurrency
				+ ", baseSkuCostDetails=" + Arrays.toString(baseSkuCostDetails) + ", rapidSkuCostDetails="
				+ rapidSkuCostDetails + ", accessDeniedProducts=" + accessDeniedProducts + "]";
	}

}
