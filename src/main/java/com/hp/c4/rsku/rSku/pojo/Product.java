package com.hp.c4.rsku.rSku.pojo;

public class Product {

	private String prodId;
	private String opt;
	private String spn;
	private String mcc;

	private String prodDesc;
	private boolean isRSku;

	private boolean isRequestedMcc;
	private boolean isAutoMccAvl;

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

	public boolean isRequestedMcc() {
		return isRequestedMcc;
	}

	public void setRequestedMcc(boolean isRequestedMcc) {
		this.isRequestedMcc = isRequestedMcc;
	}

	public boolean isAutoMccAvl() {
		return isAutoMccAvl;
	}

	public void setAutoMccAvl(boolean isAutoMccAvl) {
		this.isAutoMccAvl = isAutoMccAvl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isAutoMccAvl ? 1231 : 1237);
		result = prime * result + (isRequestedMcc ? 1231 : 1237);
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
		if (isAutoMccAvl != other.isAutoMccAvl)
			return false;
		if (isRequestedMcc != other.isRequestedMcc)
			return false;
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
		return "[" + prodId + "|" + opt + "|" + spn + "|" + mcc + "]";
	}

}
