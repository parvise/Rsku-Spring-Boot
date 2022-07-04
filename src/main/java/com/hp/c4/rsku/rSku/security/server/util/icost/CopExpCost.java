/**
 * CopExpCost.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.hp.c4.rsku.rSku.security.server.util.icost;

public class CopExpCost implements java.io.Serializable {
	private java.lang.String pl;
	private java.lang.String platform;
	private java.lang.String subplatform;
	private java.lang.String sellingMotion;
	private java.lang.String ww;
	private java.lang.String region;
	private java.lang.String country;
	private java.lang.String elementType;
	private java.lang.String periodStart;
	private float cost;
	private java.lang.String action;

	public CopExpCost() {
	}

	public java.lang.String getPl() {
		return pl;
	}

	public void setPl(java.lang.String pl) {
		this.pl = pl;
	}

	public java.lang.String getPlatform() {
		return platform;
	}

	public void setPlatform(java.lang.String platform) {
		this.platform = platform;
	}

	public java.lang.String getSubplatform() {
		return subplatform;
	}

	public void setSubplatform(java.lang.String subplatform) {
		this.subplatform = subplatform;
	}

	public java.lang.String getSellingMotion() {
		return sellingMotion;
	}

	public void setSellingMotion(java.lang.String sellingMotion) {
		this.sellingMotion = sellingMotion;
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
		if (!(obj instanceof CopExpCost))
			return false;
		CopExpCost other = (CopExpCost) obj;
		if (obj == null)
			return false;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true && ((pl == null && other.getPl() == null) || (pl != null && pl.equals(other.getPl())))
				&& ((platform == null && other.getPlatform() == null)
						|| (platform != null && platform.equals(other.getPlatform())))
				&& ((subplatform == null && other.getSubplatform() == null)
						|| (subplatform != null && subplatform.equals(other.getSubplatform())))
				&& ((sellingMotion == null && other.getSellingMotion() == null)
						|| (sellingMotion != null && sellingMotion.equals(other.getSellingMotion())))
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
		if (getPl() != null) {
			_hashCode += getPl().hashCode();
		}
		if (getPlatform() != null) {
			_hashCode += getPlatform().hashCode();
		}
		if (getSubplatform() != null) {
			_hashCode += getSubplatform().hashCode();
		}
		if (getSellingMotion() != null) {
			_hashCode += getSellingMotion().hashCode();
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

}
