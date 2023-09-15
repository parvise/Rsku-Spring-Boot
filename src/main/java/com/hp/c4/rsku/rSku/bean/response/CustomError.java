package com.hp.c4.rsku.rSku.bean.response;

import java.util.Map;

public class CustomError {
	private int status;
	private String error;
	private String path;
	private String message;
	private String timestamp;
	private String trace;

	public CustomError(int status, Map<String, Object> error) {
		this.status = status;
		this.error = (String) error.get("error");
		this.path = (String) error.get("path");
		this.message = "The requested resource is not Available";// (String) error.get("message");
		this.timestamp = error.get("timestamp").toString();
		this.trace = (String) error.get("trace");
	}

	public int getStatus() {
		return status;
	}

	public String getError() {
		return error;
	}

	public String getPath() {
		return path;
	}

	public String getMessage() {
		return message;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public String getTrace() {
		return trace;
	}

}
