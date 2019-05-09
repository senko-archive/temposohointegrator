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

@Data
@Entity
public class JiraWorkLog {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraWorkLogId;
		
	private String workLogURI;
	private String tempoWorkLogId;
	private String jiraWorkLogId;
	
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
	private IJiraTask jiraTaskOrSubTask;
	
	public void addJiraTask(IJiraTask jiraTask) {
		jiraTask.getWorklogList().add(this);
		this.jiraTaskOrSubTask = jiraTask;
	}
}
