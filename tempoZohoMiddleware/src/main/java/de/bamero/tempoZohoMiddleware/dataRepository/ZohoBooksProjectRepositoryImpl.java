package de.bamero.tempoZohoMiddleware.dataRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;

public class ZohoBooksProjectRepositoryImpl implements ZohoBooksProjectRepositoryCustom{
	
	@PersistenceContext
	private EntityManager em;
	
	@Transactional
	public void refresh(ZohoBooksProject zohoBooksProject) {
		em.refresh(zohoBooksProject);
		
	}
	
	@Transactional
	public void clear() {
		em.flush();
		em.clear();
		
	}
	
	
	
	

}
