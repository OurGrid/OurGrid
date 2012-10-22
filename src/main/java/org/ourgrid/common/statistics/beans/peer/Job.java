package org.ourgrid.common.statistics.beans.peer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Index;
import org.ourgrid.common.statistics.beans.status.ExecutionStatus;
import org.ourgrid.common.util.StringUtil;


@Entity
@Table(name = "job")
public class Job implements Serializable {
	
	private static final long serialVersionUID = 1564269858128434960L;

	private Integer id;

	private Long requestId;

	private Integer maxFails;

	private Integer maxReplicas;

	private ExecutionStatus status;

	private Long jobId;

	private Long creationTime;

	private Long finishTime;
	
	private Long lastModified;

	private String label;

	private String requirements;
	
	private Login login;
	
	private List<Task> tasks;
	
	public Job(){
		setTasks(new ArrayList<Task>());
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Integer getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * @return the requestId
	 */
	@Index(name="requestid")
	public Long getRequestId() {
		return requestId;
	}

	/**
	 * @param requestId
	 *            the requestId to set
	 */
	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return the creationTime
	 */
	public Long getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime
	 *            the creationTime to set
	 */
	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * @return the maxFails
	 */
	public Integer getMaxFails() {
		return maxFails;
	}

	/**
	 * @param maxFails
	 *            the maxFails to set
	 */
	public void setMaxFails(Integer maxFails) {
		this.maxFails = maxFails;
	}

	/**
	 * @return the maxReplicas
	 */
	public Integer getMaxReplicas() {
		return maxReplicas;
	}

	/**
	 * @param maxReplicas
	 *            the maxReplicas to set
	 */
	public void setMaxReplicas(Integer maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	/**
	 * @return the jobStatus
	 */
	@Enumerated(EnumType.STRING)
	public ExecutionStatus getStatus() {
		return status;
	}

	/**
	 * @param jobStatus
	 *            the jobStatus to set
	 */
	public void setStatus(ExecutionStatus jobStatus) {
		this.status = jobStatus;
	}

	/**
	 * @return the jobId
	 */
	public Long getJobId() {
		return jobId;
	}

	/**
	 * @param jobId
	 *            the jobId to set
	 */
	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	/**
	 * @return the label
	 */
	@Column(length = 45)
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return the requirements
	 */
	@Column(name="requirements", length=StringUtil.VARCHAR_MAX_LENGTH) 
	public String getRequirements() {
		return requirements;
	}

	/**
	 * @param requiriments
	 *            the requiriments to set
	 */
	public void setRequirements(String requiriments) {
		this.requirements = StringUtil.shrink(requiriments);
	}

	/**
	 * @return the tasks
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch = FetchType.LAZY, mappedBy = "job")
	public List<Task> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks
	 *            the tasks to set
	 */
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the finishTime
	 */
	public Long getFinishTime() {
		return finishTime;
	}

	/**
	 * @param finishTime the finishTime to set
	 */
	public void setFinishTime(Long finishTime) {
		this.finishTime = finishTime;
	}

	/**
	 * @return the lastModified
	 */
	public Long getLastModified() {
		return lastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Long lastModified) {
		this.lastModified = lastModified;
	}

	/**
	 * @return the login
	 */
	@ManyToOne
	public Login getLogin() {
		return login;
	}

	/**
	 * @param login the login to set
	 */
	public void setLogin(Login login) {
		this.login = login;
	}

}
