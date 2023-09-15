package com.hp.c4.rsku.rSku.rest.epam.api;

import java.io.IOException;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.epam.pwd.cache.Client;
import com.hp.c4.epam.pwd.cache.Client.APIException;
import com.hp.c4.epam.pwd.cache.helper.Json;
import com.hp.c4.epam.pwd.cache.helper.JsonObject;

import sun.misc.BASE64Decoder;

public class C4EpamApi {

	private static final Logger mLogger = LogManager.getLogger(C4EpamApi.class);

	public static String getEpamApiPaswword(Map<String, String> epamAPIMap) throws APIException {

		// System.setProperty("javax.net.ssl.trustStorePassword",
		// EPAM_API_MAP.get("EPAM_C4_API_KEY_STORE_PWD"));
		System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
		System.setProperty("javax.net.ssl.trustStore", epamAPIMap.get("EPAM_C4_API_KEY_STORE_FILE"));
		System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");

		// Password decoding from Encode password from Properties File
		String epamApiKeyDecode = null;
		// epamApiKeyDecode = new String(epamAPIMap.get("EPAM_C4_API_KEY"));

		byte[] decodeResult;
		try {
			decodeResult = new BASE64Decoder().decodeBuffer(epamAPIMap.get("EPAM_C4_API_KEY"));
			epamApiKeyDecode = new String(decodeResult);
		} catch (IOException e) {
			mLogger.error("Error occured at Decode th DB passowrds");
		}
		mLogger.info(
				"*****************************************************************************************************");

		Client client = null;
		try {
			client = new Client(epamAPIMap.get("EPAM_C4_API_HOST_NAME_PWD_CACHE"));
		} catch (APIException e) {
			mLogger.error("Exception occured at EPAM Service calls" + e.getMessage() + ": Failover at := "
					+ epamAPIMap.get("EPAM_C4_API_HOST_NAME_PWD_CACHE"));
			try {
				client = new Client(epamAPIMap.get("EPAM_C4_API_HOST_NAME_LLB"));
			} catch (APIException sub) {
				mLogger.error("Exception occured at EPAM Service calls" + sub.getMessage() + ": Failover at := "
						+ epamAPIMap.get("EPAM_C4_API_HOST_NAME_LLB"));
				return null;
			}
		}
		mLogger.info("Connecting to External API...... For EPAM Authentication........Started ...." + epamAPIMap);
		String jsonUserObjectStr;
		try {
			// Sign In
			jsonUserObjectStr = client.signAppIn(epamAPIMap.get("EPAM_C4_API_RUN_AS_USER"), epamApiKeyDecode);

		} catch (APIException e) {
			mLogger.error("Exception occured at EPAM Service calls" + e.getMessage() + ": Failover at := "
					+ epamAPIMap.get("EPAM_C4_API_HOST_NAME_PWD_CACHE"));
			try {
				client = new Client(epamAPIMap.get("EPAM_C4_API_HOST_NAME_LLB"));
				jsonUserObjectStr = client.signAppIn(epamAPIMap.get("EPAM_C4_API_RUN_AS_USER"), epamApiKeyDecode);
			} catch (APIException sub) {
				mLogger.error("Exception occured at EPAM Service calls" + sub.getMessage() + ": Failover at := "
						+ epamAPIMap.get("EPAM_C4_API_HOST_NAME_LLB"));
				return null;
			}
		}
		JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
		mLogger.info("Signed in as " + joUser.getString("UserName", ""));
		// Find Account
		String jsonAccount = client.getManagedAccountByName(epamAPIMap.get("EPAM_SYSTEM_NAME"),
				epamAPIMap.get("EPAM_ACCOUNT_NAME"));
		JsonObject joAccount = Json.parse(jsonAccount).asObject();
		mLogger.info("Found System|Account: " + joAccount.getString("SystemName", "") + "|"
				+ joAccount.getString("AccountName", ""));

		// Request (for 5 minutes)
		int accountID = joAccount.getInt("AccountId", 0);
		int systemID = joAccount.getInt("SystemId", 0);
		int durationInMinutes = 5;
		String reason = "Java Sample";
		String conflict = "reuse";
		String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason, conflict);
		int reqID = Json.parse(jsonRequest).asInt();
		mLogger.info(String.format("Request ID: %d", reqID));

		// Get Credentials
		String jsonCreds = client.retrievePassword(reqID);
		String pwd = Json.parse(jsonCreds).asString();
		mLogger.info("---");
		mLogger.info(pwd);
		mLogger.info("---");

		// Checkin Request
		client.checkinRequest(reqID, "Done Java Sample");
		mLogger.info("Request checked-in");

		// Sign Out
		client.signOut();
		mLogger.info("Signed out");

		mLogger.info(
				"*****************************************************************************************************");
		return pwd;

	}

}
