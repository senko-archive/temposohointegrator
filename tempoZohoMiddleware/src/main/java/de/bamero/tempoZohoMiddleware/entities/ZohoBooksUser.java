package de.bamero.tempoZohoMiddleware.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import de.bamero.tempoZohoMiddleware.utils.LambdaEquals;
import lombok.Data;
import lombok.ToString;
import lombok.Setter;
import lombok.Getter;

@Entity
@Getter
@Setter
public class ZohoBooksUser {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _zohoBooksUserId;
	
	private String userId;
	private String role_id;
	private String name;
	private String email;
	private String user_role;
	private String user_type;
	private Boolean is_employee;
	
	@ElementCollection
	private List<String> jiraUserIds = new ArrayList<>();
	
	@Override
	public boolean equals(Object obj) {
		return LambdaEquals.equals(this, obj, ZohoBooksUser::getRole_id,
											  ZohoBooksUser::getName,
											  ZohoBooksUser::getEmail,
											  ZohoBooksUser::getUser_role,
											  ZohoBooksUser::getUser_type,
											  ZohoBooksUser::getIs_employee);
	}

}
