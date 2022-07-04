package com.hp.c4.rsku.rSku;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

public class SftpUtil {

	private static final Logger mLogger = LogManager.getLogger(SftpUtil.class);

	final static SftpProgressMonitor monitor = new SftpProgressMonitor() {
		public void init(final int op, final String source, final String target, final long max) {
			mLogger.info("sftp start uploading file from:" + source + " to:" + target);
		}

		public boolean count(final long count) {
			mLogger.debug("sftp sending bytes: " + count);
			return true;
		}

		public void end() {
			mLogger.info("sftp uploading is done.");
		}
	};

	public static void main(String args[]) {

		String pls = new String(
				"T,16,19,1D,1M,1N,21,2A,2B,2C,2D,2G,2H,2N,2P,2Q,30,3Y,4H,4L,4M,4V,4X,52,5D,5M,5S,5T,5U,5X,63,64,65,67,6A,6B,6J,6Q,6U,6V,6X,7B,7F,7S,7T,8A,8J,8N,8W,91,9C,9F,9G,9H,9J,9R,9S,9T,A5,AK,AN,AU,B7,BO,BQ,C5,CY,DA,DG,DL,DU,DY,E0,E4,E5,ED,EE,EO,EZ,F2,F4,F7,F8,FB,FD,FF,FG,FH,FO,FW,G0,G7,G8,G9,GA,GB,GC,GD,GE,GF,GG,GH,GI,GJ,GK,GL,GM,GN,GO,GP,GQ,GR,GS,GT,GU,GV,GW,GX,GY,GZ,HF,HQ,I0,I1,I2,IA,IB,ID,IE,IF,IG,IL,IM,IQ,IR,IS,IT,IU,IV,IW,IX,IY,IZ,JP,JZ,K2,K4,K5,K6,K7,K8,KV,LY,M3,M8,MA,MC,MF,MG,MK,ML,MN,MP,MQ,PQ,R4,R6,R7,SB,ST,T2,T4,TA,TB,TW,TX,TY,UD,UI,UK,UN,UO,UR,US,UT,UV,VG,VI,VJ,VK,WS");

		StringTokenizer token = new StringTokenizer(pls, ",");
		List<String> pl = new ArrayList<String>();

		while (token.hasMoreTokens()) {
			token.countTokens();
			pl.add(token.nextToken());
		}
		System.out.println(token.countTokens() + ":" + pl.size());
		Random random = new Random();

		int randomNum = random.ints(1, pl.size()).findFirst().getAsInt();

		System.out.println(pl.get(randomNum - 1));

	}

	public static void putFile(String hostname, String username, String password, String copyFrom, String copyTo)
			throws JSchException, SftpException {
		mLogger.info("Initiate sending file to Linux Server...");
		JSch jsch = new JSch();
		Session session = null;
		mLogger.info("Trying to connect.....");
		// jsch.addIdentity("D:/Pervez/C4/C4CostFiles/id_dsa.txt", "passphrase");
		session = jsch.getSession(username, hostname, 22);
		session.setConfig("StrictHostKeyChecking", "no");
		session.setPassword(password);
		session.connect();
		mLogger.info("is server connected? " + session.isConnected());

		Channel channel = session.openChannel("exec");

		((ChannelExec) channel).setCommand("pbrun su - c4sa");
		channel = session.openChannel("sftp");

		channel.connect();
		ChannelSftp sftpChannel = (ChannelSftp) channel;
		mLogger.info("Server's home directory: " + sftpChannel.getHome());
		// sftpChannel.chown(uid, path);

		// ((ChannelExec)channel).setCommand("pbrun su - c4sa");

		try {
			sftpChannel.put(copyFrom, copyTo, monitor, ChannelSftp.OVERWRITE);
		} catch (SftpException e) {
			mLogger.error("file was not found: " + copyFrom);
		}

		if (session != null)
			session.disconnect();
		sftpChannel.quit();

		mLogger.info("Finished sending file to Linux Server...");

	}

	public static File createTestFile(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return file;
		}
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write("This is ");
			fileWriter.write("a test");
			fileWriter.write("\n");
			fileWriter.write("Ibrahim Sawalha.");
			fileWriter.flush();
			fileWriter.close();
			return file;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}