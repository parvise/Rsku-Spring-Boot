package com.hp.c4.rsku.rSku.pojo;

import java.util.Arrays;

public class PlMapping {

	private String pl;
	private String platform;
	private Product product;
	private MaskElementTypes maskElements[];

	public String getPl() {
		return pl;
	}

	public void setPl(String pl) {
		this.pl = pl;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public MaskElementTypes[] getMaskElements() {
		return maskElements;
	}

	public void setMaskElements(MaskElementTypes[] maskElements) {
		this.maskElements = maskElements;
	}

	@Override
	public String toString() {
		return "PlMapping [pl=" + pl + ", platform=" + platform + ", product=" + product + ", maskElements="
				+ Arrays.toString(maskElements) + "]";
	}

}
