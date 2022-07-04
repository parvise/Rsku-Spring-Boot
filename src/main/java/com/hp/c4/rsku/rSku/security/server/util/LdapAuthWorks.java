package com.hp.c4.rsku.rSku.security.server.util;

import java.security.Security;

import com.hp.c4.rsku.rSku.EpamTest;

import netscape.ldap.LDAPEntry;
import netscape.ldap.LDAPSearchConstraints;
import netscape.ldap.LDAPSearchResults;

public class LdapAuthWorks {

	public static void main(String[] args) throws Exception {
		
		System.setProperty("java.naming.ldap.factory.socket",
				"javax.net.ssl.SSLSocketFactory");
		System.setProperty("javax.net.ssl.trustStore",
				"D:\\epam_cer\\testing_cer\\keystore_ca.cer");
		
		//System.setProperty("javax.net.ssl.trustStore", "D:/epam_cer/testing_cer/epam.jks");
		

		EpamTest test = new EpamTest();
		System.out.println(test.getPassword());

		System.out.println("Welcome");

		DummyLdap dummy = new DummyLdap();

		netscape.ldap.LDAPConnection ldapConn = dummy.getSecureLdapConnection();
		

		// {securitygroup=C4-SECURITY-ADMN, port=389, scope=2,
		// host=hpi-pro-ods-ed-temp.infra.hpicorp.net, s
		// ecureport=636, base=o=hp.com, keystorefilepwd=123456,
		// keystorefile=/opt/sasuapps/c4/global_resources/keystore_ca.cer}

		boolean isValidUser = false;
		;
		// LDAP settings/filters
		String user_filter = "mail=" + "parvise.mohammad@hp.com";
		LDAPSearchConstraints ldSearchConst = new LDAPSearchConstraints();
		ldSearchConst.setServerTimeLimit(180);
		ldSearchConst.setTimeLimit(60000);
		ldapConn.setSearchConstraints(ldSearchConst);

		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		String host = "hpi-pro-ods-ed-temp.infra.hpicorp.net";
		int port = 636;
		String base = "o=hp.com";
		int scope = 2;

		String user_dn = null;
		String reqAttrs[] = { "cn", "sn", "givenname", "hprole",
				"hpdepartmentcode", "employeenumber", "telephoneNumber",
				"nonhpuniqueid" };
		ldapConn.connect(host, port);
		System.out.println(" **** Connected to Secure ");
		// Get the user DN, in order to authenticate
		LDAPSearchResults res = ldapConn.search(base, scope, user_filter,
				reqAttrs, false);
		if (res.hasMoreElements()) {
			LDAPEntry findEntry = (LDAPEntry) res.next();
			user_dn = findEntry.getDN();
			System.out.println(" ======== user_dn ====== " + user_dn);
			if (user_dn.contains("parvise.mohammad@hp.com")) {
				isValidUser = true;
				// return true;
			} else {
				isValidUser = false;
				// return false;
			}
		}

		System.out.println(ldapConn + "=======" + isValidUser);

		System.out.println("---------------------------");

	}
}
