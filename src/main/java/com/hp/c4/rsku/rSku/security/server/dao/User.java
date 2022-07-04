package com.hp.c4.rsku.rSku.security.server.dao;

import java.io.Serializable;
import java.util.Calendar;

/**
 * <p>Title: User.java
 * Description: It is a complete user data object which holds
 * the info about a particular user.</p>
 * 
 * <p>� 2008 Hewlett-Packard Development Company</p>
 * @ author Satish / Chetan / Srini
 * @ version v2.0
 * @ Since June 2008               
 */
public class User implements Serializable {

	private String userId = null , appName = null , passwd = null , firstName = null , action = null;
	private String status = null , userType = null , emailAddress = null , lastName = null;
	private Calendar creationDate = null , lastModDate = null , expiryDate = null;
	private int partyId = 0 , result = 0;
	  
	private String availableGroups = null;
	private String associatedGroups = null;
	
	private String[] assocGpids = null;
	
	private String[] userIds = null;

	public int getPartyId() {
		return partyId;
	}
	public void setPartyId(int partyId) {
		this.partyId = partyId;
	}
	public int getResult() {
		return result;
	}
	public void setResult(int result) {
		this.result = result;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	public String getAssociatedGroups() {
		return associatedGroups;
	}
	public void setAssociatedGroups(String associatedGroups) {
		this.associatedGroups = associatedGroups;
	}
	public String getAvailableGroups() {
		return availableGroups;
	}
	public void setAvailableGroups(String availableGroups) {
		this.availableGroups = availableGroups;
	}
	
	
	
	
	
	public Calendar getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Calendar creationDate) {
		this.creationDate = creationDate;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	public Calendar getExpiryDate() {
		return expiryDate;
	}
	public void setExpiryDate(Calendar expiryDate) {
		this.expiryDate = expiryDate;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Calendar getLastModDate() {
		return lastModDate;
	}
	public void setLastModDate(Calendar lastModDate) {
		this.lastModDate = lastModDate;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String[] getUserIds() {
		return userIds;
	}
	public void setUserIds(String[] userIds) {
		this.userIds = userIds;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String[] getAssocGpids() {
		return assocGpids;
	}
	public void setAssocGpids(String[] assocGpids) {
		this.assocGpids = assocGpids;
	}
}