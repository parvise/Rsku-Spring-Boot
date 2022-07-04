/**
 * <p>Title: SFTPUtil</p>
 * <p>Description: This class is used to do Secure FTP across HP networks using SSH2.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: HP</p>
 * @author Santosh
 * @version 1.0
 *
 * Modified By/On  : Santosh on Sept 07 2006
 *
 */

package com.hp.c4.rsku.rSku.security.server.util;

import java.util.Iterator;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.UserInfo;

public class SFTPUtil {
	public static final String SFTP_PVT_KEY = "c4.sftp.key";
	private static final Logger mLogger = LogManager.getLogger(SFTPUtil.class);

	String ftpUser = null;
	String ftpPass = null;
	String ftpHost = null;
	String sftpKey = null;

	// Standard port for SFTP is 22

	static final int FTP_PORT = 22;

	Session session = null;
	Channel channel = null;
	ChannelSftp c = null;
	JSch jsch = null;

	/**
	 * Constructor Initializes ftp parameters.
	 * 
	 * @param String ftpUser - User, String ftpPass - password, String ftpHost -
	 *               hostname
	 **/
	public SFTPUtil(String ftpUser, String ftpPass, String ftpHost) {
		sftpKey = System.getProperty(SFTP_PVT_KEY);
		mLogger.info("SFTP Key::" + sftpKey);

		this.ftpUser = ftpUser;
		this.ftpPass = ftpPass;
		this.ftpHost = ftpHost;
		jsch = new JSch();
	}

	/**
	 * This method connects to the specified host
	 * 
	 * @return: boolean true/ false
	 * @throws: Exception
	 **/
	public boolean connect() throws Exception {
		try {
			mLogger.info("Connecting..Please wait.." + sftpKey);
			mLogger.info("Using the Host: '" + ftpHost + "' and User: '" + ftpUser + "'");

			jsch.addIdentity(sftpKey, "passphrase");
			session = jsch.getSession(ftpUser, ftpHost, FTP_PORT);
			UserInfo ui = new MyUserInfo();
			session.setUserInfo(ui);
			session.connect();

			channel = session.openChannel("sftp");

			channel.connect();
			c = (ChannelSftp) channel;
			mLogger.info("Connected..");

			return true;
		} catch (Exception e) {
			mLogger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * This method used to change the directory as per the specified path
	 * 
	 * @param: String path
	 * @return: boolean true/ false
	 * @throws: Exception
	 **/
	public boolean changeDirectory(String path) throws Exception {
		try {
			c.cd(path);
			mLogger.info("Successful in cd to " + path);
			return true;
		} catch (SftpException e) {
			mLogger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * This method used to put the file from given source dir to destination dir
	 * 
	 * @param: String srcPath - Source, String destPath - destination
	 * @return: boolean true/ false
	 * @throws: Exception
	 **/
	public boolean putFile(String srcPath, String destPath) throws Exception {
		try {
			if (destPath == null || destPath.equals(""))
				destPath = ".";
			int mode = ChannelSftp.OVERWRITE;
			c.put(srcPath, destPath, null, mode);
			mLogger.info("Success in put from " + srcPath + " to " + destPath);
			return true;
		} catch (SftpException e) {
			mLogger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * It checks for the presence of the RAS FYE file in the staging dir.
	 * 
	 * @param dirpath
	 * @return
	 * @throws Exception
	 */
	public boolean chkFyeFile(String dirpath) throws Exception {
		boolean result = false;
		try {
			int mode = ChannelSftp.OVERWRITE;
			String currdir = c.lpwd();
			mLogger.info("*** currdir ==== " + currdir);
			mLogger.info("*** dirpath ==== " + dirpath);
			Vector fls = c.ls(dirpath);
			Iterator itr = fls.iterator();
			while (itr.hasNext()) {
				LsEntry les = (LsEntry) itr.next();
				String flname = les.getFilename();
				mLogger.info("*** flname ==== " + flname);
				if (flname.indexOf("FYE") > -1) {
					mLogger.info("*** It is FYE File");
					result = true;
				}
			}
			mLogger.info("*** result ==== " + result);
			return result;
		} catch (Exception e) {
			mLogger.error("Error in checking the presence of FYE file --- " + e.getMessage());
			throw e;
		}
	}

	/**
	 * This method used to get the file from given source dir to destination dir
	 * 
	 * @param: String srcPath - Source, String destPath - destination
	 * @return: boolean true/ false
	 * @throws: Exception
	 **/

	public boolean getFile(String srcPath, String destPath) throws Exception {
		try {
			if (destPath == null || destPath.equals(""))
				destPath = ".";
			int mode = ChannelSftp.OVERWRITE;
			c.get(srcPath, destPath, null, mode);
			return true;
		} catch (SftpException e) {
			mLogger.error(e.getMessage());
			throw e;
		}
	}

	/**
	 * This method dis-connects from the specified host
	 * 
	 * @return: boolean true/ false
	 * @throws: Exception
	 **/
	public boolean disConnect() throws Exception {
		try {
			if (session != null)
				session.disconnect();
			c.quit();
			mLogger.info("Disconnected..");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * This is a generic class to be required by the SFTP and is VO to store all FTP
	 * user/host related information.
	 **/
	public static class MyUserInfo implements UserInfo {
		String passwd;
		// String passphrase;

		/*
		 * public MyUserInfo(String passwd) { this.passwd = passwd ; }
		 */

		/*
		 * public MyUserInfo() { System.out.println("Inside constructor of MyUserInfo");
		 * }
		 */

		public String getPassword() {
			return null;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return false;
		}

		public void showMessage(String message) {
		}
	}

	/**
	 * 
	 * @param dirpath
	 * @return
	 * @throws Exception
	 */
	public Vector getFilesFromPath(String dirpath) throws Exception {

		mLogger.info("Im from getFilesFromPath method: " + dirpath);
		Vector xmlFilesVector = new Vector();
		try {
			int mode = ChannelSftp.OVERWRITE;
			String currdir = c.lpwd();
			mLogger.info("*** currdir ==== " + currdir);
			mLogger.info("*** dirpath ==== " + dirpath);
			Vector fls = c.ls(dirpath);
			Iterator itr = fls.iterator();
			while (itr.hasNext()) {
				LsEntry les = (LsEntry) itr.next();
				String flname = les.getFilename();
				mLogger.info("*** flname ==== " + flname);
				if (flname.indexOf(UtilConstants.HEADER_EXTENSION) > 0) {
					xmlFilesVector.add(flname);
				}
			}
			mLogger.info("*** xmlFilesVector ==== " + xmlFilesVector);
			return fls;
		} catch (Exception e) {
			mLogger.error("Error in checking the presence of FYE file --- " + e.getMessage());
			throw e;
		}
	}

	/**
	 * test main class..
	 **/
	public static void main(String ar[]) {
		try {
			SFTPUtil ftp = new SFTPUtil("c4sa", "Batch$1136c", "impitg.inc.hpicorp.net");
			ftp.connect();
			ftp.changeDirectory("/c4/apps");
			ftp.putFile("santosh.txt", null);
			ftp.disConnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
