package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraUser;

public interface IJiraUserService {
	
	List<JiraUser> getAllJiraUsers();
	JiraUser getJiraUserById(String accountId);
	boolean addJiraUser(JiraUser article);
	boolean updateJiraUser(JiraUser jiraUser);
	void deleteJiraUser(int jiraUserId);
	boolean checkJiraUserById(String accountId);
	void update(JiraUser jiraUser);
	

}
