package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;

public interface IJiraSubTaskService {
	
	List<JiraSubTask> getAllJiraSubTasks();
	JiraSubTask getJiraSubTaskById(long jiraSubTaskId);
	JiraSubTask getJiraSubTaskByJiraId(String jiraId);
	boolean addJiraSubTask(JiraSubTask jiraSubTask);
	boolean updateJiraSubTask(JiraSubTask jiraSubTask);
	void deleteJiraSubTask(long jiraSubTaskId);

}
