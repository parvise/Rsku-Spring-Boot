package com.hp.c4.rsku.rSku.bean.request;

import java.util.Date;

public class TCCosData extends C4CostData {

	public TCCosData(String prodId, String opt, String mcc, String spn, int packId, String elementType, int periodId,
			String geoCode, char geoLevel, Date timeStamp, String batchId, float cost, char periodType, Date startDate,
			String type) {
		super(prodId, opt, mcc, spn, packId, elementType, periodId, geoCode, geoLevel, timeStamp, batchId, cost,
				periodType, startDate, type);
	}

	@Override
	public String toString() {
		return "C4CostData [SKU=" + getProdId() + "|" + getOpt() + "|" + getMcc() + "|" + getSpn() + "], elementType="
				+ getElementType() + ", periodId=" + getPeriodId() + ", geoCode=" + getGeoCode() + ", cost=" + getCost()
				+ ",type=" + getType() + "]";
	}

}
