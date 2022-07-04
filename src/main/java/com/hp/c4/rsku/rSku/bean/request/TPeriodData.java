package com.hp.c4.rsku.rSku.bean.request;

import java.util.Date;

public class TPeriodData {

	private int periodId;
	private char periodType;
	private Date startDate;

	public TPeriodData(int periodId, char periodType, Date startDate) {
		super();
		this.periodId = periodId;
		this.periodType = periodType;
		this.startDate = startDate;
	}

	public int getPeriodId() {
		return periodId;
	}

	public char getPeriodType() {
		return periodType;
	}

	public Date getStartDate() {
		return startDate;
	}

	@Override
	public String toString() {
		return "TPeriodData [periodId=" + periodId + ", periodType=" + periodType + ", startDate=" + startDate + "]";
	}

}
