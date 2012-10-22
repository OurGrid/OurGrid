package org.ourgrid.broker.communication.sender;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;
import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.broker.response.ReportReplicaAccountingResponseTO;
import org.ourgrid.broker.response.to.OperationTO;
import org.ourgrid.broker.response.to.PeerBalanceTO;
import org.ourgrid.broker.response.to.TransferProgressTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.GridProcessPhasesData;
import org.ourgrid.common.interfaces.to.GridProcessResultInfo;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.TransferTime;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.CommonUtils;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

public class ReportReplicaAccountingSender implements SenderIF<ReportReplicaAccountingResponseTO> {

	public void execute(ReportReplicaAccountingResponseTO response, ServiceManager manager) {
		GridProcessAccounting acc = getGridProcessAccounting(response, manager);
		
		if (response.getPeerAddress() != null) {
			ServiceID serviceID = ServiceID.parse(response.getPeerAddress());
			LocalWorkerProvider localWorkerProvider = (LocalWorkerProvider) manager.getStub(serviceID, LocalWorkerProvider.class);
			localWorkerProvider.reportReplicaAccounting(acc);
		}
	}

	private GridProcessAccounting getGridProcessAccounting(
			ReportReplicaAccountingResponseTO response, ServiceManager manager) {
		
		long requestID = response.getRequestID();
		int jobID = response.getJobID();
		int requiredWorkers = response.getRequiredWorkers();
		int maxFails = response.getMaxFails();
		int maxReplicas = response.getMaxReplicas();
		String workerID = response.getWorkerID();
		
		WorkerSpecification workerSpec = response.getWorkerSpec();
		String workerPK = response.getWorkerPK();
		
		GridProcessAccounting acc = new GridProcessAccounting(requestID, jobID, requiredWorkers, maxFails,
				maxReplicas, workerID, workerPK, workerSpec);
		
		long creationTime = response.getCreationTime();
		String latestPhase = response.getLatestPhase();
		String sabotageCheck = response.getSabotageCheck();
		int taskSequenceNumber = response.getTaskSequenceNumber();
		int processSequenceNumber = response.getGridProcessSequenceNumber();
		String state = response.getState();
		
		acc.setCreationTime(creationTime);
		acc.setLatestPhase(latestPhase);
		acc.setSabotageCheck(sabotageCheck);
		acc.setTaskSequenceNumber(taskSequenceNumber);
		acc.setGridProcessSequenceNumber(processSequenceNumber);
		acc.setState(getState(state));
		
		
		//Phases data
		acc.setPhasesData(getPhasesData(response));
		
		//Result Info
		acc.setResultInfo(getResultInfo(response));
		
		//Peer Balance
		fillPeerBalance(response, acc);
		
		//Transfer Progress
		acc.setTransfersProgress(getTransferProgressMap(response));
		
		return acc;
	}
	
	private GridProcessState getState(String state) {
		
		GridProcessState value = null;
		
		if (state.equals("FAILED")) {
			value = GridProcessState.FAILED;
		} else if (state.equals("FINISHED")) {
			value = GridProcessState.FINISHED;
		} else if (state.equals("ABORTED")) {
			value = GridProcessState.ABORTED;
		} else if (state.equals("CANCELLED")) {
			value = GridProcessState.CANCELLED;
		} else if (state.equals("UNSTARTED")) {
			value = GridProcessState.UNSTARTED;
		} else if (state.equals("RUNNING")) {
			value = GridProcessState.RUNNING;
		} else if (state.equals("SABOTAGED")) {
			value = GridProcessState.SABOTAGED;
		}
		
		return value;
	}
	
	private GridProcessPhasesData getPhasesData(ReportReplicaAccountingResponseTO response) {
		
		long initBeginning = response.getInitBeginning();
		long initEnd = response.getInitEnd();
		long remoteBeginning = response.getRemoteBeginning();
		long remoteEnd = response.getRemoteEnd();
		long finalBeginning = response.getFinalBeginning();
		long finalEnd = response.getFinalEnd();
		
		GridProcessPhasesData pData = new GridProcessPhasesData(initBeginning, initEnd, remoteBeginning,
				remoteEnd, finalBeginning, finalEnd);
		
		Map<InitOperation, TransferTime> initTransferProgress = getInitOperations(response);
		Map<GetOperation, TransferTime> getTransferProgress = getGetOperations(response);
		
		pData.setGetOperations(getTransferProgress);
		pData.setInitOperations(initTransferProgress);
		
		return pData;
	}
	
	private Map<InitOperation, TransferTime> getInitOperations(ReportReplicaAccountingResponseTO response) {
		
		Map<InitOperation, TransferTime> transferProgress = CommonUtils.createSerializableMap();
		fillOperationMap(response, transferProgress, null, false);
		
		return transferProgress;
	}
	
	private Map<GetOperation, TransferTime> getGetOperations(ReportReplicaAccountingResponseTO response) {
		
		Map<GetOperation, TransferTime> transferProgress = CommonUtils.createSerializableMap();
		fillOperationMap(response, null, transferProgress, true);
		
		return transferProgress;
	}
	
