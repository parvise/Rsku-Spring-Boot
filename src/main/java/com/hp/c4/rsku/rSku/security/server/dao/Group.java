package com.hp.c4.rsku.rSku.security.server.dao;

import java.io.Serializable;

/**
 * <p>
 * Title: Group.java Description: It is a complete Group data object which holds
 * the info about a particular group.
 * </p>
 * 
 * <p>
 * � 2008 Hewlett-Packard Development Company
 * </p>
 * @ author Satish / Chetan / Srini @ version v2.0 @ Since June 2008
 */
public class Group implements Serializable {

	private String groupId = null, groupName = null, groupDescription = null;

	public String getGroupDescription() {
		return groupDescription;
	}

	public void setGroupDescription(String groupDescription) {
		this.groupDescription = groupDescription;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}