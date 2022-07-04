package com.hp.c4.rsku.rSku.security.server.util;


import java.sql.*;
import oracle.jdbc.*;

/**
 * <p>Title : C4Statement.java</p>
 * 
 * <p>� 2008 Hewlett-Packard Development Company</p>
 * @ version v1.0
 * @ Since 2004                            
 */
public class C4Statement {
  private PreparedStatement prep;
  public C4Statement(PreparedStatement ps) {
    this.prep = ps;
  }

   public void setChar (int index, char value) throws SQLException {
    setString (index, new String (new char[] {value}));
  }

  public void setInt (int index, int value) throws SQLException {
    prep.setInt( index, value );
  }

  public int getInt (int index) throws SQLException {
    return ((CallableStatement)prep).getInt (index);
  }

  public void setFloat (int index, float value) throws SQLException {
    prep.setFloat ( index, value );
  }

  public float getFloat (int index) throws SQLException {
    return ((CallableStatement)prep).getFloat (index);
  }

  public void setDouble (int index, double value) throws SQLException {
    prep.setDouble ( index, value );
  }

  public double getDouble (int index) throws SQLException {
    return ((CallableStatement)prep).getDouble (index);
  }

  public void setString (int index, String value) throws SQLException {
    prep.setString (index, value == null ? "<null>" : value);
  }

  public void setObject(int index, Object obj) throws SQLException {
    prep.setObject(index,obj);
  }

  public String getString (int index) throws SQLException {
    String temp = ((CallableStatement)prep).getString (index);
    if (temp != null && temp.trim().compareToIgnoreCase("<null>") == 0)
      temp = null;
    return temp;
  }

  // 0 means a null.
  public long getDate (int index) throws SQLException {
    return ((CallableStatement)prep).getDate (index).getTime();
  }

  // 0 means a null.
  public void setDate (int index, java.util.Date value) throws SQLException {
//    prep.setDate (index, value == null ?
//      new Date (0): new java.sql.Date (value.getTime()));
	    prep.setDate (index, value == null ?
	    	      null : new java.sql.Date (value.getTime()));
	  
  }

  public void setBoolean (int index, boolean value) throws SQLException {
    prep.setBoolean (index, value);
  }

  public boolean getBoolean (int index) throws SQLException {
    return ((CallableStatement)prep).getBoolean( index );
  }

  public void setNull (int index, int type) throws SQLException {
    prep.setNull( index, type );
  }

  public void setTimestamp
                      (int index, java.sql.Timestamp time) throws SQLException {
    prep.setTimestamp (index, time);
  }

  public Timestamp getTimestamp (int index) throws SQLException {
    return ((CallableStatement)prep).getTimestamp (index);
  }

  public void registerOutParameter (int index, int type) throws SQLException {
    ((CallableStatement)prep).registerOutParameter (index, type);
  }

  public void registerOutParameter
                 (int index, int type, String sqlTypeName) throws SQLException {
    ((CallableStatement)prep).registerOutParameter (index, type, sqlTypeName);
  }

  public ResultSet executeQuery () throws SQLException {
    return prep.executeQuery ();
  }

  public int executeUpdate () throws SQLException {
    return prep.executeUpdate ();
  }

  public boolean execute () throws SQLException {
    return (prep.execute ());
  }

  public Object getObject (int index) throws SQLException {
    return ((OracleCallableStatement) prep).getObject(index);
  }
  public oracle.sql.ARRAY getARRAY (int index) throws SQLException {
    return ((OracleCallableStatement) prep).getARRAY(index);
  }

  public void close() throws SQLException {
    prep.close();
  }

  public void clearParameters() throws SQLException {
    prep.clearParameters();
  }


}