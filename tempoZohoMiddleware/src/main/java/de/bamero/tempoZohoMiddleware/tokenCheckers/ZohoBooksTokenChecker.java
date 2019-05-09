package de.bamero.tempoZohoMiddleware.tokenCheckers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.parsers.ZohoBooksParser;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;

public class ZohoBooksTokenChecker {

	private static final Logger logger = LoggerFactory.getLogger(ZohoBooksTokenChecker.class);

	RestTemplate restTemplate;

	public ZohoBooksTokenChecker(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void checkValidity() {

		// test if zohoBooks access code is still valid
		Boolean status;
		status = isAccessTokenValid();

		if (status == true) {
			logger.info("No need to refresh token");
		} else {
			logger.info("System needs to refresh token");
			refreshAccessToken();
		}

	}

	private boolean isAccessTokenValid() {

		PropertiesLoader loader = new PropertiesLoader();
		Properties prop = loader.load("zohobooks.properties");

		String validityTimeInSeconds = prop.getProperty("access_token_valid_time"); // 3600
		String access_token_gather_time = prop.getProperty("access_token_gather_date");

		// convert string to localDateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime accessTokenGatherTime = LocalDateTime.parse(access_token_gather_time, formatter);

		// take current date
		LocalDateTime currentTime = LocalDateTime.now();

		// find the difference between two dates
		long diff = ChronoUnit.SECONDS.between(accessTokenGatherTime, currentTime);

		if (diff > 3000) {

			return false;
		} else {
			return true;
		}

	}

	private void refreshAccessToken() {
		logger.debug("ZohoBooksTokenChecker.refreshAccessToken() now working");

		PropertiesLoader loader = new PropertiesLoader();
		Properties prop = loader.load("zohobooks.properties");

		// prepare url and headers for POST request
		StringBuffer tokenPostRequest = new StringBuffer();
		tokenPostRequest.append("https://accounts.zoho.eu/oauth/v2/token?").append("refresh_token=")
				.append(prop.getProperty("refresh_token")).append("&").append("client_id=")
				.append(prop.getProperty("client_id")).append("&").append("client_secret=")
				.append(prop.getProperty("client_secret")).append("&").append("redirect_uri=")
				.append(prop.getProperty("redirect_uri")).append("&").append("grant_type=").append("refresh_token");
		
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("cache-control", "no-cache");
		
		logger.debug("ZohoTokenRefreshPost request string: " + tokenPostRequest.toString());
		
		ResponseEntity<String> response = ConsumerHelper.postResponse(restTemplate, requestHeader,
				tokenPostRequest.toString());
		
		ZohoBooksParser zohoBooksParser = new ZohoBooksParser();
		zohoBooksParser.zohoRefreshTokenRetrievalParser(response);
		
		
		
		

	}

}
