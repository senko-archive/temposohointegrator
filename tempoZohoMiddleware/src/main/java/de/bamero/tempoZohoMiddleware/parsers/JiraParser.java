package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
		
		if(jsonNode.has("emailAddress")) {
			jiraUser.setEmailAddress(jsonNode.get("emailAddress").asText());
		}
		else
			jiraUser.setEmailAddress("no email address found on jira");
		
		
		jiraUser.setDisplayName(jsonNode.get("displayName").asText());
		
		logger.debug("jirauser is: " + jiraUser);
		
		// control if jiraUser is already in database and have same record
		// first control if record is in database and if it is, then check the record is the same
		boolean isAvailable = jiraUserService.checkJiraUserById(jiraUser.getAccountId());
		if(isAvailable == true) {
			// check if update is needed and if needed update 
			jiraUserService.update(jiraUser);
			}
		else {
			// no record in database add new one
			// save to the database
			boolean result = jiraUserService.addJiraUser(jiraUser);
		}
		
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
				project.setProjectJiraUri(jsonNode.get("self").asText());
				project.setProjectId(jsonNode.get("id").asText());
				project.setProjectKey(jsonNode.get("key").asText());
				project.setProjectName(jsonNode.get("name").asText());
				// some project jsonNode's don't have projectCategory field
				project.setProjectCategory((jsonNode.get("projectCategory") == null) ? "no category"
						: jsonNode.get("projectCategory").get("name").asText());
				
				// control if jiraProject is already in database and have same record
				// first control if record is in database and if it is, then check the record is the same or not
				boolean isAvailable = jiraProjectService.checkJiraProjectById(project.getProjectId());
				if(isAvailable == true) {
					// check if update is needed and if needed update
					jiraProjectService.update(project);
				}
				else {
					boolean result = jiraProjectService.addJiraProject(project);
				}
				
			}
		}
	}
	
	public void jiraTaskParser(ResponseEntity<String> response, JiraProject jiraProject) {
		logger.debug("JiraParser.jiraTaskParser() now working for project: " + jiraProject.getProjectId() + "-" + jiraProject.getProjectName());
		
		// List for temp jira sub-tasks
		List<JsonNode> tempJiraSubTask = new ArrayList<>();
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			
			if(root.get("issues").isMissingNode()) {
				logger.error("Missing node match, there may be error in query");
				logger.error("response query: " + root.toString());
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
			jiraSubTask.setSummary(jsonNode.get("fields").get("summary").asText());
			
			DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			StringBuilder created = new StringBuilder(jsonNode.get("fields").get("created").asText());
			created.insert(26, ":");
			
			StringBuilder updated = new StringBuilder(jsonNode.get("fields").get("created").asText());
			updated.insert(26, ":");
			
			jiraSubTask.setCreated(LocalDateTime.parse(created.toString(), formatter));
			jiraSubTask.setUpdated(LocalDateTime.parse(updated.toString(), formatter));
			
			// assign users
			// find user from database and add
			if (!jsonNode.get("fields").get("assignee").isNull()) {
				String accountId = jsonNode.get("fields").get("assignee").get("accountId").asText();
				JiraUser assignee = matcher.findJiraUser(accountId);
				if(assignee == null) {
					logger.error("User couldn't found for account id:" + accountId + " in subtask: " + jiraSubTask.getId());
					logger.error("TEMPORARY SOLUTION: Florian.Kurz is added for non-found/deleted user");
					assignee = matcher.findJiraUser("5a168d25233b6b0a02707845");
				}
				jiraSubTask.addAssignee(assignee);
			}
			
			// save jiraSubTask to the db
			boolean result = JiraSubTaskService.addJiraSubTask(jiraSubTask);
		}

	}

	private void parseTaskRequest(JsonNode jsonNode, List<JsonNode> tempJiraSubTask, JiraProject jiraProject) {
		logger.debug("JiraParser.parseTaskRequest() now working for project: " + jiraProject.getProjectId() + "-" + jiraProject.getProjectName());
		
		// if task is not subtask then create jiraTask
		if(!jsonNode.get("fields").get("issuetype").get("subtask").asBoolean()) {
			
			JiraTask jiraTask = new JiraTask();
			jiraTask.setId(jsonNode.get("id").asText());
			jiraTask.setSelf(jsonNode.get("self").asText());
			jiraTask.setKey(jsonNode.get("key").asText());
			jiraTask.setIssueType(jsonNode.get("fields").get("issuetype").get("name").asText());
			// for now I'm putting some dump description
			jiraTask.setDescription("lorem ipsum sit amet");
			jiraTask.setSummary(jsonNode.get("fields").get("summary").asText());
			DateTimeFormatter formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
			
			// for updated and created field add ":" character to timezone
			// it comes from jira like "2019-04-17T10:18:13.611+0200"
			// but should be like "2019-04-17T10:18:13.611+020:0"
			
			StringBuilder created = new StringBuilder(jsonNode.get("fields").get("created").asText());
			created.insert(26, ":");
			
			StringBuilder updated = new StringBuilder(jsonNode.get("fields").get("created").asText());
			updated.insert(26, ":");
			
			ZoneId oldZone = ZoneId.of("GMT+0");
			ZoneId newZone = ZoneId.of("Europe/Berlin");
			
			LocalDateTime createdAt = LocalDateTime.parse(created.toString(), formatter);
			LocalDateTime createdAtForBerlin = createdAt.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();
			
			LocalDateTime updatedAt = LocalDateTime.parse(updated.toString(), formatter);
			LocalDateTime updatedAtForBerlin = updatedAt.atZone(oldZone).withZoneSameInstant(newZone).toLocalDateTime();
			
			jiraTask.setCreated(createdAtForBerlin);
			jiraTask.setUpdated(updatedAtForBerlin);
			
			// assign users
			// find user from database and add
			if (!jsonNode.get("fields").get("assignee").isNull()) {
				String accountId = jsonNode.get("fields").get("assignee").get("accountId").asText();
				JiraUser assignee = matcher.findJiraUser(accountId);
				if(assignee == null) {
					logger.error("User couldn't found for account id:" + accountId + " in subtask: " + jiraTask.getId());
					logger.error("TEMPORARY SOLUTION: Florian.Kurz is added for non-found/deleted user");
					assignee = matcher.findJiraUser("5a168d25233b6b0a02707845");
				}
				jiraTask.addAssignee(assignee);
			}
			
			// assign project
			logger.debug("Adding jiraTask: " + jiraTask.getId() + " to jiraProject: " + jiraProject.getProjectName());
			jiraTask.setJiraProject(jiraProject);
			
			// assign jiraTask in JiraProject
			jiraProject.getJiraTasks().add(jiraTask);
			
			// save jiraTask to the db
			logger.debug("Saving jiraTask to database");
			boolean result = jiraTaskService.addJiraTask(jiraTask);
			
			// find subtask for jira task
			Iterator<JsonNode> subTaskIterator = jsonNode.get("fields").get("subtasks").elements();
			logger.debug("finding subtask for jira task: " + jiraTask.getId());
			while(subTaskIterator.hasNext()) {
				JsonNode jiraSubIssueNode = subTaskIterator.next();
				String subTaskIdinParentTask = jiraSubIssueNode.get("id").asText();
				logger.debug("Jira subtaskId: " + subTaskIdinParentTask + " is searching");
				JiraSubTask jiraSubTask = JiraSubTaskService.getJiraSubTaskByJiraId(subTaskIdinParentTask);
				if(jiraSubTask != null) {
					
					logger.debug("jiraSubTask: " + jiraSubTask.getId() + " found and adding to jiraTask: " + jiraTask.getId());
					jiraSubTask.addJiraTask(jiraTask);
					
					// update jiraSubTask
					JiraSubTaskService.updateJiraSubTask(jiraSubTask);
				}
				else {
					logger.error("NO SUBTASK FOUND with ID: " + subTaskIdinParentTask + "possible error is no recorded subtask in jira, or subtask is deleted.");
				}
				
			}
				
		}
	}

}
