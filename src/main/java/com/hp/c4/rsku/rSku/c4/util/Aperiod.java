package com.hp.c4.rsku.rSku.c4.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

/**
 * Title:        C4
 * Description:  C4
 * Copyright:    Copyright (c) 2001
 * Company:      HP
 * @author
 * @version 1.0
 */

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

abstract public class Aperiod {
	//
	// public static SimpleDateFormat _formatter =
	// new SimpleDateFormat ("yyyy/MM/dd", Locale.US);

	public static SimpleDateFormat getFormat() {
		return new SimpleDateFormat("yyyy/MM/dd", Locale.US);
	}

	public static SimpleDateFormat getFormatForDelta() {
		return new SimpleDateFormat("yyyy/MM/dd HH:mm ss", Locale.US);
	}

	private Aperiod() {
	}

	public static boolean isRequiredPeriods(String periodStartOn, Date[] dateList) {
		boolean ret = false;

		for (int i = 0; i < dateList.length; i++) {
			try {
				if (getFormat().format(getPeriodStartDate(dateList[i], EperiodType.WEEKLY))
						.compareTo(periodStartOn) == 0
						|| getFormat().format(getPeriodStartDate(dateList[i], EperiodType.MONTHLY))
								.compareTo(periodStartOn) == 0
						|| getFormat().format(getPeriodStartDate(dateList[i], EperiodType.QUARTERLY))
								.compareTo(periodStartOn) == 0) {
					ret = true;
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	public static Date getPeriodStartDate(String date, EperiodType type) throws Exception {
		Date ret = null;
		if (type.equals(EperiodType.MONTHLY))
			ret = getPeriodStartDateByMonthly(date);
		else if (type.equals(EperiodType.WEEKLY))
			ret = getPeriodStartDateByWeekly(date);
		else if (type.equals(EperiodType.QUARTERLY))
			ret = getPeriodStartDateByQuarterly(date);
		else if (type.equals(EperiodType.DAILY))
			ret = getPeriodStartDateByDaily(date);
		else
			throw new Exception(
					"Cperiod.getPeriodStartDate() - invalid period type. type.toString <" + type.toString() + ">");

		return ret;
	}

	public static Date getPeriodStartDate(Date date, EperiodType type) throws Exception {
		Date ret = null;
		if (type.equals(EperiodType.MONTHLY))
			ret = getPeriodStartDateByMonthly(date);
		else if (type.equals(EperiodType.WEEKLY))
			ret = getPeriodStartDateByWeekly(date);
		else if (type.equals(EperiodType.QUARTERLY))
			ret = getPeriodStartDateByQuarterly(date);
		else if (type.equals(EperiodType.DAILY))
			ret = date;
		else
			throw new Exception(
					"Cperiod.getPeriodStartDate() - invalid period type. type.toString <" + type.toString() + ">");

		return ret;
	}

	public static Date getPeriodStartDateByMonthly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);

		// System.out.println ("Cperiod.getPeriodStartDateByMonthly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static Date getPeriodStartDateByMonthly(Date date) {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		// System.out.println ("Cperiod.getPeriodStartDateByMonthly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static Date getPeriodStartDateByWeekly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}

		Calendar newCal = Calendar.getInstance();
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY: // first day in a week
		case Calendar.TUESDAY:
		case Calendar.WEDNESDAY:
		case Calendar.THURSDAY:
		case Calendar.FRIDAY:
		case Calendar.SATURDAY:
			// cal.set (Calendar.DAY_OF_YEAR,
			// cal.get(Calendar.DAY_OF_YEAR) -
			// (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY));
			newCal.setTime(new Date(
					cal.getTime().getTime() - (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * 24 * 60 * 60 * 1000));
			break;
		case Calendar.SUNDAY:
			// cal.set (Calendar.DAY_OF_YEAR,
			// cal.get(Calendar.DAY_OF_YEAR) -
			// (Calendar.SATURDAY - Calendar.SUNDAY));
			newCal.setTime(
					new Date(cal.getTime().getTime() - (Calendar.SATURDAY - Calendar.SUNDAY) * 24 * 60 * 60 * 1000));
		}

		// System.out.println ("2 - Cperiod.getPeriodStartDateByWeekly (). date <"+
		// _formatter.format(newCal.getTime())+">");
		return newCal.getTime();
	}

	public static Date getPeriodStartDateByWeekly(Date date) {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.setTime(date);

		Calendar newCal = Calendar.getInstance();
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY: // first day in a week
		case Calendar.TUESDAY:
		case Calendar.WEDNESDAY:
		case Calendar.THURSDAY:
		case Calendar.FRIDAY:
		case Calendar.SATURDAY:
			// cal.set (Calendar.DAY_OF_YEAR,
			// cal.get(Calendar.DAY_OF_YEAR) -
			// (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY));
			newCal.setTime(new Date(
					cal.getTime().getTime() - (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY) * 24 * 60 * 60 * 1000));
			break;
		case Calendar.SUNDAY:
			// cal.set (Calendar.DAY_OF_YEAR,
			// cal.get(Calendar.DAY_OF_YEAR) -
			// (Calendar.SATURDAY - Calendar.SUNDAY));
			newCal.setTime(
					new Date(cal.getTime().getTime() - (Calendar.SATURDAY - Calendar.SUNDAY) * 24 * 60 * 60 * 1000));
		}

		// System.out.println ("2 - Cperiod.getPeriodStartDateByWeekly (). date <"+
		// _formatter.format(newCal.getTime())+">");
		return newCal.getTime();
	}

	public static Date getPeriodStartDateForLessNearest(String dateStr) throws ParseException, Exception {
		//
		java.util.Date currDate = getFormat().parse(getFormat().format(new Date()));
		java.util.Date givenDate = Aperiod.getPeriodStartDateByQuarterly(dateStr);

		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);

		cal.setTime(getFormat().parse(dateStr));

		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(Aperiod.getPeriodStartDateByQuarterly(currDate));
		cal2.add(Calendar.DAY_OF_YEAR, -45);

		if (cal.getTime().after(cal2.getTime())) {
			cal = cal2;
		} else {
			cal.add(Calendar.MONTH, -6);
		}

		return Aperiod.getPeriodStartDate(getFormat().format(cal.getTime()), EperiodType.QUARTERLY);
	}

