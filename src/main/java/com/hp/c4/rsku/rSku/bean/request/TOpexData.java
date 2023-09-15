package com.hp.c4.rsku.rSku.bean.request;

import java.util.Date;

public class TOpexData extends C4CostData {
	private String prodLine;
	private String platform;
	private String subPf;
	private String sellingModel;

	public TOpexData(String prodId, String opt, String mcc, String spn, int packId, String elementType, int periodId,
			String geoCode, char geoLevel, Date timeStamp, String batchId, Float cost, char periodType, Date startDate,
			String prodLine, String platform, String subPf, String sellingModel, String type) {
		super(prodId, opt, mcc, spn, packId, elementType, periodId, geoCode, geoLevel, timeStamp, batchId, cost,
				periodType, startDate, type);
		this.prodLine = prodLine;
		this.platform = platform;
		this.subPf = subPf;
		this.sellingModel = sellingModel;
	}

	public String getProdLine() {
		return prodLine;
	}

	public String getPlatform() {
		return platform;
	}

	public String getSubPf() {
		return subPf;
	}

	public String getSellingModel() {
		return sellingModel;
	}

	@Override
	public String toString() {
		return "TOpexData [prodLine=" + prodLine + ", platform=" + platform + ", subPf=" + subPf + ", sellingModel="
				+ sellingModel + ", toString()=" + super.toString() + "]";
	}

}
