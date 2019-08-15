package de.bamero.tempoZohoMiddleware.response.entities;

import lombok.Data;

@Data
public class JiraTaskResponse {
	
	private String taskId;
	private String description;
	private String assigneeAccountId;
	private String assigneeDisplayedName;

}
