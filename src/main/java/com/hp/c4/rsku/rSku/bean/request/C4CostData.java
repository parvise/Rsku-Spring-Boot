package com.hp.c4.rsku.rSku.bean.request;

import java.util.Date;

public class C4CostData {
	private String prodId;
	private String opt;
	private String mcc;
	private String spn;

	private int packId;
	private String elementType;
	private int periodId;
	private String geoCode;
	private char geoLevel;
	private Date timeStamp;
	private String batchId;
	private Float cost;
	private char periodType;
	private Date startDate;
	private String type;

	public C4CostData(String prodId, String opt, String mcc, String spn, int packId, String elementType, int periodId,
			String geoCode, char geoLevel, Date timeStamp, String batchId, Float cost, char periodType, Date startDate,
			String type) {
		super();
		this.prodId = prodId;
		this.opt = opt;
		this.mcc = mcc;
		this.spn = spn;
		this.packId = packId;
		this.elementType = elementType;
		this.periodId = periodId;
		this.geoCode = geoCode;
		this.geoLevel = geoLevel;
		this.timeStamp = timeStamp;
		this.batchId = batchId;
		this.cost = cost;
		this.periodType = periodType;
		this.startDate = startDate;
		this.type = type;
	}

	public String getProdId() {
		return prodId;
	}

	public String getOpt() {
		return opt;
	}

	public String getMcc() {
		return mcc;
	}

	public String getSpn() {
		return spn;
	}

	public int getPackId() {
		return packId;
	}

	public String getElementType() {
		return elementType;
	}

	public int getPeriodId() {
		return periodId;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public char getGeoLevel() {
		return geoLevel;
	}

	public Date getTimeStamp() {
		return timeStamp;
	}

	public String getBatchId() {
		return batchId;
	}

	public Float getCost() {
		return cost;
	}

	public char getPeriodType() {
		return periodType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public String getType() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		C4CostData other = (C4CostData) obj;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "C4CostData [SKU=" + prodId + "|" + opt + "|" + mcc + "|" + spn + "], elementType=" + elementType
				+ ", periodId=" + periodId + ", geoCode=" + geoCode  + ", cost=" + cost
				+ ",type=" + type + "]";
	}

}
