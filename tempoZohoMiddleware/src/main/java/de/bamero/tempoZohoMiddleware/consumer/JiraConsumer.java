package de.bamero.tempoZohoMiddleware.consumer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.dataService.JiraTaskService;
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
	
	@Autowired
	JiraTaskService jiraTaskService;
	
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
		String userRequestUrl = "https://bamero.atlassian.net/rest/api/3/project/search?startAt=0&maxResults=100";
		Map<String, String> requestHeader = new HashMap<>();
		// this password is belogs to selcukcabuk, philipp's password is not list ZOHOTESTPROJECT project
		requestHeader.put("Authorization", "Basic c2VsY3VrLmNhYnVrQGJhbWVyby5kZTpkMWRoaHJqWmhnODEzd2tJVXE5MkE5Q0Y=");
		requestHeader.put("Accept", "application/json");
		
		ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, userRequestUrl);
		//logger.debug("consumeJiraUsers response is: " + response);
		
		parseJiraProjects(response);
		
		AbstractMap.SimpleEntry<Boolean, Integer> result = checkConsumeJiraProjectResponse(response);
		
		if(result.getKey() == true) {
			
			while(result.getKey() == true) {
				// until key become false do
				String newRequestUrl = "https://bamero.atlassian.net/rest/api/3/project/search?startAt="+result.getValue()+"&maxResults=100";
				response = ConsumerHelper.getResponse(restTemplate, requestHeader, newRequestUrl);
				parseJiraProjects(response);
				
				result = checkConsumeJiraProjectResponse(response);
			}
				
		}
		
				
		// burda ilkini alirken maxresults startAt ve total i de alicaksin
		// ilki 0 dan maxResults a kadar gidecek, eger total maxResulta dan buyukse bir request daha gonderceksin
		// bu da maxResults + 1 den baslayip gidecek eger yine buyukse yine bir request gonderceksin
		// bunu bir loop icinde yap
		
		
	}
	
	private SimpleEntry<Boolean,Integer> checkConsumeJiraProjectResponse(ResponseEntity<String> response) {
		
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		int newStartAtIndex = 0;
		Boolean oneMoreQuery = false;
		try {
			root = mapper.readTree(response.getBody());
			int maxResults = root.get("maxResults").asInt();
			int startAt = root.get("startAt").asInt();
			int total = root.get("total").asInt();
			
			if((startAt + maxResults) < total) {
				oneMoreQuery = true;
				newStartAtIndex = (startAt + maxResults) + 1;
			}
			else if(newStartAtIndex < total && (newStartAtIndex + maxResults) > total) {
				oneMoreQuery = true;
			}
			else {
				oneMoreQuery = false;
	
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		AbstractMap.SimpleEntry<Boolean, Integer> returnPair = new AbstractMap.SimpleEntry<Boolean, Integer>(oneMoreQuery, newStartAtIndex); 
		return returnPair;
	}

	private boolean parseJiraProjects(ResponseEntity<String> response) {
		logger.debug("JiraProjectConsumer.parseJiraProjects() now working");
		jiraParser.jiraProjectParser(response);
		
		return true;
		
		
	}
	
	public void consumeJiraTasks() {
		
		logger.debug("JiraUserConsumer.consumeJiraTasks() now working");
		
		// before fetching data from Jira Rest truncate the tables related with tasks
		jiraTaskService.truncateTask();
		
		// take each project from db and send rest request for tasks.
		// for now just use 1 project
		// also you need to make one to many relationship between project and tasks
		
		// take all projects from db
		List<JiraProject> jiraProjectList = jiraProjectService.getAllJiraProjects();
		
		
		// try for 1 project only.
		List<JiraProject> dumpList = new ArrayList<>(); 
		
		// list all project with ids
				dumpList.add(
						jiraProjectList.stream().filter(e -> e.getProjectId().equals("10184")).findFirst().get()
				);
				
	    // add zohoTest2 and zohoTest3 jira projects
				dumpList.add(
						jiraProjectList.stream().filter(e -> e.getProjectId().equals("10186")).findFirst().get()
						);
				
				dumpList.add(
						jiraProjectList.stream().filter(e -> e.getProjectId().equals("10187")).findFirst().get()
						);
		
		// index 28 is ZOHOTESTPROJECT
		//dumpList.add(jiraProjectList.get(28));
		
		// index 26 is XPM Project
		//dumpList.add(jiraProjectList.get(26));
		
		
		
		
		// take password from properties
		Properties jiraProperties = loader.load("jiraRest.properties");
		String password = jiraProperties.getProperty("password");
		String passwordForRestRequest = "Basic " + password;
		
		// for taking only zohotest project
		/*
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
			
		}*/
		
		
		// takes tasks for all projects
		for(JiraProject jiraProject : dumpList) {
			logger.info("making request for project: " + jiraProject.getProjectId() + " " + jiraProject.getProjectName());
			
			// prepare url and headers
			StringBuffer jiraTaskRequest = new StringBuffer();
			//jiraTaskRequest.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
			//	.append(jiraProject.getProjectId()).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype");
			
			// get all task for project
			// get initial set
			String maxResults = "50";
			
			// find the last month of today
			LocalDate lastMonth = LocalDate.now().minusMonths(1);
			String lastMonthAsString = lastMonth.toString().trim();
			
			/*
			// tasks from all times
			jiraTaskRequest.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
			.append(jiraProject.getProjectId()).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype,created,updated,summary")
			.append("&startAt=0").append("&maxResults=").append(maxResults);
			*/
			
			
			// tasks from 1 month before
			jiraTaskRequest.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
			.append(jiraProject.getProjectId()).append(" AND createdDate >= ").append(lastMonthAsString).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype,created,updated,summary")
			.append("&startAt=0").append("&maxResults=").append(maxResults);
			
			
			logger.debug("Prepared uri initial: "+ jiraTaskRequest.toString() );
			
			Map<String, String> requestHeader = new HashMap<>();
			requestHeader.put("Authorization", passwordForRestRequest);
			requestHeader.put("Accept", "application/json");
			
			try {
				ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, jiraTaskRequest.toString());
				int totalRecord = calculateTotalRecords(response);
				logger.debug("totalRecord is: " + totalRecord);
				parseJiraTasks(response, jiraProject);
				
				for(int index=Integer.parseInt(maxResults)+1; index <= totalRecord; index = index + Integer.parseInt(maxResults)) {
					StringBuffer jiraTaskRequestCont = new StringBuffer();
					jiraTaskRequestCont.append("https://bamero.atlassian.net/rest/api/3/search?jql=project=")
					.append(jiraProject.getProjectId()).append("&").append("fields=id,self,key,assignee,description,subtasks,issuetype,created,updated")
					.append("&startAt=").append(Integer.toString(index)).append("&maxResults=").append(maxResults);
					
					logger.debug("Prepared uri in for loop: " + jiraTaskRequestCont.toString());
					
					ResponseEntity<String> response2 = ConsumerHelper.getResponse(restTemplate, requestHeader, jiraTaskRequest.toString());
					parseJiraTasks(response2, jiraProject);
				}
				
				
				
				
			}catch (RestClientException e) {
				logger.error("ERROR on project: " + jiraProject.getProjectId() + " for gettings tasks");
				logger.error("ERROR description: " + e.getMessage());
			}
			
			
		}
		
	}
	
	private int calculateTotalRecords(ResponseEntity<String> response) {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root;
		int result = 0;
		try {
			root = mapper.readTree(response.getBody());
			result = root.get("total").asInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private boolean parseJiraTasks(ResponseEntity<String> response, JiraProject jiraProject) {
		logger.debug("JiraProjectConsumer.parseJiraTasks() now working");
		jiraParser.jiraTaskParser(response, jiraProject);
		return false;
	}
}
