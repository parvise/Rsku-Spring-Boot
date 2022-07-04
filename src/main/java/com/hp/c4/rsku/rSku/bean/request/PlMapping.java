package com.hp.c4.rsku.rSku.bean.request;

public class PlMapping {

	private String pl;
	private RSkuProduct product;

	public String getPl() {
		if (pl == null)
			return null;
		return pl.trim();
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public RSkuProduct getProduct() {
		return product;
	}

	public void setProduct(RSkuProduct product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "PlMapping [pl=" + pl + ", product=" + product + "]";
	}

}
