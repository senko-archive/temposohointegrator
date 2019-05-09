package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraSubTask;

public interface JiraSubTaskRepository extends CrudRepository<JiraSubTask, Long> {
	
	JiraSubTask getById(String id);

}
