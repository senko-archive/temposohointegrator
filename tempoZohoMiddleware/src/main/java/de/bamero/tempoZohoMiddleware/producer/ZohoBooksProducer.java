package de.bamero.tempoZohoMiddleware.producer;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope(value="singleton")
@Slf4j
public class ZohoBooksProducer {
	
	@Autowired
	RestTemplate restTemplate;
	
	private PropertiesLoader loader;
	
	public ZohoBooksProducer() {
		this.loader = new PropertiesLoader();	
	}
	
	public String createZohoBooksTask(Map<String, String> zohoTaskValues, String zohoProjectId) {
		log.debug("ZohoBooksProducer.createZohoBooksTask is now working");
		
		// take access token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// create JSONString for POST request
		JSONObject request = new JSONObject();
		request.put("task_name", zohoTaskValues.get("taskName"));
		request.put("description", zohoTaskValues.get("description"));
		request.put("rate", "");
		request.put("budget_hours", "");
		
		log.debug("in createZohoBooksTask requestJSON is: " + request.toString());
		
		// prepare POST request
		StringBuilder zohoBooksIssuePostRequest = new StringBuilder("https://books.zoho.eu/api/v3/projects/")
				.append(zohoProjectId).append("/tasks/?organization_id=20065018387");
		
		// create and set header
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
		requestHeader.put("Content-Type", "application/x-www-form-urlencoded");
		
		// send POST response 
		ResponseEntity<String> response = ConsumerHelper.postResponseForJSONString(restTemplate, requestHeader, request, zohoBooksIssuePostRequest.toString());

		String zohoBooksTaskId = getZohoBooksTaskId(response);
		
		return zohoBooksTaskId;
	}
	
	private String getZohoBooksTaskId(ResponseEntity<String> response) {
		log.debug("ZohoBooksProducer.getZohoBooksTaskId is now working");
		
		String zohoBooksTaskId = null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			zohoBooksTaskId = root.get("task").get("task_id").asText();
		}
		catch (Exception e) {
			log.error("Error on zohoBooksProjectParser: " + e.getMessage());	
		}
		return zohoBooksTaskId;
	}

}