	private void fillOperationMap(ReportReplicaAccountingResponseTO response, Map<InitOperation, TransferTime> initTransferProgress,
			Map<GetOperation, TransferTime> getTransferProgress, boolean get) {
		
		
		List<OperationTO> operationsList = new ArrayList<OperationTO>();
		
		if (get) {
			operationsList.addAll(response.getGetOperationsList());
		} else {
			operationsList.addAll(response.getInitOperationsList());
		}
		
		for (OperationTO operation : operationsList) {
			int jobID = operation.getJobID();
			int taskID = operation.getTaskID();
			int processID = operation.getProcessID();
			long requestID2 = operation.getRequestID2();
			String workerID2 = operation.getWorkerID2();
			String localFilePath = operation.getLocalFilePath();
			String remoteFilePath = operation.getRemoteFilePath();
			String transferDescription = operation.getTransferDescription();
			long initTime = operation.getInitTime();
			long endTime = operation.getEndTime();
			
			TransferTime transferTime = new TransferTime();
			transferTime.setInitTime(initTime);
			transferTime.setEndTime(endTime);
			
			if (get) {
				GetOperation getOp = new GetOperation(new GridProcessHandle(jobID, taskID, processID), requestID2, workerID2, 
						localFilePath, remoteFilePath, transferDescription, null);
				((IncomingHandle)getOp.getHandle()).setFileSize(operation.getFileSize());
				getTransferProgress.put(getOp, transferTime);
				
			} else {
				InitOperation initOp = new InitOperation(new GridProcessHandle(jobID, taskID, processID), requestID2, workerID2, 
						localFilePath, remoteFilePath, transferDescription, null);
				initTransferProgress.put(initOp, transferTime);
			}
		}
	}
	
	private GridProcessResultInfo getResultInfo(ReportReplicaAccountingResponseTO response) {
		
		int exitValue = response.getExitValue();
		String errorCause = response.getErrorCause();
		String executionErrorType = response.getExecutionErrorType();
		String stderr = response.getStderr();
		String stdout = response.getStdout();
		
		GridProcessResultInfo resultInfo = new GridProcessResultInfo(exitValue, errorCause, executionErrorType,
				stderr, stdout);
		
		return resultInfo; 
	}
	
	private void fillPeerBalance(ReportReplicaAccountingResponseTO response, GridProcessAccounting acc) {
		for (PeerBalanceTO peerBalanceTO : response.getPeerBalancesList()) {
			
			String property = peerBalanceTO.getProperty();
			double value = peerBalanceTO.getValue();
			
			acc.setAccounting(property, value);
		}
	}
	
	private Map<TransferHandle, TransferProgress> getTransferProgressMap(ReportReplicaAccountingResponseTO response) {
		Map<TransferHandle, TransferProgress> tpMap = CommonUtils.createSerializableMap();

		for (TransferProgressTO transferProgressTO : response.getTransferProgressList()) {
			
			long handleID = transferProgressTO.getHandleID();
			String localFileName = transferProgressTO.getLocalFileName();
			long fileSize = transferProgressTO.getFileSize();
			String description = transferProgressTO.getDescription();
			String id = transferProgressTO.getId();
			String newStatus = transferProgressTO.getNewStatus();
			long amountWritten = transferProgressTO.getAmountWritten();
			double progress = transferProgressTO.getProgress();
			double transferRate = transferProgressTO.getTransferRate();
			boolean outgoing = transferProgressTO.isOutgoing();
			
			TransferHandle transferHandle = null;
			
			if (outgoing) {
				transferHandle = new OutgoingTransferHandle(handleID, localFileName, new File(localFileName), 
						description, new DeploymentID(id)); 
			} else {
				transferHandle = new IncomingTransferHandle(handleID, localFileName, 
						description, fileSize, ContainerID.parse(id));
			}
			
			TransferProgress transferProgress = new TransferProgress(transferHandle, localFileName, fileSize, getStatus(newStatus),
					amountWritten, progress, transferRate, outgoing);
			
			tpMap.put(transferHandle, transferProgress);
		}
		
		return tpMap;
	}
	
	private Status getStatus(String status) {
		
		Status value = null;
		
		if (status.startsWith("cancelled")) {
			value = Status.cancelled;
		} else if (status.startsWith("complete")) {
			value = Status.complete;
		} else if (status.startsWith("error")) {
			value = Status.error;
		} else if (status.startsWith("in_progress")) {
			value = Status.in_progress;
		} else if (status.startsWith("initial")) {
			value = Status.initial;
		} else if (status.startsWith("negotiated")) {
			value = Status.negotiated;
		} else if (status.startsWith("negotiating_stream")) {
			value = Status.negotiating_stream;
		} else if (status.startsWith("negotiating_transfer")) {
			value = Status.negotiating_transfer;
		} else if (status.startsWith("refused")) {
			value = Status.refused;
		}
		
		return value;
	}
}
