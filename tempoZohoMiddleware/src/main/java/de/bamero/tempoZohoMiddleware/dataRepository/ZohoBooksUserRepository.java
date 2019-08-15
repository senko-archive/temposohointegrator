package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;

public interface ZohoBooksUserRepository extends CrudRepository<ZohoBooksUser, Long> {
	Optional<ZohoBooksUser> findByUserId(String user_id);
	Optional<ZohoBooksUser> findOptionalByUserId(String userId);

}
