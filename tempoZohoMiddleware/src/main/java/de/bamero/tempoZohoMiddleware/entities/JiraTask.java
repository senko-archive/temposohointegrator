package de.bamero.tempoZohoMiddleware.entities;

import java.time.LocalDateTime;
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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.FetchMode;
import org.hibernate.annotations.Fetch;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class JiraTask extends JiraBaseTask {
	/*
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraTaskId;
	*/




	private String id;
	private String self;
	private String key;
	private String issueType;
	private String description;
	private String summary;
	private LocalDateTime created;
	private LocalDateTime updated;
	
	@Transient
	private Boolean isSubTask;
	
	@ManyToOne
	@JoinColumn
	private JiraUser assignee;
	
	@OneToMany(mappedBy="jiraTask", cascade=CascadeType.PERSIST, fetch=FetchType.EAGER)
	@Fetch(value=org.hibernate.annotations.FetchMode.SUBSELECT)
	@ElementCollection(targetClass=JiraSubTask.class)
	private List<JiraSubTask> subTasks = new ArrayList<JiraSubTask>();
	
	@ManyToOne
	@JoinColumn
	private JiraProject jiraProject;
	
	public void addAssignee(JiraUser jiraUser) {
		this.assignee = jiraUser;
		jiraUser.getJiraTasks().add(this);
	}
	

	public String getTaskId() {
		return this.id;
	}
	

	
	
}
