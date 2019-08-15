package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.TempoWorklogJPARepository;
import de.bamero.tempoZohoMiddleware.dataRepository.TempoWorklogRepository;
import de.bamero.tempoZohoMiddleware.entities.JiraWorkLog;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TempoWorklogService implements ITempoWorklogService {
	
	@Autowired
	TempoWorklogRepository tempoWorklogRepository;
	
	@Autowired
	TempoWorklogJPARepository tempoWorklogJPARepository;

	@Override
	public List<JiraWorkLog> getAllTempoWorklogs() {
		throw new NotImplementedException("Not yet implemented");
	}

	@Override
	public JiraWorkLog getTempoWorkLogById(String tempoWorklogId) {
		throw new NotImplementedException("Not yet implemented");
	}

	@Override
	public boolean addTempoWorklog(JiraWorkLog tempoWorklog) {
		log.debug("TempoWorklogService.addTempoWorklog() now working"); 
		tempoWorklogRepository.save(tempoWorklog);
		return true;
	}

	@Override
	public void updateTempoWorklog(JiraWorkLog tempoWorklog) {
		throw new NotImplementedException("Not yet implemented");
		
	}

	@Override
	public void deleteTempoWorklog(int _tempoWorklogId) {
		throw new NotImplementedException("Not yet implemented");
		
	}

	@Override
	@Transactional
	public void truncateWorklogs() {
		tempoWorklogJPARepository.truncateTempoWorklogTable();
		
	}

}
