package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;

public interface ZohoBooksTimeEntryRepository extends CrudRepository<ZohoBooksTimeEntry, String> {
	

}
