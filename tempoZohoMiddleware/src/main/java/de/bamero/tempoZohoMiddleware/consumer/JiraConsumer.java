package de.bamero.tempoZohoMiddleware.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.parsers.JiraParser;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;


@Component
@Scope("singleton")
public class JiraConsumer {
	
	private static final Logger logger = LoggerFactory.getLogger(JiraConsumer.class);
	
	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	JiraParser jiraParser;
	
	@Autowired 
	JiraProjectService jiraProjectService;
	
	PropertiesLoader loader;
	
	public JiraConsumer() {
		this.loader = new PropertiesLoader();
		
	}
	
	public void consumeJiraUsers() {
		
		logger.debug("JiraUserConsumer.consumeJiraUsers now working");
		
		// take password from properties
		Properties jiraProperties = loader.load("jiraRest.properties");
		String password = jiraProperties.getProperty("password");
		String passwordForRestRequest = "Basic " + password;
		
		// prepare REST request
		String userRequestUrl = "https://bamero.atlassian.net/rest/api/3/group?groupname=jira-software-users&expand=users";
		Map<String, String> requestHeader = new HashMap<>();
		requestHeader.put("Authorization", passwordForRestRequest);
		requestHeader.put("Accept", "application/json");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, userRequestUrl);
		logger.debug("consumeJiraUsers response is: " + response);
		
		parseJiraUserResponse(response);
	}
	
	private boolean parseJiraUserResponse(ResponseEntity<String> response) {
		logger.debug("JiraUserConsumer.parseJiraUserResponse now working");
		
		jiraParser.jiraUserParser(response);
		return true;
		
	}

	
	public void consumeJiraProjects() {
		logger.debug("JiraProjectConsumer.consumeJiraProjects() now working");
		
		// take password from properties
		Properties prop = loader.load("jiraRest.properties");
		String password = prop.getProperty("password");
		String passwordForRestRequest = "Basic " + password;
		
		// prepare REST request
		String userRequestUrl = "https://bamero.atlassian.net/rest/api/3/project/search";
		Map<String, String> requestHeader = new HashMap<>();
		// this password is belogs to selcukcabuk, philipp's password is not list ZOHOTESTPROJECT project
		requestHeader.put("Authorization", "Basic c2VsY3VrLmNhYnVrQGJhbWVyby5kZTpkMWRoaHJqWmhnODEzd2tJVXE5MkE5Q0Y=");
		requestHeader.put("Accept", "application/json");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, userRequestUrl);
		logger.debug("consumeJiraUsers response is: " + response);
		
		parseJiraProjects(response);
		
	}
	
	private boolean parseJiraProjects(ResponseEntity<String> response) {
		logger.debug("JiraProjectConsumer.parseJiraProjects() now working");
		jiraParser.jiraProjectParser(response);
		
		return true;
		
		
	}
	
	public void consumeJiraTasks() {
		
		logger.debug("JiraUserConsumer.consumeJiraTasks() now working");
		// take each project from db and send rest request for tasks.
		// for now just use 1 project
		// also you need to make one to many relationship between project and tasks
		
		// take all projects from db
		List<JiraProject> jiraProjectList = jiraProjectService.getAllJiraProjects();
		
		// try for 1 project only.
		List<JiraProject> dumpList = new ArrayList<>(); 
		// index 28 is ZOHOTESTPROJECT
		dumpList.add(jiraProjectList.get(28));
		
		// take password from properties
		Properties jiraProperties = loader.load("jiraRest.properties");
		String password = jiraProperties.getProperty("password");
		String passwordForRestRequest = "Basic " + password;
		
		for(JiraProject jiraProject : dumpList) {
			logger.info("making request for project: " + jiraProject.getProjectId() + " " + jiraProject.getProjectName());
			
			// prepare url and headers
			StringBuffer jiraTaskRequest = new StringBuffer();
			//jiraTaskRequest.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
			//	.append(jiraProject.getProjectId()).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype");
			
			// test for zohotest project
			jiraTaskRequest.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
			.append(jiraProject.getProjectId()).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype");
			
			
			Map<String, String> requestHeader = new HashMap<>();
			requestHeader.put("Authorization", passwordForRestRequest);
			requestHeader.put("Accept", "application/json");
			
			ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, jiraTaskRequest.toString());
			parseJiraTasks(response, jiraProject);
			
		}
		
	}
	
	private boolean parseJiraTasks(ResponseEntity<String> response, JiraProject jiraProject) {
		logger.debug("JiraProjectConsumer.parseJiraTasks() now working");
		jiraParser.jiraTaskParser(response, jiraProject);
		return false;
	}
}
