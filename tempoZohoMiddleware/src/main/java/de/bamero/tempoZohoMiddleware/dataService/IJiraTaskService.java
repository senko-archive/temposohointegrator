package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraTask;

public interface IJiraTaskService {
	
	List<JiraTask> getAllJiraTasks();
	JiraTask getJiraTaskById(long jiraTaskId);
	boolean addJiraTask(JiraTask jiraTask);
	void updateJiraTask(JiraTask jiraTask);
	void deleteJiraTask(long jiraTaskId);

}
