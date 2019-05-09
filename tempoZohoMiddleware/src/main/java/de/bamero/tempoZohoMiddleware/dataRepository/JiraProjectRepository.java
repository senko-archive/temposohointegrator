package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;

public interface JiraProjectRepository extends CrudRepository<JiraProject, Long> {

}
