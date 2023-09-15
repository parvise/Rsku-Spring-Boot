package com.hp.pcp.c4.security.server.dao.epam.api;

import java.io.Serializable;

public class EpamBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8823599172195297561L;
	private String userName;
	private String password;
	private String accountName;
	private String systemName;

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

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	@Override
	public String toString() {
		return "EpamBean [userName=" + userName + ", password=" + password + ", accountName=" + accountName
				+ ", systemName=" + systemName + "]";
	}

}
