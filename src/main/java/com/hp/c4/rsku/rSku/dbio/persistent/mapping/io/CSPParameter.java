package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

/**
 * Title:        C4
 * Description:  C4
 * Copyright:    Copyright (c) 2001
 * Company:      HP
 * @author viswa
 * @version 1.0
 */

public class CSPParameter implements java.lang.Cloneable {
 /**
  * from among the java.sql.Types!
  */
  private int dataType;

  /**
   * is the parameter in or out?
   */
  private boolean in;

  /**
   * name of the variable in the data object
   */
  private String name;

  /**
   * value of the parameter
   * could also be result set in case of ref cursor types.
   */
  private Object value;

  private CSPParameter() {
   //just for cloning.
  }

  public CSPParameter(int dataType, boolean in, String name) {
    this.dataType = dataType;
    this.in = in;
    this.name = name;
  }

  public void setValue (Object obj) {
   this.value = obj;
  }

  public Object getValue() {
   return this.value;
  }

  public int getDataType () {
   return this.dataType;
  }

  public String getName() {
   return this.name;
  }

  public boolean isParamIn() {
   return this.in;
  }

  public boolean isParamOut() {
   return !this.in;
  }

  public Object clone() {
   CSPParameter p = new CSPParameter();
   p.in = in;
   p.name = name;
   p.value = value;
   p.dataType = dataType;
   return p;
  }
}