package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;

public interface ZohoBooksTaskRepository extends CrudRepository<ZohoBooksTask, String> {
	ZohoBooksTask findByZohoBooksTaskId(String zohoBooksTaskId);
	Optional<ZohoBooksTask> findOptionalByZohoBooksTaskId(String zohoBooksTaskId);

}
