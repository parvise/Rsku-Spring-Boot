package com.hp.c4.rsku.rSku.bean.request;

public class RSkuProduct {

	private String rSkuProd;

	private String rSkuDec;

	public String getrSkuProd() {
		if (rSkuProd == null)
			return null;
		return rSkuProd.trim();
	}

	public void setrSkuProd(String rSkuProd) {
		this.rSkuProd = rSkuProd;
	}

	public String getrSkuDec() {
		if (rSkuDec == null)
			return null;
		return rSkuDec.trim();
	}

	public void setrSkuDec(String rSkuDec) {
		this.rSkuDec = rSkuDec;
	}

	@Override
	public String toString() {
		return "RSkuProduct [rSkuProd=" + rSkuProd + ", rSkuDec=" + rSkuDec + "]";
	}

}
