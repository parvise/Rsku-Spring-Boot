package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.util.HashMap;
/**
 * Title:        C4
 * Description:  Info regarding stored procedure to be called.
 * Copyright:    Copyright (c) 2001
 * Company:      HP
 * @author viswa
 * @version 1.0
 */

public class CProcedureInfo implements java.lang.Cloneable {
   private String name;
   private HashMap params;
   private CSPParameter[] paramsarray;
   private boolean mUseOracleExtensions;

   private CProcedureInfo() {
    //empty cons
   }

   public CProcedureInfo(String name,CSPParameter[] params) {
    this.name = name;
    this.params = createHashMap(params);

    //maintain the arry too! need to know the exact location and cannot be done
    //with array!, will remove hashmap soon. -viswa.
    this.paramsarray = params;
  }

  /**
   * Constructs a <code>CProcedureInfo</code> object with the option of using Oracle Objects
   * defined in the database
   *
   * @param name the procedure name
   * @param params params for calling the procedure
   * @param pUseOracleObjectAsOutParam true if out param is to be registered using name of
   * the object type as defined in the database
   * @author Tanay Srivastava
   */
  public CProcedureInfo(String name,CSPParameter[] params, boolean pUseOracleObjectAsOutParam) {
      this(name,params);
      mUseOracleExtensions = pUseOracleObjectAsOutParam;
  }

  /**
   * @return true if oracle object type is used as an out param
   * @author Tanay Srivastava
   */
  public boolean isOutParamOracleObject()
  {
      return mUseOracleExtensions;
  }

  public CSPParameter getParameterAt(int i) {
   if (this.paramsarray.length < i)
     return null;
   return this.paramsarray[i];
  }

  private HashMap createHashMap(CSPParameter[] params) {
   HashMap ht = new HashMap();
   for (int i=0; i<params.length; i++) {
     ht.put(params[i].getName(), params[i]);
   }
   return ht;
  }

  public String getName() {
   return this.name;
  }
  public int getParamCount() {
    return params.size();
  }

  public CSPParameter getParameter(String name) {
    return (CSPParameter)params.get(name);
  }

  public void setParamValue(String name,Object object) {
   CSPParameter p = (CSPParameter)params.get(name);
   p.setValue(object);
  }

  public Object getParamValue(String name) {

  CSPParameter p = (CSPParameter)params.get(name);
  //System.out.println("in side CProcInfo -->getParameterValue() :"+p.getName()+"  value " +p.getValue()+" asdasdas");
    if (p == null) return null;
   return p.getValue();
  }

  public Object clone() {
   CProcedureInfo info = new CProcedureInfo();
   info.name = this.name;
   info.params = (HashMap)this.params.clone();
   return info;
  }

}