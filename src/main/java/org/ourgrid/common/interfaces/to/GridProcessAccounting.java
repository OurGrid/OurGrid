/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.common.interfaces.to;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.peer.to.PeerBalance;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;


@Req("REQ027")
public class GridProcessAccounting extends Accounting {
	
	private static final long serialVersionUID = 1L;
	private final String workerID;
	private GridProcessState state;
	private Long initCPUTime; 
	
	private long requestId;
	private long jobId;
	private int requiredWorkers;
	private int maxFails;
	private int maxReplicas;
	private int taskSequenceNumber;
	private int gridProcessSequenceNumber;
	
	private Long creationTime;
	private String latestPhase;
	private String sabotageCheck;
	private GridProcessPhasesData phasesData;
	private GridProcessResultInfo resultInfo;
	private WorkerSpecification workerSpec;
	private Map<TransferHandle, TransferProgress> transfersProgress;
	private String workerPublicKey;

	public GridProcessAccounting(long requestId, long jobId, int requiredWorkers,
			int maxFails, int maxReplicas, String workerID, String workerPK, WorkerSpecification workerSpec) {
		
		this.requestId = requestId;
		this.jobId = jobId;
		this.maxFails = maxFails;
		this.maxReplicas = maxReplicas;
		this.requiredWorkers = requiredWorkers;
		
		this.workerID = workerID;
		this.phasesData = new GridProcessPhasesData();
		this.resultInfo = new GridProcessResultInfo();
		this.workerSpec = workerSpec;
		this.taskSequenceNumber = 1;
		this.workerPublicKey = workerPK;
	}

	@Deprecated
	public GridProcessAccounting(RequestSpecification requestSpec, String workerID,  String workerPK, 
			double cpuTime, double data, GridProcessState state, WorkerSpecification workerSpec) {
		this(requestSpec.getRequestId(), requestSpec.getJobId(), requestSpec.getRequiredWorkers(),
				requestSpec.getMaxFails(), requestSpec.getMaxReplicas(), workerID, workerPK, workerSpec);
		this.state = state;
		setAccounting(PeerBalance.CPU_TIME, cpuTime);
		setAccounting(PeerBalance.DATA, data);
	}

	public void incDataTransfered(long dataTransfered) {
		Double oldData = getAccountings().getAttribute(PeerBalance.DATA);
		oldData = oldData == null ? 0 : oldData;
		
		setAccounting(PeerBalance.DATA, dataTransfered + oldData);
	}
	
	public void startCPUTiming() {
		initCPUTime = System.currentTimeMillis();
	}
	
	public Long getCPUTiming() {
		return this.initCPUTime;
	}
	
	public void stopCPUTiming() {
		setAccounting(PeerBalance.CPU_TIME, System.currentTimeMillis() - (double)initCPUTime);
	}
	
	public String getWorkerID() {
		return workerID;
	}

	public GridProcessState getState() {
		return state;
	}

	public void setState(GridProcessState state) {
		this.state = state;
	}
	
//	public RequestSpec getRequestSpec() {
//		return requestSpec;
//	}
//	

	public int getTaskSequenceNumber() {
		return this.taskSequenceNumber;
	}

	public Long getCreationTime() {
		return this.creationTime;
	}

	public Integer getExitValue() {
		return this.resultInfo.getExitValue();
	}

	public String getErrorCause() {
		return this.resultInfo.getErrorCause();
	}

	public String getExecutionErrorType() {
		return this.resultInfo.getExecutionErrorType();
	}

	public Long getInitBeginning() {
		return this.phasesData.getInitBeginning();
	}

	public Long getInitEnd() {
		return this.phasesData.getInitEnd();
	}

	public Long getRemoteBeginning() {
		return this.phasesData.getRemoteBeginning();
	}

	public Long getRemoteEnd() {
		return this.phasesData.getRemoteEnd();
	}

	public Long getFinalBeginning() {
		return this.phasesData.getFinalBeginning();
	}

	public Long getFinalEnd() {
		return this.phasesData.getFinalEnd();
	}

	public String getLatestPhase() {
		return this.latestPhase;
	}

	public String getSabotageCheck() {
		return this.sabotageCheck;
	}

	public String getStderr() {
		return this.resultInfo.getStderr();
	}

	public String getStdout() {
		return this.resultInfo.getStdout();
	}

