package com.hp.c4.rsku.rSku.bean.response;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.hp.c4.rsku.rSku.pojo.MaskElementTypes;

@JsonIgnoreProperties(value = { "maskElements" })
public class PLMaskElements {

	private String pl;
	private String platform;
	private List<MaskElementTypes> maskElements = new ArrayList<MaskElementTypes>();

	public PLMaskElements(String pl, String platform, List<MaskElementTypes> maskElements) {
		super();
		this.pl = pl;
		this.platform = platform;
		this.maskElements = maskElements;
	}

	public String getPl() {
		return pl;
	}

	public String getPlatform() {
		return platform;
	}

	public List<MaskElementTypes> getMaskElements() {
		return maskElements;
	}

	@Override
	public String toString() {
		return "PLMaskElements [pl=" + pl + ", platform=" + platform + ", maskElements=" + maskElements + "]";
	}

}
