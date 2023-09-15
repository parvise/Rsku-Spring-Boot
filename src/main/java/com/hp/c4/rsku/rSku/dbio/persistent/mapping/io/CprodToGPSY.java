package com.hp.c4.rsku.rSku.dbio.persistent.mapping.io;

/**
 * Title: C4 Description: C4 Copyright: Copyright (c) 2001 Company: HP
 * 
 * @author
 * @version 1.0
 */

public class CprodToGPSY {
	private static final int totalLength = 18;
	private static final int prodOffSet = 0;
	private static final int optOffSet = 12;
	private static final int spnOffSet = 15;
	private static final int prodLength = 12;
	private static final int optLength = 3;
	private static final int spnLength = 3;

	private String _gpsyId = "";

	public CprodToGPSY() {
	}

	/*
	 * public CprodToGPSY(String prodId, String opt, String spn) { _gpsyId =
	 * createGPSYID(prodId, opt, spn); } // gpsyProdID.substring (0, 12), //
	 * gpsyProdID.substring (12, 15), // gpsyProdID.substring (15, 18),
	 * 
	 * 
	 * public String createGPSYID(String prodId, String opt, String spn){
	 * StringBuffer sb = new StringBuffer(); sb.setLength(totalLength); for(int i=0;
	 * i< totalLength; i++) sb.setCharAt(i, ' '); sb.insert(prodOffSet,
	 * trimInput(prodId, prodLength)); sb.insert(optOffSet, trimInput(opt,
	 * optLength)); sb.insert(spnOffSet, trimInput(spn, spnLength)); return
	 * sb.toString(); }
	 * 
	 * private String trimInput(String str, int length){ if(str.length() < length)
	 * length = str.length(); return str.substring(0, length) ; }
	 * 
	 * public String getGPSYID() { return _gpsyId;}
	 * 
	 * public void setGPSYID(String prodId, String opt, String spn){ _gpsyId =
	 * createGPSYID(prodId, opt, spn); }
	 */
	public static String createGPSYID(String prodId, String opt, String spn) {
		StringBuffer sb = new StringBuffer();
		sb.setLength(totalLength);
		for (int i = 0; i < totalLength; i++)
			sb.setCharAt(i, ' ');
		sb.insert(prodOffSet, trimInput(prodId, prodLength));
		if (opt == null)
			opt = "";

		sb.insert(optOffSet, trimInput(opt, optLength));
		if (spn == null)
			spn = "";
		sb.insert(spnOffSet, trimInput(spn, spnLength));
		return sb.toString().trim();
	}

	private static String trimInput(String str, int length) {
		if (str != null && str.length() < length)
			length = str.trim().length();
		return str.substring(0, length);
	}

	private static int getLength(String str) {
		int retValue = 0;
		if (str != null || str.trim().length() > 0)
			retValue = str.length();
		return retValue;
	}

	public static void main(String argv[]) {
		String temp = CprodToGPSY.createGPSYID("J7196A", "   ", "   ");
		System.out.println(temp + " " + temp.length());

	}
}
