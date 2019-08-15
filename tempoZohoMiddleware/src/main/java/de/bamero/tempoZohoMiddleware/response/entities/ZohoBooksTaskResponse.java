package de.bamero.tempoZohoMiddleware.response.entities;

import lombok.Data;

@Data
public class ZohoBooksTaskResponse {
	
	private String zohoBooksTaskId;
	private String taskName;
	private String description;
	
	private String jiraTaskId;
	private String jiraTaskName;

}
