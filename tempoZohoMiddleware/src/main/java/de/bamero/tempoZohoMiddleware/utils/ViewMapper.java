package de.bamero.tempoZohoMiddleware.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraUser;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;
import de.bamero.tempoZohoMiddleware.response.entities.JiraProjectResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.JiraUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksTaskResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoBooksUserResponse;
import de.bamero.tempoZohoMiddleware.response.entities.ZohoProjectResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ViewMapper {
	
	@Autowired
	JiraProjectService jiraProjectService;
	
	public List<JiraProjectResponse> mapToJiraProjectView(List<JiraProject> jiraProjects) {
		
		return jiraProjects.stream().map(project -> 
		{
			JiraProjectResponse jiraProjectResponse = new JiraProjectResponse();
			jiraProjectResponse.setJiraProjectId(project.getProjectId());
			jiraProjectResponse.setJiraProjectName(project.getProjectName());
			jiraProjectResponse.setJiraProjectKey(project.getProjectKey());
			return jiraProjectResponse;
		}).collect(Collectors.toList());
		
	}
	
	public List<ZohoProjectResponse> mapToZohoBooksProjectView(List<ZohoBooksProject> zohoBooksProjects) {
		
		log.debug("All zohoBooksProjects:" + zohoBooksProjects);
		
		return zohoBooksProjects.stream().map(project -> {
			ZohoProjectResponse zohoProjectResponse = new ZohoProjectResponse();
			zohoProjectResponse.setProjectName(project.getProjectName());
			zohoProjectResponse.setProjectId(project.getProjectId());
			
			// there may be empty list in zohoBooksProject.jiraProjectIds
			if(!project.getJiraProjectIds().isEmpty()) {
				log.debug("Project: " + project.getProjectName() + " has no empty JiraProjectList");
				log.debug("JiraProjectList: " + project.getJiraProjectIds() + " list size is: " + project.getJiraProjectIds().size());
				zohoProjectResponse.setRelatedJiraProjects(
						// get jira projects by id and convert to JiraProjectResponse
						mapToJiraProjectView(jiraProjectsById(project.getJiraProjectIds()))
						);				
			}
			else {
				// fix this later, find another solution instead of new ArrayList
				zohoProjectResponse.setRelatedJiraProjects(new ArrayList<JiraProjectResponse>());
			}

			return zohoProjectResponse;
		}).collect(Collectors.toList());
	}
	
	public List<ZohoProjectResponse> mapToZohoBooksProjectView(ZohoBooksProject zohoBooksProject) {
		log.debug("ViewMapper.mapToZohoBooksProjectView() now working");
		
		ZohoProjectResponse zohoProjectResponse = new ZohoProjectResponse();
		zohoProjectResponse.setProjectName(zohoBooksProject.getProjectName());
		zohoProjectResponse.setProjectId(zohoBooksProject.getProjectId());
		
		// there may be empty list in zohoBooksProject.jiraProjectIds
		if(!zohoBooksProject.getJiraProjectIds().isEmpty()) {
			log.debug("Project: " + zohoBooksProject.getProjectName() + " has no empty jiraProjectList");
			zohoProjectResponse.setRelatedJiraProjects(
					// get jira projects by id and convert to JiraProjectResponse
					mapToJiraProjectView(jiraProjectsById(zohoBooksProject.getJiraProjectIds())));
		}
		else {
			// fix this later, find another solution instead of putting empty arraylist
			zohoProjectResponse.setRelatedJiraProjects(new ArrayList<JiraProjectResponse>());
		}
		
		List<ZohoProjectResponse> zohoProjectResponseOne = new ArrayList<>();
		zohoProjectResponseOne.add(zohoProjectResponse);
		return zohoProjectResponseOne;
	}
	
	private List<JiraProject> jiraProjectsById(List<String> projectIds) {
		log.debug("ViewMapper.jiraProjectsById() now working");
		
		// find jiraProjects by projectId and return as List
		return projectIds.stream().map(id -> {
			return jiraProjectService.getJiraProjectByJiraProjectId(id);
		}).collect(Collectors.toList());
	}
	
	public List<JiraUserResponse> mapToJiraUserView(List<JiraUser> jiraUsers) {
		log.debug("ViewMapper.mapToJiraUserView() now working");
		
		return jiraUsers.stream().map(user -> {
			JiraUserResponse jiraUserResponse = new JiraUserResponse();
			jiraUserResponse.setJiraUserAccountId(user.getAccountId());
			jiraUserResponse.setJiraUserName(user.getName());
			jiraUserResponse.setJiraUserDisplayName(user.getDisplayName());
			return jiraUserResponse;
		}).collect(Collectors.toList());
		
	}
	
	public List<ZohoBooksUserResponse> mapToZohoBookUserView(List<ZohoBooksUser> zohoBookUsers) {
		log.debug("viewMapper.mapToZohoBookUserView() now working");
		
		return zohoBookUsers.stream().map(user -> {
			ZohoBooksUserResponse zohoBooksUserResponse = new ZohoBooksUserResponse();
			zohoBooksUserResponse.setUserId(user.getUserId());
			zohoBooksUserResponse.setName(user.getName());
			zohoBooksUserResponse.setJiraUserIds(user.getJiraUserIds());
			return zohoBooksUserResponse;
		}).collect(Collectors.toList());
	}
	
	public List<ZohoBooksUserResponse> mapToZohoBookUserView(ZohoBooksUser zohoBookUser) {
		
		ZohoBooksUserResponse zohoBooksUserResponse = new ZohoBooksUserResponse();
		zohoBooksUserResponse.setUserId(zohoBookUser.getUserId());
		zohoBooksUserResponse.setName(zohoBookUser.getName());
		zohoBooksUserResponse.setJiraUserIds(zohoBookUser.getJiraUserIds());
		List<ZohoBooksUserResponse> oneList = new ArrayList<>();
		oneList.add(zohoBooksUserResponse);
		
		return oneList;
	}
	
	public ZohoBooksUser reverseMaptoZohoBooksUser(ZohoBooksUserResponse zohoBooksUserResponse, ZohoBooksUser zohoBooksUser) {
		log.debug("viewMapper.reverseMaptoZohoBooksUser() now working");
		
		zohoBooksUser.setJiraUserIds(zohoBooksUserResponse.getJiraUserIds());
		return zohoBooksUser;
		
		// gun sonunda yeni icine user
	}
	
	public ZohoBooksProject reverseMaptoZohoBooksProject(ZohoProjectResponse zohoBooksProjectResponse, ZohoBooksProject zohoBooksProject) {
		log.debug("viewMapper.reverseMaptoZohoBooksProject() now working");
		
		List<String> jiraProjectList = zohoBooksProjectResponse.getRelatedJiraProjects().stream().map(jiraProjectView -> jiraProjectView.getJiraProjectId()).collect(Collectors.toList());
		zohoBooksProject.setJiraProjectIds(jiraProjectList);
		return zohoBooksProject;
	}
	
	public JiraTaskResponse mapToJiraTaskView(JiraTask jiraTask) {
		log.debug("viewMapper.mapToJiraTaskView() now working");
		
		JiraTaskResponse jiraTaskResponse = new JiraTaskResponse();
		jiraTaskResponse.setTaskId(jiraTask.getId());
		jiraTaskResponse.setDescription(jiraTask.getDescription());
		
		// check jiraTask has Asssignee
		if(jiraTask.getAssignee() == null) {
			jiraTaskResponse.setAssigneeAccountId("not assigned");
			jiraTaskResponse.setAssigneeDisplayedName("not assigned");
		}
		else {
	 		jiraTaskResponse.setAssigneeAccountId(jiraTask.getAssignee().getAccountId());
			jiraTaskResponse.setAssigneeDisplayedName(jiraTask.getAssignee().getDisplayName());
		}

		
		return jiraTaskResponse;
	}
	
	public ZohoBooksTaskResponse mapToZohoBooksTaskView(ZohoBooksTask zohoBooksTask) {
		log.debug("viewMapper.mapToZohoBooksView() now working");
		
		ZohoBooksTaskResponse zohoBooksTaskResponse = new ZohoBooksTaskResponse();
		zohoBooksTaskResponse.setZohoBooksTaskId(zohoBooksTask.getZohoBooksTaskId());
		zohoBooksTaskResponse.setTaskName(zohoBooksTask.getTaskName());
		zohoBooksTaskResponse.setDescription(zohoBooksTask.getDescription());
		zohoBooksTaskResponse.setJiraTaskId(zohoBooksTask.getJiraTaskId());
		zohoBooksTaskResponse.setJiraTaskName(zohoBooksTask.getJiraTaskName());
		
		return zohoBooksTaskResponse;
	}

	public ZohoBooksTask reverseMaptoZohoBooksTask(ZohoBooksTaskResponse zohoBooksTaskResponse,
			ZohoBooksProject zohoBooksProject) {
		log.debug("viewMapper.reverseMaptoZohoBooksTask() now working");
		
		String zohoBookTaskId = zohoBooksTaskResponse.getZohoBooksTaskId();
		ZohoBooksTask updatedZohoBookTask = null;
		Optional<ZohoBooksTask> optZohoBookTask = zohoBooksProject.getZohoBooksTasks().stream().filter(zohoBooksTask -> 
			zohoBooksTask.getZohoBooksTaskId().equals(zohoBookTaskId)).findFirst();
		
		if(optZohoBookTask.isPresent()) {
			updatedZohoBookTask = optZohoBookTask.get();
			updatedZohoBookTask.setJiraTaskId(zohoBooksTaskResponse.getJiraTaskId());
			updatedZohoBookTask.setJiraTaskName(zohoBooksTaskResponse.getJiraTaskName());
		}
		else {
			log.error("In projectId: " + zohoBooksProject.getProjectId() + " couldn't find task with id: " + zohoBooksTaskResponse.getZohoBooksTaskId());
		}
		
		return updatedZohoBookTask;
		
	}
	
	
	
	

}
