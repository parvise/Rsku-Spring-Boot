package com.hp.c4.rsku.rSku.pojo;

public class Product {

	private String prodId;
	private String opt;
	private String spn;
	private String mcc;

	private String prodDesc;
	private boolean isRSku;

	public String getProdId() {
		return prodId;
	}

	public void setProdId(String prodId) {
		this.prodId = prodId;
	}

	public String getOpt() {
		return opt;
	}

	public void setOpt(String opt) {
		this.opt = opt;
	}

	public String getSpn() {
		return spn;
	}

	public void setSpn(String spn) {
		this.spn = spn;
	}

	public String getMcc() {
		return mcc;
	}

	public void setMcc(String mcc) {
		this.mcc = mcc;
	}

	public String getProdDesc() {
		return prodDesc;
	}

	public void setProdDesc(String prodDesc) {
		this.prodDesc = prodDesc;
	}

	public boolean isRSku() {
		return isRSku;
	}

	public void setRSku(boolean isRSku) {
		this.isRSku = isRSku;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mcc == null) ? 0 : mcc.hashCode());
		result = prime * result + ((opt == null) ? 0 : opt.hashCode());
		result = prime * result + ((prodId == null) ? 0 : prodId.hashCode());
		result = prime * result + ((spn == null) ? 0 : spn.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (mcc == null) {
			if (other.mcc != null)
				return false;
		} else if (!mcc.equals(other.mcc))
			return false;
		if (opt == null) {
			if (other.opt != null)
				return false;
		} else if (!opt.equals(other.opt))
			return false;
		if (prodId == null) {
			if (other.prodId != null)
				return false;
		} else if (!prodId.equals(other.prodId))
			return false;
		if (spn == null) {
			if (other.spn != null)
				return false;
		} else if (!spn.equals(other.spn))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + prodId + "]";
	}

}
