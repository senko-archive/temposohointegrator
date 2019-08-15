package de.bamero.tempoZohoMiddleware.transfer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.dataService.JiraTaskService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksProjectService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksTimeEntryService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksUserService;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;
import de.bamero.tempoZohoMiddleware.parsers.ZohoBooksParser;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JiraToZohoWorklogTransfer {

	@Autowired
	ZohoBooksProjectService zohoBooksProjectService;

	@Autowired
	ZohoBooksUserService zohoBooksUserService;
	
	@Autowired
	ZohoBooksTimeEntryService zohoBooksTimeEntryService;

	@Autowired
	JiraTaskService jiraTaskService;
	
	@Autowired
	ZohoBooksParser zohoBooksParser;

	@Autowired
	RestTemplate restTemplate;

	PropertiesLoader loader;

	public JiraToZohoWorklogTransfer() {
		this.loader = new PropertiesLoader();
	}

	public String testTransfer() {

		// for test purpose add some zohobooksusers to jira users
		connectZohoUsersWithJiraUsers();

		// take all zohoBooks Projects from database
		List<ZohoBooksProject> zohoBooksProjectList = zohoBooksProjectService.getAllZohoBooksProjects();
		
		// for test just add some projects 
		List<ZohoBooksProject> dummyZohoBooksProjectList = new ArrayList<>();
		
		zohoBooksProjectList.forEach(prj -> {
			if(prj.getProjectId().equalsIgnoreCase("54386000000948200")) { //ZohoTest_Jira_1
				dummyZohoBooksProjectList.add(prj);
			}
		});
		
		zohoBooksProjectList.forEach(prj -> {
			if(prj.getProjectId().equalsIgnoreCase("54386000001056045")) { //ZohoTest_Jira_2
				dummyZohoBooksProjectList.add(prj);
			}
		});
		
		

		// take tasks for all zohoBooks projects
		for (ZohoBooksProject project : dummyZohoBooksProjectList) {
			List<ZohoBooksTask> zohoBooksTaskList = project.getZohoBooksTasks();

			// for each task
			if (!zohoBooksTaskList.isEmpty()) {
				for (ZohoBooksTask zohoTask : zohoBooksTaskList) {

					// find task's related jira task
					String jiraTaskId = zohoTask.getJiraTaskId();
					try {
						JiraTask jiraTask = jiraTaskService.getJiraTaskById(jiraTaskId);
						List<JiraWorkLog> jiraWorkLogs = jiraTask.getWorkLogs();

						// look for all worklogs
						if (!jiraWorkLogs.isEmpty()) {
							for (JiraWorkLog jiraWorkLog : jiraWorkLogs) {

								// look for this jiraUserId is in available in zohoBookProject
								String jiraUserId = jiraWorkLog.getAuthorAccountId();

								// get all ZohoBooksUser from a project
								Set<ZohoBooksUser> zohoBooksUserList = project.getZohoBooksUsers();

								ZohoBooksUser zohoUserOwnWorklog = null;

								// for all zohoUser search jiraUserId
								for (ZohoBooksUser zohoUser : zohoBooksUserList) {
									boolean status = zohoUser.getJiraUserIds().stream()
											.anyMatch(jId -> jId.equalsIgnoreCase(jiraUserId));
									if (status == true) {
										zohoUserOwnWorklog = zohoUser;
									}
									else {
										// TODO : if no match for jira user in zohobooks then?
									}

								}

								// in this part if zohoUserOwnWorklog is false, means we could't find matched
								// jira User
								if (zohoUserOwnWorklog == null) {
									log.error("there is no matched zohoBooksUser for jiraUserId: " + jiraUserId);
								}

								// turkish : elinde jiraworklog id var, bu jiraworklogID su an elimizdeki zoho time entry larda var mi ona bakicaz
								// buna database'den bakicaz, eger varsa bu sefer update tarihine bakicaz
								// eger jiraworklog un update tarihi, eslenigi olan zohotask in created time indan sonra ise, bu task guncellenmis demektir
								// biz de zohodaki eslenigini guncellececez
								// eger jiraworklog un update tarihi, eslenigi olan zohotask in created indan daha gerideyse update olmamistir problem yok
								// eger jiraworklog icin bir eslenik bulamazsak o zaman yeni yaraticaz
							
								// zohoTask var elinde, bu zohoTask in tum timeEntrylerine bakicaksin
								Set<ZohoBooksTimeEntry> zohoBooksTimeEntries = zohoTask.getZohoBooksTimeEntries();
								if(!zohoBooksTimeEntries.isEmpty()) {
									String jiraWorkLogId = jiraWorkLog.getJiraWorkLogId();
									// elindeki jira id sendeki tasklarin birinde var mi diye bak
									Optional<ZohoBooksTimeEntry> matchedZohoBooksTimeEntry = findOneorNonMatchedTimeEntry(zohoBooksTimeEntries, jiraWorkLogId);
									
									if(matchedZohoBooksTimeEntry.isPresent()) {
										// check zohoTimeEntry updated time and zohoBooksCreated time
										// if updated time is bigger than created time it means after we create zohoBookstimeEntry, someone 
										// updated the jiraTask so we don't have the updated version
										Boolean needToUpdateZohoTimeEntry = checkDate(jiraWorkLog.getUpdatedDate(), matchedZohoBooksTimeEntry.get().getCreatedTime(), 
												matchedZohoBooksTimeEntry.get().getUpdatedTime());
										
										if(needToUpdateZohoTimeEntry == true) {
											prepareZohoBooksTimeEntry(jiraWorkLog, zohoUserOwnWorklog,
													zohoTask.getZohoBooksTaskId(), project.getProjectId(), zohoTask, "update", matchedZohoBooksTimeEntry);
										}
										else {
											// no need to update
										}
										
										
									}
									else {
										log.debug("testTransfer -> there is no zohoBooksTimeEntry matched with jiraWorkLogId: " + jiraWorkLogId);
										
										// bu eger hicbir eslesen jiraworklogid bulamazsak caliscak
										// prepare ZohoBooksWorklog and send with POST
										prepareZohoBooksTimeEntry(jiraWorkLog, zohoUserOwnWorklog,
												zohoTask.getZohoBooksTaskId(), project.getProjectId(), zohoTask, "create", matchedZohoBooksTimeEntry);
									}
									
								}
								else {
									log.debug("testTransfer zohoBookTask's zohoBookTimeEntry set is empty");
									prepareZohoBooksTimeEntry(jiraWorkLog, zohoUserOwnWorklog,
											zohoTask.getZohoBooksTaskId(), project.getProjectId(), zohoTask, "create", null);
								}
								
								
								

							}
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

		}

		return null;
	}

	private Boolean checkDate(LocalDateTime updatedDate, LocalDateTime createdTime, LocalDateTime updatedTime) {
		
		// if jira time is updated after creation time of zohobooks time entry or the last updated time of zohobooks time entry
		if(updatedDate.isAfter(createdTime) && updatedDate.isAfter(updatedTime)) {
			return true;
		}
		else {
			return false;
		}
		
	}

	private Optional<ZohoBooksTimeEntry> findOneorNonMatchedTimeEntry(Set<ZohoBooksTimeEntry> zohoBooksTimeEntries,
			String jiraWorkLogId) {
		
		return zohoBooksTimeEntries.stream().filter(timeEntries -> timeEntries.getJiraWorkLogId().equalsIgnoreCase(jiraWorkLogId))
			.findAny();
	}

	private void connectZohoUsersWithJiraUsers() {
		// for test purpose, connect some users

		// add jira user information for zoho user selcuk
		ZohoBooksUser user = zohoBooksUserService.getZohoBooksUserByUserId("54386000000404001");
		List<String> jiraUserList = new ArrayList<>();
		jiraUserList.add("5bdff330f8460347a10cd737");
		user.setJiraUserIds(jiraUserList);
		zohoBooksUserService.update(user);

	}

	private void prepareZohoBooksTimeEntry(JiraWorkLog jiraWorkLog, ZohoBooksUser zohoUserOwnWorklog,
			String zohoBooksTaskId, String projectId, ZohoBooksTask zohoBooksTask, String createOrUpdate, Optional<ZohoBooksTimeEntry> matchedZohoBooksTimeEntry) {

		// if in jiraWorklog, there is no billableSeconds than no need to create in
		// ZohoBooks

		Map<String, String> zohoTimeEntryEntities = new HashMap<>();
		if (!(jiraWorkLog.getBillableSeconds() == 0)) {
			String zohoProjectId = projectId;
			String zohoTaskId = zohoBooksTaskId;
			LocalDate logDate = jiraWorkLog.getStartDate();
			Boolean isBillable = true;

			String logTime = jiraWorkLog.getBillableSeconds().toString();

			// start build Post structure
			zohoTimeEntryEntities.put("project_id", zohoProjectId);
			zohoTimeEntryEntities.put("task_id", zohoTaskId);
			zohoTimeEntryEntities.put("user_id", zohoUserOwnWorklog.getUserId());
			zohoTimeEntryEntities.put("log_date", logDate.toString());
			zohoTimeEntryEntities.put("log_time", logTime);
			zohoTimeEntryEntities.put("is_billable", isBillable.toString());
			zohoTimeEntryEntities.put("jiraWorkLogId", jiraWorkLog.getJiraWorkLogId());

		} else {
			log.debug("JiraWorkLog with id:" + jiraWorkLog.getJiraWorkLogId()
					+ " has no billable hours and exiting createZohoBooksTimeEntry");
		}

		if(createOrUpdate.equalsIgnoreCase("create")) {
			createZohoBooksTimeEntry(zohoTimeEntryEntities, zohoBooksTask);
		}
		else if(createOrUpdate.equalsIgnoreCase("update")) {
			zohoTimeEntryEntities.put("timeEntryId", matchedZohoBooksTimeEntry.get().getTimeEntryId());
			updateZohoBooksTimeEntry(zohoTimeEntryEntities, zohoBooksTask, matchedZohoBooksTimeEntry.get());
		}
		else {
			log.error("Error on createOrUpdate string it should be create or update not " + createOrUpdate);
		}

	}

	private void updateZohoBooksTimeEntry(Map<String, String> zohoTimeEntryEntities, ZohoBooksTask zohoBooksTask, ZohoBooksTimeEntry zohoBooksTimeEntry) {
	
		String timeEntryId = zohoTimeEntryEntities.get("timeEntryId");
		String putURL = "https://books.zoho.eu/api/v3/projects/timeentries/"+timeEntryId+"?organization_id=20065018387";
		
		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String authorization = "Zoho-oauthtoken " + access_token;
		
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		headerMap.put("Content-Type", "multipart/form-data");
		
		// convert the log_time to HH:mm format, it comes as second
		Date d = new Date(Integer.valueOf(zohoTimeEntryEntities.get("log_time")) * 1000);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = df.format(d);
		
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		
		requestBody.add("JSONString",
				"{\r\n" + "    \"project_id\": " + "\"" + zohoTimeEntryEntities.get("project_id") + "\"" + ",\r\n"
						+ "    \"task_id\": " + "\"" + zohoTimeEntryEntities.get("task_id") + "\"" + ",\r\n"
						+ "    \"user_id\": " + "\"" + zohoTimeEntryEntities.get("user_id") + "\"" + ",\r\n"
						+ "    \"log_date\": " + "\"" + zohoTimeEntryEntities.get("log_date") + "\"" + ",\r\n"
						+ "    \"log_time\": " + "\"" + time + "\"" + ",\r\n" + "    \"is_billable\": " + "\""
						+ zohoTimeEntryEntities.get("is_billable") + "\"" + "\r\n" + "}");
		
		log.debug("createZohoBooksTimeEntry put request: " + requestBody.toString());
		
		ResponseEntity<String> response = ConsumerHelper.putResponse(restTemplate, headerMap, requestBody, putURL);
		
		if(response.getStatusCode() == HttpStatus.OK) {
			// update entitty and update on db too -> here I create a new zohoBooksEntitiy no need that just get the old one
			// which is matchedZohoBooksTimeEntry and just update this
			// zohoBooksParser.zohoBooksTimeEntryParser(response, zohoTimeEntryEntities.get("jiraWorkLogId"), zohoBooksTask);
			updateOldTimeEntry(zohoBooksTimeEntry, zohoTimeEntryEntities);
			
		}
		else {
			log.error("problem on rest call : " + response.getStatusCode() + " response: " + response.getBody());
		}
		
		
	}

	private void updateOldTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry, Map<String, String> zohoTimeEntryEntities) {
		
		String pattern = "yyyy-MM-dd'T'HH:mm";
		//SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
	    
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
	    
	    LocalDateTime updatedTime = LocalDateTime.parse(LocalDateTime.now().toString().substring(0, 16), dateTimeFormatter);
		
		zohoBooksTimeEntry.setUpdatedTime(updatedTime);
		
		
		// convert the log_time to HH:mm format, it comes as second
		Date d = new Date(Integer.valueOf(zohoTimeEntryEntities.get("log_time")) * 1000);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = df.format(d);
		
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    LocalTime logTime = LocalTime.parse(time, timeFormatter);
	    zohoBooksTimeEntry.setLogTime(logTime);
	    
	    zohoBooksTimeEntryService.updateZohoBooksTimeEntry(zohoBooksTimeEntry);
	    
	    
		
	}

	private void createZohoBooksTimeEntry(Map<String, String> zohoTimeEntryEntities, ZohoBooksTask zohoBooksTask) {

		String postURL = "https://books.zoho.eu/api/v3/projects/timeentries?organization_id=20065018387";

		Properties zohoBooksProperties = loader.load("zohobooks.properties");
		String access_token = zohoBooksProperties.getProperty("access_token");
		String authorization = "Zoho-oauthtoken " + access_token;

		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", authorization);
		headerMap.put("Content-Type", "multipart/form-data");

		// convert the log_time to HH:mm format, it comes as second
		Date d = new Date(Integer.valueOf(zohoTimeEntryEntities.get("log_time")) * 1000);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm");
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		String time = df.format(d);

		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();

		requestBody.add("JSONString",
				"{\r\n" + "    \"project_id\": " + "\"" + zohoTimeEntryEntities.get("project_id") + "\"" + ",\r\n"
						+ "    \"task_id\": " + "\"" + zohoTimeEntryEntities.get("task_id") + "\"" + ",\r\n"
						+ "    \"user_id\": " + "\"" + zohoTimeEntryEntities.get("user_id") + "\"" + ",\r\n"
						+ "    \"log_date\": " + "\"" + zohoTimeEntryEntities.get("log_date") + "\"" + ",\r\n"
						+ "    \"log_time\": " + "\"" + time + "\"" + ",\r\n" + "    \"is_billable\": " + "\""
						+ zohoTimeEntryEntities.get("is_billable") + "\"" + "\r\n" + "}");

		/*
		 * requestBody.add("JSONString", "{\r\n" +
		 * "    \"project_id\": \"54386000000948200\",\r\n" +
		 * "    \"task_id\": \"54386000001061469\",\r\n" +
		 * "    \"user_id\": \"54386000000404001\",\r\n" +
		 * "    \"log_date\": \"2019-06-04\",\r\n" + "    \"log_time\": \" \",\r\n" +
		 * "    \"begin_time\": \"12:00\",\r\n" + "    \"end_time\": \"15:00\",\r\n" +
		 * "    \"is_billable\": false,\r\n" + "    \"notes\": \" \",\r\n" +
		 * "    \"start_timer\": \" \"\r\n" + "}");
		 */
		log.debug("createZohoBooksTimeEntry post request: " + requestBody.toString());

		ResponseEntity<String> response = ConsumerHelper.postResponse(restTemplate, headerMap, requestBody, postURL);
		
		// if response is OK then create this TimeEntry also in database
		if(response.getStatusCode() == HttpStatus.CREATED ) {
			
			// create entity and add to database
			zohoBooksParser.zohoBooksTimeEntryParser(response, zohoTimeEntryEntities.get("jiraWorkLogId"), zohoBooksTask);
				
		}
		else {
			log.error("problem on rest call : " + response.getStatusCode() + " response: " + response.getBody());
		}

		log.debug("createZohoBooksTimeEntry response: " + response.toString());

	}

}
