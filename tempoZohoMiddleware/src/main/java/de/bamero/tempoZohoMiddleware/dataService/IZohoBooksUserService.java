package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;

import de.bamero.tempoZohoMiddleware.entities.ZohoBooksProject;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;

public interface IZohoBooksUserService {
	
	List<ZohoBooksUser> getAllZohoBooksUsers();
	ZohoBooksUser getZohoBooksUserById(Long _zohoBooksUserId);
	ZohoBooksUser getZohoBooksUserByUserId(String user_id);
	boolean addZohoBooksUser(ZohoBooksUser zohoBooksUser);
	ZohoBooksUser updateZohoBooksUser(ZohoBooksUser zohoBooksUser);
	void deleteZohoBooksUser(ZohoBooksUser zohoBooksUser);
	boolean checkZohoBooksUserById(String userId);
	void update(ZohoBooksUser zohoBooksUser);
	

}
