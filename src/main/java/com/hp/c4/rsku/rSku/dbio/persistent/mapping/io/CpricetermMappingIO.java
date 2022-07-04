package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

import com.hp.c4.rsku.rSku.c4.util.C4Exception;

public class CpricetermMappingIO extends ObjectIO {
  private final static String SP_GETMAPPING = "c4mcc.getPricetermMapping";
  private final static String SP_GETALLMAPPING = "c4mcc.getAllPricetermMapping";
  private static final String CURSOR = "CURSOR";

  public CpricetermMappingIO() {}

  public CpricetermMappingIO(String poolName) {
    super(poolName);
  }

  public String getPricetermMapping(String pricetermCode) throws C4Exception {
    try {
      return (String) callSp(getSelectProcedureInfo(SP_GETMAPPING, pricetermCode));
    } catch(SQLException e) {
      e.printStackTrace();
      throw new C4Exception(e.getMessage());
    }
  }

  public HashMap<String, String> getAllPricetermMapping() throws C4Exception {
    try {
      return (HashMap<String, String>) callSp(getSelectProcedureInfo(SP_GETALLMAPPING));
    } catch(SQLException e) {
      e.printStackTrace();
      throw new C4Exception(e.getMessage());
    }
  }

  public Object processResult(CProcedureInfo info) throws java.sql.SQLException, C4Exception {
    ResultSet rs = (ResultSet) info.getParamValue (CURSOR);

    if(info.getName().equals(SP_GETMAPPING)) {
      if (rs.next())
        return rs.getString("ELEMENT_TYPE");
      return null;
    } else if (info.getName().equals(SP_GETALLMAPPING)) {
      HashMap<String, String> mappings = new HashMap<String, String>();
      while (rs.next()) {
        mappings.put(rs.getString("PRC_TERM_CD"), rs.getString("ELEMENT_TYPE"));
      }
      return mappings;
    }

    return null;
  }

  private CProcedureInfo getSelectProcedureInfo(String name, String pricetermCode) {
    CSPParameter[] cpa = new CSPParameter[2];
    cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.VARCHAR, true, "PRC_TERM_CD");
    cpa[0].setValue(pricetermCode);
    cpa[1] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
    return new CProcedureInfo(name, cpa);
  }

  private CProcedureInfo getSelectProcedureInfo(String name) {
    CSPParameter[] cpa = new CSPParameter[1];
    cpa[0] = new CSPParameter(oracle.jdbc.OracleTypes.CURSOR, false, CURSOR);
    return new CProcedureInfo(name, cpa);
  }
}