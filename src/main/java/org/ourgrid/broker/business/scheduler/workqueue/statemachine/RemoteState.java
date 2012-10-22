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
package org.ourgrid.broker.business.scheduler.workqueue.statemachine;

import java.util.List;

import org.ourgrid.broker.business.messages.WorkerClientMessages;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.business.scheduler.workqueue.WorkQueueExecutionController;
import org.ourgrid.broker.communication.operations.GridProcessOperations;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 *
 */
public class RemoteState extends AbstractRunningState {
	
	private final String STATE_NAME = "Remote";

	public RemoteState(WorkQueueExecutionController heuristic) {
		super(heuristic);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#errorOcurred(org.ourgrid.worker.controller.ExecutionError)
	 */
	public void errorOcurred(GridProcessError error, GridProcess gridProcessg, List<IResponseTO> responses) {
		fail(error, gridProcessg, responses);
	}

	public void fileRejected(OutgoingHandle handle, GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileRejected", STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void fileTransferRequestReceived(IncomingHandle handle, GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileTransferRequestReceived", 
				STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void hereIsExecutionResult(ExecutorResult result, GridProcess gridProcess, List<IResponseTO> responses) {
		
		GridProcessOperations operations = gridProcess.getOperations();
		operations.removeRemotePhaseOperation();
		
		GridProcessExecutionResult replicaResult = gridProcess.getResult();
		replicaResult.setExecutorResult(result);
		replicaResult.setRemotePhaseEndTime();
		
		if (result.getExitValue() != 0) {
			fail(new GridProcessError( new Exception( "Command exit value indicates error: "
					+ result.getExitValue() ), GridProcessErrorTypes.EXECUTION_ERROR ), gridProcess, responses);
			
		} else {
			
			gridProcess.stopCPUTiming();
			
			if (operations.areAllOperationsFinished()) {
				finish(gridProcess, responses);
			} else if (!operations.areAllFinalPhaseOperationsFinished()) {
				gridProcess.gridProcessPhaseUpdate(GridProcessPhase.FINAL);
				gridProcess.getResult().setFinalPhaseStartTime();
				startFinalState(gridProcess, responses);
			} else {
				sabotageCheck(gridProcess, responses);
				
				if (!gridProcess.getResult().wasSabotaged()) {
					finish(gridProcess, responses);
				}
			}
			
		}

	}

	public void hereIsFileInfo(long operationHandle, FileInfo fileInfo, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("hereIsFileInfo", STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("incomingTransferCompleted", 
				STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void incomingTransferFailed(IncomingHandle handle, Exception failCause, long amountWritten, 
			GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("incomingTransferFailed", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	public void outgoingTransferCancelled(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCancelled", 
				STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCompleted", 
				STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, long amountWritten, 
			GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferFailed", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	public void updateTransferProgress(GenericTransferProgress fileTransferProgress, GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferFailed", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#workerIsReady()
	 */
	public void workerIsReady(GridProcess gridProcessg, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("workerIsReady", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

}
