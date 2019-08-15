package de.bamero.tempoZohoMiddleware.entities;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import de.bamero.tempoZohoMiddleware.utils.LambdaEquals;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
public class ZohoBooksProject {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long _zohoBooksProjectId;
	
	private String projectId;
	private String projectName;
	private String customerId;
	private String customerName;
	private String description;
	private String status;
	private String billingType;
	private Long rate;
	private Date createdTime;
	private String total_hours;
	private String billed_hours;
	private String unbilled_hours;
	private String billable_hours;
	private String non_billable_hours;
	private String jiraKey;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private List<String> jiraProjectIds;
	
	@OneToMany(mappedBy="zohoBooksProject", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@Fetch(value=FetchMode.SUBSELECT)
	private List<ZohoBooksTask> zohoBooksTasks;
	
	@ManyToMany(cascade = CascadeType.MERGE)
	@JoinTable(joinColumns=@JoinColumn(name="projectId"), inverseJoinColumns=@JoinColumn(name="userId"))
	private Set<ZohoBooksUser> zohoBooksUsers;
	
	@Override
	public boolean equals(Object obj) {
		return LambdaEquals.equals(this, obj, ZohoBooksProject::getProjectName,
											  ZohoBooksProject::getCustomerId,
											  ZohoBooksProject::getCustomerName,
											  ZohoBooksProject::getDescription,
											  ZohoBooksProject::getStatus,
											  ZohoBooksProject::getBillingType,
											  ZohoBooksProject::getRate,
											  ZohoBooksProject::getJiraKey);
	}
	

}
