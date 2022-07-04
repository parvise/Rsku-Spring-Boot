package com.hp.c4.rsku.rSku.rest.epam.controller;

import java.io.IOException;
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
import com.hp.c4.rsku.rSku.rest.epam.api.json.JsonObject;

import sun.misc.BASE64Decoder;

@RestController
public class EpamController {

	private static final Logger mLogger = LogManager.getLogger(EpamController.class);

	private static Map<String, String> EPAM_SERVICE_API_MAPS = new HashMap<String, String>();

	@Autowired
	private RestTemplate restTemplate;

	@Value("${epam.api.host.name}")
	private String EPAM_C4_API_HOST_NAME;

	@Value("${epam.api.runas.user}")
	private String EPAM_C4_API_RUN_AS_USER;

	@Value("${epam.api.key}")
	private String EPAM_C4_API_KEY;

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

	@Value("${epam.api.keystore.file.location}")
	private String EPAM_KEYSTORE_FILE_LOC_PATH;

	@Value("${c4.db.service.name.ons}")
	private String C4_DB_SERVICE_NAME_ONS;

	@Value("${c4.db.service.name.off}")
	private String C4_DB_SERVICE_NAME_OFF;

	@Value("${c4.db.service.name.inf}")
	private String C4_DB_SERVICE_NAME_INF;

	@Value("${c4.db.service.name.rep}")
	private String C4_DB_SERVICE_NAME_REP;

	@Value("${epam.endpoint.sub.url}")
	private String EPAM_REST_END_POINT_URL;

	@Value("${c4.db.list.of.user.names}")
	private String C4_DB_LIST_OF_USER_NAMES;

	@RequestMapping(value = "/epam/{user_name}/{service_name}", method = RequestMethod.GET)
	public String getEpamApiPwd(@PathVariable("user_name") String userName,
			@PathVariable("service_name") String serviceName)
			throws JsonParseException, JsonMappingException, IOException, Exception {

		List<String> DB_USER_NAMES = Arrays.asList(C4_DB_LIST_OF_USER_NAMES.split(","));

		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_ONS, EPAM_SYSTEM_NAME_ONS);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_OFF, EPAM_SYSTEM_NAME_OFF);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_REP, EPAM_SYSTEM_NAME_REP);
		EPAM_SERVICE_API_MAPS.put(C4_DB_SERVICE_NAME_INF, EPAM_SYSTEM_NAME_INF);

		// mLogger.info("EPAM_SERVICE_API_MAPS" + EPAM_SERVICE_API_MAPS + ":" +
		// EPAM_C4_API_KEY);

		System.setProperty("javax.net.ssl.trustStorePassword", EPAM_KEYSTORE_FILE_PASSWORD);
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

		mLogger.info("-----------------------------------Sign-In -------------------------------------------------");
		restTemplate.exchange(EPAM_REST_END_POINT_URL + "/auth/SignAppin", HttpMethod.POST, entity, String.class);

		// mLogger.info( "******************" + signInApiResponse.getBody() + ":" +
		// signInApiResponse.getStatusCode() + ":");

		String systemName = EPAM_SERVICE_API_MAPS.get(serviceName);

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
		ResponseEntity<String> managedAccountByNameResponse = restTemplate.exchange(builder.toString(), HttpMethod.GET,
				null, String.class, uriParam);

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
		request.add("DurationMinutes", 10);
		request.add("ConflictOption", "reuse");

		HttpEntity<String> entity1 = new HttpEntity<String>(request.toString(), headers);

		ResponseEntity<Integer> immediatePasswordRequest = restTemplate.exchange(EPAM_REST_END_POINT_URL + "/Requests",
				HttpMethod.POST, entity1, Integer.class, new Object[] {});

		// mLogger.info(immediatePasswordRequest.getBody());
		Integer requestId = immediatePasswordRequest.getBody();

		if (requestId > 0) {
			ResponseEntity<String> retrievePasswordResponse = restTemplate.exchange(
					EPAM_REST_END_POINT_URL + "/Credentials/" + requestId, HttpMethod.GET, entity1, String.class,
					new Object[] {});

			mLogger.info("Password = " + retrievePasswordResponse.getBody());

			restTemplate.exchange(EPAM_REST_END_POINT_URL + "/Requests/" + requestId + "/Checkin", HttpMethod.PUT,
					entity1, String.class, new Object[] {});

			mLogger.info("Request checked-in");

			restTemplate.exchange(EPAM_REST_END_POINT_URL + "/Auth/Signout/", HttpMethod.POST, entity1, String.class,
					new Object[] {});

			mLogger.info("------------------------------Signed out--------------------------------------");
			return retrievePasswordResponse.getBody();
		}

		else {
			return "NOT_FOUND";
		}
	}

}
