package com.hp.c4.rsku.rSku.bean.response;

import java.util.LinkedHashMap;
import java.util.Map;

public class ProductCost {

	private String prodIdBase;
	private Map<String, Float> skuCostElements = new LinkedHashMap<String, Float>();
	private String costStatus;
	private Float tCos;

	private PLMaskElements maskElements;

	public ProductCost(String prodIdBase, Map<String, Float> skuCostElements, String costStatus, Float tCos,
			PLMaskElements maskElements) {
		super();
		this.prodIdBase = prodIdBase;
		this.skuCostElements = skuCostElements;
		this.costStatus = costStatus;
		this.tCos = tCos;
		this.maskElements = maskElements;
	}

	public String getProdIdBase() {
		return prodIdBase;
	}

	public Map<String, Float> getSkuCostElements() {
		return skuCostElements;
	}

	public String getCostStatus() {
		return costStatus;
	}

	public Float gettCos() {
		return tCos;
	}

	public PLMaskElements getMaskElements() {
		return maskElements;
	}

	@Override
	public String toString() {
		return "ProductCost [prodIdBase=" + prodIdBase + ", skuCostElements=" + skuCostElements + ", costStatus="
				+ costStatus + ", TCOS=" + tCos + ", maskElements=" + maskElements + "]";
	}

}