	public List<ProcessCommand> getInitCommands() {
		
		List<ProcessCommand> commands = new ArrayList<ProcessCommand>();
		Map<InitOperation, TransferTime> initOperations = phasesData.getInitOperations();
		
		if (initOperations != null) {
			
			OutgoingHandle handle = null;
			OutgoingTransferHandle outgoing = null; 
			ProcessCommand pCommand = null;
			
			for (Map.Entry<InitOperation, TransferTime> entry : initOperations.entrySet()) {
				InitOperation init = entry.getKey();
				TransferTime time = entry.getValue();
				
				handle = (OutgoingHandle) init.getHandle();
				
//				outgoing = new OutgoingTransferHandle(handle.getId(), handle.getLogicalFileName(), 
//						handle.getLocalFile(), handle.getDescription(), 
//						new DeploymentID(handle.getDestinationID()));

				outgoing = new OutgoingTransferHandle(handle.getId(), handle.getLogicalFileName(), 
						handle.getFileSize(), handle.getDescription(), 
						new DeploymentID(handle.getDestinationID()));
				
				String operationName = FileTransferHandlerUtils.getOperationType(init.getHandle().getDescription());
				
				pCommand = new ProcessCommand(init.getLocalFilePath(), init.getRemoteFilePath(), 
						init.getHandle().getLogicalFileName(), time.getInitTime(), time.getEndTime(), operationName,
						outgoing);
				pCommand.setFileSize(handle.getFileSize());
				
				commands.add(pCommand);
			}
		}
		
		return commands;
	}

	public List<ProcessCommand> getFinalCommands() {
		List<ProcessCommand> commands = new ArrayList<ProcessCommand>();
		Map<GetOperation, TransferTime> getOperations = phasesData.getGetOperations();
		
		if (getOperations != null) {
			
			IncomingHandle handle = null;
			ContainerID id = null;
			IncomingTransferHandle incoming = null; 
			ProcessCommand pCommand = null;
							
			for (Map.Entry<GetOperation, TransferTime> entry : getOperations.entrySet()) {
				GetOperation get = entry.getKey();
				TransferTime time = entry.getValue();
				
				handle = (IncomingHandle) get.getHandle();
				id = ContainerID.parse(handle.getSenderContainerID());
				
				incoming = new IncomingTransferHandle(handle.getId(), handle.getLogicalFileName(), 
						handle.getDescription(), handle.getFileSize(), id);
				
				String operationName = FileTransferHandlerUtils.getOperationType(get.getHandle().getDescription());
				
				pCommand = new ProcessCommand(get.getLocalFilePath(), get.getRemoteFilePath(), 
						get.getHandle().getLogicalFileName(), time.getInitTime(), time.getEndTime(), operationName,
						incoming);
				pCommand.setFileSize(handle.getFileSize());
				
				commands.add(pCommand);
			}
		}
		
		return commands;
	}

	public void setPhasesData(GridProcessPhasesData phasesData) {
		this.phasesData = phasesData;
	}

	public void setResultInfo(GridProcessResultInfo resultInfo) {
		this.resultInfo = resultInfo;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public void setLatestPhase(String latestPhase) {
		this.latestPhase = latestPhase;
	}

	public void setSabotageCheck(String sabotageCheck) {
		this.sabotageCheck = sabotageCheck;
	}

	public void setTaskSequenceNumber(int taskSequenceNumber) {
		this.taskSequenceNumber = taskSequenceNumber;
	}
	
	public WorkerSpecification getWorkerSpec() {
		return workerSpec;
	}
	
	public TransferProgress getTransferProgress( TransferHandle handle ) {
		return this.transfersProgress.get( handle );
	}

	public void setTransfersProgress(Map<TransferHandle, TransferProgress> transferProgress) {
		this.transfersProgress = transferProgress;
	}
	
	public GridProcessPhasesData getPhasesData() {
		return phasesData;
	}

	public GridProcessResultInfo getResultInfo() {
		return resultInfo;
	}

	public void setInitCPUTime(Long initCPUTime) {
		this.initCPUTime = initCPUTime;
	}

	public Long getInitCPUTime() {
		return initCPUTime;
	}

	public Map<TransferHandle, TransferProgress> getTransfersProgress() {
		return transfersProgress;
	}
	
	public String getWorkerPublicKey() {
		return workerPublicKey;
	}

	public void setGridProcessSequenceNumber(int gridProcessSequenceNumber) {
		this.gridProcessSequenceNumber = gridProcessSequenceNumber;
	}

	public int getGridProcessSequenceNumber() {
		return gridProcessSequenceNumber;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setJobId(long jobId) {
		this.jobId = jobId;
	}

	public long getJobId() {
		return jobId;
	}

	public void setRequiredWorkers(int requiredWorkers) {
		this.requiredWorkers = requiredWorkers;
	}

	public int getRequiredWorkers() {
		return requiredWorkers;
	}

	public void setMaxFails(int maxFails) {
		this.maxFails = maxFails;
	}

	public int getMaxFails() {
		return maxFails;
	}

	public void setMaxReplicas(int maxReplicas) {
		this.maxReplicas = maxReplicas;
	}

	public int getMaxReplicas() {
		return maxReplicas;
	}
}