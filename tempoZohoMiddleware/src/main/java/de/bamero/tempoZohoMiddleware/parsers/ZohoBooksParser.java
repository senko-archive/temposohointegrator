package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksProjectService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksTaskService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksTimeEntryService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksUserService;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;

@Component
public class ZohoBooksParser {

	private static final Logger logger = LoggerFactory.getLogger(ZohoBooksParser.class);

	@Autowired
	ZohoBooksProjectService zohoBooksProjectService;

	@Autowired
	ZohoBooksUserService zohoBooksUserService;
	
	@Autowired
	ZohoBooksTaskService zohoBooksTaskService;

	@Autowired
	JiraProjectService jiraProjectService;
	
	@Autowired
	ZohoBooksTimeEntryService zohoBooksTimeEntryService;
	
	

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

	/*
	 * public void zohoBooksProjectParser(ResponseEntity<String> response) {
	 * logger.debug("ZohoBooksParser.zohoBooksProjectParser() now working");
	 * 
	 * try { ObjectMapper mapper = new ObjectMapper(); JsonNode root; root =
	 * mapper.readTree(response.getBody()); JsonNode results = root.get("projects");
	 * 
	 * Iterator<JsonNode> iter = results.iterator();
	 * 
	 * while(iter.hasNext()) { parseZohoBooksProjects(iter.next()); } } catch
	 * (Exception e) { logger.error("Error on zohoBooksProjectParser: " +
	 * e.getMessage()); } }
	 */

	public void parseZohoBooksProjects(ResponseEntity<String> response) {
		logger.debug("ZohoBooksParser.parseZohoBooksProjects() now working");

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode jsonNode = root.get("project");

			// parse JSON body
			logger.debug("parseZohoBooksProjects is working for:" + jsonNode.get("project_name").asText());

			ZohoBooksProject zohoBooksProject = new ZohoBooksProject();
			zohoBooksProject.setProjectId(jsonNode.get("project_id").asText());
			zohoBooksProject.setProjectName(jsonNode.get("project_name").asText());
			zohoBooksProject.setCustomerId(jsonNode.get("customer_id").asText());
			zohoBooksProject.setCustomerName(jsonNode.get("customer_name").asText());
			zohoBooksProject.setDescription(jsonNode.get("description").asText());
			zohoBooksProject.setStatus(jsonNode.get("status").asText());
			zohoBooksProject.setBillingType(jsonNode.get("billing_type").asText());

			if (jsonNode.has("rate")) {
				zohoBooksProject.setRate(jsonNode.get("rate").asLong());
			} else {
				zohoBooksProject.setRate(new Long(0));
			}

			String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
			try {
				zohoBooksProject.setCreatedTime(simpleDateFormat.parse(jsonNode.get("created_time").asText()));
			} catch (ParseException e) {
				logger.debug("parseZohoBooksProjects error on parsing created_time: " + e.getMessage());
			}

			zohoBooksProject.setTotal_hours(jsonNode.get("total_hours").asText());
			zohoBooksProject.setBilled_hours(jsonNode.get("billed_hours").asText());
			zohoBooksProject.setUnbilled_hours(jsonNode.get("un_billed_hours").asText());
			zohoBooksProject.setBillable_hours(jsonNode.get("billable_hours").asText());
			zohoBooksProject.setNon_billable_hours(jsonNode.get("non_billable_hours").asText());
			
			// get ZohoBooksUsers from Project
			JsonNode zohoBooksUserNode = jsonNode.get("users");
			if(zohoBooksUserNode.isArray()) {
				logger.debug("parseZohoBooksProjects -> users is array in zohoBookProject response");
				Iterator<JsonNode> usersIter = zohoBooksUserNode.iterator();
				Set<ZohoBooksUser> zohoBooksUsersSet = new HashSet<>();
				while (usersIter.hasNext()) {
					ZohoBooksUser zohoBooksUser = parseZohoBookUserInsideProjectResponse(usersIter.next());
					zohoBooksUsersSet.add(zohoBooksUser);
				}
				
				zohoBooksProject.setZohoBooksUsers(zohoBooksUsersSet);
				
			}
			
			else {
				logger.error("Project id: " + zohoBooksProject.getProjectId() + " has not users list");
			}
			
			
			

			// get jira-key from custom_fields list
			zohoBooksProject.setJiraKey(getJiraKey(jsonNode.get("custom_fields")));

			// find proper jira project id for zoho project by using zohoProject jiraKey
			// keyword
			if (!zohoBooksProject.getJiraKey().isEmpty()) {

				// there may case that, zohoBooks jirakey have multiple keys
				List<String> jiraKeys = Arrays.asList(zohoBooksProject.getJiraKey().split(";"));

				// look in db for matching jirakey and return matching jira project Id's
				List<String> jiraProjectIds = jiraKeys.stream().map(key -> {

					Optional<JiraProject> jiraProject = jiraProjectService.getJiraProjectByKey(key.toUpperCase());

					if (jiraProject.isPresent()) {
						return jiraProject.get().getProjectId();
					} else {
						logger.warn("With this jirakey: " + key + " no project found in jira");
						return null;
					}
					// return
					// jiraProjectService.getJiraProjectByKey(key.toUpperCase()).getProjectId();
				}).collect(Collectors.toList());

				// add jira project id's to zohoProject entity
				zohoBooksProject.setJiraProjectIds(jiraProjectIds);

			} else {
				// add empty list
				List<String> emptyjiraProjectIds = new ArrayList<>();
				// emptyjiraProjectIds.add("");
				zohoBooksProject.setJiraProjectIds(emptyjiraProjectIds);
			}

			// before add zohoBooksProject to database first control if zohoBooksProject is already in database and have same record
			// first control if record is in database, if it is, check the record needs to be updated or not
			boolean isAvailable = zohoBooksProjectService.checkZohoBooksProjectById(zohoBooksProject.getProjectId());
			if(isAvailable == true) {
				// check if update is needed and if it is, update
				zohoBooksProjectService.update(zohoBooksProject);
			}
			else {
				zohoBooksProjectService.addZohoBooksProject(zohoBooksProject);
			}
			//zohoBooksProjectService.clear();
			//zohoBooksProjectService.addZohoBooksProjectWithoutCRUD(zohoBooksProject);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ZohoBooksUser parseZohoBookUserInsideProjectResponse(JsonNode jsonNode) {
		String userId = jsonNode.get("user_id").asText();
		ZohoBooksUser user = zohoBooksUserService.getZohoBooksUserByUserId(userId);
		return user;
	}

