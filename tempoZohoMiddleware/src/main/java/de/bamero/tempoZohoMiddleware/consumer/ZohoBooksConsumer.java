package de.bamero.tempoZohoMiddleware.consumer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksProjectService;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.parsers.ZohoBooksParser;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;

@Component
@Scope(value="singleton")
@Slf4j
public class ZohoBooksConsumer {
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	ZohoBooksParser zohoBooksParser;
	
	@Autowired
	ZohoBooksProjectService zohoBooksProjectService;
	
	private PropertiesLoader loader;
	
	public ZohoBooksConsumer() {
		this.loader = new PropertiesLoader();
		
	}
	
	public void consumeZohoBooksProjects() {
		log.debug("ZohoBooksConsumer.consumeZohoBooksProjects() now working");
		
		// take access_token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// prepare rest request
		String zohobooksProjectRequest = "https://books.zoho.eu/api/v3/projects?organization_id=20065018387";
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
		requestHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, zohobooksProjectRequest);
		log.debug("consumeZohoBooksProjects() response is: " + response);
		
		List<String> zohoBooksProjectIdList = getZohoBooksProjectIdList(response);
		parseZohoBooksProjects(zohoBooksProjectIdList);
	}

	private List<String> getZohoBooksProjectIdList(ResponseEntity<String> response) {
		log.debug("ZohoBooksConsumer.parseZohoBooksProjects() now working");
		return zohoBooksParser.getZohoBooksProjectList(response);
		
	}

	private boolean parseZohoBooksProjects(List<String> projectIdList) {
		log.debug("ZohoBooksConsumer.parseZohoBooksProjects() now working");
		
		// prepare rest request for getting info for ZohoBooks projects
		projectIdList.forEach(id -> {
			consumeZohoBookProjectById(id);
		});
		
		
		//zohoBooksParser.zohoBooksProjectParser(projectIdList);
		return true;
		
	}

	private void consumeZohoBookProjectById(String id) {
		log.debug("ZohoBooksConsumer.consumeZohoBookProjectById() now working");
		
		// take access_token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// prepare rest request
		StringBuilder zohobooksProjectByIdRequest = new StringBuilder();
		zohobooksProjectByIdRequest.append("https://books.zoho.eu/api/v3/projects/").append(id).append("?organization_id=20065018387");
				
		// prepare headers
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
		requestHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, zohobooksProjectByIdRequest.toString());
		zohoBooksParser.parseZohoBooksProjects(response);
	}

	public void consumeZohoBooksUsers() {
		log.debug("ZohoBooksConsumer.consumeZohoBooksUsers() now working");
		
		// take access_token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// prepare rest request
		String zohobooksUsersRequest = "https://books.zoho.eu/api/v3/users?organization_id=20065018387";
		
		// prepare headers
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
		requestHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, zohobooksUsersRequest);
		
		zohoBooksParser.zohoBooksUsersParser(response);
		
	}
	
	public void consumeZohoBooksTasks() {
		log.debug("ZohoBooksConsumer.consumeZohoBooksTasks() now working");
		
		// take access_token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// take all zohoBooksProjectsId's from db
		
		zohoBooksProjectService.getAllZohoBooksProjects().forEach(project -> {
			// prepare rest request
			StringBuilder zohoBooksTaskRequest = new StringBuilder("https://books.zoho.eu/api/v3/projects/");
			zohoBooksTaskRequest.append(project.getProjectId()).append("/tasks?organization_id=20065018387");
			
			// prepare headers
			Map<String, String> requestHeader = new HashMap<>();
			requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
			requestHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			
			ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, zohoBooksTaskRequest.toString());
			zohoBooksParser.zohoBooksTaskParser(response, project);
			
		});
		
	}
	
	public void consumeOneZohoBooksTask(ZohoBooksProject project, String taskId) {
		log.debug("ZohoBooksConsumer.consumeOneZohoBooksTask() now working");
		
		// take access_token from properties
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String passwordForOAuth2ZohoBooks = "Zoho-oauthtoken " + access_token;
		
		// prepare rest request
		StringBuilder zohoBooksTaskRequest = new StringBuilder("https://books.zoho.eu/api/v3/projects/")
				.append(project.getProjectId()).append("/tasks/").append(taskId).append("?organization_id=20065018387");
		
		// prepare headers
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForOAuth2ZohoBooks);
		requestHeader.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
	
		
		// burda gidip bu task in bagli oldugunu projenin object'ini de alman gerekiyor
		//ZohoBooksProject zohoBooksProject = zohoBooksProjectService.getZohoBooksProjectByProjectId(projectId);
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, zohoBooksTaskRequest.toString());
		zohoBooksParser.zohoBooksTaskParser(response, project);
		
	}

}
