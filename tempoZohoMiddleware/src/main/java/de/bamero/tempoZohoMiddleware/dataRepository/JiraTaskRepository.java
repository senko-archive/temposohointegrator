package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraTask;

public interface JiraTaskRepository extends CrudRepository<JiraTask, Long> {

}
