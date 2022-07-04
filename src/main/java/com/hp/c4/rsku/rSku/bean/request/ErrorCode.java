package com.hp.c4.rsku.rSku.bean.request;

public class ErrorCode {

	private String errorCode;
	private String description;

	public ErrorCode(String errorCode, String description) {
		super();
		this.errorCode = errorCode;
		this.description = description;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "ErrorCode [errorCode=" + errorCode + ", description=" + description + "]";
	}

}
