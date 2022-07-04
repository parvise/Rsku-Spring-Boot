package com.hp.c4.rsku.rSku.security.server.util;

/**
 * Title    : C4SecurityException.java
 * Description : This is C4SecurityException wrapper over Exception 
 * 
 * <p>� 2008 Hewlett-Packard Development Company</p>
 * @ author Srini
 * @ version v2.0
 * @ Since June 2008               
 */

public class C4SecurityException extends Exception {

  public C4SecurityException() {
  }
  public C4SecurityException(String msg) {
   super(msg);
  }

}