package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraTaskRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;

@Service
public class JiraTaskService implements IJiraTaskService {

	@Autowired
	JiraTaskRepository JiraTaskRepository;
	
	@Override
	public List<JiraTask> getAllJiraTasks() {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public JiraTask getJiraTaskById(long jiraTaskId) {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public boolean addJiraTask(JiraTask jiraTask) {
		JiraTaskRepository.save(jiraTask);
		return true;
	}

	@Override
	public void updateJiraTask(JiraTask jiraTask) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public void deleteJiraTask(long jiraTaskId) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

}
