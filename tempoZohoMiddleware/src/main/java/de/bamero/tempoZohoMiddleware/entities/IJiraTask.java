package de.bamero.tempoZohoMiddleware.entities;

import java.util.List;

public interface IJiraTask {
	
	public List<JiraWorkLog> getWorklogList();
	
	public String getTaskId();
	
	public Boolean getIsSubTask();

}
