package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraSubTaskRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;

@Service
public class JiraSubTaskService implements IJiraSubTaskService {

	@Autowired
	JiraSubTaskRepository JiraSubTaskRepository;
	
	@Override
	public List<JiraSubTask> getAllJiraSubTasks() {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public JiraSubTask getJiraSubTaskById(long jiraSubTaskId) {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public boolean addJiraSubTask(JiraSubTask jiraSubTask) {
		JiraSubTaskRepository.save(jiraSubTask);
		return true;
	}

	@Override
	public boolean updateJiraSubTask(JiraSubTask jiraSubTask) {
		JiraSubTaskRepository.save(jiraSubTask);
		return true;
		
	}

	@Override
	public void deleteJiraSubTask(long jiraSubTaskId) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public JiraSubTask getJiraSubTaskByJiraId(String jiraId) {
		return JiraSubTaskRepository.getById(jiraId);
	}
	

}
