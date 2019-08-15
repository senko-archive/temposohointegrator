package de.bamero.tempoZohoMiddleware.entities;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import de.bamero.tempoZohoMiddleware.utils.LambdaEquals;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class JiraProject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _jiraProjectId;
	
	private String projectJiraUri;
	private String projectId;
	private String projectKey;
	private String projectName;
	private String projectCategory;
	
	
	@OneToMany(mappedBy="jiraProject", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<JiraTask> jiraTasks;
	
	@Override
	public boolean equals(Object obj) {
		return LambdaEquals.equals(this, obj, JiraProject::getProjectJiraUri,
											  JiraProject::getProjectName,
											  JiraProject::getProjectKey,
											  JiraProject::getProjectCategory);
	}

	
	
	
}
