package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;



public interface IJiraProjectService {
	
	List<JiraProject> getAllJiraProjects();
	JiraProject getJiraProjectById(long jiraProjectId);
	boolean addJiraProject(JiraProject jiraProject);
	void updateJiraProject(JiraProject jiraProject);
	void deleteJiraProject(int jiraProjectId);

}
