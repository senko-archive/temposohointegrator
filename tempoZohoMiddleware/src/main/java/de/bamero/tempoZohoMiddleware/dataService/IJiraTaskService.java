package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraTask;

public interface IJiraTaskService {
	
	List<JiraTask> getAllJiraTasks();
	JiraTask getJiraTaskById(String jiraTaskId) throws Exception;
	boolean addJiraTask(JiraTask jiraTask);
	void updateJiraTask(JiraTask jiraTask);
	void deleteJiraTask(long jiraTaskId);
	void truncateTask();

}
