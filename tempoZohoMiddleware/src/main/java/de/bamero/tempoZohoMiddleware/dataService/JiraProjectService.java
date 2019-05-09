package de.bamero.tempoZohoMiddleware.dataService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.JiraProjectRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;

@Service
public class JiraProjectService implements IJiraProjectService {
	
	@Autowired
	JiraProjectRepository JiraProjectRepository;

	@Override
	public List<JiraProject> getAllJiraProjects() {
		Iterable<JiraProject> jiraProjectsIterator = JiraProjectRepository.findAll();
		List<JiraProject> jiraProjectList = new ArrayList<>();
		jiraProjectsIterator.forEach(jiraProjectList::add);
		return jiraProjectList;
	}

	@Override
	public JiraProject getJiraProjectById(long jiraProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public boolean addJiraProject(JiraProject jiraProject) {
		JiraProjectRepository.save(jiraProject);
		return true;
	}

	@Override
	public void updateJiraProject(JiraProject jiraProject) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public void deleteJiraProject(int jiraProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

}
