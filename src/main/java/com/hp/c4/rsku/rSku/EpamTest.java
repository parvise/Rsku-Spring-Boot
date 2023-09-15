package com.hp.c4.rsku.rSku;

import com.hp.c4.epam.pwd.cache.Client;
import com.hp.c4.epam.pwd.cache.Client.APIException;
import com.hp.c4.epam.pwd.cache.helper.Json;
import com.hp.c4.epam.pwd.cache.helper.JsonObject;

public class EpamTest {

	public static void main(String[] args) throws APIException {
		EpamTest test = new EpamTest();
		System.setProperty("java.naming.ldap.factory.socket", "javax.net.ssl.SSLSocketFactory");
		System.setProperty("javax.net.ssl.trustStore", "D:\\epam_cer\\testing_cer\\keystore_ca.cer");
		System.setProperty("javax.net.ssl.trustStore", "D:\\epam_cer\\testing_cer\\keystore_ca.cer");
		test.getPassword();
		System.out.println("Latest");
	}

	public String getPassword() throws APIException {
		// System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		// System.setProperty("javax.net.ssl.trustStore",
		// "C:/temp/src/basicpbpsdemo/cacerts.jks");

		// System.setProperty("javax.net.ssl.keyStore",
		// "C:/temp/src/basicpbpsdemo/cacerts.jks");

		// System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
		// System.setProperty("javax.net.ssl.trustStore",
		// "C:\\\\temp\\\\src\\\\basicpbpsdemo\\\\test\\\\cacerts.jks");

		// https://epam-itg.corp.hpicloud.net/BeyondTrust/api/public/v3

		// https://epam-dc-itg.corp.hpicloud.net/BeyondTrust/api/public/v3

		String host = "epam-dc-itg.corp.hpicloud.net";
		String user = "auth.hpicorp.net\\epamitgC4RunAs";
		String apiKey = "1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019";
		String systemName = "c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)";
		// String systemName = "c4infi.inc.hpicorp.net (Db Instance: C4INFI,
		// Port:1525)";
		String accountName = "infoshu";

		accountName = "c4prod";
		systemName = "c4onsi.inc.hpicorp.net%20(Db%20Instance:%20C4ONSI,%20Port:1525)";
		systemName = "c4offi.inc.hpicorp.net%20(Db%20Instance:%20C4OFFI,%20Port:1525)";

		Client client = new Client(host);

		// Sign In
		String jsonUserObjectStr = client.signAppIn(user, apiKey);
		JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
		System.out.println("Signed in as " + joUser.getString("UserName", ""));

		// Find Account
		System.out.println("After SIgn-In:" + systemName + ":" + accountName);
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
		String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason, conflict);
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
