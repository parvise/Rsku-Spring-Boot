package com.hp.c4.rsku.rSku.bean.request;

public class Connect {

	private String userName;
	private String password;

	public String getUserName() {
		if (userName == null)
			return null;
		return userName.trim();
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		if (password == null)
			return null;
		return password.trim();
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Connect [userName=" + userName + ", password=" + password + "]";
	}

}
