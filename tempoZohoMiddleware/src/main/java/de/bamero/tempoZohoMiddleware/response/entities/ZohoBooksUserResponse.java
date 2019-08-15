package de.bamero.tempoZohoMiddleware.response.entities;

import java.util.List;

import lombok.Data;

@Data
public class ZohoBooksUserResponse {
	
	private String userId;
	private String name;
	private List<String> jiraUserIds;

}
