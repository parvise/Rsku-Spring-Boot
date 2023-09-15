package com.hp.c4.rsku.rSku.bean.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductWithMcc {

	private String sku;
	private Map<String, Float> mccCostElements = new LinkedHashMap<String, Float>();

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public Map<String, Float> getMccCostElements() {
		return mccCostElements;
	}

	public void setMccCostElements(Map<String, Float> mccCostElements) {
		this.mccCostElements = mccCostElements;
	}

	@Override
	public String toString() {
		return "ProductWithMcc [sku=" + sku + ", mccCostElements=" + mccCostElements + "]";
	}

}
