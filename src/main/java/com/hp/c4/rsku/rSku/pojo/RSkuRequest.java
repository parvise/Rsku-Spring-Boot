package com.hp.c4.rsku.rSku.pojo;

import java.util.Arrays;

public class RSkuRequest {

	private PlMapping rapidSku;

	private Product[] listOfProducts;

	private String dates[];
	private char frequency; // (W,M,Q ) Derived internally in C4
	private int totalPeriods; // By default one will consider in C4

	private String country;
	private PriceDescriptor descriptor;

	private String delivery; // By Default Delivery Duty PAID DDP we consider in C4
	private String mot; // MOT By default Air we consider in C4
	private String outputCurrency; // By default USD has Output Currency In C4

	public PlMapping getRapidSku() {
		return rapidSku;
	}

	public void setRapidSku(PlMapping rapidSku) {
		this.rapidSku = rapidSku;
	}

	public Product[] getListOfProducts() {
		return listOfProducts;
	}

	public void setListOfProducts(Product[] listOfProducts) {
		this.listOfProducts = listOfProducts;
	}

	public String[] getDates() {
		return dates;
	}

	public void setDates(String[] dates) {
		this.dates = dates;
	}

	public char getFrequency() {
		return frequency;
	}

	public void setFrequency(char frequency) {
		this.frequency = frequency;
	}

	public int getTotalPeriods() {
		return totalPeriods;
	}

	public void setTotalPeriods(int totalPeriods) {
		this.totalPeriods = totalPeriods;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public PriceDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(PriceDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public String getDelivery() {
		return delivery;
	}

	public void setDelivery(String delivery) {
		this.delivery = delivery;
	}

	public String getMot() {
		return mot;
	}

	public void setMot(String mot) {
		this.mot = mot;
	}

	public String getOutputCurrency() {
		return outputCurrency;
	}

	public void setOutputCurrency(String outputCurrency) {
		this.outputCurrency = outputCurrency;
	}

	@Override
	public String toString() {
		return "RSkuRequest [rapidSku=" + rapidSku + ", listOfProducts=" + Arrays.toString(listOfProducts) + ", dates="
				+ Arrays.toString(dates) + ", frequency=" + frequency + ", totalPeriods=" + totalPeriods + ", country="
				+ country + ", descriptor=" + descriptor + ", delivery=" + delivery + ", mot=" + mot
				+ ", outputCurrency=" + outputCurrency + "]";
	}

}
