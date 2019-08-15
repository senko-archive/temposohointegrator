package de.bamero.tempoZohoMiddleware.dataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

public class ZohoBooksTaskRepositoryImpl implements ZohoBooksTaskRepositoryCustom {
	
	@PersistenceContext
	EntityManager em;
	
	@Transactional
	public void flushAndClear() {
		em.flush();
		em.clear();
		
	}

}
