package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;

public interface IZohoBooksTimeEntryService {
	
	List<ZohoBooksTimeEntry> getAllZohoBooksTimeEntries();
	Optional<ZohoBooksTimeEntry> getZohoBooksTimeEntryById(String zohoBooksTimeEntryId);
	Optional<ZohoBooksTimeEntry> getZohoBooksTimeEntryByJiraWorkLogId(String jiraWorkLogId);
	boolean addZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry);
	ZohoBooksTimeEntry updateZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry);
	boolean deleteZohoBooksTimeEntry(ZohoBooksTimeEntry zohoBooksTimeEntry);
	
	

}
