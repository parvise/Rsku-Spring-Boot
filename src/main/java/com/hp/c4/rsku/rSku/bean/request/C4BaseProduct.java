package com.hp.c4.rsku.rSku.bean.request;

public class C4BaseProduct {
	private String prodIdBase;

	public String getProdIdBase() {
		return prodIdBase;
	}

	public void setProdIdBase(String prodIdBase) {
		this.prodIdBase = prodIdBase;
	}

	@Override
	public String toString() {
		return "Product [prodIdBase=" + prodIdBase + "]";
	}
}
