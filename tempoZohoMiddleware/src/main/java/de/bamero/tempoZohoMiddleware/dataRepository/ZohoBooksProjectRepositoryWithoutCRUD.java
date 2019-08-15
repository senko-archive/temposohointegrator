package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;

public interface ZohoBooksProjectRepositoryWithoutCRUD extends JpaRepository<ZohoBooksProject, Long> {
	

}
