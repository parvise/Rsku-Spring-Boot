/**
 * CcosCost.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.hp.c4.rsku.rSku.security.server.util.icost;

public class CcosCost implements java.io.Serializable {
	private java.lang.String productId;
	private java.lang.String opt;
	private java.lang.String spn;
	private java.lang.String mcc;
	private java.lang.String ww;
	private java.lang.String region;
	private java.lang.String country;
	private java.lang.String elementType;
	private java.lang.String periodStart;
	private float cost;
	private java.lang.String action;

	public CcosCost() {
	}

	public java.lang.String getProductId() {
		return productId;
	}

	public void setProductId(java.lang.String productId) {
		this.productId = productId;
	}

	public java.lang.String getOpt() {
		return opt;
	}

	public void setOpt(java.lang.String opt) {
		this.opt = opt;
	}

	public java.lang.String getSpn() {
		return spn;
	}

	public void setSpn(java.lang.String spn) {
		this.spn = spn;
	}

	public java.lang.String getMcc() {
		return mcc;
	}

	public void setMcc(java.lang.String mcc) {
		this.mcc = mcc;
	}

	public java.lang.String getWw() {
		return ww;
	}

	public void setWw(java.lang.String ww) {
		this.ww = ww;
	}

	public java.lang.String getRegion() {
		return region;
	}

	public void setRegion(java.lang.String region) {
		this.region = region;
	}

	public java.lang.String getCountry() {
		return country;
	}

	public void setCountry(java.lang.String country) {
		this.country = country;
	}

	public java.lang.String getElementType() {
		return elementType;
	}

	public void setElementType(java.lang.String elementType) {
		this.elementType = elementType;
	}

	public java.lang.String getPeriodStart() {
		return periodStart;
	}

	public void setPeriodStart(java.lang.String periodStart) {
		this.periodStart = periodStart;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public java.lang.String getAction() {
		return action;
	}

	public void setAction(java.lang.String action) {
		this.action = action;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof CcosCost))
			return false;
		CcosCost other = (CcosCost) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((productId == null && other.getProductId() == null)
						|| (productId != null && productId.equals(other.getProductId())))
				&& ((opt == null && other.getOpt() == null) || (opt != null && opt.equals(other.getOpt())))
				&& ((spn == null && other.getSpn() == null) || (spn != null && spn.equals(other.getSpn())))
				&& ((mcc == null && other.getMcc() == null) || (mcc != null && mcc.equals(other.getMcc())))
				&& ((ww == null && other.getWw() == null) || (ww != null && ww.equals(other.getWw())))
				&& ((region == null && other.getRegion() == null)
						|| (region != null && region.equals(other.getRegion())))
				&& ((country == null && other.getCountry() == null)
						|| (country != null && country.equals(other.getCountry())))
				&& ((elementType == null && other.getElementType() == null)
						|| (elementType != null && elementType.equals(other.getElementType())))
				&& ((periodStart == null && other.getPeriodStart() == null)
						|| (periodStart != null && periodStart.equals(other.getPeriodStart())))
				&& cost == other.getCost() && ((action == null && other.getAction() == null)
						|| (action != null && action.equals(other.getAction())));
		__equalsCalc = null;
		return _equals;
	}

	private boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getProductId() != null) {
			_hashCode += getProductId().hashCode();
		}
		if (getOpt() != null) {
			_hashCode += getOpt().hashCode();
		}
		if (getSpn() != null) {
			_hashCode += getSpn().hashCode();
		}
		if (getMcc() != null) {
			_hashCode += getMcc().hashCode();
		}
		if (getWw() != null) {
			_hashCode += getWw().hashCode();
		}
		if (getRegion() != null) {
			_hashCode += getRegion().hashCode();
		}
		if (getCountry() != null) {
			_hashCode += getCountry().hashCode();
		}
		if (getElementType() != null) {
			_hashCode += getElementType().hashCode();
		}
		if (getPeriodStart() != null) {
			_hashCode += getPeriodStart().hashCode();
		}
		_hashCode += new Float(getCost()).hashCode();
		if (getAction() != null) {
			_hashCode += getAction().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

	@Override
	public String toString() {
		return "Cost [" + productId + "|" + opt + "|" + spn + "|" + mcc + "|" + ww + "|" + region + "|" + country + "|"
				+ elementType + "|" + periodStart + "|" + cost + "|" + action + "]";
	}

}
