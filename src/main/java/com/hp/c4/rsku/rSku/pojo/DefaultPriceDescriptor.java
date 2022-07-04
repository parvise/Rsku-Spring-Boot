package com.hp.c4.rsku.rSku.pojo;

public class DefaultPriceDescriptor {

	private String twoLetterCntry;
	private String country;
	private String geoCode;
	private String region;
	private String priceCountryCode;
	private String priceCurrencyCode;
	private String priceTermCode;
	private String defaultDelivery;

	public DefaultPriceDescriptor() {
	}

	public DefaultPriceDescriptor(String twoLetterCntry, String country, String geoCode, String priceCountryCode,
			String priceCurrencyCode, String priceTermCode, String region, String defaultDelivery) {
		super();
		this.twoLetterCntry = twoLetterCntry;
		this.country = country;
		this.geoCode = geoCode;
		this.priceCountryCode = priceCountryCode;
		this.priceCurrencyCode = priceCurrencyCode;
		this.priceTermCode = priceTermCode;
		this.region = region;
		this.defaultDelivery = defaultDelivery;
	}

	public String getTwoLetterCntry() {
		return twoLetterCntry;
	}

	public String getGeoCode() {
		return geoCode;
	}

	public String getPriceCountryCode() {
		return priceCountryCode;
	}

	public String getPriceCurrencyCode() {
		return priceCurrencyCode;
	}

	public String getPriceTermCode() {
		return priceTermCode;
	}

	public String getRegion() {
		return region;
	}

	public String getDefaultDelivery() {
		return defaultDelivery;
	}

	public String getCountry() {
		return country;
	}

	@Override
	public String toString() {
		return "DefaultPriceDescriptor [twoLetterCntry=" + twoLetterCntry + ", country=" + country + ", geoCode="
				+ geoCode + ", region=" + region + ", priceCountryCode=" + priceCountryCode + ", priceCurrencyCode="
				+ priceCurrencyCode + ", priceTermCode=" + priceTermCode + ", defaultDelivery=" + defaultDelivery + "]";
	}

}
