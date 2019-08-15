package de.bamero.tempoZohoMiddleware.dataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public interface ZohoBooksTaskRepositoryCustom {
	
	void flushAndClear();	
	

}
