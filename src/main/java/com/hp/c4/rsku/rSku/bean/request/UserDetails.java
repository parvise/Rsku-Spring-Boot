package com.hp.c4.rsku.rSku.bean.request;

public class UserDetails {

	private String userName;
	private String password;
	private String appName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	@Override
	public String toString() {
		return "UserDetails [userName=" + userName + ", password=" + password + ", appName=" + appName + "]";
	}

}
