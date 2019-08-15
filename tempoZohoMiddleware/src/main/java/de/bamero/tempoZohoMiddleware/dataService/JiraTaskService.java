package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraTaskJPARepository;
import de.bamero.tempoZohoMiddleware.dataRepository.JiraTaskRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JiraTaskService implements IJiraTaskService {

	@Autowired
	JiraTaskRepository jiraTaskRepository;
	
	@Autowired
	JiraTaskJPARepository jiraTaskJpaRepository;
	
	@Override
	public List<JiraTask> getAllJiraTasks() {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public JiraTask getJiraTaskById(String jiraTaskId) throws Exception {
		Optional<JiraTask> optionalJiraTask = jiraTaskRepository.findJiraTaskById(jiraTaskId);
		if(!optionalJiraTask.isPresent()) {
			log.error("JiraTaskService.getJiraTaskById : there is no return value for: " + jiraTaskId);
			throw new Exception("jira task with id: " + jiraTaskId + " not found");
		}
		else {
			return optionalJiraTask.get();
		}
		
	}

	@Override
	public boolean addJiraTask(JiraTask jiraTask) {
		jiraTaskRepository.save(jiraTask);
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
	
	@Override
	@Transactional
	public void truncateTask() {
		jiraTaskJpaRepository.truncateJiraTaskTable();
	}

}
