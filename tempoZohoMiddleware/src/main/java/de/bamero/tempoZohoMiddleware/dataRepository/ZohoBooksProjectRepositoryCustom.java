package de.bamero.tempoZohoMiddleware.dataRepository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;

public interface ZohoBooksProjectRepositoryCustom {
	void refresh(ZohoBooksProject zohoBooksProject);
	void clear();


}
