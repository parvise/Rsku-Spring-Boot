package com.hp.c4.rsku.rSku.pojo;

public class DefaultMotTradingExpense {

	private String deliveryMethMot;
	private String elementType;
	private String defaultElementType;

	public DefaultMotTradingExpense(String deliveryMethMot, String elementType, String defaultElementType) {
		super();
		this.deliveryMethMot = deliveryMethMot;
		this.elementType = elementType;
		this.defaultElementType = defaultElementType;
	}

	public String getDeliveryMethMot() {
		return deliveryMethMot;
	}

	public String getElementType() {
		return elementType;
	}

	public String getDefaultElementType() {
		return defaultElementType;
	}

	@Override
	public String toString() {
		return "DefaultMotTradingExpense [deliveryMethMot=" + deliveryMethMot + ", elementType=" + elementType
				+ ", defaultElementType=" + defaultElementType + "]";
	}

}
