package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;

public interface TempoWorklogRepository extends CrudRepository<JiraWorkLog, Long> {

}