	// take jira_key from custom_fields list
	private String getJiraKey(JsonNode customFieldsList) {
		logger.debug("ZohoBooksParser.getJiraKey() now working");

		Iterator<JsonNode> iter = customFieldsList.iterator();
		while (iter.hasNext()) {
			JsonNode customField = iter.next();

			if (customField.has("label") && customField.get("label").asText().equalsIgnoreCase("jira-key")) {
				return customField.get("value").asText();
			}
		}

		return "";
	}

	public List<String> getZohoBooksProjectList(ResponseEntity<String> response) {
		logger.debug("ZohoBooksParser.getZohoBooksProjectList() now working");

		List<String> zohoBooksProjectList = new ArrayList<>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode results = root.get("projects");

			Iterator<JsonNode> iter = results.iterator();

			while (iter.hasNext()) {
				extractZohoBooksProjectIds(iter.next(), zohoBooksProjectList);
			}
		} catch (Exception e) {
			logger.error("Error on zohoBooksProjectParser: " + e.getMessage());
		}

		return zohoBooksProjectList;

	}

	private void extractZohoBooksProjectIds(JsonNode jsonNode, List<String> zohoBooksProjectList) {
		logger.debug("ZohoBooksParser.extractZohoBooksProjectIds() now working");
		logger.debug("extractZohoBooksProjectIds is working for:" + jsonNode.get("project_name").asText());

		zohoBooksProjectList.add(jsonNode.get("project_id").asText());
	}

	public void zohoBooksUsersParser(ResponseEntity<String> response) {
		logger.debug("ZohoBooksParser.parseZohoBooksUsers() now working");

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode results = root.get("users");

			Iterator<JsonNode> iter = results.iterator();

			while (iter.hasNext()) {
				parseZohoBooksUsers(iter.next());
			}
		} catch (Exception e) {
			logger.debug("Error on parseZohoBooksUsers: " + e.getMessage());
		}

	}

	private void parseZohoBooksUsers(JsonNode jsonNode) {
		logger.debug("ZohoBooksParser.parseZohoBooksUsers() now working");

		ZohoBooksUser zohoBooksUser = new ZohoBooksUser();
		zohoBooksUser.setUserId(jsonNode.get("user_id").asText());
		zohoBooksUser.setRole_id(jsonNode.get("role_id").asText());
		zohoBooksUser.setName(jsonNode.get("name").asText());
		zohoBooksUser.setEmail(jsonNode.get("email").asText());
		zohoBooksUser.setUser_role(jsonNode.get("user_role").asText());
		zohoBooksUser.setUser_type(jsonNode.get("user_type").asText());
		zohoBooksUser.setIs_employee(jsonNode.get("is_employee").asBoolean());
		
		// control if zohobooksUser is already in database and have same record
		// first control if record is in database and if it is, then check the record is same or not
		boolean isAvailable = zohoBooksUserService.checkZohoBooksUserById(zohoBooksUser.getUserId());
		if(isAvailable == true) {
			// check if update is needed and if it is, update
			zohoBooksUserService.update(zohoBooksUser);
		}
		else {
			zohoBooksUserService.addZohoBooksUser(zohoBooksUser);
		}

	}

	public void zohoBooksTaskParser(ResponseEntity<String> response, ZohoBooksProject project) {
		logger.debug("ZohoBooksParser.zohoBooksTaskParser() now working");
		
		ZohoBooksTask zohoBooksTask;

		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode results = root.get("task");
			
			// if getTask worked for only 1 element then result is not an array.
			if(results.isArray()) {
				Iterator<JsonNode> iter = results.iterator();

				while (iter.hasNext()) {
					parseZohoBooksTasks(iter.next(), project);
				}
			}
			else {
				parseZohoBooksTasks(results, project);
			}


			
		} catch (Exception e) {
			logger.error("error on parseZohoBooksTasks:" + e.getMessage());
		}
	}

	private void parseZohoBooksTasks(JsonNode jsonNode, ZohoBooksProject project) {
		logger.debug("ZohoBooksParser.parseZohoBooksTasks() now working");
		
		ZohoBooksTask zohoBookTask = new ZohoBooksTask();
		zohoBookTask.setZohoBooksTaskId(jsonNode.get("task_id").asText());
		zohoBookTask.setProjectId(jsonNode.get("project_id").asText());
		zohoBookTask.setTaskName(jsonNode.get("task_name").asText());
		zohoBookTask.setDescription(jsonNode.get("description").asText());
		zohoBookTask.setProjectName(jsonNode.get("project_name").asText());
		zohoBookTask.setIsBillable(jsonNode.get("is_billable").asBoolean());
		
		if(jsonNode.has("currency_id")) {
			zohoBookTask.setCurrencyId(jsonNode.get("currency_id").asText());
		}
		else {
			// currency of euro = 54386000000000071
			zohoBookTask.setCurrencyId("54386000000000071");
		}

		if(jsonNode.has("customer_id")) {
			zohoBookTask.setCustomerId(jsonNode.get("customer_id").asText());
		}
		else {
			zohoBookTask.setCustomerId(project.getCustomerId());
		}
		
		if(jsonNode.has("customer_name")) {
			zohoBookTask.setCustomerName(jsonNode.get("customer_name").asText());
		}
		else {
			zohoBookTask.setCustomerName(project.getCustomerName());
		}
		
		if(jsonNode.has("billed_hours")) {
			zohoBookTask.setBilledHours(jsonNode.get("billed_hours").asText());
		}
		else {
			zohoBookTask.setBilledHours("0.0");
		}
		if(jsonNode.has("log_time")) {
			zohoBookTask.setLogTime(jsonNode.get("log_time").asText());
		}
		else {
			zohoBookTask.setLogTime("0.0");
		}
		
		if(jsonNode.has("un_billed_hours")) {
			zohoBookTask.setUnbilledHours(jsonNode.get("un_billed_hours").asText());
		}
		else {
			zohoBookTask.setUnbilledHours("0.0");
		}
		if(jsonNode.has("rate")) {
			zohoBookTask.setRate(jsonNode.get("rate").asText());
		}
		else {
			zohoBookTask.setRate("0.0");
		}
		if(jsonNode.has("status")) {
			zohoBookTask.setStatus(jsonNode.get("status").asText());
		}
		else {
			zohoBookTask.setStatus("active");
		}
		
		// here implement check / update mechanisim
		boolean isAvailable = zohoBooksTaskService.checkZohoBooksTaskByzohoBooksTaskId(zohoBookTask.getZohoBooksTaskId());
		if(isAvailable == true) {
			// check if update is needed and if it is, update
			zohoBooksTaskService.update(zohoBookTask);
		}
		else {
			zohoBookTask.addTask(project);
			zohoBooksTaskService.addZohoBooksTask(zohoBookTask);
		}
		
		
		
	
		

	}

	public void zohoBooksTimeEntryParser(ResponseEntity<String> response, String jiraWorkLogId, ZohoBooksTask zohoBooksTask) {
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode result = root.get("time_entry");
			
			parseZohoBooksTimeEntry(result, jiraWorkLogId, zohoBooksTask);
		}
		catch (Exception e) {
			logger.error("error on parseZohoBooksTimeEntry: " + e.getMessage());
		}
	}
	
	private void parseZohoBooksTimeEntry(JsonNode jsonNode, String jiraWorkLogId, ZohoBooksTask zohoBooksTask) {
		logger.debug("parseZohoBooksTimeEntry() now working ");
		
		ZohoBooksTimeEntry zohoBooksTimeEntry = new ZohoBooksTimeEntry();
		zohoBooksTimeEntry.setTimeEntryId(jsonNode.get("time_entry_id").asText());
		zohoBooksTimeEntry.setProjectId(jsonNode.get("project_id").asText());
		zohoBooksTimeEntry.setProjectName(jsonNode.get("project_name").asText());
		zohoBooksTimeEntry.setTaskId(jsonNode.get("task_id").asText());
		zohoBooksTimeEntry.setTaskName(jsonNode.get("task_name").asText());
		zohoBooksTimeEntry.setUserId(jsonNode.get("user_id").asText());
		zohoBooksTimeEntry.setUserName(jsonNode.get("user_name").asText());
		zohoBooksTimeEntry.setCustomerId(jsonNode.get("customer_id").asText());
		zohoBooksTimeEntry.setCustomerName(jsonNode.get("customer_name").asText());
		zohoBooksTimeEntry.setIsBillable(jsonNode.get("is_billable").asBoolean());
		zohoBooksTimeEntry.setBilledStatus(jsonNode.get("billed_status").asText());
		zohoBooksTimeEntry.setInvoiceId(jsonNode.get("invoice_id").asText());
		zohoBooksTimeEntry.setInvoiceNumber(jsonNode.has("invoice_number") ? jsonNode.get("invoice_number").asText() : "no invoice number");
		
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
	    LocalDate logDate = LocalDate.parse(jsonNode.get("log_date").asText(), dateFormatter);
	    zohoBooksTimeEntry.setLogDate(logDate);
	    
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    LocalTime logTime = LocalTime.parse(jsonNode.get("log_time").asText(), timeFormatter);
	    zohoBooksTimeEntry.setLogTime(logTime);
	    
	    //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	    
		String pattern = "yyyy-MM-dd'T'HH:mm:ssZ";
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
	    //String date = jsonNode.get("created_time").asText();
	    
	    LocalDateTime createdTime = LocalDateTime.parse(jsonNode.get("created_time").asText(), dateTimeFormatter);
	    zohoBooksTimeEntry.setCreatedTime(createdTime);
	   
	    zohoBooksTimeEntry.setUpdatedTime(createdTime);
	    
	    zohoBooksTimeEntry.setNotes(jsonNode.get("notes").asText());
	    
	    zohoBooksTimeEntry.setJiraWorkLogId(jiraWorkLogId);
	   
	    // TODO do we need hibernate relationship between zohoTask and zohotimeEntry?
	    
	    zohoBooksTimeEntry.addZohoBooksTask(zohoBooksTask);
	    
	    zohoBooksTimeEntryService.addZohoBooksTimeEntry(zohoBooksTimeEntry);
	    
	    
		
		
		
	}
	
}
