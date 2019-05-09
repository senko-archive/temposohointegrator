package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


import de.bamero.tempoZohoMiddleware.dataService.IJiraProjectService;
import de.bamero.tempoZohoMiddleware.dataService.IJiraSubTaskService;
import de.bamero.tempoZohoMiddleware.dataService.IJiraTaskService;
import de.bamero.tempoZohoMiddleware.dataService.IJiraUserService;
import de.bamero.tempoZohoMiddleware.dataService.JiraSubTaskService;
import de.bamero.tempoZohoMiddleware.dataService.JiraUserService;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraUser;
import de.bamero.tempoZohoMiddleware.utils.Matcher;

@Component
public class JiraParser {
	
	private static final Logger logger = LoggerFactory.getLogger(JiraParser.class);
	
	@Autowired
	IJiraUserService jiraUserService;
	
	@Autowired
	IJiraProjectService jiraProjectService;
	
	@Autowired
	IJiraTaskService jiraTaskService;
	
	@Autowired
	IJiraSubTaskService JiraSubTaskService;
	
	@Autowired
	Matcher matcher;
	
	public void jiraUserParser(ResponseEntity<String> response) {
		logger.debug("JiraParser.jiraUserParser() now working");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode listOfUsers = root.get("users").get("items");
			Iterator<JsonNode> iter = listOfUsers.elements();
			
			while(iter.hasNext()) {
				
				parseUserRequest(iter.next());	
			}
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void parseUserRequest(JsonNode jsonNode) {
		logger.debug("JiraParser.parseUserRequest() now working");
		
		JiraUser jiraUser = new JiraUser();
		jiraUser.setUserJiraUri(jsonNode.get("self").asText());
		jiraUser.setKey(jsonNode.get("key").asText());
		jiraUser.setAccountId(jsonNode.get("accountId").asText());
		jiraUser.setName(jsonNode.get("name").asText());
		jiraUser.setEmailAddress(jsonNode.get("emailAddress").asText());
		jiraUser.setDisplayName(jsonNode.get("displayName").asText());
		
		logger.debug("jirauser is: " + jiraUser);
		
		// save to the database
		boolean result = jiraUserService.addJiraUser(jiraUser);
	}

	public void jiraProjectParser(ResponseEntity<String> response) {
		logger.debug("JiraParser.jiraProjectParser() now working");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode results = root.get("values");
			
			Iterator<JsonNode> iter = results.iterator();
			
			while(iter.hasNext()) {	
				parseProjectRequest(iter.next());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private void parseProjectRequest(JsonNode jsonNode) {
		logger.debug("JiraParser.parseProjectRequest() now working");
		
		// check node has "projectCategory" field
		if (jsonNode.has("projectCategory")) {
			// add only external projects
			if (jsonNode.get("projectCategory").get("name").asText().equalsIgnoreCase("external")) {
				JiraProject project = new JiraProject();
				project.setProjectJiraUri(jsonNode.get("self").toString());
				project.setProjectId(jsonNode.get("id").toString());
				project.setProjectKey(jsonNode.get("key").toString());
				project.setProjectName(jsonNode.get("name").toString());
				// some project jsonNode's don't have projectCategory field
				project.setProjectCategory((jsonNode.get("projectCategory") == null) ? "no category"
						: jsonNode.get("projectCategory").get("name").asText());
				
				boolean result = jiraProjectService.addJiraProject(project);
			}
		}
	}
	
	public void jiraTaskParser(ResponseEntity<String> response, JiraProject jiraProject) {
		logger.debug("JiraParser.jiraTaskParser() now working");
		
		// List for temp jira sub-tasks
		List<JsonNode> tempJiraSubTask = new ArrayList<>();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			
			if(root.get("issues").isMissingNode()) {
				logger.debug("Missing node match, there may be error in query");
				logger.debug("response query: " + root.toString());
			}
			else {
				JsonNode issues = root.get("issues");
				
				// Iterate for sub Issues
				Iterator<JsonNode> iterForSubTasks = issues.elements();
				while(iterForSubTasks.hasNext()) {
					JsonNode next = iterForSubTasks.next();
					parseJiraSubTask(next, tempJiraSubTask);
	
				}
				
				// Iterate for main Issues
				Iterator<JsonNode> iterForTasks = issues.elements();
				while(iterForTasks.hasNext()) {
					parseTaskRequest(iterForTasks.next(), tempJiraSubTask, jiraProject);
				}
			}
			
		} catch (IOException e) {
			logger.error("jiraTaskParser error on reading responseBody");
			e.printStackTrace();
		}
		
	}
	
	private void parseJiraSubTask(JsonNode jsonNode, List<JsonNode> tempJiraSubTask) {
		logger.debug("JiraParser.parseJiraSubTask() now working");
		// parse sub tasks only
		if (jsonNode.get("fields").get("issuetype").get("subtask").asBoolean()) {
			tempJiraSubTask.add(jsonNode);
			
			// create new JiraSubTask instance
			JiraSubTask jiraSubTask = new JiraSubTask();
			jiraSubTask.setId(jsonNode.get("id").asText());
			jiraSubTask.setKey(jsonNode.get("key").asText());
			jiraSubTask.setSelf(jsonNode.get("self").asText());
			// Issue Type will be always "Sub-task"
			jiraSubTask.setIssueType("Sub-task");
			
			// assign users
			// find user from database and add
			if (!jsonNode.get("fields").get("assignee").isNull()) {
				String accountId = jsonNode.get("fields").get("assignee").get("accountId").asText();
				JiraUser assignee = matcher.findJiraUser(accountId);
				jiraSubTask.addAssignee(assignee);
			}
			
			// save jiraSubTask to the db
			boolean result = JiraSubTaskService.addJiraSubTask(jiraSubTask);
		}

	}

	private void parseTaskRequest(JsonNode jsonNode, List<JsonNode> tempJiraSubTask, JiraProject jiraProject) {
		logger.debug("JiraParser.parseTaskRequest() now working");
		
		// if task is not subtask then create jiraTask
		if(!jsonNode.get("fields").get("issuetype").get("subtask").asBoolean()) {
			
			JiraTask jiraTask = new JiraTask();
			jiraTask.setId(jsonNode.get("id").asText());
			jiraTask.setSelf(jsonNode.get("self").asText());
			jiraTask.setKey(jsonNode.get("key").asText());
			jiraTask.setIssueType(jsonNode.get("fields").get("issuetype").get("name").asText());
			// for now I'm putting some dump description
			jiraTask.setDescription("lorem ipsum sit amet");
			
			// assign users
			// find user from database and add
			if (!jsonNode.get("fields").get("assignee").isNull()) {
				String accountId = jsonNode.get("fields").get("assignee").get("accountId").asText();
				JiraUser assignee = matcher.findJiraUser(accountId);
				jiraTask.addAssignee(assignee);
			}
			
			// assign project
			jiraTask.setJiraProject(jiraProject);
			
			// assign jiraTask in JiraProject
			jiraProject.getJiraTasks().add(jiraTask);
			
			// save jiraTask to the db
			boolean result = jiraTaskService.addJiraTask(jiraTask);
			
			// find subtask for jira task
			Iterator<JsonNode> subTaskIterator = jsonNode.get("fields").get("subtasks").elements();
			while(subTaskIterator.hasNext()) {
				JsonNode jiraSubIssueNode = subTaskIterator.next();
				String subTaskIdinParentTask = jiraSubIssueNode.get("id").asText();
				JiraSubTask jiraSubTask = JiraSubTaskService.getJiraSubTaskByJiraId(subTaskIdinParentTask);
				jiraSubTask.addJiraTask(jiraTask);
				
				// update jiraSubTask
				JiraSubTaskService.updateJiraSubTask(jiraSubTask);
			}
				
		}
	}

}
