package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;
import de.bamero.tempoZohoMiddleware.response.entities.JiraProjectResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoProjectResponse;
import de.bamero.tempoZohoMiddleware.utils.ViewMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RestDataService {
	
	@Autowired
	JiraProjectService jiraProjectService;
	
	@Autowired
	ZohoBooksProjectService zohoBooksProjectService;
	
	@Autowired
	JiraUserService jiraUserService;
	
	@Autowired
	ZohoBooksUserService zohoBooksUserService;
	
	@Autowired
	ZohoBooksTaskService zohoBooksTaskService;
	
	
	@Autowired
	ViewMapper viewMapper;
	
	public RestDataService() {
		
	}
	
	// get all jiraProjects and return for rest api
	public List<JiraProjectResponse> getAllJiraProjectView() {
		
		// get all jiraProjects from db
		List<JiraProject> allJiraProjects = jiraProjectService.getAllJiraProjects();
		
		// map to JiraProjectResponse and return
		return viewMapper.mapToJiraProjectView(allJiraProjects);
	}
	
	// get all zohoProjects and return for rest api
	public List<ZohoProjectResponse> getAllZohoProjectView() {
		log.debug("RestDataService.getAllZohoProjectView() now working");
		
		// get all zohoProjects from db
		// map to ZohoProjectResponse
		return viewMapper.mapToZohoBooksProjectView(zohoBooksProjectService.getAllZohoBooksProjects());
		
	}
	
	public List<JiraUserResponse> getAllJiraUserView() {
		log.debug("RestDataService.getAllJiraUserView() now working");
		
		return viewMapper.mapToJiraUserView(jiraUserService.getAllJiraUsers());
	}
	
	public List<ZohoBooksUserResponse> getAllZohoBooksUserView() {
		log.debug("RestDataService.getAllZohoBooksUserView() now working");
		
		return viewMapper.mapToZohoBookUserView(zohoBooksUserService.getAllZohoBooksUsers());
	}
	
	public ZohoBooksUserResponse updateZohoBooksUserView(ZohoBooksUserResponse zohoBooksUserResponse) {
		log.debug("RestDataService.updateZohoBooksUserView() now working");
		// burda orijinal olani bul onu ve post tan geleni reverse map e gonder
		ZohoBooksUser zohoBooksUser = zohoBooksUserService.getZohoBooksUserByUserId(zohoBooksUserResponse.getUserId());
		
		zohoBooksUser = viewMapper.reverseMaptoZohoBooksUser(zohoBooksUserResponse, zohoBooksUser);
		
		// update to database
		ZohoBooksUser returnValue = zohoBooksUserService.updateZohoBooksUser(zohoBooksUser);
		
		// mapToZohoBookUserView returns list of only one element, get this element and return
		return viewMapper.mapToZohoBookUserView(returnValue).get(0);
	}
	
	public ZohoProjectResponse updateZohoBooksProjectView(ZohoProjectResponse zohoProjectResponse) {
		log.debug("RestDataService.updateZohoBooksProjectView() now working");
		
		// orjinal zohoBooksProject i bul response'tan gelen id'yi kullanarak
		ZohoBooksProject zohoBooksProject = zohoBooksProjectService.getZohoBooksProjectByProjectId(zohoProjectResponse.getProjectId());
		
		zohoBooksProject = viewMapper.reverseMaptoZohoBooksProject(zohoProjectResponse, zohoBooksProject);
		
		// update the database
		ZohoBooksProject returnValue = zohoBooksProjectService.updateZohoBooksProject(zohoBooksProject);
		
		// mapToZohoBooksProjectView returns list of only one element, get this element and return
		return viewMapper.mapToZohoBooksProjectView(returnValue).get(0);
	}
	
	public List<JiraTaskResponse> getAllJiraTasksForProjectView(String jiraProjectId) {
		log.debug("RestDataService.getAllJiraTasksForProjectView() now working");
		
		JiraProject jiraProject = jiraProjectService.getJiraProjectByJiraProjectId(jiraProjectId);
		return jiraProject.getJiraTasks().stream().map(jiraTask -> {
			return viewMapper.mapToJiraTaskView(jiraTask);
		}).collect(Collectors.toList());
	}
	
	public List<ZohoBooksTaskResponse> getAllZohoBooksTaskForProjectView(String zohoBooksProjectId) {
		log.debug("RestDataService.getAllZohoBooksTaskForProjectView() now working");
		
		ZohoBooksProject zohoBooksProject = zohoBooksProjectService.getZohoBooksProjectByProjectId(zohoBooksProjectId);
		return zohoBooksProject.getZohoBooksTasks().stream().map(zohoBooksTask -> {
			return viewMapper.mapToZohoBooksTaskView(zohoBooksTask);
		}).collect(Collectors.toList());
		
	}
	
	public ZohoBooksTaskResponse updateZohoBooksTaskForProjectView(String zohoBooksProjectId, ZohoBooksTaskResponse zohoBooksTaskResponse) {
		log.debug("RestDataService. updateZohoBooksTaskForProjectView() now working");
		
		ZohoBooksProject zohoBooksProject = zohoBooksProjectService.getZohoBooksProjectByProjectId(zohoBooksProjectId);
		
		ZohoBooksTask updatedZohoBookTask = viewMapper.reverseMaptoZohoBooksTask(zohoBooksTaskResponse, zohoBooksProject);
		
		ZohoBooksTask returnValue = zohoBooksTaskService.updateZohoBooksTask(updatedZohoBookTask);
		
		return viewMapper.mapToZohoBooksTaskView(returnValue);
	}

}
