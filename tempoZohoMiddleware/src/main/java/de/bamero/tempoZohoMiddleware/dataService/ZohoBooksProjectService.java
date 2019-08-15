package de.bamero.tempoZohoMiddleware.dataService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksProjectRepository;
import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksProjectRepositoryImpl;
import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksProjectRepositoryWithoutCRUD;
import de.bamero.tempoZohoMiddleware.entities.JiraProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZohoBooksProjectService implements IZohoBooksProjectService {
	
	@Autowired
	ZohoBooksProjectRepository zohoBooksProjectRepository;
	
	@Autowired
	ZohoBooksProjectRepositoryWithoutCRUD zohoBooksProjectRepositoryWithoutCRUD;
	
	@Autowired
	ZohoBooksProjectRepositoryImpl zohoBooksProjectRepositoryImpl;

	@Override
	public List<ZohoBooksProject> getAllZohoBooksProjects() {
		
		Iterable<ZohoBooksProject> zohoBooksProjectsIterator = zohoBooksProjectRepository.findAll();
		// convert Iterable to Stream and then convert stream to List
		return StreamSupport.stream(zohoBooksProjectsIterator.spliterator(), false).collect(Collectors.toList());
		
	}

	@Override
	public ZohoBooksProject getZohoBooksProjectById(Long _zohoBooksProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
	}
	
	@Override
	public ZohoBooksProject getZohoBooksProjectByProjectId(String projectId) {
		Optional<ZohoBooksProject> zohoBooksProject = zohoBooksProjectRepository.findByProjectId(projectId);
		if(zohoBooksProject.isPresent()) {
			return zohoBooksProject.get();
		}
		else {
			log.error("User with id: " + projectId + " is not found on database");
			throw new EntityNotFoundException();
		}
	}

	@Override
	public boolean addZohoBooksProject(ZohoBooksProject zohoBooksProject) {
		log.debug("ZohoBooksProjectService.addZohoBooksProject() now working"); 
		zohoBooksProjectRepository.save(zohoBooksProject);
		return true;
	}
	
	@Override
	public boolean addZohoBooksProjectWithoutCRUD(ZohoBooksProject zohoBooksProject) {
		log.debug("ZohoBooksProjectService.addZohoBooksProjectwithoutCRUD() now working");
		zohoBooksProjectRepositoryWithoutCRUD.saveAndFlush(zohoBooksProject);
		return true;
	}
	
	@Override
	public boolean clear() {
		log.debug("ZohoBooksProjectService.addZohoBooksProjectWithClear() now working");
		zohoBooksProjectRepositoryImpl.clear();
		return true;
	}

	@Override
	public ZohoBooksProject updateZohoBooksProject(ZohoBooksProject zohoBooksProject) {
		return zohoBooksProjectRepository.save(zohoBooksProject);
		
	}

	@Override
	public void deleteZohoBooksProject(Long _zohoBooksProjectId) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public boolean checkZohoBooksProjectById(String projectId) {
		Optional<ZohoBooksProject> optionalZohoBooksProject = zohoBooksProjectRepository.findOptionalByProjectId(projectId);
		return optionalZohoBooksProject.isPresent();
	}

	@Override
	public void update(ZohoBooksProject zohoBooksProject) {
		
		// get zohoBooksProject from database
		ZohoBooksProject zohoBooksProjectFromDB = getZohoBooksProjectByProjectId(zohoBooksProject.getProjectId());
		
		// compare if both are same or not
		// if zohoBooksProject in database is not equal zohobBooksProject from RestAPI, means something changed in ZohoBooks App
		if(!zohoBooksProjectFromDB.equals(zohoBooksProject)) {
			// update with new values
			zohoBooksProjectFromDB.setProjectName(zohoBooksProject.getProjectName());
			zohoBooksProjectFromDB.setCustomerId(zohoBooksProject.getCustomerId());
			zohoBooksProjectFromDB.setCustomerName(zohoBooksProject.getCustomerName());
			zohoBooksProjectFromDB.setDescription(zohoBooksProject.getDescription());
			zohoBooksProjectFromDB.setStatus(zohoBooksProject.getStatus());
			zohoBooksProjectFromDB.setBillingType(zohoBooksProject.getBillingType());
			zohoBooksProjectFromDB.setRate(zohoBooksProject.getRate());
			zohoBooksProjectFromDB.setJiraKey(zohoBooksProject.getJiraKey());
			
			// put updated zohoBooksProject to database
			updateZohoBooksProject(zohoBooksProjectFromDB);
		}
	} 

}
