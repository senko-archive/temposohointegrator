package de.bamero.tempoZohoMiddleware.dataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraProjectRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JiraProjectService implements IJiraProjectService {
	
	@Autowired
	JiraProjectRepository jiraProjectRepository;

	@Override
	public List<JiraProject> getAllJiraProjects() {
		Iterable<JiraProject> jiraProjectsIterator = jiraProjectRepository.findAll();
		List<JiraProject> jiraProjectList = new ArrayList<>();
		jiraProjectsIterator.forEach(jiraProjectList::add);
		return jiraProjectList;
	}

	@Override
	public JiraProject getJiraProjectById(long jiraProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
	}
	
	public Optional<JiraProject> getJiraProjectByKey(String projectKey) {
		log.debug("JiraProjectService.getJiraProjectByKey() now running");
		log.debug("projectKey is: " + projectKey);
		return jiraProjectRepository.getByProjectKey(projectKey);
	}

	@Override
	public boolean addJiraProject(JiraProject jiraProject) {
		jiraProjectRepository.save(jiraProject);
		return true;
	}

	@Override
	public boolean updateJiraProject(JiraProject jiraProject) {
		jiraProjectRepository.save(jiraProject);
		return true;
		
	}

	@Override
	public void deleteJiraProject(int jiraProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public JiraProject getJiraProjectByJiraProjectId(String jiraProjectId) {
		log.debug("JiraProjectService.getJiraProjectByJiraProjectId now working");
		log.debug("id to search: " + jiraProjectId);
		Optional<JiraProject> foundedJiraProject = jiraProjectRepository.getByProjectId(jiraProjectId);
		log.debug("sdfsdf");
		return foundedJiraProject.get();
	}

	@Override
	public boolean checkJiraProjectById(String projectId) {
		Optional<JiraProject> optionalJiraProject = jiraProjectRepository.findOptionalByProjectId(projectId);
		return optionalJiraProject.isPresent();
	}

	@Override
	public void update(JiraProject project) {
		// get JiraProject from database
		JiraProject jiraProjectFromDB = getJiraProjectByJiraProjectId(project.getProjectId());
		
		// compare if both same or not 
		// if jiraProject in database is not equal jiraProject, means something changed in jira App
		if(!jiraProjectFromDB.equals(project)) {
			log.debug("JiraProjectService.update() - JiraProject from Rest have some new values");
			jiraProjectFromDB.setProjectJiraUri(project.getProjectJiraUri());
			jiraProjectFromDB.setProjectKey(project.getProjectKey());
			jiraProjectFromDB.setProjectName(project.getProjectName());
			jiraProjectFromDB.setProjectCategory(project.getProjectCategory());
			
			// put updated project to database
			boolean result = updateJiraProject(jiraProjectFromDB);
		}
		
		// if they are equal do nothing
		
	}


}
