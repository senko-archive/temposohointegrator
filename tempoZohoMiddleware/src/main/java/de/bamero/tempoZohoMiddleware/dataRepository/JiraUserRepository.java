package de.bamero.tempoZohoMiddleware.dataRepository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import de.bamero.tempoZohoMiddleware.entities.JiraUser;

public interface JiraUserRepository extends CrudRepository<JiraUser, Long>  {
	
	JiraUser getByAccountId(String accountId);
	Optional<JiraUser> findOptionalByAccountId(String accountId);

}
