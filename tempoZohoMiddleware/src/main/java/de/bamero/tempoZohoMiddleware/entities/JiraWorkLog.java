package de.bamero.tempoZohoMiddleware.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class JiraWorkLog {
	
	@Id
	private String jiraWorkLogId;
		
	private String workLogURI;
	private String tempoWorkLogId;
	
	
	private String issueURI;
	private String issueKey;
	
	private Long timeSpentSeconds;
	private Long billableSeconds;
	
	private LocalDate startDate;
	private LocalTime startTime;
	
	private String Description;
	
	private LocalDateTime createdDate;
	private LocalDateTime updatedDate;
	
	private String authorURI;
	private String authorAccountId;
	private String authorDipslayName;
	
	@ManyToOne
	@JoinColumn
	private JiraBaseTask jiraTaskOrSubTask;
	
	public void addJiraTask(JiraBaseTask jiraBaseTask) {
		jiraBaseTask.getWorkLogs().add(this);
		this.jiraTaskOrSubTask = jiraBaseTask;
	}
}
