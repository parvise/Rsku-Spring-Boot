package com.hp.c4.rsku.rSku.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "fieldType" })
public class MaskElementTypes {

	private String elementType;
	private char flag;
	private String fieldType;

	public MaskElementTypes(String elementType, char flag, String fieldType) {
		super();
		this.elementType = elementType;
		this.flag = flag;
		this.fieldType = fieldType;
	}

	public String getElementType() {
		return elementType;
	}

	public String getFieldType() {
		return fieldType;
	}

	public char getFlag() {
		return flag;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elementType == null) ? 0 : elementType.hashCode());
		result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
		result = prime * result + flag;
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
		MaskElementTypes other = (MaskElementTypes) obj;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		if (fieldType == null) {
			if (other.fieldType != null)
				return false;
		} else if (!fieldType.equals(other.fieldType))
			return false;
		if (flag != other.flag)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MaskElementTypes [elementType=" + elementType + ", fieldType=" + fieldType + ", flag=" + flag + "]";
	}

}
