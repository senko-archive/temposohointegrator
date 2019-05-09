package de.bamero.tempoZohoMiddleware.tokenCheckers;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;

public class TempoTokenChecker {

	private static final Logger logger = LoggerFactory.getLogger(TempoTokenChecker.class);

	public void checkValidity() {
		
		// test if tempo access code is still valid
		Boolean status;
		status = isAccessTokenValid();
		
		if (status == true)
			logger.info("Tempo Access Code is still valid");
		else {
			logger.info("Tempo Access Code is expired, new Request is preparing for renew access code");
			// get new access token via refresh token
			refreshAccessToken();
		}
		
	}

	private boolean isAccessTokenValid() {

		PropertiesLoader loader = new PropertiesLoader();
		Properties prop = loader.load("tempoRest.properties");

		String validityTimeInSeconds = prop.getProperty("access_token_valid_time"); // 5184000
		String access_token_gather_time = prop.getProperty("access_token_gather_date");

		// convert string to localDateTime
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		LocalDateTime accessTokenGatherTime = LocalDateTime.parse(access_token_gather_time, formatter);

		// take current date
		LocalDateTime currentTime = LocalDateTime.now();

		// find the difference between two dates
		long diff = ChronoUnit.SECONDS.between(accessTokenGatherTime, currentTime);

		if (diff > 5180000) {
			System.out.println("System needs to refresh token");
			return false;
		} else {
			System.out.println("No need to refresh token");
			return true;
		}

	}
	
	private void refreshAccessToken() {
		logger.debug("TempoTokenChecker refreshAccessToken() is working");
		
		throw new NotImplementedException("refreshAccessToken() is not implemented yet"); 
	}

}
