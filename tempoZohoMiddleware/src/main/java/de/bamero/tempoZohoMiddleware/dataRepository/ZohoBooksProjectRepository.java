package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;

public interface ZohoBooksProjectRepository extends CrudRepository<ZohoBooksProject, Long> {
	
	Optional<ZohoBooksProject> findByProjectId(String projectId);
	Optional<ZohoBooksProject> findOptionalByProjectId(String projectId);

}
