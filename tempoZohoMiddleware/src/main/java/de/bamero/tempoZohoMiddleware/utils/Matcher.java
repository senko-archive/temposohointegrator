package de.bamero.tempoZohoMiddleware.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import de.bamero.tempoZohoMiddleware.dataService.IJiraUserService;
import de.bamero.tempoZohoMiddleware.dataService.JiraUserService;
import de.bamero.tempoZohoMiddleware.entities.JiraUser;

@Component
public class Matcher {
	private static final Logger logger = LoggerFactory.getLogger(Matcher.class);
	
	@Autowired
	IJiraUserService jiraUserService;
	
	
	public JiraUser findJiraUser(String accountId) {
		
		return jiraUserService.getJiraUserById(accountId);
		
	}

}
