package com.hp.c4.rsku.rSku.rest.epam.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hp.c4.epam.pwd.cache.helper.Json;
import com.hp.c4.epam.pwd.cache.helper.JsonObject;
import com.hp.pcp.c4.security.server.dao.epam.api.EpamBean;

import sun.misc.BASE64Decoder;

@RestController
public class EpamController {

	private static final Logger mLogger = LogManager.getLogger(EpamController.class);

	private static Map<String, String> EPAM_SERVICE_API_MAPS = new HashMap<String, String>();

	@Autowired
	private RestTemplate restTemplate;

	@Value("${epam.api.host.name.llb}")
	private String EPAM_C4_API_HOST_NAME_LLB;

	@Value("${epam.api.host.name.pwd.cache}")
	private String EPAM_C4_API_HOST_NAME_PWD_CACHE;

	@Value("${epam.api.runas.user}")
	private String EPAM_C4_API_RUN_AS_USER;

	@Value("${epam.api.key}")
	private String EPAM_C4_API_KEY;

	@Value("${postman.epam.api.system.name.c4dart}")
	private String EPAM_SYSTEM_NAME_C4DART;

	@Value("${postman.epam.api.system.name.c4inf}")
	private String EPAM_SYSTEM_NAME_INF;

	@Value("${postman.epam.api.system.name.c4off}")
	private String EPAM_SYSTEM_NAME_OFF;

	@Value("${postman.epam.api.system.name.c4rep}")
	private String EPAM_SYSTEM_NAME_REP;

	@Value("${postman.epam.api.system.name.c4ons}")
	private String EPAM_SYSTEM_NAME_ONS;

	@Value("${epam.api.keystore.password}")
	private String EPAM_KEYSTORE_FILE_PASSWORD;

	@Value("${epam.api.keystore.file.jks.location}")
	private String EPAM_KEYSTORE_FILE_LOC_PATH;

	@Value("${c4.db.service.name.ons}")
	private String C4_DB_SERVICE_NAME_ONS;

	@Value("${c4.db.service.name.off}")
	private String C4_DB_SERVICE_NAME_OFF;

	@Value("${c4.db.service.name.c4dart}")
	private String C4_DB_SERVICE_NAME_C4DART;

	@Value("${c4.db.service.name.inf}")
	private String C4_DB_SERVICE_NAME_INF;

	@Value("${c4.db.service.name.rep}")
	private String C4_DB_SERVICE_NAME_REP;

	@Value("${epam.endpoint.sub.url.llb}")
	private String EPAM_REST_END_POINT_URL_LLB;

	@Value("${epam.endpoint.sub.url.pwd.cache}")
	private String EPAM_REST_END_POINT_URL_PWD_CACHE;

	@Value("${c4.db.list.of.user.names}")
	private String C4_DB_LIST_OF_USER_NAMES;

	@Value("${epam.all.db.pwd.ser.location}")
	private String C4_EPAM_PWD_OBJ_SERILIZATION_PATH;

	private String EPAM_REST_END_POINT_URL;

