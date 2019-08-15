package de.bamero.tempoZohoMiddleware.dataService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.bamero.tempoZohoMiddleware.dataRepository.ZohoBooksUserRepository;
import de.bamero.tempoZohoMiddleware.entities.ZohoBooksUser;
import de.bamero.tempoZohoMiddleware.exceptions.NotImplementedException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ZohoBooksUserService implements IZohoBooksUserService {
	
	@Autowired
	ZohoBooksUserRepository zohoBooksUserRepository;

	@Override
	public List<ZohoBooksUser> getAllZohoBooksUsers() {
		
		Iterable<ZohoBooksUser> zohoBooksUserIterator = zohoBooksUserRepository.findAll();
		// convert Iterable to Stream and then convert stream to List
		return StreamSupport.stream(zohoBooksUserIterator.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public ZohoBooksUser getZohoBooksUserById(Long _zohoBooksUserId) {
		throw new NotImplementedException("this method is not implemented yet");
	}
	
	@Override
	public ZohoBooksUser getZohoBooksUserByUserId(String user_id) {
		Optional<ZohoBooksUser> zohoBooksUser = zohoBooksUserRepository.findByUserId(user_id);
		if(zohoBooksUser.isPresent()) {
			return zohoBooksUser.get();
		}
		else {
			log.error("User with id: " + user_id + " is not found on database");
			throw new EntityNotFoundException();
		}
	}

	@Override
	public boolean addZohoBooksUser(ZohoBooksUser zohoBooksUser) {
		zohoBooksUserRepository.save(zohoBooksUser);
		return true;
	}

	@Override
	public ZohoBooksUser updateZohoBooksUser(ZohoBooksUser zohoBooksUser) {
		return zohoBooksUserRepository.save(zohoBooksUser);
		
	}

	@Override
	public void deleteZohoBooksUser(ZohoBooksUser zohoBooksUser) {
		throw new NotImplementedException("this method is not implemented yet");
		
	}

	@Override
	public boolean checkZohoBooksUserById(String userId) {
		Optional<ZohoBooksUser> optionalZohoUser = zohoBooksUserRepository.findOptionalByUserId(userId);
		return optionalZohoUser.isPresent();
	}

	@Override
	public void update(ZohoBooksUser zohoBooksUser) {
		// get zohoBooksUser from database
		ZohoBooksUser zohoBooksUserFromDB = getZohoBooksUserByUserId(zohoBooksUser.getUserId());
		
		// compare if both are same or not
		// if zohoBooksUser in database is not equal zohoBooksUser, means something changed in ZohoBooks app
		if(!zohoBooksUserFromDB.equals(zohoBooksUser)) {
			// udpate with new values
			log.debug("ZohoBooksUserService.update - zohoBooksUser from RestAPI have some new values");
			zohoBooksUserFromDB.setRole_id(zohoBooksUser.getRole_id());
			zohoBooksUserFromDB.setName(zohoBooksUser.getName());
			zohoBooksUserFromDB.setEmail(zohoBooksUser.getEmail());
			zohoBooksUserFromDB.setUser_role(zohoBooksUser.getUser_role());
			zohoBooksUserFromDB.setUser_type(zohoBooksUser.getUser_type());
			zohoBooksUserFromDB.setIs_employee(zohoBooksUser.getIs_employee());
			
			// put updated zohoBooksUser to database again
			updateZohoBooksUser(zohoBooksUserFromDB);
		}
		
		// if they are equal do nothing
		
		
	}

}
