package de.bamero.tempoZohoMiddleware.dataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.data.annotation.Persistent;

public class ZohoBooksTimeEntryRepositoryImpl implements ZohoBooksTimeEntryRepositoryCustom {

	@PersistenceContext
	EntityManager em;
	
	@Transactional
	public void flushAndClear() {
		em.flush();
		em.clear();
		
	}

}
