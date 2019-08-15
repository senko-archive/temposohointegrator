package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;



public interface IJiraProjectService {
	
	List<JiraProject> getAllJiraProjects();
	JiraProject getJiraProjectById(long jiraProjectId);
	boolean addJiraProject(JiraProject jiraProject);
	boolean updateJiraProject(JiraProject jiraProject);
	void deleteJiraProject(int jiraProjectId);
	Optional<JiraProject> getJiraProjectByKey(String projectKey);
	JiraProject getJiraProjectByJiraProjectId(String jiraProjectId);
	boolean checkJiraProjectById(String projectId);
	void update(JiraProject project);

}
