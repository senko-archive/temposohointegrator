package de.bamero.tempoZohoMiddleware.transfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.consumer.ZohoBooksConsumer;
import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksProjectService;
import de.bamero.tempoZohoMiddleware.dataService.ZohoBooksTaskService;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.producer.ZohoBooksProducer;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JiraToZohoTaskTransfer {
	
	@Autowired
	ZohoBooksProjectService zohoBooksProjectService;
	
	@Autowired
	ZohoBooksTaskService zohoBooksTaskService;
	
	@Autowired
	JiraProjectService jiraProjectService;
	
	@Autowired
	ZohoBooksProducer zohoBooksProducer;
	
	@Autowired
	ZohoBooksConsumer zohoBooksConsumer;
	
	@Autowired
	RestTemplate restTemplate;
	
	// oyle bi method yaz ki zoho projeleri tek tek dolassin fakat simdilik sadece test projesini alsin.
	
	// zoho projelerini tek tek gez, bir listeye al, ama simdilik sadece bizim test projesi icin aktive et
	// zoho projesinin ilgili jira projesine gidip, onun tasklarini alicaksin
	// zoho projesindeki tasklari alicaksin -> jira esleniklerine bakicaksin
	// karsilastir jira da olan ve zoho da olmayan tasklari o proje altinda yarat
	
	public void startTransferJiraTasks() {
		List<ZohoBooksProject> zohoProjects = zohoBooksProjectService.getAllZohoBooksProjects();
		
	}
	
	public String testingCreatingIssue() {
		
		// create new ZohoBooks issue in 54386000000400041 -> zoho test project
		String postURL = "https://books.zoho.eu/api/v3/projects/54386000000400041/tasks?organization_id=20065018387";
		
		/*
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Zoho-oauthtoken 1000.c361254f6aa208a7653534fca4e9b847.68081cbe7e3c9087a3e9be81c4f753c5");
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		*/
		
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", "Zoho-oauthtoken 1000.c361254f6aa208a7653534fca4e9b847.68081cbe7e3c9087a3e9be81c4f753c5");
		headerMap.put("Content-Type", "application/x-www-form-urlencoded");
		
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("JSONString", "{\r\n" + 
				"    \"task_name\": \"Painting3\",\r\n" + 
				"    \"description\": \"Painting3 Description\",\r\n" + 
				"    \"rate\": 3,\r\n" + 
				"    \"budget_hours\": \"\",\r\n" + 
				"    \"is_billable\": false\r\n" + 
				"}");
		
		//HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(requestBody, headers);
		
		ResponseEntity<String> response = ConsumerHelper.postResponse(restTemplate, headerMap, requestBody, postURL);
		
		//ResponseEntity<String> response = restTemplate.postForEntity(postURL, request, String.class);
		
		log.debug("RETURN response is: " + response.toString());
		
		return "OK";
		
	}
	
	public String testingCreatingLogTime() {
		
		// create new LogTime in project 54386000000400041, issue 54386000000950123 with user 54386000000404001
		String postURL = "https://books.zoho.eu/api/v3/projects/timeentries?organization_id=20065018387";
		
		Map<String, String> headerMap = new HashMap<>();
		headerMap.put("Authorization", "Zoho-oauthtoken 1000.0625abe368338cd326216838effa4073.c14854d4b417217359960308bbe5c68f");
		headerMap.put("Content-Type", "multipart/form-data");
		
		MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
		requestBody.add("JSONString", "{\r\n" + 
				"    \"project_id\": \"54386000000400041\",\r\n" + 
				"    \"task_id\": \"54386000000950123\",\r\n" + 
				"    \"user_id\": \"54386000000404001\",\r\n" + 
				"    \"log_date\": \"2019-06-04\",\r\n" + 
				"    \"log_time\": \" \",\r\n" + 
				"    \"begin_time\": \"12:00\",\r\n" + 
				"    \"end_time\": \"15:00\",\r\n" + 
				"    \"is_billable\": false,\r\n" + 
				"    \"notes\": \" \",\r\n" + 
				"    \"start_timer\": \" \"\r\n" + 
				"}");
		
		ResponseEntity<String> response = ConsumerHelper.postResponse(restTemplate, headerMap, requestBody, postURL);
		
		log.debug("Creating logtime response is: " + response.toString());
		
		return "OK";
	}
	
	
	public String testing() {
		log.debug("JiraToZohoTaskTransfer.testing() now working");
		return "OK I'M WOKRING";
	}

	public String testTransfer() {
		
		// first take the ZohoTest_Jira_1 project from zohobooks. we know this is ZohoTest1. Control first if jira project is exists
		List<ZohoBooksProject> zohoBooksProjectList = zohoBooksProjectService.getAllZohoBooksProjects();
		
		// list for only ZohoTest_Jira_1
		List<ZohoBooksProject> dummyList = new ArrayList<>();
		
		dummyList.add(
				zohoBooksProjectList.stream().filter(e -> e.getProjectId().equals("54386000001056045")).findFirst().get()
				);
		
		dummyList.add(
				zohoBooksProjectList.stream().filter(e -> e.getJiraKey().equalsIgnoreCase("ZOH1")).findFirst().get()
				);
		
		
		
		for(ZohoBooksProject project : dummyList) {
			
			if(project.getJiraProjectIds().isEmpty() || project.getJiraProjectIds() == null) {
				log.error("ZohoBooks project name: " + project.getProjectName() + " with id: " + project.getProjectId() + " has no connected jira project");
			}
			
			// take tasks from the jira project and look if tasks is already created in zoho
			// if not create tasks and transfer worklog
			List<String> jiraProjectIds = project.getJiraProjectIds();
			
			List<JiraTask> jiraTasks = new ArrayList<>();
			
			for(String id : jiraProjectIds) {
				// take jira project with id and take it's tasks. and add to jiraTasks list
				JiraProject jiraProject = jiraProjectService.getJiraProjectByJiraProjectId(id);
				jiraTasks.addAll(jiraProject.getJiraTasks());
			}
			
			// look if jira tasks are created in zohobooks, we will compare with names
			List<ZohoBooksTask> zohoBookTasks = project.getZohoBooksTasks();
			
			if(zohoBookTasks.isEmpty()) {
				// create all tasks
				// while creating set jiraTaskName and jiraTaskId to the zohoTask
				boolean result = createAllZohoTasks(jiraTasks, project);
			}

			// else check jira tasks one by one
			// check jira task is already created in zohobooks
			else {
				List<ZohoBooksTask> matchedTask = new ArrayList<>();
				List<JiraTask> unMatchedJiraTask = new ArrayList<>();
				// convert this 2 for loop in stream and functional
				for(ZohoBooksTask zohoTask : zohoBookTasks) {
					
					for(JiraTask jiraTask : jiraTasks) {
						
						if(zohoTask.getJiraTaskId().equalsIgnoreCase(jiraTask.getTaskId())) {
							matchedTask.add(zohoTask);
						}	
					}
						
				}
				
				outerLoop:
				for(JiraTask jiraTask : jiraTasks) {
					for(ZohoBooksTask matchedTaskElement : matchedTask ) {
						if(jiraTask.getTaskId().equalsIgnoreCase(matchedTaskElement.getJiraTaskId())) {
							// than we got match go to next jiraTask element
							break outerLoop;
						}	
					}
					unMatchedJiraTask.add(jiraTask);
				}
				
				/*
				List<ZohoBooksTask> matchedTask = zohoBookTasks.stream()
					.filter(zohoTask -> jiraTasks.stream()
							.anyMatch(jiraTask -> 
									jiraTask.getTaskId().equals(zohoTask.getJiraTaskId())))
							.collect(Collectors.toList());
				*/
				//log.debug("matchedtask is: " + matchedTask);
				
				if(!matchedTask.isEmpty()) {
					log.debug("matchedTask is not empty");
					// if list is not empty, ot means we already create this tasks in ZohoBooks, just insert new worklogs and update old ones if neccessary
					
					// but also need to check if unMatchedJiraTask list is not empty, this means we got new jira tasks that haven't been in ZohoBooks
					if(!unMatchedJiraTask.isEmpty()) {
						// create these new jiraTasks in ZohoBooks
						boolean result = createAllZohoTasks(unMatchedJiraTask, project);
					}
				}
			
				
			}
			
			
			
			
			/*
			for(JiraTask task : jiraTasks) {
				String taskId = task.getTaskId();
				
				if(zohoBookTasks.stream().filter(zTask -> zTask.getJiraTaskId().equals(task.getTaskId())).findFirst().get() != null ) {
					// it means we already create this task, just insert new wokrlogs and update old ones if neccessary.
				}
				else {
					// this means jiraTask is not in related zohoProject so we need to create it
				}
			}
			*/
			
			
			
		}
		
		
		
		
		
		// if it is created then just transfer worklogs. While transfering worklogs, look the hours are they same in jira or not, if not update it. 
		
		return "OK";
	}

	private boolean createAllZohoTasks(List<JiraTask> jiraTasks, ZohoBooksProject zohoProject) {
		log.debug("JiraToZohoTaskTransfer.createAllZohoTasks() now working");
		// create jira tasks to zoho
		jiraTasks.forEach(task -> createZohoTask(task, zohoProject));
		
		return false;
	}

	private void createZohoTask(JiraTask task, ZohoBooksProject zohoProject) {
		log.debug("JiraToZohoTaskTransfer.createZohoTask() now wokring");
		// create jiraTask to the ZohoSystem.
		// call zoho rest api with post call and create a new task
		
		// ilk once jiraTask tan gerekli bilgileri al bunlari bir map e koy. Atiyorum key zohoTask valuelari, value jira value'lari seklinde
		// Map<ZohoValueName, JiraValue>
		Map<String, String> zohoEntities = new HashMap<>();
		zohoEntities.put("taskName", task.getSummary());
		zohoEntities.put("description", task.getDescription());
		zohoEntities.put("rate", "");
		zohoEntities.put("budgetHours", "");
		
		// daha sonra bu mapi ve project id yi post yapacak methoda gonder
	    String zohoBooksTaskId = zohoBooksProducer.createZohoBooksTask(zohoEntities, zohoProject.getProjectId());
	    
	    // create new ZohoTask from response and save it to database
	    // first get this task from zohobooks rest api with get
	    zohoBooksConsumer.consumeOneZohoBooksTask(zohoProject, zohoBooksTaskId);
	    
	    // get your task from database
	    ZohoBooksTask zohoBooksTask = zohoBooksTaskService.getZohoBookTaskByTaskId(zohoBooksTaskId);
	    
	    // add jira taskId and jira taskName to zohoBooksTask object and update database
	    zohoBooksTask.setJiraTaskId(task.getId());
	    zohoBooksTask.setJiraTaskName(task.getSummary());
	    
	    zohoBooksTaskService.updateZohoBooksTask(zohoBooksTask);
		
	}
	
	

}
