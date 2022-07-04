package com.hp.c4.rsku.rSku.pojo;

public class PriceDescriptor {

	private String countryCode;
	private String currencyCode;
	private String priceTermCode;
	private String priceDescriptor;

	public PriceDescriptor(String countryCode, String currencyCode, String priceTermCode) {
		super();
		this.countryCode = countryCode;
		this.currencyCode = currencyCode;
		this.priceTermCode = priceTermCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public String getPriceTermCode() {
		return priceTermCode;
	}

	public String getPriceDescriptor() {
		return countryCode + currencyCode + priceTermCode;
	}

	@Override
	public String toString() {
		return "PriceDescriptor [priceDescriptor=" + priceDescriptor + "]";
	}

}
