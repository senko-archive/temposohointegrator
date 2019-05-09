package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bamero.tempoZohoMiddleware.entities.IJiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;

@Component
public class TempoParser {
	
	private static final Logger logger = LoggerFactory.getLogger(TempoParser.class);
	
	public void tempoWorklogParser(ResponseEntity<String> response, IJiraTask iJiraTask) {
		logger.debug("TempoParser.tempoWorklogParser is now working");
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root;
			root = mapper.readTree(response.getBody());
			JsonNode worklogList = root.get("results");
			Iterator<JsonNode> iter = worklogList.iterator();
			
			while(iter.hasNext()) {
				// parse each worklog
				parseTempoWorklog(iter.next(), iJiraTask);
			}
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		
	}
	
	private void parseTempoWorklog(JsonNode jsonNode, IJiraTask jiraTask) {
		logger.debug("TempoParser.parseTempoWorklog()");
		logger.debug("I'm parsing for jiraTask or Subtask id:" + jiraTask.getTaskId());
		
		JiraWorkLog tempoWorklog = new JiraWorkLog();
		tempoWorklog.setWorkLogURI(jsonNode.get("self").asText());
		tempoWorklog.setTempoWorkLogId(jsonNode.get("tempoWorklogId").asText());
		tempoWorklog.setJiraWorkLogId(jsonNode.get("jiraWorklogId").asText());
		tempoWorklog.setIssueURI(jsonNode.get("issue").get("self").asText());
		tempoWorklog.setIssueKey(jsonNode.get("issue").get("key").asText());
		tempoWorklog.setTimeSpentSeconds(jsonNode.get("timeSpentSeconds").asLong());
		tempoWorklog.setBillableSeconds(jsonNode.get("billableSeconds").asLong());
		
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		tempoWorklog.setStartDate(LocalDate.parse(jsonNode.get("startDate").asText(), dateFormatter));
		
		DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
		tempoWorklog.setStartTime(LocalTime.parse(jsonNode.get("startTime").asText(), timeFormatter));
		
		tempoWorklog.setDescription(jsonNode.get("description").asText());
		
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_INSTANT;
		tempoWorklog.setCreatedDate(LocalDateTime.parse(jsonNode.get("startDate").asText(), dateTimeFormatter));
		tempoWorklog.setUpdatedDate(LocalDateTime.parse(jsonNode.get("updatedAt").asText(), dateTimeFormatter));
		tempoWorklog.setAuthorURI(jsonNode.get("author").get("self").asText());
		tempoWorklog.setAuthorAccountId(jsonNode.get("author").get("accountId").asText());
		tempoWorklog.setAuthorDipslayName(jsonNode.get("author").get("displayName").asText());
		
		// decide IJiraTask is JiraTask or JiraSubTask
		if(jiraTask.getIsSubTask() == true) {
			JiraTask myJiraTask = (JiraTask) jiraTask;
			logger.debug("I'm jira task my id:" + myJiraTask.getId());
			tempoWorklog.addJiraTask(myJiraTask);
		}
		
		if(jiraTask.getIsSubTask()==false) {
			JiraSubTask myJiraSubTask = (JiraSubTask) jiraTask;
			logger.debug("I'm jira subtask my id: " + myJiraSubTask.getId());
			tempoWorklog.addJiraTask(myJiraSubTask);
		}
		
		
		
	}

}
