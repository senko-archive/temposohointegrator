package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraUserRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraUser;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JiraUserService implements IJiraUserService {
	
	@Autowired
	JiraUserRepository jiraUserRepository;

	@Override
	public List<JiraUser> getAllJiraUsers() {
		Iterable<JiraUser> jiraUserIterator = jiraUserRepository.findAll();
		// convert Iterable to Stream and then convert stream to List
		return StreamSupport.stream(jiraUserIterator.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public JiraUser getJiraUserById(String accountId) {
		return jiraUserRepository.getByAccountId(accountId);
	}
	
	@Override
	public boolean checkJiraUserById(String accountId) {
		Optional<JiraUser> optionalJiraUser = jiraUserRepository.findOptionalByAccountId(accountId);
		return optionalJiraUser.isPresent();
	}

	@Override
	public boolean addJiraUser(JiraUser jiraUser) {
		jiraUserRepository.save(jiraUser);
		return true;
	}

	@Override
	public boolean updateJiraUser(JiraUser jiraUser) {
		jiraUserRepository.save(jiraUser);
		return true;
		
	}

	@Override
	public void deleteJiraUser(int jiraUserId) {
		throw new NotImplementedException("Not yet implemented");
		
	}

	@Override
	public void update(JiraUser jiraUser) {
		// get jiraUsre from database
		JiraUser jiraUserFromDB = getJiraUserById(jiraUser.getAccountId());
		
		// compare if both are same or not
		// if jiraUser in database is not equal jiraUser, means something chaned in Jira app
		if(!jiraUserFromDB.equals(jiraUser)) {
			// update with new values
			log.debug("JiraUserService.update - jiraUser from RestApi have some new values");
			jiraUserFromDB.setUserJiraUri(jiraUser.getUserJiraUri());
			jiraUserFromDB.setKey(jiraUser.getKey());
			jiraUserFromDB.setName(jiraUser.getName());
			jiraUserFromDB.setEmailAddress(jiraUser.getEmailAddress());
			jiraUserFromDB.setDisplayName(jiraUser.getDisplayName());
			
			// put updated jiraUser to database again 
			boolean result = updateJiraUser(jiraUserFromDB);
		}
		
		// if they are equal do nothing
		
	}

}
