package com.hp.c4.rsku.rSku.rest.epam.api;

import java.util.HashMap;
import java.util.Map;

import com.hp.c4.epam.pwd.cache.Client;
import com.hp.c4.epam.pwd.cache.Client.APIException;
import com.hp.c4.epam.pwd.cache.helper.Json;
import com.hp.c4.epam.pwd.cache.helper.JsonObject;

////BeyondInsight and Password Safe API: 6.2+
////Workflow: Sign in, Find Account, Request, Retrieve Password, Checkin Request, Sign out
////Permissions: Requestor Role with valid Access Policy (View Password + Auto Approve)

//// Script Version: 1.1
//// Modified: 18-Nov-2016

public class ClientTest {

	private static String EPAM_C4_API_HOST_NAME_LLB = "epam.api.host.name.llb";
	private static String EPAM_C4_API_HOST_NAM_PWD_CACHE = "epam.api.host.name.pwd.cache";
	private static String EPAM_C4_API_KEY = "epam.api.key";
	private static String EPAM_C4_API_KEY_STORE_PWD = "epam.api.keystore.password";
	private static String EPAM_C4_API_KEY_STORE_FILE = "epam.api.keystore.file.location";
	private static String EPAM_C4_API_RUN_AS_USER = "epam.api.runas.user";
	private static String EPAM_SYSTEM_NAME = "epam.api.system.name";
	private static String EPAM_ACCOUNT_NAME = "epam.api.account.name";

	public static void main(String[] args) throws APIException {
		Map<String, String> EPAM_API_MAP = new HashMap<String, String>();
		getPassword();
		System.out.println("************************************");

		EPAM_API_MAP.put(EPAM_C4_API_KEY,
				"1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019");
		EPAM_API_MAP.put(EPAM_C4_API_RUN_AS_USER, "auth.hpicorp.net\\epamitgC4RunAs");
		EPAM_API_MAP.put(EPAM_C4_API_HOST_NAME_LLB, "epam-dc-itg.corp.hpicloud.net");
		EPAM_API_MAP.put(EPAM_C4_API_HOST_NAM_PWD_CACHE, "15.63.4.247");
		EPAM_API_MAP.put(EPAM_SYSTEM_NAME, "c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)");
		EPAM_API_MAP.put(EPAM_ACCOUNT_NAME, "infoshu");
		EPAM_API_MAP.put(EPAM_C4_API_KEY_STORE_PWD, "changeit");
		EPAM_API_MAP.put(EPAM_C4_API_KEY_STORE_FILE, "D:\\epam_cer\\testing_cer\\keystore_ca.cer");
		getMapPassword(EPAM_API_MAP);
	}

	public static String getPassword() throws APIException {
		System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
		System.setProperty("javax.net.ssl.trustStore", "D:\\epam_cer\\testing_cer\\keystore_ca.cer");
		System.setProperty("javax.net.ssl.trustStore", "D:\\epam_cer\\testing_cer\\keystore_ca.cer");

		System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");

		// BeyondInsight server hostname
		// String host = "the-server";
		String host = "epam-dc-itg.corp.hpicloud.net";

		// BeyondInsight Username
		// String user = "the-bi-user";
		String user = "auth.hpicorp.net\\epamitgC4RunAs";

		// BeyondInsight API Key
		// String apiKey =
		// "691081d1b89cb9950f54ebba8b655cfb78c6f479723007144a673da8d75a299c5b3f72f2833fba7bc56ed33707ef98116b694492f32dacb52f8f3ffe03bc1e34";
		String apiKey = "1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019";

		// System Name
		// String systemName = "QA-XRP-1";
		String systemName = "c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)";
		systemName = "c4onsi.inc.hpicorp.net%20(Db%20Instance:%20C4ONSI,%20Port:1525)";

		// Account Name
		// String accountName = "test-account";
		String accountName = "infoshu";
		accountName = "c4prod";

		Client client = new Client(host);

		// Sign In
		String jsonUserObjectStr = client.signAppIn(user, apiKey);
		JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
		System.out.println("Signed in as " + joUser.getString("UserName", ""));

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
		String conflict="reuse";
		String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason,conflict);
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

	public static String getMapPassword(Map<String, String> EPAM_API_MAP) throws APIException {
		System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
		System.setProperty("javax.net.ssl.trustStore", "D:\\epam_cer\\testing_cer\\keystore_ca.cer");

		System.setProperty("https.protocols", "SSLv3,TLSv1,TLSv1.1,TLSv1.2");

		// BeyondInsight server hostname
		// String host = "the-server";
		String host = "epam-dc-itg.corp.hpicloud.net";

		// BeyondInsight Username
		// String user = "the-bi-user";
		String user = "auth.hpicorp.net\\epamitgC4RunAs";

		// BeyondInsight API Key
		// String apiKey =
		// "691081d1b89cb9950f54ebba8b655cfb78c6f479723007144a673da8d75a299c5b3f72f2833fba7bc56ed33707ef98116b694492f32dacb52f8f3ffe03bc1e34";
		String apiKey = "1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019";

		// System Name
		// String systemName = "QA-XRP-1";
		String systemName = "c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)";
		systemName = "c4onsi.inc.hpicorp.net%20(Db%20Instance:%20C4ONSI,%20Port:1525)";

		// Account Name
		// String accountName = "test-account";
		String accountName = "infoshu";
		accountName = "c4prod";

		Client client = new Client(host);

		// Sign In
		String jsonUserObjectStr = client.signAppIn(user, apiKey);
		JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
		System.out.println("Signed in as " + joUser.getString("UserName", ""));

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
		String conflict="reuse";
		String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason,conflict);
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
