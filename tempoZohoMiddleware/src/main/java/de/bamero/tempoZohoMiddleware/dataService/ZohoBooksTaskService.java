package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksTaskRepository;
import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksTaskRepositoryImpl;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksTask;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZohoBooksTaskService implements IZohoBooksTaskService {
	
	@Autowired
	ZohoBooksTaskRepository zohoBooksTaskRepository;
	
	@Autowired
	ZohoBooksTaskRepositoryImpl zohoBooksTaskRepositoryImpl;

	@Override
	public List<ZohoBooksTask> getAllZohoBooksTasks() {
		throw new NotImplementedException("this method is not implemented yet");
	}

	@Override
	public ZohoBooksTask getZohoBookTaskByTaskId(String taskId) {
		log.debug("ZohoBooksTaskService.getZohoBooksTaskByTaskId() now wokring");
		
		log.debug("id to search: " + taskId);
		//ZohoBooksTask foundedZohoBooksTask = zohoBooksTaskRepository.findByZohoBooksTaskId(taskId);
		Optional<ZohoBooksTask> optTask = zohoBooksTaskRepository.findById(taskId);
		if(!optTask.isPresent()) {
			log.error("there is no record with id: " + taskId);
		}
		return optTask.get();
	}

	@Override
	public boolean addZohoBooksTask(ZohoBooksTask zohoBooksTask) {
		log.debug("ZohoBooksTaskService.addZohoBooksTask() now working");
		zohoBooksTaskRepositoryImpl.flushAndClear();
		zohoBooksTaskRepository.save(zohoBooksTask);
		return true;
	}

	@Override
	public ZohoBooksTask updateZohoBooksTask(ZohoBooksTask zohoBooksTask) {
		return zohoBooksTaskRepository.save(zohoBooksTask);
	}

	@Override
	public void deleteZohoBooksTask(ZohoBooksTask zohoBooksTask) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}
	
	@Override
	public boolean checkZohoBooksTaskByzohoBooksTaskId(String zohoBooksTaskId) {
		
		Optional<ZohoBooksTask> optionalZohoBooksTask = zohoBooksTaskRepository.findOptionalByZohoBooksTaskId(zohoBooksTaskId);
		return optionalZohoBooksTask.isPresent();
	}

	public void update(ZohoBooksTask zohoBookTask) {
		
		// get zohoBooksTask from database
		ZohoBooksTask zohoBooksTaskFromDB = getZohoBookTaskByTaskId(zohoBookTask.getZohoBooksTaskId());
		
		// compare if both are same or not
		// if zohoBooksTask in database is not equal zohoBooksTask from RestAPI, means something changed in ZohoBooks App
		if(!zohoBooksTaskFromDB.equals(zohoBookTask)) {
			zohoBooksTaskFromDB.setProjectId(zohoBookTask.getProjectId());
			zohoBooksTaskFromDB.setCurrencyId(zohoBookTask.getCurrencyId());
			zohoBooksTaskFromDB.setCustomerId(zohoBookTask.getCustomerId());
			zohoBooksTaskFromDB.setTaskName(zohoBookTask.getTaskName());
			zohoBooksTaskFromDB.setDescription(zohoBookTask.getDescription());
			zohoBooksTaskFromDB.setProjectName(zohoBookTask.getProjectName());
			zohoBooksTaskFromDB.setRate(zohoBookTask.getRate());
			zohoBooksTaskFromDB.setStatus(zohoBookTask.getStatus());
			zohoBooksTaskFromDB.setIsBillable(zohoBooksTaskFromDB.getIsBillable());
			
			// put updated zohoBooksTask to database
			updateZohoBooksTask(zohoBooksTaskFromDB);
		}
		
	}

}
