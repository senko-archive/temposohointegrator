package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Set;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTimeEntry;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;

public interface IZohoBooksTaskService {
	
	List<ZohoBooksTask> getAllZohoBooksTasks();
	ZohoBooksTask getZohoBookTaskByTaskId(String taskId);
	boolean addZohoBooksTask(ZohoBooksTask zohoBooksTask);
	ZohoBooksTask updateZohoBooksTask(ZohoBooksTask zohoBooksTask);
	void deleteZohoBooksTask(ZohoBooksTask zohoBooksTask);
	boolean checkZohoBooksTaskByzohoBooksTaskId(String zohoBooksTaskId);
	
}
