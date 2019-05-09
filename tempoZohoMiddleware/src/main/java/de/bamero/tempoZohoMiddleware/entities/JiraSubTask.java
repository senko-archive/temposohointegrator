package de.bamero.tempoZohoMiddleware.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import lombok.Data;

@Data
@Entity
public class JiraSubTask extends JiraBaseTask {
	/*
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraSubTaskId;
	*/
	
	private String id;
	private String key;
	private String self;
	private String issueType; // allways will be "Sub-task"
	private String description;
	
	@Transient
	private Boolean isSubTask;
	
	@ManyToOne
	@JoinColumn
	private JiraUser assignee;
	
	@ManyToOne(targetEntity=JiraTask.class)
	@JoinColumn()
	private JiraTask jiraTask;
	
	public void addAssignee(JiraUser jiraUser) {
		this.assignee = jiraUser;
		jiraUser.getJiraSubTasks().add(this);
	}
	
	public void addJiraTask(JiraTask jiraTask) {
		this.jiraTask = jiraTask;
		jiraTask.getSubTasks().add(this);
	}

	public String getTaskId() {
		return this.id;
	}
}
