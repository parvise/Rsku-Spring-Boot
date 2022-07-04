package com.hp.c4.rsku.rSku.rest.epam.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hp.c4.rsku.rSku.rest.epam.api.Client.APIException;
import com.hp.c4.rsku.rSku.rest.epam.api.json.Json;
import com.hp.c4.rsku.rSku.rest.epam.api.json.JsonObject;

import sun.misc.BASE64Decoder;

public class C4EpamAPIPassword {

	private static final Logger mLogger = LogManager.getLogger(C4EpamAPIPassword.class);

	private static Map<String, String> EPAM_API_MAP = new HashMap<String, String>();

	private static String EPAM_C4_API_HOST_NAME = "epam.api.host.name";
	private static String EPAM_C4_API_KEY = "epam.api.key";
	private static String EPAM_C4_API_KEY_STORE_PWD = "epam.api.keystore.password";
	private static String EPAM_C4_API_KEY_STORE_FILE = "epam.api.keystore.file.location";
	private static String EPAM_C4_API_RUN_AS_USER = "epam.api.runas.user";

	static Properties prop = new Properties();
	private static final String PATH = "../epam_config.properties";

	static {
		try {
			prop.load(new FileInputStream(PATH));
			mLogger.info("EPam Property File Path : "+PATH);
			Enumeration<Object> enum0 = prop.keys();
			while (enum0.hasMoreElements()) {
				String key = (String) enum0.nextElement();
				mLogger.info("TEsting.." + key + ":" + prop.getProperty(key));
			}

			EPAM_API_MAP.put("EPAM_C4_API_KEY", prop.getProperty(EPAM_C4_API_KEY));
			EPAM_API_MAP.put("EPAM_C4_API_RUN_AS_USER", prop.getProperty(EPAM_C4_API_RUN_AS_USER));
			EPAM_API_MAP.put("EPAM_C4_API_HOST_NAME", prop.getProperty(EPAM_C4_API_HOST_NAME));

			EPAM_API_MAP.put("EPAM_C4_API_KEY_STORE_PWD", prop.getProperty(EPAM_C4_API_KEY_STORE_PWD));
			EPAM_API_MAP.put("EPAM_C4_API_KEY_STORE_FILE", prop.getProperty(EPAM_C4_API_KEY_STORE_FILE));

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getEpamApiPaswword(Map<String, String> epamAPIMap) throws APIException {

		System.setProperty("javax.net.ssl.trustStorePassword", EPAM_API_MAP.get("EPAM_C4_API_KEY_STORE_PWD"));
		System.setProperty("javax.net.ssl.trustStore", EPAM_API_MAP.get("EPAM_C4_API_KEY_STORE_FILE"));

		epamAPIMap.putAll(EPAM_API_MAP);
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
		Client client = new Client(epamAPIMap.get("EPAM_C4_API_HOST_NAME"));
		mLogger.info("Connecting to External API...... For EPAM Authentication........Started ...." + epamAPIMap);

		// Sign In
		String jsonUserObjectStr = client.signAppIn(epamAPIMap.get("EPAM_C4_API_RUN_AS_USER"), epamApiKeyDecode);
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
		String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason);
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
