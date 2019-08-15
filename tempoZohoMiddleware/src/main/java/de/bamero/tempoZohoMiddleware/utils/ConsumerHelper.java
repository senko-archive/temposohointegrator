package de.bamero.tempoZohoMiddleware.utils;

import java.util.Map;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.StreamingHttpOutputMessage.Body;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ConsumerHelper {
	
private static final Logger logger = LoggerFactory.getLogger(ConsumerHelper.class);
	
	public static ResponseEntity<String> getResponse(RestTemplate restTemplate, Map<String, String> headerOptions, String url) {
		logger.debug("ConsumerHelper.getResponse() now working"); 
		
		HttpHeaders headers = new HttpHeaders();
		headerOptions.forEach((k, v) -> headers.add(k, v));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
		
		return response;
	}
	
	public static ResponseEntity<String> postResponse(RestTemplate restTemplate, Map<String, String> headerOptions, String url) {
		logger.debug("ConsumerHelper.postResponse() now working");
		
		HttpHeaders headers = new HttpHeaders();
		headerOptions.forEach((k, v) -> headers.add(k, v));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		if(response == null) {
			logger.error("http post response is null");
		}
		else {
			System.out.println("http post response is not null no problem");
		}
		
		return response;
	}
	
	public static ResponseEntity<String> postResponseForJSONString(RestTemplate restTemplate, Map<String, String> headerOptions, JSONObject request, String url) {
		logger.debug("ConsumerHelper.postRestponseForJSONString() now working");
		
		HttpHeaders headers = new HttpHeaders();
		headerOptions.forEach((k, v) -> headers.add(k, v));
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("JSONString", request.toString());
		
		logger.debug("postResponseForJSONString: " + requestBody.toString());
		
		HttpEntity<MultiValueMap<String, String>> httpRequest = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);
		if(response == null) {
			logger.error("http post response is null");
		}
		else {
			logger.debug("http post response is not null no problem");
		}
		
		return response;
		
	}
	
	public static ResponseEntity<String> postResponse(RestTemplate restTemplate, Map<String, String> headerOptions, MultiValueMap<String, String> body, String url) {
		logger.debug("ConsumerHelper.postResponse() now working");
		
		HttpHeaders headers = new HttpHeaders();
		headerOptions.forEach((k, v) -> headers.add(k, v));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		if(response == null) {
			logger.error("http post response is null");
		}
		else {
			logger.debug("http post response is not null no problem");
		}
		
		return response;
	}
	
	public static ResponseEntity<String> putResponse(RestTemplate restTemplate, Map<String, String> headerOptions, MultiValueMap<String, String> body, String url) {
		logger.debug("ConsumerHelper.putResponse() now working");
		
		HttpHeaders headers = new HttpHeaders();
		headerOptions.forEach((k,v) -> headers.add(k, v));
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
		
		if(response == null) {
			logger.error("http post response is null");
		}
		else {
			logger.debug("http post response is not null no problem");
		}
		
		return response;
	}

}
