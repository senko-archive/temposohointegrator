package de.bamero.tempoZohoMiddleware.dataRepository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import de.bamero.tempoZohoMiddleware.entities.JiraTask;

public interface JiraTaskJPARepository extends JpaRepository<JiraTask, Long> {
	
	@Modifying
	@Query(
			value = "SET REFERENTIAL_INTEGRITY FALSE; TRUNCATE TABLE JIRA_TASK; TRUNCATE TABLE JIRA_SUB_TASK; TRUNCATE TABLE JIRA_BASE_TASK; SET REFERENTIAL_INTEGRITY TRUE;",
			nativeQuery = true)
   void truncateJiraTaskTable(); 
	
	
}