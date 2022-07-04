/**
 * Cheader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.hp.c4.rsku.rSku.security.server.util.icost;

public class Cheader implements java.io.Serializable {
	private java.lang.String author;
	private java.lang.String sender;
	private java.lang.String pls;
	private java.lang.String regions;
	private java.lang.String email;
	private java.lang.String creationDate;
	private java.lang.String effectiveDate;
	private java.lang.String batchType;
	private java.lang.String startDate;
	private java.lang.String periodType;

	public Cheader() {
	}

	public java.lang.String getAuthor() {
		return author;
	}

	public void setAuthor(java.lang.String author) {
		this.author = author;
	}

	public java.lang.String getSender() {
		return sender;
	}

	public void setSender(java.lang.String sender) {
		this.sender = sender;
	}

	public java.lang.String getPls() {
		return pls;
	}

	public void setPls(java.lang.String pls) {
		this.pls = pls;
	}

	public java.lang.String getRegions() {
		return regions;
	}

	public void setRegions(java.lang.String regions) {
		this.regions = regions;
	}

	public java.lang.String getEmail() {
		return email;
	}

	public void setEmail(java.lang.String email) {
		this.email = email;
	}

	public java.lang.String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.lang.String creationDate) {
		this.creationDate = creationDate;
	}

	public java.lang.String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(java.lang.String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public java.lang.String getBatchType() {
		return batchType;
	}

	public void setBatchType(java.lang.String batchType) {
		this.batchType = batchType;
	}

	public java.lang.String getStartDate() {
		return startDate;
	}

	public void setStartDate(java.lang.String startDate) {
		this.startDate = startDate;
	}

	public java.lang.String getPeriodType() {
		return periodType;
	}

	public void setPeriodType(java.lang.String periodType) {
		this.periodType = periodType;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Cheader))
			return false;
		Cheader other = (Cheader) obj;
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
				&& ((author == null && other.getAuthor() == null)
						|| (author != null && author.equals(other.getAuthor())))
				&& ((sender == null && other.getSender() == null)
						|| (sender != null && sender.equals(other.getSender())))
				&& ((pls == null && other.getPls() == null) || (pls != null && pls.equals(other.getPls())))
				&& ((regions == null && other.getRegions() == null)
						|| (regions != null && regions.equals(other.getRegions())))
				&& ((email == null && other.getEmail() == null) || (email != null && email.equals(other.getEmail())))
				&& ((creationDate == null && other.getCreationDate() == null)
						|| (creationDate != null && creationDate.equals(other.getCreationDate())))
				&& ((effectiveDate == null && other.getEffectiveDate() == null)
						|| (effectiveDate != null && effectiveDate.equals(other.getEffectiveDate())))
				&& ((batchType == null && other.getBatchType() == null)
						|| (batchType != null && batchType.equals(other.getBatchType())))
				&& ((startDate == null && other.getStartDate() == null)
						|| (startDate != null && startDate.equals(other.getStartDate())))
				&& ((periodType == null && other.getPeriodType() == null)
						|| (periodType != null && periodType.equals(other.getPeriodType())));
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
		if (getAuthor() != null) {
			_hashCode += getAuthor().hashCode();
		}
		if (getSender() != null) {
			_hashCode += getSender().hashCode();
		}
		if (getPls() != null) {
			_hashCode += getPls().hashCode();
		}
		if (getRegions() != null) {
			_hashCode += getRegions().hashCode();
		}
		if (getEmail() != null) {
			_hashCode += getEmail().hashCode();
		}
		if (getCreationDate() != null) {
			_hashCode += getCreationDate().hashCode();
		}
		if (getEffectiveDate() != null) {
			_hashCode += getEffectiveDate().hashCode();
		}
		if (getBatchType() != null) {
			_hashCode += getBatchType().hashCode();
		}
		if (getStartDate() != null) {
			_hashCode += getStartDate().hashCode();
		}
		if (getPeriodType() != null) {
			_hashCode += getPeriodType().hashCode();
		}
		__hashCodeCalc = false;
		return _hashCode;
	}

}
