package org.ourgrid.common.statistics.beans.aggregator;

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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.ourgrid.common.statistics.beans.status.ExecutionStatus;


@Entity
@Table(name = "execution")
public class AG_GridProcess implements Serializable {

	private static final long serialVersionUID = 7669549910842812140L;

	private Integer id;

	private Integer sequenceNumber;
	
	private ExecutionStatus status;

	private Long creationTime;

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

	private AG_Worker worker;

	//redundancy
	private String workerAddress;
	
	private AG_Task task;
	
	private List<AG_Command> commands;
	
	public AG_GridProcess(){
		setCommands(new ArrayList<AG_Command>());
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
	@Column(length=20)
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
	@Lob
	public String getStdout() {
		return stdout;
	}

	/**
	 * @param stdout
	 *            the stdout to set
	 */
	public void setStdout(String stdout) {
		this.stdout = stdout;
	}

	/**
	 * @return the stderr
	 */
	@Lob
	public String getStderr() {
		return stderr;
	}

	/**
	 * @param stderr
	 *            the stderr to set
	 */
	public void setStderr(String stderr) {
		this.stderr = stderr;
	}

	/**
	 * @return the executionErrorType
	 */
	@Column(length=45)
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
	@Lob
	public String getErrorCause() {
		return errorCause;
	}

	/**
	 * @param errorCause
	 *            the errorCause to set
	 */
	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}

	/**
	 * @return the task
	 */
	@ManyToOne
	public AG_Task getTask() {
		return task;
	}

	/**
	 * @param task
	 *            the task to set
	 */
	public void setTask(AG_Task task) {
		this.task = task;
	}

	/**
	 * @return the worker
	 */
	@ManyToOne(optional=true)
	@JoinColumn(name="worker_id")
	public AG_Worker getWorker() {
		return worker;
	}

	/**
	 * @param worker
	 *            the worker to set
	 */
	public void setWorker(AG_Worker worker) {
		this.worker = worker;
	}

	/**
	 * @return the commands
	 */
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.LAZY, mappedBy="process")
	public List<AG_Command> getCommands() {
		return commands;
	}

	/**
	 * @param commands the commands to set
	 */
	public void setCommands(List<AG_Command> commands) {
		this.commands = commands;
	}
	
	
	/**
	 * @return the sabotageCheck
	 */
	@Column(length=500)
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

	public void setSequenceNumber(Integer sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public Integer getSequenceNumber() {
		return sequenceNumber;
	}

}