	@RequestMapping(value = "/epam/{user_name}/{service_name}", method = RequestMethod.GET)
	public String getEpamApiPwd(@PathVariable("user_name") String userName,
			@PathVariable("service_name") String serviceName)
			throws JsonParseException, JsonMappingException, IOException, Exception {

		List<String> DB_USER_NAMES = Arrays.asList(C4_DB_LIST_OF_USER_NAMES.split(","));

		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_ONS, EPAM_SYSTEM_NAME_ONS);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_OFF, EPAM_SYSTEM_NAME_OFF);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_REP, EPAM_SYSTEM_NAME_REP);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_INF, EPAM_SYSTEM_NAME_INF);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_C4DART, EPAM_SYSTEM_NAME_C4DART);

		mLogger.info("EPAM_KEYSTORE_FILE_LOC_PATH" + EPAM_KEYSTORE_FILE_LOC_PATH);

		// System.setProperty("javax.net.ssl.trustStorePassword",
		// EPAM_KEYSTORE_FILE_PASSWORD);
		System.setProperty("javax.net.ssl.trustStore", EPAM_KEYSTORE_FILE_LOC_PATH);

		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		// Password decoding from Encode password from Properties File
		String EPAM_API_DECODE_KEY = null;
		byte[] decodeResult;
		try {
			decodeResult = new BASE64Decoder().decodeBuffer(EPAM_C4_API_KEY);
			EPAM_API_DECODE_KEY = new String(decodeResult);
		} catch (IOException e) {
			mLogger.error("Error occured at Decode th DB passowrds");
		}

		// mLogger.info("EPAM_API_DECODE_KEY : " + EPAM_API_DECODE_KEY);
		// mLogger.info("EPAM_C4_API_RUN_AS_USER:" + EPAM_C4_API_RUN_AS_USER);

		headers.set("Authorization", "PS-Auth key=" + EPAM_API_DECODE_KEY + "; runas=" + EPAM_C4_API_RUN_AS_USER);

		// mLogger.info("EPAM_SYSTEM_NAME_INF::" + EPAM_SYSTEM_NAME_INF);
		// mLogger.info(userName + ":" + serviceName);
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		try {

			try {
				EPAM_REST_END_POINT_URL = EPAM_REST_END_POINT_URL_PWD_CACHE;
				mLogger.info(
						"-----------------------------------Sign-In -------------------------------------------------");
				restTemplate.exchange(EPAM_REST_END_POINT_URL + "/auth/SignAppin", HttpMethod.POST, entity,
						String.class);
			} catch (Exception e) {
				mLogger.error("Error occured at Epam API service call" + e.getMessage() + ": Failover at := "
						+ EPAM_REST_END_POINT_URL);

				EPAM_REST_END_POINT_URL = EPAM_REST_END_POINT_URL_LLB;
				restTemplate.exchange(EPAM_REST_END_POINT_URL + "/auth/SignAppin", HttpMethod.POST, entity,
						String.class);
			}

			// mLogger.info( "******************" + signInApiResponse.getBody() + ":" +
			// signInApiResponse.getStatusCode() + ":");

			String systemName = EPAM_SERVICE_API_MAPS.get(serviceName);
			// systemName = systemName.replaceAll(" ", "%20");
			mLogger.info(userName + ":" + serviceName);
			if (systemName == null) {
				return "Service Name is In-Valid";
			}

			mLogger.info("DB_USER_NAMES" + DB_USER_NAMES + ":" + DB_USER_NAMES.size());
			// Account Name
			String accountName = (DB_USER_NAMES.contains(userName.toLowerCase())) ? userName : null;

			if (accountName == null || accountName.trim() == "") {
				return "User Name is In-Valid";
			}

			Map<String, String> uriParam = new HashMap<String, String>();
			uriParam.put("account", "ManagedAccounts");

			String uri = EPAM_REST_END_POINT_URL + "/{account}";

			UriComponents builder = UriComponentsBuilder.fromHttpUrl(uri).queryParam("systemName", systemName)
					.queryParam("accountName", accountName).build();
			// mLogger.info("*******************************");
			// mLogger.info("Test:" + builder.toString());

			mLogger.info("URL-Builder=" + builder.toString());
			ResponseEntity<String> managedAccountByNameResponse = restTemplate.exchange(builder.toString(),
					HttpMethod.GET, entity, String.class, uriParam);

			ObjectMapper mapper = new ObjectMapper();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = mapper.readValue(managedAccountByNameResponse.getBody(), Map.class);

			// mLogger.info("Map..." + map);

			Integer accountId = (Integer) map.get("AccountId");
			Integer systemId = (Integer) map.get("SystemId");

			// mLogger.info("System & Account..." + accountId + ":" + systemId);

			JsonObject request = new JsonObject();
			request.add("SystemId", systemId);
			request.add("AccountId", accountId);
			request.add("DurationMinutes", 1);
			// request.add("ConflictOption", "reuse");

			HttpEntity<String> entity1 = new HttpEntity<String>(request.toString(), headers);

			ResponseEntity<Integer> immediatePasswordRequest = restTemplate.exchange(
					EPAM_REST_END_POINT_URL + "/Requests", HttpMethod.POST, entity1, Integer.class, new Object[] {});

			// mLogger.info(immediatePasswordRequest.getBody());
			Integer requestId = immediatePasswordRequest.getBody();

			if (requestId > 0) {
				ResponseEntity<String> retrievePasswordResponse = restTemplate.exchange(
						EPAM_REST_END_POINT_URL + "/Credentials/" + requestId, HttpMethod.GET, entity1, String.class,
						new Object[] {});

				String password = retrievePasswordResponse.getBody();
				password = Json.parse(password).asString();

				mLogger.info("Password = " + password);
				restTemplate.exchange(EPAM_REST_END_POINT_URL + "/Requests/" + requestId + "/Checkin", HttpMethod.PUT,
						entity1, String.class, new Object[] {});

				mLogger.info("Request checked-in");

				restTemplate.exchange(EPAM_REST_END_POINT_URL + "/Auth/Signout", HttpMethod.POST, entity, String.class,
						new Object[] {});

				mLogger.info("------------------------------Signed out--------------------------------------");
				return password;
			}

			else {
				return "NOT_FOUND";
			}
		} catch (IOException e) {
			mLogger.error("Error occured at Decode th DB passowrds");
		}
		return "NOT_FOUND";
	}

	@RequestMapping(value = "/allEpamPwd", method = RequestMethod.GET)
	public List<EpamBean> allDatabasePasswords()
			throws JsonParseException, JsonMappingException, IOException, Exception {

		List<EpamBean> epamList = new ArrayList<EpamBean>();

		EpamBean infoshu = new EpamBean();
		infoshu.setUserName("infoshu");
		infoshu.setSystemName(C4_DB_SERVICE_NAME_INF);
		infoshu.setAccountName(EPAM_SYSTEM_NAME_INF);

		EpamBean c4report = new EpamBean();
		c4report.setUserName("c4report");
		c4report.setSystemName(C4_DB_SERVICE_NAME_REP);
		c4report.setAccountName(EPAM_SYSTEM_NAME_REP);

		EpamBean c4prod = new EpamBean();
		c4prod.setUserName("c4prod");
		c4prod.setSystemName(C4_DB_SERVICE_NAME_OFF);
		c4prod.setAccountName(EPAM_SYSTEM_NAME_OFF);

		EpamBean c4 = new EpamBean();
		c4.setUserName("c4");
		c4.setSystemName(C4_DB_SERVICE_NAME_C4DART);
		c4.setAccountName(EPAM_SYSTEM_NAME_C4DART);

		EpamBean eclp_cach = new EpamBean();
		eclp_cach.setUserName("eclp_cach");
		eclp_cach.setSystemName(C4_DB_SERVICE_NAME_ONS);
		eclp_cach.setAccountName(EPAM_SYSTEM_NAME_ONS);

		EpamBean gpssnap = new EpamBean();
		gpssnap.setUserName("gpssnap");
		gpssnap.setSystemName(C4_DB_SERVICE_NAME_ONS);
		gpssnap.setAccountName(EPAM_SYSTEM_NAME_ONS);

		EpamBean dart_cach = new EpamBean();
		dart_cach.setUserName("dart_cach");
		dart_cach.setSystemName(C4_DB_SERVICE_NAME_ONS);
		dart_cach.setAccountName(EPAM_SYSTEM_NAME_ONS);

		EpamBean c4tera = new EpamBean();
		c4tera.setUserName("c4tera");
		c4tera.setSystemName(C4_DB_SERVICE_NAME_ONS);
		c4tera.setAccountName(EPAM_SYSTEM_NAME_ONS);

		EpamBean c4prodONS = new EpamBean();
		c4prodONS.setUserName("c4prod");
		c4prodONS.setSystemName(C4_DB_SERVICE_NAME_ONS);
		c4prodONS.setAccountName(EPAM_SYSTEM_NAME_ONS);

		epamList.add(infoshu);
		epamList.add(c4report);
		epamList.add(c4prod);
		epamList.add(c4);
		epamList.add(eclp_cach);
		epamList.add(gpssnap);
		epamList.add(dart_cach);
		epamList.add(c4tera);
		epamList.add(c4prodONS);

		for (EpamBean epamBean : epamList) {
			String password = getEpamApiPwd(epamBean.getUserName(), epamBean.getSystemName());
			epamBean.setPassword(password);
		}

		try (FileOutputStream fout = new FileOutputStream(C4_EPAM_PWD_OBJ_SERILIZATION_PATH);
				ObjectOutputStream oos = new ObjectOutputStream(fout);) {
			oos.writeObject(epamList);
			mLogger.info("-------------- All DB pwds Object Serilaized  at this location--------------"
					+ C4_EPAM_PWD_OBJ_SERILIZATION_PATH);
			mLogger.info(EpamBean.class);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return epamList;
	}
}
