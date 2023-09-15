package com.hp.c4.rsku.rSku.bean.response;

import java.util.LinkedHashMap;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(value = Include.NON_EMPTY)
public class ProductCost {

	private String period;
	private String frequency;

	private String prodIdBase;
	private Map<String, Float> skuCostElements = new LinkedHashMap<String, Float>();
	private String costStatus;
	private Float tCos;
	private ProductWithMcc mcc;

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

	public String getPeriod() {
		if (period == null)
			this.period = "";
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getFrequency() {
		if (frequency == null)
			this.frequency = "";
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public String getProdIdBase() {
		return prodIdBase;
	}

	public void setProdIdBase(String prodIdBase) {
		this.prodIdBase = prodIdBase;
	}

	public Map<String, Float> getSkuCostElements() {
		return skuCostElements;
	}

	public void setSkuCostElements(Map<String, Float> skuCostElements) {
		this.skuCostElements = skuCostElements;
	}

	public String getCostStatus() {
		return costStatus;
	}

	public void setCostStatus(String costStatus) {
		this.costStatus = costStatus;
	}

	public Float gettCos() {
		return tCos;
	}

	public void settCos(Float tCos) {
		this.tCos = tCos;
	}

	public PLMaskElements getMaskElements() {
		return maskElements;
	}

	public void setMaskElements(PLMaskElements maskElements) {
		this.maskElements = maskElements;
	}

	public ProductWithMcc getMcc() {
		return mcc;
	}

	public void setMcc(ProductWithMcc mcc) {
		this.mcc = mcc;
	}

	@Override
	public String toString() {
		return "ProductCost [prodIdBase=" + prodIdBase + ", skuCostElements=" + skuCostElements + ", costStatus="
				+ costStatus + ", TCOS=" + tCos + ", maskElements=" + maskElements + "]";
	}

}
