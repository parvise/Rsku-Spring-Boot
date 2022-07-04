/**
 * C4LoaderBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis WSDL2Java emitter.
 */

package com.hp.c4.rsku.rSku.security.server.util.icost;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class C4LoaderBindingImpl implements C4LoaderPortType, C4LoaderConstants {
	private static final String DATA_EXTENSION = ".dat";
	private static final String HEADER_EXTENSION = ".xml";
	private static final String SEPARATOR = "|";
	private static final String EOF = "EOF::EOF|||||||||||||||||||||||||";
	private static final int MAX_SEQUENCE_NUM = 99;

	/**
	 * Sets the header data and returns the file name, minus the extension<br>
	 * The file name format is A_B_CCYYMMDD_NN_SS.xml where<br>
	 * <li>NN is Sequence Number that has to be determined in order to create a
	 * valid file name
	 * <li>SS is the Segment Number
	 * 
	 * @param aDirectory      the directory in which the header data file has to be
	 *                        created. - The directory path must end with the file
	 *                        separator.
	 * @param aFileNamePrefix prefix of the file name right until the sequence
	 *                        number
	 * @param aSegment        the Segment Number
	 * @param anHeader        Header Data to be stored
	 * @return file name if the header values are set successfully <br>
	 *         an error message if not
	 * @throws java.rmi.RemoteException
	 */
	public java.lang.String setHeaderTemp(java.lang.String aDirectory, java.lang.String aFileNamePrefix,
			java.lang.String aSegment, Cheader anHeader) throws java.rmi.RemoteException {
		System.out.println("setHeader()");
		/**
		 * determine the next available sequence number and set header data
		 */
		String tNextSeqNum = null;
		String tFileName = null;
		try {
			tNextSeqNum = getNextSequenceNumber(aDirectory, aFileNamePrefix, aSegment);
			tFileName = aFileNamePrefix + tNextSeqNum + "_" + aSegment;

			// set the header data and account for error cases
			int headerRetVal = setHeader(aDirectory + tFileName, anHeader);
			if (headerRetVal == FILE_EXISTS) {
				// this shouldn't be happening. something's wrong here
				throw new Exception("Header file with name:" + tFileName + HEADER_EXTENSION + " already exists");
			} else if (headerRetVal == UNKNOWN) {
				// some error occured while setting header values
				throw new Exception(
						"Some unknown error occured while setting header values:" + tFileName + HEADER_EXTENSION);
			}

			System.out.println("Header Data set successfully");

		} catch (Exception ex) {
			ex.printStackTrace(System.out);
			return "ERROR:" + ex.getMessage();
		}

		return tFileName;
	}

	public int setHeader(java.lang.String aFileName, Cheader anHeader) throws java.rmi.RemoteException {
		FileWriter res = null;
		BufferedWriter buffer = null;

		final File file = new File(aFileName + HEADER_EXTENSION);

		if (file.exists()) { // the file exist already
			return FILE_EXISTS;
		}

		try {
			res = new FileWriter(aFileName + HEADER_EXTENSION);
			buffer = new BufferedWriter(res);
			pushXMLHeader(buffer);
			pushDTD(buffer);
			pushContent(buffer, anHeader);
			System.out.println(res.toString());
			return OK;
		} catch (Exception e) {
			e.printStackTrace();
			return UNKNOWN;
		} finally {
			try {
				buffer.flush();
				buffer.close();
				res.close();
			} catch (Exception e) {
			}
		}
	}

	public int setCosFullData(java.lang.String aFileName, CcosCost[] aCostList, boolean isLast)
			throws java.rmi.RemoteException {
		System.out.println("setCosFullData()");
		return setCosData(aFileName, aCostList, isLast, false);
	}

	public int setCosPartialData(java.lang.String aFileName, CcosCost[] aCostList, boolean isLast)
			throws java.rmi.RemoteException {
		System.out.println("setCosPartialData()");
		return setCosData(aFileName, aCostList, isLast, true);
	}

	public int setOpExpFullData(java.lang.String aFileName, CopExpCost[] aCostList, boolean isLast)
			throws java.rmi.RemoteException {
		return setOpExpData(aFileName, aCostList, isLast, false);
	}

	public int setOpExpPartialData(java.lang.String aFileName, CopExpCost[] aCostList, boolean isLast)
			throws java.rmi.RemoteException {
		return setOpExpData(aFileName, aCostList, isLast, true);
	}

	private int setOpExpData(java.lang.String aFileName, CopExpCost[] aCostList, boolean isLast, boolean isPartial)
			throws java.rmi.RemoteException {
		FileWriter res = null;
		BufferedWriter buffer = null;
		try {
			res = new FileWriter(aFileName + DATA_EXTENSION, true);
			buffer = new BufferedWriter(res);
			pushLines(buffer, aCostList, isPartial);
			if (isLast) {
				pushEOF(buffer);
			}
			return OK;
		} catch (Exception e) {
			e.printStackTrace();
			return UNKNOWN;
		} finally {
			try {
				buffer.flush();
				buffer.close();
				res.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * sets cost of sale data
	 * 
	 * @param pFileName
	 * @param pCosCostList
	 * @param isLast
	 * @param isPartial
	 * @return OK or UNKNOWN depending on whether cos data was set successfully or
	 *         not
	 * @throws java.rmi.RemoteException
	 */
	private int setCosData(java.lang.String pFileName, CcosCost[] pCosCostList, boolean isLast, boolean isPartial)
			throws java.rmi.RemoteException {

		FileWriter res = null;
		BufferedWriter tBuffer = null;
		try {
			res = new FileWriter(pFileName + DATA_EXTENSION, true);
			tBuffer = new BufferedWriter(res);
			pushCosLines(tBuffer, pCosCostList, isPartial);
			if (isLast) {
				pushEOF(tBuffer);
			}
			return OK;
		} catch (Exception e) {
			e.printStackTrace();
			return UNKNOWN;
		} finally {
			try {
				tBuffer.flush();
				tBuffer.close();
				res.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * returns the next available sequence number for a given file prefix and
	 * segment number.
	 * 
	 * @param pDirectory      the directory path with path separator affixed at the
	 *                        end
	 * @param pFileNamePrefix
	 * @param pSegment
	 * @return the next available sequence number
	 * @throws Exception if the <code>MAX_SEQUENCE_NUM</code> value has already been
	 *                   reached
	 */
	private String getNextSequenceNumber(String pDirectory, String pFileNamePrefix, String pSegment) throws Exception {
		File[] tFiles = new File(pDirectory).listFiles();

		int tSequenceNumber = 0;
		String tRetValue = null;
		for (int i = 0; i < tFiles.length; i++) {
			File tFile = tFiles[i];
			// if the file is a directory, just move on
			if (tFile.isDirectory())
				continue;
			String tFileName = tFile.getName();

			if (tFileName.startsWith(pFileNamePrefix) && tFileName.endsWith("_" + pSegment + HEADER_EXTENSION)) {
				String seqNumString = tFileName.substring(pFileNamePrefix.length(), pFileNamePrefix.length() + 2);
				int tCurrSeqNo = Integer.parseInt(seqNumString);
				if (tCurrSeqNo >= MAX_SEQUENCE_NUM) {
					throw new Exception("Maximum Sequence Number has already been reached for given file name prefix:"
							+ pFileNamePrefix + " and Segment Number:" + pSegment);
				}
				if (tCurrSeqNo > tSequenceNumber)
					tSequenceNumber = tCurrSeqNo;
			}
		}
		// add one to this value and return its String equivalent
		tSequenceNumber++;
		if (tSequenceNumber < 10) {
			tRetValue = "0" + tSequenceNumber;
		} else {
			tRetValue = "" + tSequenceNumber;
		}
		System.out.println("Returning seq num:" + tRetValue);
		return tRetValue;

	}

	private void pushXMLHeader(BufferedWriter buffer) throws IOException {
		buffer.write("<?xml version=\"1.0\" standalone=\"yes\"?>");
		buffer.newLine();
		buffer.write("<?xml-stylesheet type=\"text/css\" href=\"c4import.css\"?>");
		buffer.newLine();
	}

	private void pushDTD(BufferedWriter buffer) throws IOException {
		buffer.write("<!DOCTYPE HEADER [");
		buffer.newLine();
		buffer.write("<!ELEMENT HEADER (INFORMATIONAL, DATA)>");
		buffer.newLine();
		buffer.write(
				"<!ELEMENT INFORMATIONAL (AUTHOR_NAME?, SENDER_NAME, DATA_SOURCE, DATE_CREATED, EFFECTIVE_DATE, BATCH_TYPE)>");
		buffer.newLine();
		buffer.write("<!ELEMENT AUTHOR_NAME (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT SENDER_NAME (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT DATA_SOURCE (PLS, REGIONS, NOTIFICATION_EMAIL)>");
		buffer.newLine();
		buffer.write("<!ELEMENT PLS (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT REGIONS (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT NOTIFICATION_EMAIL (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT DATE_CREATED (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT EFFECTIVE_DATE (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT BATCH_TYPE (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT DATA (BUCKET_ZERO, PERIOD_TYPE)>");
		buffer.newLine();
		buffer.write("<!ELEMENT BUCKET_ZERO (START_DATE)>");
		buffer.newLine();
		buffer.write("<!ELEMENT START_DATE (#PCDATA)>");
		buffer.newLine();
		buffer.write("<!ELEMENT PERIOD_TYPE (#PCDATA)>");
		buffer.newLine();
		buffer.write("]>");
		buffer.newLine();
	}

	private void pushContent(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<HEADER>");
		buffer.newLine();
		pushInformational(buffer, anHeader);
		pushData(buffer, anHeader);
		buffer.write("</HEADER>");
		buffer.newLine();
	}

	private void pushInformational(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<INFORMATIONAL>");
		buffer.newLine();
		pushAuthorName(buffer, anHeader);
		pushSenderName(buffer, anHeader);
		pushDataSource(buffer, anHeader);
		pushDateCreated(buffer, anHeader);
		pushEffectiveDate(buffer, anHeader);
		pushBatchType(buffer, anHeader);
		buffer.write("</INFORMATIONAL>");
		buffer.newLine();
	}

	private void pushAuthorName(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<AUTHOR_NAME>");
		buffer.write(anHeader.getAuthor());
		buffer.write("</AUTHOR_NAME>");
		buffer.newLine();
	}

	private void pushSenderName(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<SENDER_NAME>");
		buffer.write(anHeader.getSender());
		buffer.write("</SENDER_NAME>");
		buffer.newLine();
	}

	private void pushDataSource(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<DATA_SOURCE>");
		buffer.newLine();
		pushPls(buffer, anHeader);
		pushRegions(buffer, anHeader);
		pushNotificationMail(buffer, anHeader);
		buffer.write("</DATA_SOURCE>");
		buffer.newLine();
	}

	private void pushPls(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<PLS>");
		buffer.write(anHeader.getPls());
		buffer.write("</PLS>");
		buffer.newLine();
	}

	private void pushRegions(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<REGIONS>");
		buffer.write(anHeader.getRegions());
		buffer.write("</REGIONS>");
		buffer.newLine();
	}

	private void pushNotificationMail(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<NOTIFICATION_EMAIL>");
		buffer.write(anHeader.getEmail());
		buffer.write("</NOTIFICATION_EMAIL>");
		buffer.newLine();
	}

	private void pushDateCreated(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<DATE_CREATED>");
		buffer.write(anHeader.getCreationDate());
		buffer.write("</DATE_CREATED>");
		buffer.newLine();
	}

	private void pushEffectiveDate(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<EFFECTIVE_DATE>");
		buffer.write(anHeader.getEffectiveDate());
		buffer.write("</EFFECTIVE_DATE>");
		buffer.newLine();
	}

	private void pushBatchType(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<BATCH_TYPE>");
		buffer.write(anHeader.getBatchType());
		buffer.write("</BATCH_TYPE>");
		buffer.newLine();
	}

	private void pushData(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<DATA>");
		buffer.newLine();
		pushBucketZero(buffer, anHeader);
		pushPeriodType(buffer, anHeader);
		buffer.write("</DATA>");
		buffer.newLine();
	}

	private void pushBucketZero(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<BUCKET_ZERO>");
		buffer.newLine();
		pushStartDate(buffer, anHeader);
		buffer.write("</BUCKET_ZERO>");
		buffer.newLine();
	}

	private void pushStartDate(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<START_DATE>");
		buffer.write(anHeader.getStartDate());
		buffer.write("</START_DATE>");
		buffer.newLine();
	}

	private void pushPeriodType(BufferedWriter buffer, Cheader anHeader) throws IOException {
		buffer.write("<PERIOD_TYPE>");
		buffer.write(anHeader.getPeriodType());
		buffer.write("</PERIOD_TYPE>");
		buffer.newLine();
	}

	private void pushLines(BufferedWriter buffer, CopExpCost[] aCostList, boolean isPartial) throws IOException {
		for (int i = 0; i < aCostList.length; i++) {
			pushLine(buffer, aCostList[i], isPartial);
		}
	}

	private void pushLine(BufferedWriter buffer, CopExpCost aCost, boolean isPartial) throws IOException {
		buffer.write(aCost.getPl());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getPlatform());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getSubplatform());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getSellingMotion());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getWw());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getRegion());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getCountry());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getElementType());
		buffer.write(SEPARATOR);
		buffer.write(aCost.getPeriodStart());
		buffer.write(SEPARATOR);
		buffer.write("" + aCost.getCost());
		if (isPartial) {
			buffer.write(SEPARATOR);
			buffer.write(aCost.getAction());
		}
		buffer.newLine();
	}

	/**
	 * pushes cost of sale lines into the buffered writer
	 * 
	 * @param pBuffer
	 * @param pCosCostList
	 * @param pIsPartial
	 * @throws IOException
	 */
	private void pushCosLines(BufferedWriter pBuffer, CcosCost[] pCosCostList, boolean pIsPartial) throws IOException {
		for (int i = 0; i < pCosCostList.length; i++) {
			pushCosLine(pBuffer, pCosCostList[i], pIsPartial);
		}
	}

	/**
	 * pushes a cost of sale line into the buffered writer
	 * 
	 * @param pBuffer
	 * @param pCosCost
	 * @param pIsPartial
	 * @throws IOException
	 */
	private void pushCosLine(BufferedWriter pBuffer, CcosCost pCosCost, boolean pIsPartial) throws IOException {
		pBuffer.write(pCosCost.getProductId());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getOpt());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getMcc());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getSpn());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getWw());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getRegion());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getCountry());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getElementType());
		pBuffer.write(SEPARATOR);
		pBuffer.write(pCosCost.getPeriodStart());
		pBuffer.write(SEPARATOR);
		pBuffer.write("" + pCosCost.getCost());
		if (pIsPartial) {
			pBuffer.write(SEPARATOR);
			pBuffer.write(pCosCost.getAction());
		}
		pBuffer.newLine();
	}

	private void pushEOF(BufferedWriter buffer) throws IOException {
		buffer.write(EOF);
	}

	public static void main(String[] args) throws Exception {
		C4LoaderPortType port = new C4LoaderBindingImpl();

		Cheader header = new Cheader();
		header.setAuthor("Philippe Rodriguez");
		header.setSender("Rodriguez Philippe");
		header.setPls("7A");
		header.setRegions("WW");
		header.setEmail("yoyo@yoyo.com");
		header.setCreationDate("2002/12/31");
		header.setEffectiveDate("2003/02/01");
		header.setBatchType("Full");
		header.setPeriodType("Quarter");
		header.setStartDate("2003/01/01");

		// C_F_20211005_01_4M
		// C_F_20210905_01_4M.dat
		// C_F_20210905_01_4M.xml

		String sEffectiveDate = "2003/02/01 03:26:10";// default, if below operation fails.
		// convert effective date to importer timezone
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		Date effectiveDate = simpleDateFormat.parse(sEffectiveDate);

		simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

		System.out.println(simpleDateFormat.format(new Date("01-Aug-21")));

		try {
			sEffectiveDate = simpleDateFormat.format(effectiveDate);
			// at this point now effective date contains the date in importer timezone
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// at this point now effective date contains the date in GMT
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("CST"));

//	    chead.setEffectiveDate(sEffectiveDate)

		SimpleDateFormat sdfFilename = new SimpleDateFormat("yyyyMMdd");
		sdfFilename.setTimeZone(TimeZone.getTimeZone("CST"));
		// C_F_20211006_

		// build filename prefix & segment
		String sFileNamePrefix = "C_" + ("Full".equals("Full") ? "F" : "P") + "_" + sdfFilename.format(new Date())
				+ "_00_4M";

		final String fileName = "D:/Pervez/C4/C4CostFiles/" + sFileNamePrefix;

		port.setHeader(fileName, header);

		CopExpCost cost1 = new CopExpCost();
		CopExpCost cost2 = new CopExpCost();
		CopExpCost cost3 = new CopExpCost();
		CopExpCost cost4 = new CopExpCost();

		cost1.setPl("7A");
		cost1.setPlatform("");
		cost1.setSubplatform("");
		cost1.setSellingMotion("");
		cost1.setWw("WW");
		cost1.setRegion("");
		cost1.setCountry("");
		cost1.setElementType("VFSCP");
		cost1.setPeriodStart("2001/08/01");
		cost1.setCost((float) 1001.2);
		cost1.setAction("Insert");

		cost2.setPl("7A");
		cost2.setPlatform("");
		cost2.setSubplatform("");
		cost2.setSellingMotion("");
		cost2.setWw("WW");
		cost2.setRegion("");
		cost2.setCountry("");
		cost2.setElementType("FFSCP");
		cost2.setPeriodStart("2001/08/01");
		cost2.setCost((float) 1006.5);
		cost2.setAction("Insert");

		cost3.setPl("7A");
		cost3.setPlatform("");
		cost3.setSubplatform("");
		cost3.setSellingMotion("");
		cost3.setWw("WW");
		cost3.setRegion("");
		cost3.setCountry("");
		cost3.setElementType("VMRKT");
		cost3.setPeriodStart("2001/08/01");
		cost3.setCost((float) 1001.1);
		cost3.setAction("Insert");

		cost4.setPl("7A");
		cost4.setPlatform("");
		cost4.setSubplatform("");
		cost4.setSellingMotion("");
		cost4.setWw("WW");
		cost4.setRegion("");
		cost4.setCountry("");
		cost4.setElementType("FMRKT");
		cost4.setPeriodStart("2001/08/01");
		cost4.setCost((float) 6.5);
		cost4.setAction("Insert");

		CopExpCost[] list1 = new CopExpCost[2];
		CopExpCost[] list2 = new CopExpCost[2];

		list1[0] = cost1;
		list1[1] = cost2;
		list2[0] = cost3;
		list2[1] = cost4;

		port.setOpExpFullData(fileName, list1, false);
		port.setOpExpFullData(fileName, list2, true);
	}
}