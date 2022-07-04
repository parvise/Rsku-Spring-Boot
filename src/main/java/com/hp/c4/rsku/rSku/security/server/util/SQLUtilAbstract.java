package com.hp.c4.rsku.rSku.security.server.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public abstract class SQLUtilAbstract {
  //
  public static final int ORACLE_GID_LENGTH = 32;
  public static final boolean      PREPARED = true;
  public static final boolean      CALLABLE = false;
  public static final String statusIn = "ACTIV";

  private Locale _locale = Locale.US;
  private SimpleDateFormat _formatToDByyy_MM_dd_HH_mm_ss =
                         new SimpleDateFormat ("yyyy/MM/dd HH:mm:ss", _locale);
//                    (new CdateFormat()).getFormatterCCYY_MM_DD_HH_SS();

  private Connection conn;
  private C4Statement prep;
  private ResultSet rset;
  protected String _dbName;

  private boolean mode = PREPARED;

  public SQLUtilAbstract () throws SQLException {
    reset();
  }

  public Connection getConnection() throws SQLException {
    if (conn == null) {
      //ugly cast is there any  way around.. viswa
        conn = getConnection(_dbName);
      conn.setAutoCommit (true);
    }
    return conn;
  }
  public abstract Connection getConnection
                   (String dbName) throws SQLException;


  // public abstract void freeConnection (Connection conn) throws SQLException;
  public abstract void freeConnection () throws SQLException;

  public void setChar (int index, char value) throws SQLException {
    prep.setChar(index,value);
  }

  public void setInt (int index, int value) throws SQLException {
    prep.setInt( index, value );
  }

  public int getInt (int index) throws SQLException {
    return prep.getInt (index);
  }

  public void setFloat (int index, float value) throws SQLException {
    prep.setFloat ( index, value );
  }

  public float getFloat (int index) throws SQLException {
    return prep.getFloat (index);
  }

  public void setString (int index, String value) throws SQLException {
    prep.setString (index, value);
    // prep.setString (index, value);
  }

  public void setObject(int index, Object obj) throws SQLException {
    prep.setObject(index,obj);
  }

  public String getString (int index) throws SQLException {
     return prep.getString (index);
  }

  // 0 means a null.
  public long getDate (int index) throws SQLException {
    return prep.getDate(index);
  }

  //public void setDate (int index, long value) throws SQLException {
  //  prep.setDate (index, new java.sql.Date (value));
  //}

  // 0 means a null.
  public void setDate (int index, java.util.Date value) throws SQLException {
   prep.setDate(index,value);
  }

  public void setBoolean (int index, boolean value) throws SQLException {
    prep.setBoolean (index, value);
  }

  public boolean getBoolean (int index) throws SQLException {
    return prep.getBoolean( index );
  }

  public void setNull (int index, int type) throws SQLException {
    prep.setNull( index, type );
  }

  public void setTimestamp
                      (int index, java.sql.Timestamp time) throws SQLException {
    prep.setTimestamp (index, time);
  }

  public Timestamp getTimestamp (int index) throws SQLException {
    return prep.getTimestamp (index);
  }

  public void registerOutParameter (int index, int type) throws SQLException {
    prep.registerOutParameter (index, type);
  }

  public void registerOutParameter
                 (int index, int type, String sqlTypeName) throws SQLException {
    prep.registerOutParameter (index, type, sqlTypeName);
  }

  public ResultSet executeQuery () throws SQLException {
    rset = prep.executeQuery ();
    return (rset);

  }

  public int executeUpdate () throws SQLException {
    return prep.executeUpdate ();
  }

  public boolean execute () throws SQLException {
    return (prep.execute ());
  }

//  public STRUCT getSTRUCT (int index) throws SQLException {
//    return ((OracleCallableStatement) prep).getSTRUCT (index);
//  }

  public Object getObject (int index) throws SQLException {
    return prep.getObject(index);
  }
  public oracle.sql.ARRAY getARRAY (int index) throws SQLException {
    return prep.getARRAY(index);
  }
  public void setMode (boolean mode) {
    this.mode = mode;
  }

  public void reset () {
    subclose ();
    setMode (CALLABLE);
  }

  //
  // Make sure you catch this exception if db connection failed.
  //
  public void setSQL (String sql) throws SQLException {
    if (conn == null) {
      getConnection();
    }

    try{
      prep = new C4Statement(mode == PREPARED ?
                             conn.prepareStatement (sql) : conn.prepareCall (sql));
    }catch(NullPointerException ee){
      if(prep == null)
        System.err.println("\nNull 2 - SQL: prep is null" + (new java.util.Date()));
      prep = new C4Statement(mode == PREPARED ?
                         conn.prepareStatement(sql) : conn.prepareCall(sql));
      if(prep != null)
        System.err.println("\nPrep re-creation succeded: Not null" + (new java.util.Date()));

      ee.printStackTrace();
      System.err.println("SQL Statmenet 2");
    }
  }



  public void resetResultSet () {
    if (rset != null) {
      try {
        rset.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
      rset = null;
    }
  }

  public void subclose () {
    resetResultSet ();
    if (prep != null) {
      try {
        prep.close ();
      } catch (SQLException e) {
        e.printStackTrace ();
      }
      prep = null;
    }
  }

  public void close() {
    subclose();

    if (conn != null) {
      try {
        conn.close ();
        conn = null;
      } catch (SQLException e) {
        e.printStackTrace ();
      }
    }
  }

  public String checkEmptyString(String eStr){
    if(eStr == null || eStr.trim().length() == 0)
      return null;
    else
      return eStr.trim();
  }

  public static boolean isValidOraGID (String gid) {
    return (gid != null) && (gid.length() == ORACLE_GID_LENGTH);
  }

  // Check for null string.
  public static String checkNullString (String columnName) {
    String temp = columnName;
    if (temp != null && temp.trim().compareToIgnoreCase("<null>") == 0)
      temp = null;
    return temp;
  }

  //
}
