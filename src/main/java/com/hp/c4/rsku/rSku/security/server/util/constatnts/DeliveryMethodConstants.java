package com.hp.c4.rsku.rSku.security.server.util.constatnts;

public enum DeliveryMethodConstants {

	DDP("DDP", "Delivered Duty Paid", "VTRDX"), CIP("CIP", "Carriage and Insurance Paid To", "CIPTX"),
	CPT("CPT", "Carriage Paid To", "CPTTX"), CIF("CIF", "Cost, Insurance and Freight", "CIFTX"),
	DAF("DAF", "Delivered At Frontier", "DAFTX"), DDU("DDU", "Delivered Duty Unpaid", "DDUTX"),
	EXW("EXW", "EXWorks", "EXWTX"), FCA("FCA", "Free CArrier", "FCATX"), FOB("FOB", "Free On Board", "FOBTX"),
	DAP("DAP", "Delivered Duty Unpaid", "DDUTX"), DAT("DAT", "Delivered Duty Unpaid", "DDUTX");

	private String incoTermCode;
	private String deliveryCodeDesc;
	private String tradingExpCode;

	private DeliveryMethodConstants(String incoTermCode, String deliveryCodeDesc, String tradingExpCode) {
		this.incoTermCode = incoTermCode;
		this.deliveryCodeDesc = deliveryCodeDesc;
		this.tradingExpCode = tradingExpCode;
	}

	public String getIncoTermCode() {
		return incoTermCode;
	}

	public void setIncoTermCode(String incoTermCode) {
		this.incoTermCode = incoTermCode;
	}

	public String getDeliveryCodeDesc() {
		return deliveryCodeDesc;
	}

	public void setDeliveryCodeDesc(String deliveryCodeDesc) {
		this.deliveryCodeDesc = deliveryCodeDesc;
	}

	public String getTradingExpCode() {
		return tradingExpCode;
	}

	public void setTradingExpCode(String tradingExpCode) {
		this.tradingExpCode = tradingExpCode;
	}

}
