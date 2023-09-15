package com.hp.c4.rsku.rSku.bean.response;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.hp.c4.rsku.rSku.pojo.Product;

public class Response {
	private String costDate;
	private String countryCode;
	private String countryDesc;
	private String deliveryMethod;

	private String mot;
	private String outputCurrency;

	private List<ProductCost> baseSkuCostDetails = new ArrayList<ProductCost>();
	private Map<Product, String> accessDeniedProducts = new LinkedHashMap<Product, String>();

//	public Response(String costDate, String countryCode, String countryDesc, String deliveryMethod, String mot,
//			String outputCurrency, ProductCost[] baseSkuCostDetails, ProductCost rapidSkuCostDetails,
//			Map<Product, String> accessDeniedProducts) {
//		super();
//		this.costDate = costDate;
//		this.countryCode = countryCode;
//		this.countryDesc = countryDesc;
//		this.deliveryMethod = deliveryMethod;
//		this.mot = mot;
//		this.outputCurrency = outputCurrency;
//		this.baseSkuCostDetails = baseSkuCostDetails;
//		this.rapidSkuCostDetails = rapidSkuCostDetails;
//		this.accessDeniedProducts = accessDeniedProducts;
//	}

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

	public List<ProductCost> getBaseSkuCostDetails() {
		return baseSkuCostDetails;
	}

	public void setBaseSkuCostDetails(List<ProductCost> baseSkuCostDetails) {
		this.baseSkuCostDetails = baseSkuCostDetails;
	}

	public Map<Product, String> getAccessDeniedProducts() {
		return accessDeniedProducts;
	}

	public void setAccessDeniedProducts(Map<Product, String> accessDeniedProducts) {
		this.accessDeniedProducts = accessDeniedProducts;
	}

	@Override
	public String toString() {
		return "Response [costDate=" + costDate + ", countryCode=" + countryCode + ", countryDesc=" + countryDesc
				+ ", deliveryMethod=" + deliveryMethod + ", mot=" + mot + ", outputCurrency=" + outputCurrency
				+ ", baseSkuCostDetails=" + (baseSkuCostDetails) + ", accessDeniedProducts=" + accessDeniedProducts
				+ "]";
	}

}