	public static Date getPeriodStartDateByQuarterly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);

		switch (cal.get(Calendar.MONTH)) {
		case Calendar.NOVEMBER: // first month in the quarter
		case Calendar.FEBRUARY:
		case Calendar.MAY:
		case Calendar.AUGUST:
			break;
		case Calendar.DECEMBER: // second month in the quarter
		case Calendar.MARCH:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			break;
		case Calendar.JANUARY: // last month in the quarter
			cal.set(Calendar.MONTH, Calendar.NOVEMBER);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
			break;
		case Calendar.APRIL:
		case Calendar.JULY:
		case Calendar.OCTOBER:
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);
		}

		// System.out.println ("Cperiod.getPeriodStartDateByQuarterly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static Date getPeriodStartDateByQuarterly(Date date) {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setTime(date);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		switch (cal.get(Calendar.MONTH)) {
		case Calendar.NOVEMBER: // first month in the quarter
		case Calendar.FEBRUARY:
		case Calendar.MAY:
		case Calendar.AUGUST:
			break;
		case Calendar.DECEMBER: // second month in the quarter
		case Calendar.MARCH:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			break;
		case Calendar.JANUARY: // last month in the quarter
			cal.set(Calendar.MONTH, Calendar.NOVEMBER);
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
			break;
		case Calendar.APRIL:
		case Calendar.JULY:
		case Calendar.OCTOBER:
			cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 2);
		}

		// System.out.println ("Cperiod.getPeriodStartDateByQuarterly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static Date getPeriodStartDateByDaily(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}

		// System.out.println ("Cperiod.getPeriodStartDateByDaily (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static CperiodDatesWithQueryDateImpl[] getPeriodStartDates(String[] dateList) {
		Vector list = new Vector();

		for (int i = 0; i < dateList.length; i++) {
			if (dateList[i].trim().length() > 0) {
				CperiodDatesWithQueryDateImpl date = new CperiodDatesWithQueryDateImpl();
				date.queryDate = dateList[i];

				date.startOnDates = new String[] { "", "", "" };
				try {
					date.startOnDates[0] = getFormat().format(getPeriodStartDate(dateList[i], EperiodType.WEEKLY));
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					date.startOnDates[1] = getFormat().format(getPeriodStartDate(dateList[i], EperiodType.MONTHLY));
				} catch (Exception e) {
					e.printStackTrace();
				}

				try {
					date.startOnDates[2] = getFormat().format(getPeriodStartDate(dateList[i], EperiodType.QUARTERLY));
				} catch (Exception e) {
					e.printStackTrace();
				}

				list.addElement(date);
			}
		}

		CperiodDatesWithQueryDateImpl[] ret = new CperiodDatesWithQueryDateImpl[list.size()];

		list.copyInto(ret);

		return ret;
	}

	public static Date[] getAscendingDates(String[] dateList) {
		Vector list = new Vector();

		for (int i = 0; i < dateList.length; i++) {
			try {
				list.addElement(getPeriodStartDateByDaily(dateList[i]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		java.util.Collections.sort(list);

		Date[] ret = new Date[list.size()];

		list.copyInto(ret);

		// showDates (ret);
		return ret;
	}

	private static void showDates(Date[] dateList) {
		System.out.println("\n\nAperiod.showDates ().");
		for (int i = 0; i < dateList.length; i++)
			System.out.println("\tindex <" + i + ">  date <" + dateList[i].getTime() + ">\n");

		System.out.println("\n");
	}
	//

	public static void main(String argv[]) throws Exception {
		String date = "2021/07/11";
		System.out.println(getPeriodEndDate(date, EperiodType.WEEKLY));
		System.out.println(getPeriodEndDate(date, EperiodType.MONTHLY));
		System.out.println(getPeriodEndDate(date, EperiodType.QUARTERLY));

		CperiodDatesWithQueryDateImpl[] impl = getPeriodStartDates(new String[] { "2021/09/24" });

		SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");

		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");

		Calendar c = Calendar.getInstance();
		String[] c4CostDates = new String[3];
		for (CperiodDatesWithQueryDateImpl cperiodDatesWithQueryDateImpl : impl) {
			System.out.println("Test:" + cperiodDatesWithQueryDateImpl.queryDate + ":"
					+ Arrays.toString(cperiodDatesWithQueryDateImpl.startOnDates));

			c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[0]));
			c4CostDates[0] = sdf.format(c.getTime());

			System.out.println(sdf.format(c.getTime()));
			c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[1]));
			c4CostDates[1] = sdf.format(c.getTime());

			System.out.println(sdf.format(c.getTime()));
			c.setTime(sdf1.parse(cperiodDatesWithQueryDateImpl.startOnDates[2]));
			c4CostDates[2] = sdf.format(c.getTime());

			System.out.println(sdf.format(c.getTime()));

		}

		StringBuilder dates = new StringBuilder();
		for (String date1 : c4CostDates) {
			dates.append(date1 + ",");

		}
		System.out.println(
				dates.toString().length() > 0 ? dates.toString().substring(0, dates.toString().length() - 1) : "");
	}

	// calculate end dates..
	public static Date getPeriodEndDate(String date, EperiodType type) throws Exception {
		Date ret = null;
		if (type.equals(EperiodType.MONTHLY))
			ret = getPeriodEndDateByMonthly(date);
		else if (type.equals(EperiodType.WEEKLY))
			ret = getPeriodEndDateByWeekly(date);
		else if (type.equals(EperiodType.QUARTERLY))
			ret = getPeriodEndDateByQuarterly(date);
		else
			throw new Exception(
					"Cperiod.getPeriodStartDate() - invalid period type. type.toString <" + type.toString() + ">");

		return ret;
	}

	public static Date getPeriodEndDateByMonthly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DATE, -1);
		// System.out.println ("Cperiod.getPeriodStartDateByMonthly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();
	}

	public static Date getPeriodEndDateByWeekly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}
		Calendar newCal = Calendar.getInstance();
		int days = 0;
		// used to calculate how many days must be added to get to the nearest sunday.
		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			days++;
		case Calendar.TUESDAY:
			days++;
		case Calendar.WEDNESDAY:
			days++;
		case Calendar.THURSDAY:
			days++;
		case Calendar.FRIDAY:
			days++;
		case Calendar.SATURDAY:
			days++;
		case Calendar.SUNDAY:
		}
		cal.add(Calendar.DATE, days);
		return cal.getTime();
	}

	public static Date getPeriodEndDateByQuarterly(String dateStr) throws ParseException {
		//
		Calendar cal = Calendar.getInstance();
		cal.setLenient(true);
		try {
			cal.setTime(getFormat().parse(dateStr));
		} catch (ParseException e) {
			throw e;
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int months = 0;
		switch (cal.get(Calendar.MONTH)) {
		case Calendar.NOVEMBER: // first month in the quarter
		case Calendar.FEBRUARY:
		case Calendar.MAY:
		case Calendar.AUGUST:
			months = 3;
			break;
		case Calendar.DECEMBER: // second month in the quarter
		case Calendar.MARCH:
		case Calendar.JUNE:
		case Calendar.SEPTEMBER:
			months = 2;
			break;
		case Calendar.JANUARY: // last month in the quarter
		case Calendar.APRIL:
		case Calendar.JULY:
		case Calendar.OCTOBER:
			months = 1;
		}
		cal.set(Calendar.DATE, 1);
		cal.add(Calendar.MONTH, months);
		cal.add(Calendar.DATE, -1);

		// System.out.println ("Cperiod.getPeriodStartDateByQuarterly (). date <"+
		// _formatter.format(cal.getTime())+">");
		return cal.getTime();

	}

}