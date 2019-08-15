package de.bamero.tempoZohoMiddleware;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.consumer.JiraConsumer;
import de.bamero.tempoZohoMiddleware.consumer.TempoConsumer;
import de.bamero.tempoZohoMiddleware.consumer.ZohoBooksConsumer;
import de.bamero.tempoZohoMiddleware.utils.TokenCheckHelper;

@Component
@Scope("singleton")
public class Runner {
	
	private static final Logger logger = LoggerFactory.getLogger(Runner.class);
	
	@Autowired
	TokenCheckHelper tokenCheckHelper;
	
	@Autowired
	JiraConsumer jiraConsumer;
	
	@Autowired
	TempoConsumer tempoConsumer;
	
	@Autowired
	ZohoBooksConsumer zohoBooksConsumer;
		
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}
	
	
	public void run() {
		logger.debug("Runner.run() now working");
		
		
		// check if tempo and/or zohobooks access_token's should renew or not.
		tokenCheckHelper.checkForTempoRestAPI();
		tokenCheckHelper.checkForZohoBooksRestAPI();

		// parse and save db to JiraUsers
		jiraConsumer.consumeJiraUsers();

		// get all external projects from jira
		jiraConsumer.consumeJiraProjects();

				
		// for all projects get tasks.
		jiraConsumer.consumeJiraTasks();
		
		// for all jira tasks get worklogs
		tempoConsumer.consumeTempoWorklogForEachJiraTask();
	
		
		// get all zohobooks users
		zohoBooksConsumer.consumeZohoBooksUsers();
	
		// start taking data from zohobooks
		zohoBooksConsumer.consumeZohoBooksProjects();
		
		// start taking zohoBooksTasks per project
		zohoBooksConsumer.consumeZohoBooksTasks();

		
		
	}

}
