package org.ourgrid.broker.response;

import java.util.List;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.broker.response.to.GetOperationTO;
import org.ourgrid.broker.response.to.InitOperationTO;
import org.ourgrid.broker.response.to.PeerBalanceTO;
import org.ourgrid.broker.response.to.TransferProgressTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;

public class ReportReplicaAccountingResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = BrokerResponseConstants.REPORT_REPLICA_ACCOUNTING;
	
	private String peerAddress;
	private long requestID;
	private int jobID;
	private int requiredWorkers;
	private int maxFails;
	private int maxReplicas;
	private String workerID;
	private long creationTime;
	private String latestPhase;
	private String sabotageCheck;
	private int taskSequenceNumber;
	private String state;
	private WorkerSpecification workerSpec;
	private String workerPK;
	private long initBeginning;
	private long initEnd;
	private long remoteBeginning;
	private long remoteEnd;
	private long finalBeginning;
	private long finalEnd;
	private int exitValue;
	private String errorCause;
	private String executionErrorType;
	private String stderr;
	private String stdout;
	private int gridProcessSequenceNumber;
	
	
	private List<InitOperationTO> initOperationsList;
	private List<GetOperationTO> getOperationsList;
	private List<PeerBalanceTO> peerBalancesList;
	private List<TransferProgressTO> transferProgressList;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public long getRequestID() {
		return requestID;
	}

	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}

	public int getJobID() {
		return jobID;
	}

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public int getRequiredWorkers() {
		return requiredWorkers;
	}

	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}

	public int getMaxFails() {
		return maxFails;
	}

	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}

	public int getMaxReplicas() {
		return maxReplicas;
	}

	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	public String getWorkerID() {
		return workerID;
	}

	public void setWorkerID(String workerID) {
		this.workerID = workerID;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public String getLatestPhase() {
		return latestPhase;
	}

	public void setLatestPhase(String latestPhase) {
		this.latestPhase = latestPhase;
	}

	public String getSabotageCheck() {
		return sabotageCheck;
	}

	public void setSabotageCheck(String sabotageCheck) {
		this.sabotageCheck = sabotageCheck;
	}

	public int getTaskSequenceNumber() {
		return taskSequenceNumber;
	}

	public void setTaskSequenceNumber(int taskSequenceNumber) {
		this.taskSequenceNumber = taskSequenceNumber;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}

	public void setWorkerSpec(WorkerSpecification workerSpec) {
		this.workerSpec = workerSpec;
	}

	public String getWorkerPK() {
		return workerPK;
	}

	public void setWorkerPK(String workerPK) {
		this.workerPK = workerPK;
	}


	public long getInitBeginning() {
		return initBeginning;
	}


	public void setInitBeginning(long initBeginning) {
		this.initBeginning = initBeginning;
	}


	public long getInitEnd() {
		return initEnd;
	}


	public void setInitEnd(long initEnd) {
		this.initEnd = initEnd;
	}


	public long getRemoteBeginning() {
		return remoteBeginning;
	}


	public void setRemoteBeginning(long remoteBeginning) {
		this.remoteBeginning = remoteBeginning;
	}


	public long getRemoteEnd() {
		return remoteEnd;
	}


	public void setRemoteEnd(long remoteEnd) {
		this.remoteEnd = remoteEnd;
	}


	public long getFinalBeginning() {
		return finalBeginning;
	}


	public void setFinalBeginning(long finalBeginning) {
		this.finalBeginning = finalBeginning;
	}


	public long getFinalEnd() {
		return finalEnd;
	}


	public void setFinalEnd(long finalEnd) {
		this.finalEnd = finalEnd;
	}


	public void setInitOperationsList(List<InitOperationTO> initOperationsList) {
		this.initOperationsList = initOperationsList;
	}


	public List<InitOperationTO> getInitOperationsList() {
		return initOperationsList;
	}


	public void setGetOperationsList(List<GetOperationTO> getOperationsList) {
		this.getOperationsList = getOperationsList;
	}


	public List<GetOperationTO> getGetOperationsList() {
		return getOperationsList;
	}


	public int getExitValue() {
		return exitValue;
	}


	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}


	public String getErrorCause() {
		return errorCause;
	}


	public void setErrorCause(String errorCause) {
		this.errorCause = errorCause;
	}


	public String getExecutionErrorType() {
		return executionErrorType;
	}


	public void setExecutionErrorType(String executionErrorType) {
		this.executionErrorType = executionErrorType;
	}


	public String getStderr() {
		return stderr;
	}


	public void setStderr(String stderr) {
		this.stderr = stderr;
	}


	public String getStdout() {
		return stdout;
	}


	public void setStdout(String stdout) {
		this.stdout = stdout;
	}


	public void setPeerBalancesList(List<PeerBalanceTO> peerBalancesList) {
		this.peerBalancesList = peerBalancesList;
	}


	public List<PeerBalanceTO> getPeerBalancesList() {
		return peerBalancesList;
	}


	public void setTransferProgressList(List<TransferProgressTO> transferProgressList) {
		this.transferProgressList = transferProgressList;
	}


	public List<TransferProgressTO> getTransferProgressList() {
		return transferProgressList;
	}


	public void setGridProcessSequenceNumber(int gridProcessSequenceNumber) {
		this.gridProcessSequenceNumber = gridProcessSequenceNumber;
	}


	public int getGridProcessSequenceNumber() {
		return gridProcessSequenceNumber;
	}

}
