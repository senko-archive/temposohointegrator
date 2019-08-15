package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;

public interface IZohoBooksProjectService {
	
	List<ZohoBooksProject> getAllZohoBooksProjects();
	ZohoBooksProject getZohoBooksProjectById(Long _zohoBooksProjectId);
	ZohoBooksProject getZohoBooksProjectByProjectId(String projectId);
	boolean addZohoBooksProject(ZohoBooksProject zohoBooksProject);
	boolean addZohoBooksProjectWithoutCRUD(ZohoBooksProject zohoBooksProject);
	boolean clear();
	ZohoBooksProject updateZohoBooksProject(ZohoBooksProject zohoBooksProject);
	void deleteZohoBooksProject(Long _zohoBooksProjectId);
	boolean checkZohoBooksProjectById(String projectId);
	void update(ZohoBooksProject zohoBooksProject);
	
}
