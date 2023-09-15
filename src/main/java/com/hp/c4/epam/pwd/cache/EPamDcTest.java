package com.hp.c4.epam.pwd.cache;

import java.util.HashMap;
import java.util.Map;

import com.hp.c4.epam.pwd.cache.Client.APIException;
import com.hp.c4.epam.pwd.cache.helper.Json;
import com.hp.c4.epam.pwd.cache.helper.JsonObject;

////BeyondInsight and Password Safe API: 6.2+
////Workflow: Sign in, Find Account, Request, Retrieve Password, Checkin Request, Sign out
////Permissions: Requestor Role with valid Access Policy (View Password + Auto Approve)

//// Script Version: 1.1
//// Modified: 18-Nov-2016

public class EPamDcTest {
	private static String EPAM_C4_API_HOST_NAME = "epam.api.host.name";
	private static String EPAM_C4_API_KEY = "epam.api.key";
	private static String EPAM_C4_API_KEY_STORE_PWD = "epam.api.keystore.password";
	private static String EPAM_C4_API_KEY_STORE_FILE = "epam.api.keystore.file.location";
	private static String EPAM_C4_API_RUN_AS_USER = "epam.api.runas.user";
	private static String EPAM_SYSTEM_NAME = "epam.api.system.name";
	private static String EPAM_ACCOUNT_NAME = "epam.api.account.name";

	public static void main(String[] args) throws APIException {
		Map<String, String> EPAM_API_MAP = new HashMap<String, String>();
		EPAM_API_MAP.put(EPAM_C4_API_KEY,
				"1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019");
		EPAM_API_MAP.put(EPAM_C4_API_RUN_AS_USER, "auth.hpicorp.net\\epamitgC4RunAs");
		EPAM_API_MAP.put(EPAM_C4_API_HOST_NAME, "15.63.4.247");
		EPAM_API_MAP.put(EPAM_SYSTEM_NAME,"c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)");
		EPAM_API_MAP.put(EPAM_ACCOUNT_NAME,"infoshu");
		EPAM_API_MAP.put(EPAM_C4_API_KEY_STORE_PWD, "changeit");
		EPAM_API_MAP.put(EPAM_C4_API_KEY_STORE_FILE, "D:/epam_cer/2023/keystore_ca.cer");
		getPassword(EPAM_API_MAP);
	}

	public static String getPassword(Map<String, String> EPAM_API_MAP) throws APIException {
		
		System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
		//System.setProperty("javax.net.ssl.trustStorePassword", EPAM_API_MAP.get(EPAM_C4_API_KEY_STORE_PWD));
		System.setProperty("javax.net.ssl.trustStore", EPAM_API_MAP.get(EPAM_C4_API_KEY_STORE_FILE));
		System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");
		

		String host = EPAM_API_MAP.get(EPAM_C4_API_HOST_NAME);

		String user = EPAM_API_MAP.get(EPAM_C4_API_RUN_AS_USER);

		String apiKey = EPAM_API_MAP.get(EPAM_C4_API_KEY);

		String systemName = EPAM_API_MAP.get(EPAM_SYSTEM_NAME);
//		systemName = "c4onsi.inc.hpicorp.net%20(Db%20Instance:%20C4ONSI,%20Port:1525)";

		String accountName = "infoshu";
		//accountName = "c4prod";

		Client client = new Client(host);

		// Sign In
		String jsonUserObjectStr = client.signAppIn(user, apiKey);
		JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
		System.out.println("Signed in as " + joUser.getString("UserName", ""));
		System.out.println("accountName" + accountName+":"+systemName);

		// Find Account
		String jsonAccount = client.getManagedAccountByName(systemName, accountName);
		JsonObject joAccount = Json.parse(jsonAccount).asObject();
		System.out.println("Found System|Account: " + joAccount.getString("SystemName", "") + "|"
				+ joAccount.getString("AccountName", ""));

		// Request (for 5 minutes)
		int accountID = joAccount.getInt("AccountId", 0);
		int systemID = joAccount.getInt("SystemId", 0);
		int durationInMinutes = 5;
		String reason = "Java Sample";
		String conflict = "reuse";
		String jsonRequest = client.immediatePasswordRequest(accountID,
				systemID, durationInMinutes, reason, conflict);
		int reqID = Json.parse(jsonRequest).asInt();
		System.out.println(String.format("Request ID: %d", reqID));

		// Get Credentials
		String jsonCreds = client.retrievePassword(reqID);
		String pwd = Json.parse(jsonCreds).asString();
		System.out.println("---");
		System.out.println(pwd);
		System.out.println("---");

		// Checkin Request
		client.checkinRequest(reqID, "Done Java Sample");
		System.out.println("Request checked-in");

		// Sign Out
		client.signOut();
		System.out.println("Signed out");

		return pwd;
	}
}
