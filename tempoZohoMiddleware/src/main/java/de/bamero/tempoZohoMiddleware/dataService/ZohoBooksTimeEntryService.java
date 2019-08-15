package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksTimeEntryRepository;
import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksTimeEntryRepositoryImpl;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZohoBooksTimeEntryService implements IZohoBooksTimeEntryService {
	
	@Autowired
	ZohoBooksTimeEntryRepository zohoBooksTimeEntryRepository;
	
	@Autowired
	ZohoBooksTimeEntryRepositoryImpl zohoBooksTimeEntryRepositoryImpl;

	@Override
	public List<ZohoBooksTimeEntry> getAllZohoBooksTimeEntries() {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public Optional<ZohoBooksTimeEntry> getZohoBooksTimeEntryById(String zohoBooksTimeEntryId) {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public Optional<ZohoBooksTimeEntry> getZohoBooksTimeEntryByJiraWorkLogId(String jiraWorkLogId) {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public boolean addZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry) {
		zohoBooksTimeEntryRepositoryImpl.flushAndClear();
		ZohoBooksTimeEntry timeEntry = zohoBooksTimeEntryRepository.save(zohoBooksTimeEntry);
		if(!(timeEntry == null)) {
			return true;
		}
		else {
			return false;
		}
		
	}

	@Override
	public ZohoBooksTimeEntry updateZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry) {
		ZohoBooksTimeEntry timeEntry = zohoBooksTimeEntryRepository.save(zohoBooksTimeEntry);
		return timeEntry;
	}

	@Override
	public boolean deleteZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry) {
		throw new NotImplementedException("this method is not implemented yet");
	}
	
	

}
