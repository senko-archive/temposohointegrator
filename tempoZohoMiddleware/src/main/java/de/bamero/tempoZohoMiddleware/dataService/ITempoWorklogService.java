package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;



public interface ITempoWorklogService {
	
	List<JiraWorkLog> getAllTempoWorklogs();
	JiraWorkLog getTempoWorkLogById(String tempoWorklogId);
	boolean addTempoWorklog(JiraWorkLog tempoWorklog);
	void updateTempoWorklog(JiraWorkLog tempoWorklog);
	void deleteTempoWorklog(int _tempoWorklogId);

}
