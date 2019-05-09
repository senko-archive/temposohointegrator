package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;

public class ZohoBooksParser {
	
	private static final Logger logger = LoggerFactory.getLogger(ZohoBooksParser.class);
	
	public void zohoRefreshTokenRetrievalParser(ResponseEntity<String> response) {
		logger.debug("zohoRefreshTokenRetrievalParser is working");
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		
		try {
			root = mapper.readTree(response.getBody());
			String access_token = root.get("access_token").toString();

			// get the current date and time
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date date = new Date();
			String access_token_gather_date = formatter.format(date).toString();

			// set new access_token and refresh_token
			logger.info("new informations are writing in config.properties");
			PropertiesLoader loader = new PropertiesLoader();
			loader.saveZohoBooks("zohobooks.properties", access_token, access_token_gather_date);
			logger.info("new properties is written");

		} catch (IOException e) {
			logger.error("zohoTokenRetrievalParser error on reading responseBody: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
