package com.hp.c4.rsku.rSku.security.server.util;

import java.security.Security;
import java.util.HashMap;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.net.ssl.SSLSocket;

import netscape.ldap.LDAPConnection;
import netscape.ldap.LDAPException;
import netscape.ldap.factory.JSSESocketFactory;

public class DummyLdap {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		System.out.println("Welcome");
		
		DummyLdap dummy =new DummyLdap();
		dummy.getSecureLdapConnection();
		InitialLdapContext ctx = null;
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		// o=AUTH.hpicorp.net
		// o=hp.com
		env.put(Context.PROVIDER_URL,
				"ldap://hpi-pro-ods-ed.infra.hpicorp.net:389/ou=People,o=hp.com");

		// env.put(Context.REFERRAL, "follow");

		// Needed for the Bind (User Authorized to Query the LDAP server)
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PRINCIPAL, "mail=parvise.mohammad@hp.com");
		env.put(Context.SECURITY_CREDENTIALS, "Aprapr@2022r");

		NamingEnumeration<SearchResult> results = null;
		// Set<EditUserDto> editUserDtoSet = (Set<EditUserDto>)
		// editUserDtoMap.values();
		DirContext dir = null;
		try {
			// ctx = new InitialLdapContext(env, null);
			dir = new InitialDirContext(env);
			System.out.println(dir);
			SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setCountLimit(1); // Sets the maximum number of entries to
										// be returned as a result of the search
			controls.setTimeLimit(5000); // Sets the time limit of these
											// SearchControls in milliseconds
			// results = ctx.search("",
			// "(&(objectClass=person)(uid="+"gourav.soni@hp.com"+"))",
			// controls);

			// results = ctx.search("",
			// "(&(objectClass=person)(mail="+"$c4service001@AUTH.hpicorp.net"+"))",
			// controls);
			results = dir.search("", "(&(objectClass=ntuser)(ntuserdomainid="
					+ "AUTH:mohammap" + "))", controls);
			// results = ctx.search("",
			// "(&(objectClass=ntuser)(ntuserdomainid="+"AUTH:$c4service001"+"))",
			// controls);

			// results = ctx.search("",
			// "(&(objectCategory=user)(ntUserDomainId="+"AUTH:santhosv "+"))",
			// controls);
			System.out.println(results);
			if (results.hasMore()) {

				SearchResult result = (SearchResult) results.next();
				Attributes attrs = result.getAttributes();
				System.out.println(attrs);
				Attribute dnAttr = attrs.get("hpPayrollCountryCode");
				String dn = (String) dnAttr.get();

				System.out.println("True---->" + dn);
			}
		} catch (NameNotFoundException e) {
			// The base context was not found.
			// Just clean up and exit.
			throw new RuntimeException(e);
		} catch (NamingException e) {
			throw new RuntimeException(e);
		} finally {
			if (results != null) {
				try {
					results.close();
				} catch (Exception e) {
					// Never mind this.
				}
			}
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					// Never mind this.
				}
			}
		}
	}

	LDAPConnection getSecureLdapConnection() throws Exception {
		// Split Work Start - Respective LDAP will be called based on the login
		// email domain.
		LDAPConnection ldapConnection = null;
		boolean isValidUser = false;
		HashMap tmpMap = null;
		JSSESocketFactory jsseFactory = null;
		javax.net.ssl.SSLSocket ssSocket = null;
		try {
			System.out.println(" ***  LDAP Details Map : " + tmpMap);

			Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
			
			// System.setProperty("javax.net.ssl.keyStore",(String)tmpMap.get(KEYSTOREFILE));
			// System.setProperty("javax.net.ssl.keyStorePassword",(String)tmpMap.get(KEYSTOREPWD));
			// System.setProperty("javax.net.ssl.trustStorePassword",(String)tmpMap.get(KEYSTOREPWD));
			jsseFactory = new JSSESocketFactory(null);
			System.setProperty("https.protocols", "TLSv1.2");
			System.out.println("TSL suuports check: 544 -> "
					+ System.getProperty("https.protocols"));
			System.out.println("javax.net.ssl.trustStore -> "
					+ System.getProperty("javax.net.ssl.trustStore"));
			// _logger.info("javax.net.ssl.trustStorePassword -> "+System.getProperty("javax.net.ssl.trustStorePassword"));
			ssSocket = (SSLSocket) jsseFactory.makeSocket(
					"hpi-pro-ods-ed-temp.infra.hpicorp.net",
					Integer.parseInt("636"));
			System.out.println("TSL suuports check: 544 -> "
					+ System.getProperty("https.protocols"));
			ldapConnection = new LDAPConnection(jsseFactory);
			ldapConnection.setConnectTimeout(1800);
			System.out.println(" **** LDAPConnection to Secure  LDAP :  ");
		} catch (LDAPException ldep) {
			System.out
					.println(" LDAPException in getSecureLdapConnection() :  "
							+ ldep.getMessage());
			ldep.printStackTrace();
		} catch (Exception e) {
			System.out.println(" Exception in getSecureLdapConnection() :  "
					+ e.getMessage());
			e.printStackTrace();
		}
		// Split Work End.
		return ldapConnection;
	}

}
