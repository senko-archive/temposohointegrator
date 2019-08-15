package de.bamero.tempoZohoMiddleware.parsers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.bamero.tempoZohoMiddleware.dataService.TempoWorklogService;
import de.bamero.tempoZohoMiddleware.entities.JiraBaseTask;
import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;
import de.bamero.tempoZohoMiddleware.entities.JiraTask;
import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;

@Component
public class TempoParser {
	
	@Autowired
	TempoWorklogService tempoWorklogService;
	
	private static final Logger logger = LoggerFactory.getLogger(TempoParser.class);
	
	public void tempoWorklogParser(ResponseEntity<String> response, JiraBaseTask iJiraTask) {
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
	
	private void parseTempoWorklog(JsonNode jsonNode, JiraBaseTask jiraBaseTask) {
		logger.debug("TempoParser.parseTempoWorklog()");
		
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
		
		
		String createdAt = jsonNode.get("createdAt").asText().replaceAll("[TZ]", " ").trim();
		String updateAt = jsonNode.get("updatedAt").asText().replaceAll("[TZ]", " ").trim();
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		LocalDateTime createdAtLocalDateTime = LocalDateTime.parse(createdAt, dateTimeFormatter);
		ZoneId oldZone = ZoneId.of("GMT+0");
		ZoneId newZone = ZoneId.of("Europe/Berlin");
		
		LocalDateTime createdLocalDateTimeForBerlin = createdAtLocalDateTime.atZone(oldZone)
															.withZoneSameInstant(newZone)
															.toLocalDateTime();
		
		LocalDateTime updatedAtLocalDateTime = LocalDateTime.parse(updateAt, dateTimeFormatter);
		LocalDateTime updatedAtLocalDateTimeForBerlin = updatedAtLocalDateTime.atZone(oldZone)
				.withZoneSameInstant(newZone)
				.toLocalDateTime();
		
		tempoWorklog.setCreatedDate(createdLocalDateTimeForBerlin);
		tempoWorklog.setUpdatedDate(updatedAtLocalDateTimeForBerlin);
		tempoWorklog.setAuthorURI(jsonNode.get("author").get("self").asText());
		tempoWorklog.setAuthorAccountId(jsonNode.get("author").get("accountId").asText());
		tempoWorklog.setAuthorDipslayName(jsonNode.get("author").get("displayName").asText());
		
		tempoWorklog.addJiraTask(jiraBaseTask);
		tempoWorklogService.addTempoWorklog(tempoWorklog);
		
		/*
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
		*/
		
		
		
	}

}
