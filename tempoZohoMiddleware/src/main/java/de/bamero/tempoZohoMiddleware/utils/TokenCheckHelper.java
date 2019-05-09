package de.bamero.tempoZohoMiddleware.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.tokenCheckers.TempoTokenChecker;
import de.bamero.tempoZohoMiddleware.tokenCheckers.ZohoBooksTokenChecker;

@Component
public class TokenCheckHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(TokenCheckHelper.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	// checks tempo and zohobooks access tokens if still valid or not
	// for non valid tokens, renew it using refresh_token
	
	
	public void checkForTempoRestAPI() {
		TempoTokenChecker tempoTokenChecker = new TempoTokenChecker();
		tempoTokenChecker.checkValidity();
	}
	
	public void checkForZohoBooksRestAPI() {
		ZohoBooksTokenChecker zohoBooksTokenChecker = new ZohoBooksTokenChecker(restTemplate);
		zohoBooksTokenChecker.checkValidity();
		
	}
	
	
	
	
	
	
	 

}
