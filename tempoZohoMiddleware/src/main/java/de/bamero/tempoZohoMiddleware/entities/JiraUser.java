package de.bamero.tempoZohoMiddleware.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
public class JiraUser {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraUserId;
	
	private String userJiraUri;
	private String key;
	private String accountId;
	private String name;
	private String emailAddress;
	private String displayName;
	
	@OneToMany(mappedBy="assignee", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@ElementCollection(targetClass=JiraTask.class)
	private List<JiraTask> jiraTasks = new ArrayList<>();
	
	@OneToMany(mappedBy="assignee", cascade=CascadeType.ALL, fetch = FetchType.EAGER)
	@ElementCollection(targetClass=JiraSubTask.class)
	private List<JiraSubTask> jiraSubTasks = new ArrayList<>();
	
	public JiraUser() {
		
	}

}
