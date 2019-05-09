package de.bamero.tempoZohoMiddleware.consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import de.bamero.tempoZohoMiddleware.dataService.JiraProjectService;
import de.bamero.tempoZohoMiddleware.entities.JiraBaseTask;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.parsers.TempoParser;
import de.bamero.tempoZohoMiddleware.utils.ConsumerHelper;
import de.bamero.tempoZohoMiddleware.utils.PropertiesLoader;

@Component
@Scope("singleton")
public class TempoConsumer {

	private static final Logger logger = LoggerFactory.getLogger(TempoConsumer.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired 
	JiraProjectService jiraProjectService;
	
	@Autowired
	TempoParser tempoParser;

	PropertiesLoader loader;

	public TempoConsumer() {
		this.loader = new PropertiesLoader();

	}
	
	public void consumeTempoWorklogForEachJiraTask() {
		
		logger.debug("TempoConsumer.consumeTempoWorklogForEachJiraTask() now working");
		
		// take access_token from properties
		Properties tempoProperties = loader.load("tempoRest.properties");
		String accessToken = tempoProperties.getProperty("access_token");
		
		// init Lists for jira tasks and projects
		List<String> jiraTaskAndSubTaskIdList = new ArrayList<>();
		List<JiraProject> allJiraProjects = new ArrayList<>();
		List<String> dumpList = new ArrayList<>();
		
		// first get all projects
		allJiraProjects = jiraProjectService.getAllJiraProjects();
		
		// get all jiraTask Id's
		/*
		jiraTaskAndSubTaskIdList = allJiraProjects.stream().flatMap(
				project -> project.getJiraTasks().stream().map(
						tasks -> tasks.getId())).collect(Collectors.toList());
		*/
		
		// get all jiraSubTask Id's
		/*
		jiraTaskAndSubTaskIdList.addAll( 
				allJiraProjects.stream().flatMap(
				project -> project.getJiraTasks().stream().flatMap(
						tasks -> tasks.getSubTasks().stream().map(
								subtasks -> subtasks.getId()))).collect(Collectors.toList())
				);
		*/
		
		
		// get all jira task and jira subtask id's with type Map<JiraTask/JiraSubTask Id, type(task or subtask)
		Map<String, JiraBaseTask> allJiraTaskandSubTaskIds = new HashMap<>();
		allJiraProjects.forEach(project -> project.getJiraTasks().forEach(task -> {
			task.setIsSubTask(false);
			allJiraTaskandSubTaskIds.put(task.getId(), task);	
		}));
		allJiraProjects.forEach(project -> project.getJiraTasks().forEach(task -> task.getSubTasks().forEach(subTask -> {
			task.setIsSubTask(true);
			allJiraTaskandSubTaskIds.put(subTask.getId(), subTask);	
		})));
		
		System.out.println("All Id's are gathered");
		
		// parse worklogs for each JiraTask or JiraSubTask
		for(Map.Entry<String, JiraBaseTask> entry : allJiraTaskandSubTaskIds.entrySet()) {
			logger.info("making request for task: " + entry.getKey());
			
			// prepare url and headers
			// TODO add query parameters to this request you can use this link for help -> https://tempo-io.github.io/tempo-api-docs/#worklogs
			StringBuffer tempoWorklogRequest = new StringBuffer();
			tempoWorklogRequest.append("https://api.tempo.io/core/3/worklogs/issue/").append(entry.getKey());
			
			String fullAccessCode = "Bearer " + accessToken;
			Map<String, String> requestHeader = new HashMap<>();
			requestHeader.put("Authorization", fullAccessCode);
			
			// send request
			ResponseEntity<String> response = ConsumerHelper.getResponse(restTemplate, requestHeader, tempoWorklogRequest.toString());
			
			parseTempoWorklogs(response, entry.getValue());
			
		}
		
	}

	private void parseTempoWorklogs(ResponseEntity<String> response, JiraBaseTask iJiraTask) {
		logger.debug("TempoConsumer.parseTempoWorklogs() now working");
		tempoParser.tempoWorklogParser(response, iJiraTask);
		
	}

}
