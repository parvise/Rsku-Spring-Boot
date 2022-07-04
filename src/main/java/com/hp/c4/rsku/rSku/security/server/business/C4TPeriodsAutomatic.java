package com.hp.c4.rsku.rSku.security.server.business;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class C4TPeriodsAutomatic {

	private static String BEGIN_DATE = "01-Jan-28";
	private static String END_DATE = "31-Dec-28";
	private static String QUARTER_DATE = "01-Feb-28";
	private static int maxId = 1820;

	public static void main(String[] args) {
		try {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");

			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Connection c4offi = DriverManager.getConnection(
					"jdbc:oracle:thin:@(DESCRIPTION =(SDU = 32768)(enable = broken)(LOAD_BALANCE = yes)(ADDRESS = (PROTOCOL = TCP)(HOST = gcu43288.houston.hpicorp.net)(PORT = 1526))(ADDRESS=(PROTOCOL = TCP)(HOST = c4offi.inc.hpicorp.net)(PORT = 1525))(CONNECT_DATA = (SERVICE_NAME =C4OFFI)))",
					"c4prod", "");

			Connection c4onsi = DriverManager.getConnection(
					"jdbc:oracle:thin:@(DESCRIPTION =(SDU = 32768)(enable = broken)(LOAD_BALANCE = yes)(ADDRESS = (PROTOCOL = TCP)(HOST = gcu43288.houston.hpicorp.net)(PORT = 1526))(ADDRESS=(PROTOCOL = TCP)(HOST = c4onsi.inc.hpicorp.net)(PORT = 1525))(CONNECT_DATA = (SERVICE_NAME =C4ONSI)))",
					"c4prod", "");
			if (c4offi != null) {
				System.out.println(c4offi);
			}

			if (c4onsi != null) {
				System.out.println(c4onsi);
			}

			String sHierCurSql = null;
			PreparedStatement pstmtHierCurSql = null;
			ResultSet rsHierCurSql = null;

			updateTPeriods(pstmtHierCurSql, c4onsi, c4offi);

			c4onsi.close();
			c4offi.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void updateTPeriods(PreparedStatement pstmtHierCurSql, Connection c4onsi, Connection c4offi) {

		String oldDate = BEGIN_DATE;
		String newDate = END_DATE;
		System.out.println("Date before Addition: " + oldDate);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
		Calendar c = Calendar.getInstance();
		Calendar c1 = Calendar.getInstance();

		try {
			c.setTime(sdf.parse(oldDate));
			c1.setTime(sdf.parse(newDate));

			while (c.getTimeInMillis() < c1.getTimeInMillis()) {
				c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
				newDate = sdf.format(c.getTime());
				System.out.println(maxId + ":" + "W" + ":" + newDate);
				insertTPeriodOnsi(pstmtHierCurSql, c4onsi, maxId, "W", newDate);
				insertTPeriodOffi(pstmtHierCurSql, c4offi, maxId, "W", newDate);
				c.add(Calendar.DATE, 7);
				maxId++;
			}

			c.setTime(sdf.parse(oldDate));
			c1.setTime(sdf.parse(newDate));

			while (c.getTimeInMillis() < c1.getTimeInMillis()) {
				c.set(Calendar.DAY_OF_MONTH, 1);
				newDate = sdf.format(c.getTime());
				System.out.println(maxId + ":" + "M" + ":" + newDate);
				insertTPeriodOnsi(pstmtHierCurSql, c4onsi, maxId, "M", newDate);
				insertTPeriodOffi(pstmtHierCurSql, c4offi, maxId, "M", newDate);
				c.add(Calendar.MONTH, 1);
				maxId++;
			}

			oldDate = QUARTER_DATE;
			c.setTime(sdf.parse(oldDate));
			c1.setTime(sdf.parse(newDate));
			while (c.getTimeInMillis() < c1.getTimeInMillis()) {
				c.set(Calendar.DAY_OF_MONTH, 1);
				newDate = sdf.format(c.getTime());
				System.out.println(maxId + ":" + "Q" + ":" + newDate);
				insertTPeriodOnsi(pstmtHierCurSql, c4onsi, maxId, "Q", newDate);
				insertTPeriodOffi(pstmtHierCurSql, c4offi, maxId, "Q", newDate);
				c.add(Calendar.MONTH, 3);
				maxId++;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void insertTPeriodOnsi(PreparedStatement pstmtHierCurSql, Connection con, int periodId,
			String periodType, String startDate) throws SQLException {
		pstmtHierCurSql = con
				.prepareStatement("insert into t_period (period_id,period_type,start_date) values (?,?,?)");
		pstmtHierCurSql.setInt(1, periodId);
		pstmtHierCurSql.setString(2, periodType);
		pstmtHierCurSql.setString(3, startDate);

		int row = pstmtHierCurSql.executeUpdate();
		if (row > 0) {
			System.out.println("Success" + periodId);
		}

		pstmtHierCurSql.close();
	}

	public static void insertTPeriodOffi(PreparedStatement pstmtHierCurSql, Connection con, int periodId,
			String periodType, String startDate) throws SQLException {
		pstmtHierCurSql = con.prepareStatement("insert into t_period (seq_num,period_type,start_date) values (?,?,?)");
		pstmtHierCurSql.setInt(1, periodId);
		pstmtHierCurSql.setString(2, periodType);
		pstmtHierCurSql.setString(3, startDate);

		int row = pstmtHierCurSql.executeUpdate();
		if (row > 0) {
			System.out.println("Success" + periodId);
		}

		pstmtHierCurSql.close();
	}
}
