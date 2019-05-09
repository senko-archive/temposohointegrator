package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraUserRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraUser;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;

@Service
public class JiraUserService implements IJiraUserService {
	
	@Autowired
	JiraUserRepository jiraUserRepository;

	@Override
	public List<JiraUser> getAllJiraUsers() {
		throw new NotImplementedException("Not yet implemented");
	}

	@Override
	public JiraUser getJiraUserById(String accountId) {
		return jiraUserRepository.getByAccountId(accountId);
	}

	@Override
	public boolean addJiraUser(JiraUser jiraUser) {
		jiraUserRepository.save(jiraUser);
		return true;
	}

	@Override
	public void updateJiraUser(JiraUser jiraUser) {
		throw new NotImplementedException("Not yet implemented");
		
	}

	@Override
	public void deleteJiraUser(int jiraUserId) {
		throw new NotImplementedException("Not yet implemented");
		
	}

}
