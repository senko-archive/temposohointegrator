package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraTask;

public interface JiraTaskRepository extends CrudRepository<JiraTask, Long> {
	Optional<JiraTask> findJiraTaskById(String id);
	
	

}
