package de.bamero.tempoZohoMiddleware.response.entities;

import java.util.List;

import lombok.Data;

@Data
public class ZohoProjectResponse {
	
	private String projectId;
	private String projectName;
	private List<JiraProjectResponse> relatedJiraProjects; 

}
