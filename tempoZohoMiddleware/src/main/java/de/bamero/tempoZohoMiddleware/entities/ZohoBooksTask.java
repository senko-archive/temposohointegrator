package de.bamero.tempoZohoMiddleware.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import de.bamero.tempoZohoMiddleware.utils.LambdaEquals;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class ZohoBooksTask {
	
	@Id
	private String zohoBooksTaskId;
	
	private String projectId;
	private String currencyId;
	private String customerId;
	private String taskName;
	private String description;
	private String projectName;
	private String customerName;
	
	// total billed hours of time-entries of this task
	private String billedHours;
	
	// total log time of time-entries of this task
	private String logTime;
	
	// total unbilled hours of time-entries of this task
	private String unbilledHours;
	
	private String rate;
	private String status;
	private Boolean isBillable;
	
	@ManyToOne
	@JoinColumn
	private ZohoBooksProject zohoBooksProject;
	
	// oneToOne mapping between jiraTask and zohoBooksTask, but in future maybe we can return this JPA oneToOne
	private String jiraTaskId = "not assigned";
	private String jiraTaskName = "not assigned";
	
	// for zohobooks time entry
	@OneToMany(mappedBy="zohoBooksTask", cascade= {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE}, fetch=FetchType.LAZY)
	private Set<ZohoBooksTimeEntry> zohoBooksTimeEntries;
	
	public void addTask(ZohoBooksProject zohoBooksProject) {
		this.zohoBooksProject = zohoBooksProject;
		zohoBooksProject.getZohoBooksTasks().add(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return LambdaEquals.equals(this, obj, ZohoBooksTask::getProjectId,
											  ZohoBooksTask::getCurrencyId,
											  ZohoBooksTask::getCustomerId,
											  ZohoBooksTask::getTaskName,
											  ZohoBooksTask::getDescription,
											  ZohoBooksTask::getProjectName,
											  ZohoBooksTask::getCustomerName,
											  ZohoBooksTask::getRate,
											  ZohoBooksTask::getStatus,
											  ZohoBooksTask::getIsBillable);
	}
	
}
