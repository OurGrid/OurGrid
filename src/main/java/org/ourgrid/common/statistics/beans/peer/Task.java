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


@Entity
@Table(name = "task")
public class Task implements Serializable {

	private static final long serialVersionUID = 4216409628547429801L;

	private Integer id;

	private Integer actualFails;
	
	private Integer sequenceNumber;

	private ExecutionStatus status;

	private String remoteExec;

	private String sabotageCheck;
	
	private Long lastModified;

	private Job job;
	
	private List<GridProcess> processes;
	
	public Task(){
		setProcesses(new ArrayList<GridProcess>());
	}
	
	/**
	 * @return the id
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
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
	 * @return the actualFails
	 */
	public Integer getActualFails() {
		return actualFails;
	}

	/**
	 * @param actualFails
	 *            the actualFails to set
	 */
	public void setActualFails(Integer actualFails) {
		this.actualFails = actualFails;
	}

	/**
	 * @return the taskStatus
	 */
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	public ExecutionStatus getStatus() {
		return status;
	}

	/**
	 * @param taskStatus
	 *            the taskStatus to set
	 */
	public void setStatus(ExecutionStatus taskStatus) {
		this.status = taskStatus;
	}

	/**
	 * @return the remoteExec
	 */
	@Column(length=500)
	public String getRemoteExec() {
		return remoteExec;
	}

	/**
	 * @param remoteExec
	 *            the remoteExec to set
	 */
	public void setRemoteExec(String remoteExec) {
		this.remoteExec = remoteExec;
	}

	/**
	 * @return the sabotageCheck
	 */
	@Column(length=255)
	public String getSabotageCheck() {
		return sabotageCheck;
	}

	/**
	 * @param sabotageCheck
	 *            the sabotageCheck to set
	 */
	public void setSabotageCheck(String sabotageCheck) {
		this.sabotageCheck = sabotageCheck;
	}

	/**
	 * @return the job
	 */
	@ManyToOne
	public Job getJob() {
		return job;
	}

	/**
	 * @param job
	 *            the job to set
	 */
	public void setJob(Job job) {
		this.job = job;
	}

	/**
	 * @return the processes
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="task")
	public List<GridProcess> getProcesses() {
		return processes;
	}

	/**
	 * @param executions the processes to set
	 */
	public void setProcesses(List<GridProcess> executions) {
		this.processes = executions;
	}

	/**
	 * @return the serialVersionUID
	 */
	public static long getSerialVersionUID() {
		return serialVersionUID;
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
	 * @return the sequenceNumber
	 */
	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

	/**
	 * @param sequenceNumber the sequenceNumber to set
	 */
	@Index(name="sequencenumber")
	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}
	
}
