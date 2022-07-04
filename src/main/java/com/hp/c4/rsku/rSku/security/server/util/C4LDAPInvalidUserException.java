/**
 * 
 */
package com.hp.c4.rsku.rSku.security.server.util;

/**
 * @author atthulur
 *
 */
public class C4LDAPInvalidUserException extends Exception{
	
	public static final String INVALID_USER_MSG = "Invalid User Id/password.";
	public static final String INVALID_GROUP_MEMBER = "Not a valid group member.";
	
	private String errorMessage=null;
	
	public C4LDAPInvalidUserException(String errorMsg) {
		super(errorMsg);
		this.errorMessage = errorMsg;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}//end of class
