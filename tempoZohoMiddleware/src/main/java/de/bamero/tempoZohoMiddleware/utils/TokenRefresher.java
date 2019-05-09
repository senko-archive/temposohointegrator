package de.bamero.tempoZohoMiddleware.utils;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class TokenRefresher {
	
	RestTemplate restTemplate;
	
	public TokenRefresher(RestTemplate restTemplate) {
		
		this.restTemplate = restTemplate;
	}

}
