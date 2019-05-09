package de.bamero.tempoZohoMiddleware.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

import lombok.Data;

@Data
@Entity
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name="JiraTaskType")
public class JiraBaseTask {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraBaseTaskId;
	
	// worklogs will be there
	@OneToMany(mappedBy="jiraTaskOrSubTask", cascade=CascadeType.PERSIST, fetch = FetchType.EAGER)
	private List<JiraWorkLog> workLogs;
	
	

}
