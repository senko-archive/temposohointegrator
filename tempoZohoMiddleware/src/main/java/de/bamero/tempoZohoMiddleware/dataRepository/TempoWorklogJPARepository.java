package de.bamero.tempoZohoMiddleware.dataRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;

public interface TempoWorklogJPARepository extends JpaRepository<JiraWorkLog, String> {
	
	@Modifying
	@Query(
			value = "SET REFERENTIAL_INTEGRITY FALSE; TRUNCATE TABLE JIRA_WORK_LOG; SET REFERENTIAL_INTEGRITY TRUE;",
			nativeQuery = true
			)
	void truncateTempoWorklogTable();
	

}
