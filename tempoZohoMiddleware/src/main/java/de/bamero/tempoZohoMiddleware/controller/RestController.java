package de.bamero.tempoZohoMiddleware.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import de.bamero.tempoZohoMiddleware.dataService.RestDataService;
import de.bamero.tempoZohoMiddleware.response.entities.JiraProjectResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoProjectResponse;
import de.bamero.tempoZohoMiddleware.transfer.JiraToZohoTaskTransfer;
import de.bamero.tempoZohoMiddleware.transfer.JiraToZohoWorklogTransfer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@org.springframework.web.bind.annotation.RestController
public class RestController {

	@Autowired
	RestDataService restDataService;
	
	@Autowired
	JiraToZohoTaskTransfer jiraToZohoTaskTransfer;
	
	@Autowired
	JiraToZohoWorklogTransfer jiraToZohoWorklogTransfer;

	// Jira Rest Requests

	// get mapping for listing all jira projects
	@GetMapping("/jiraProjects")
	public List<JiraProjectResponse> retrieveAllJiraProjects() {
		return restDataService.getAllJiraProjectView();
	}

	// getMapping for listing jiraTasks for specified project with projectId
	@GetMapping("/jiraTasks/{projectId}")
	public List<JiraTaskResponse> retrieveAllJiraTasksForProject(@PathVariable("projectId") String projectId) {
		return restDataService.getAllJiraTasksForProjectView(projectId);
	}

	// getMapping for list all jira Users
	@GetMapping("/JiraUsers")
	public List<JiraUserResponse> retrieveAllJiraUsers() {
		return restDataService.getAllJiraUserView();
	}

	// Zoho REST Requests

	// getMapping for zohoProjects and mapped jira projects
	@GetMapping("/zohoProjects")
	public List<ZohoProjectResponse> retrieveAllZohoProjects() {
		return restDataService.getAllZohoProjectView();
	}

	// postMapping for addign a jiraProject inside zohoProject
	@PostMapping("/zohoProjects")
	public ZohoProjectResponse updateZohoProject(@RequestBody ZohoProjectResponse zohoProjectResponse) {
		return restDataService.updateZohoBooksProjectView(zohoProjectResponse);
	}

	// getMapping for list all zoho users
	@GetMapping("/zohoUsers")
	public List<ZohoBooksUserResponse> retrieveAllZohoBooksUsers() {
		return restDataService.getAllZohoBooksUserView();
	}

	// postMapping for adding jiraUser to zohoUser
	@PostMapping("/zohoUsers")
	public ZohoBooksUserResponse updateZohoBooksUser(@RequestBody ZohoBooksUserResponse zohoBooksUserResponse) {
		return restDataService.updateZohoBooksUserView(zohoBooksUserResponse);
	}

	// getMapping for listing zohoBooksTasks for given zohoBooksProject
	@GetMapping("zohoTasks/{projectId}")
	public List<ZohoBooksTaskResponse> retrieveAllZohoBooksTasksForProject(
			@PathVariable("projectId") String projectId) {
		return restDataService.getAllZohoBooksTaskForProjectView(projectId);
	}

	// postMapping for change jiraTask for zohoBooksTask
	@PostMapping("/zohoTasks/{projectId}")
	public ZohoBooksTaskResponse updateZohoBooksTaskForProject(@PathVariable("projectId") String projectId,
			@RequestBody ZohoBooksTaskResponse zohoBooksTaskResponse) {
		log.debug("RestController.updateZohoBooksTaskForProject() now working");
		log.debug("projectID is: " + projectId + " requestBody is: " + zohoBooksTaskResponse);
		return restDataService.updateZohoBooksTaskForProjectView(projectId, zohoBooksTaskResponse);
	}
	
	// rest api for transfer button (in react)
	@GetMapping("/transferJiraTasks")
	public String startTransfer() {
		log.debug("RestController.startTransfer() now working");
		return jiraToZohoTaskTransfer.testingCreatingLogTime();
		
	}
	
	@GetMapping("/testButton")
	public String triggerTestButton() {
		log.debug("RestController.triggerTestButton() now working");
		return jiraToZohoTaskTransfer.testTransfer();
	}
	
	@GetMapping("/testWorklogTransfer")
	public String triggerWorklogTransferButton() {
		log.debug("RestController.triggerWorklogTransferButton() now working");
		return jiraToZohoWorklogTransfer.testTransfer();
		
	}

}
