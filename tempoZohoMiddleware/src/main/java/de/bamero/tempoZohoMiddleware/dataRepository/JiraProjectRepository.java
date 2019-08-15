package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;

public interface JiraProjectRepository extends CrudRepository<JiraProject, Long> {
	
	Optional<JiraProject> getByProjectKey(String projectKey);
	Optional<JiraProject> getByProjectId(String projectId);
	Optional<JiraProject> findOptionalByProjectId(String projectId);

	

	
	
}
