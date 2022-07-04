package com.hp.c4.rsku.rSku.rest.epam.api;

import com.hp.c4.rsku.rSku.rest.epam.api.Client.APIException;
import com.hp.c4.rsku.rSku.rest.epam.api.json.Json;
import com.hp.c4.rsku.rSku.rest.epam.api.json.JsonObject;


////BeyondInsight and Password Safe API: 6.2+
////Workflow: Sign in, Find Account, Request, Retrieve Password, Checkin Request, Sign out
////Permissions: Requestor Role with valid Access Policy (View Password + Auto Approve)

//// Script Version: 1.1
//// Modified: 18-Nov-2016


public class ClientTest
{
  public static void main(String[] args) throws APIException
  {
	// -D options could also be used for these parameters
  
	// Setup the java keystore for the CA certificate using keytool.exe before calling this script.  The keystore location and password are required.
	System.setProperty("javax.net.ssl.trustStorePassword","changeit");
	System.setProperty("javax.net.ssl.trustStore","C:\\temp\\src\\basicpbpsdemo\\cacerts.jks");
	
	// If the UVM/install only support TLSv1, use this option.  A "connection reset" results if v1.1 or v1.2 are used and unsupported by the server.
	//System.setProperty("https.protocols","TLSv1");
	
	// To diagnose SSL handshake issues.
	//System.setProperty("javax.net.debug","ssl:handshake:verbose");
 
 
	// BeyondInsight server hostname
	String host = "epam-itg.corp.hpicloud.net";
	
	// BeyondInsight Username
	String user = "auth.hpicorp.net\\epamitgC4RunAs";
	
	// BeyondInsight API Key
	String apiKey = "1e60d42d1f3c4fe3ac6df1873d0eb9bc7fd51682f5f96a504b2a1e75f95f20451225883ccaa0cee0bea1d3dae0c395088738c0ef78d4e3180846d0d9fcca6019";
	
	// System Name
	// c4infi.inc.hpicorp.net (Db Instance: C4INFI, Port:1525)
	String systemName = "c4infi.inc.hpicorp.net%20(Db%20Instance:%20C4INFI,%20Port:1525)";
	
	// Account Name
	String accountName = "infoshu";

	
    Client client = new Client(host);
	
	// Sign In
    String jsonUserObjectStr = client.signAppIn(user, apiKey);
	JsonObject joUser = Json.parse(jsonUserObjectStr).asObject();
	System.out.println("Signed in as " + joUser.getString("UserName", ""));
	
	// Find Account
	String jsonAccount = client.getManagedAccountByName(systemName, accountName);
	JsonObject joAccount = Json.parse(jsonAccount).asObject();
	System.out.println("Found System|Account: " + joAccount.getString("SystemName", "") + "|" + joAccount.getString("AccountName", ""));
	
	// Request (for 5 minutes)
	int accountID = joAccount.getInt("AccountId", 0);
	int systemID = joAccount.getInt("SystemId", 0);
	int durationInMinutes = 5;
	String reason = "Java Sample";
	String jsonRequest = client.immediatePasswordRequest(accountID, systemID, durationInMinutes, reason);
	int reqID = Json.parse(jsonRequest).asInt();
	System.out.println(String.format("Request ID: %d", reqID));
		
	// Get Credentials
	String jsonCreds = client.retrievePassword(reqID);
	String pwd = Json.parse(jsonCreds).asString();
	System.out.println("---");
	System.out.println("Passwd ::::"+pwd);
	System.out.println("---");
	
	// Checkin Request
	client.checkinRequest(reqID, "Done Java Sample");
	System.out.println("Request checked-in");
	
	// Sign Out
	client.signOut();
	System.out.println("Signed out");
  }
}
