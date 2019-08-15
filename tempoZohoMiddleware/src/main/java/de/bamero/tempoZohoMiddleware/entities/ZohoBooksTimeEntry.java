package de.bamero.tempoZohoMiddleware.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import de.bamero.tempoZohoMiddleware.utils.LambdaEquals;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ZohoBooksTimeEntry {
	
	@Id
	private String timeEntryId;
	
	private String projectId;
	private String projectName;
	private String taskId;
	private String taskName;
	private String userId;
	private String userName;
	private String customerId;
	private String customerName;
	
	//private Boolean canBeInvoiced;
	private Boolean isBillable;
	
	private String billedStatus;
	
	private String invoiceId;
	private String invoiceNumber;
	
	private LocalDate logDate;
	private LocalTime logTime;
	
	private LocalDateTime createdTime;
	private LocalDateTime updatedTime;
	
	private String notes;
	
	private String jiraWorkLogId;
	
	
	@ManyToOne
	@JoinColumn
	private ZohoBooksTask zohoBooksTask;
	
	public void addZohoBooksTask(ZohoBooksTask zohoBooksTask) {
		zohoBooksTask.getZohoBooksTimeEntries().add(this);
		this.zohoBooksTask = zohoBooksTask;
	}
	
	@Override
	public boolean equals(Object obj) {
		return LambdaEquals.equals(this, obj, ZohoBooksTimeEntry::getProjectId,
				ZohoBooksTimeEntry::getProjectName,
				ZohoBooksTimeEntry::getTaskId,
				ZohoBooksTimeEntry::getTaskName,
				ZohoBooksTimeEntry::getUserId,
				//ZohoBooksTimeEntry::getCanBeInvoiced,
				ZohoBooksTimeEntry::getIsBillable,
				ZohoBooksTimeEntry::getInvoiceId,
				ZohoBooksTimeEntry::getInvoiceNumber,
				ZohoBooksTimeEntry::getNotes);
	}
	
	

}
