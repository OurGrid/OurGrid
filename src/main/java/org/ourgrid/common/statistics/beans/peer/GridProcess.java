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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.ourgrid.common.statistics.beans.status.ExecutionStatus;
import org.ourgrid.common.util.StringUtil;


@Entity
@Table(name = "execution")
public class GridProcess implements Serializable {
	
	private static final long serialVersionUID = 7669549910842812140L;

	private Integer id;

	private ExecutionStatus status;

	private Long creationTime;

	private int sequenceNumber;
	
	private String latestPhase;

	private Long initBeginning;

	private Long initEnd;

	private Long remoteBeginning;

	private Long remoteEnd;

	private Long finalBeginning;

	private Long finalEnd;
	
	private Long lastModified;

	private Integer exitValue;

	private String stdout;

	private String stderr;

	private String executionErrorType;

	private String errorCause;

	private String sabotageCheck;

	private Worker worker;

	private String workerAddress;
	
	private Task task;
	
	private List<Command> commands;
	
	private Double cpuConsumed;
	
	private Double dataConsumed;
	
	private String providerDN;
	
	public GridProcess(){
		setCommands(new ArrayList<Command>());
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
	 * @return the status
	 */
	@Column(name="status")
	@Enumerated(EnumType.STRING)
	public ExecutionStatus getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(ExecutionStatus status) {
		this.status = status;
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
	 * @return the latestPhase
	 */
	@Column(length=50)
	public String getLatestPhase() {
		return latestPhase;
	}

	/**
	 * @param latestPhase
	 *            the latestPhase to set
	 */
	public void setLatestPhase(String latestPhase) {
		this.latestPhase = latestPhase;
	}

	/**
	 * @return the initBeginning
	 */
	public Long getInitBeginning() {
		return initBeginning;
	}

	/**
	 * @param initBeginning
	 *            the initBeginning to set
	 */
	public void setInitBeginning(Long initBeginning) {
		this.initBeginning = initBeginning;
	}

	/**
	 * @return the initEnd
	 */
	public Long getInitEnd() {
		return initEnd;
	}

	/**
	 * @param initEnd
	 *            the initEnd to set
	 */
	public void setInitEnd(Long initEnd) {
		this.initEnd = initEnd;
	}

	/**
	 * @return the remoteBeginning
	 */
	public Long getRemoteBeginning() {
		return remoteBeginning;
	}

	/**
	 * @param remoteBeginning
	 *            the remoteBeginning to set
	 */
	public void setRemoteBeginning(Long remoteBeginning) {
		this.remoteBeginning = remoteBeginning;
	}

	/**
	 * @return the remoteEnd
	 */
	public Long getRemoteEnd() {
		return remoteEnd;
	}

	/**
	 * @param remoteEnd
	 *            the remoteEnd to set
	 */
	public void setRemoteEnd(Long remoteEnd) {
		this.remoteEnd = remoteEnd;
	}

	/**
	 * @return the finalBeginning
	 */
	public Long getFinalBeginning() {
		return finalBeginning;
	}

	/**
	 * @param finalBeginning
	 *            the finalBeginning to set
	 */
	public void setFinalBeginning(Long finalBeginning) {
		this.finalBeginning = finalBeginning;
	}

	/**
	 * @return the finalEnd
	 */
	public Long getFinalEnd() {
		return finalEnd;
	}

	/**
	 * @param finalEnd
	 *            the finalEnd to set
	 */
	public void setFinalEnd(Long finalEnd) {
		this.finalEnd = finalEnd;
	}

	/**
	 * @return the exitValue
	 */
	public Integer getExitValue() {
		return exitValue;
	}

	/**
	 * @param exitValue
	 *            the exitValue to set
	 */
	public void setExitValue(Integer exitValue) {
		this.exitValue = exitValue;
	}

	/**
	 * @return the stdout
	 */
	@Column(length=StringUtil.VARCHAR_MAX_LENGTH)
	public String getStdout() {
		return stdout;
	}

	/**
	 * @param stdout
	 *            the stdout to set
	 */
	public void setStdout(String stdout) {
		this.stdout = StringUtil.shrink(stdout);
	}

	/**
	 * @return the stderr
	 */
	@Column(length=StringUtil.VARCHAR_MAX_LENGTH)
	public String getStderr() {
		return stderr;
	}

	/**
	 * @param stderr
	 *            the stderr to set
	 */
	public void setStderr(String stderr) {
		this.stderr = StringUtil.shrink(stderr);
	}

	/**
	 * @return the executionErrorType
	 */
	@Column(length=100)
	public String getExecutionErrorType() {
		return executionErrorType;
	}

	/**
	 * @param executionErrorType
	 *            the executionErrorType to set
	 */
	public void setExecutionErrorType(String executionErrorType) {
		this.executionErrorType = executionErrorType;
	}

	/**
	 * @return the errorCause
	 */
	@Column(length=StringUtil.VARCHAR_MAX_LENGTH)
	public String getErrorCause() {
		return errorCause;
	}

	/**
	 * @param errorCause
	 *            the errorCause to set
	 */
	public void setErrorCause(String errorCause) {
		this.errorCause = StringUtil.shrink(errorCause);
	}

	/**
	 * @return the task
	 */
	@ManyToOne
	public Task getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(Task task) {
		this.task = task;
	}

	/**
	 * @return the worker
	 */
	@ManyToOne
	@JoinColumn(name="worker_id")
	public Worker getWorker() {
		return worker;
	}

	/**
	 * @param worker
	 *            the worker to set
	 */
	public void setWorker(Worker worker) {
		this.worker = worker;
	}

	/**
	 * @return the commands
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="process")
	public List<Command> getCommands() {
		return commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<Command> commands) {
		this.commands = commands;
	}
	
	
	/**
	 * @return the sabotageCheck
	 */
	@Column(length=StringUtil.VARCHAR_MAX_LENGTH)
	public String getSabotageCheck() {
		return sabotageCheck;
	}

	/**
	 * @param sabotageCheck
	 *            the sabotageCheck to set
	 */
	public void setSabotageCheck(String sabotageCheck) {
		this.sabotageCheck = StringUtil.shrink(sabotageCheck);
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

	public void setWorkerAddress(String workerAddress) {
		this.workerAddress = workerAddress;
	}

	public String getWorkerAddress() {
		return workerAddress;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setCpuConsumed(Double cpuConsumed) {
		this.cpuConsumed = cpuConsumed;
	}

	public Double getCpuConsumed() {
		return cpuConsumed;
	}

	public void setDataConsumed(Double dataConsumed) {
		this.dataConsumed = dataConsumed;
	}

	public Double getDataConsumed() {
		return dataConsumed;
	}

	public void setProviderDN(String providerDN) {
		this.providerDN = providerDN;
	}

	public String getProviderDN() {
		return providerDN;
	}

}
