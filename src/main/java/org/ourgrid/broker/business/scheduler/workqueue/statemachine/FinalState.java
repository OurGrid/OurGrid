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
import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.GridProcessOperations;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 *
 */
public class FinalState extends AbstractRunningState {
	
	private final String STATE_NAME = "Final";

	public FinalState(WorkQueueExecutionController heuristic) {
		super(heuristic);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#errorOcurred(org.ourgrid.worker.controller.ExecutionError)
	 */
	public void errorOcurred(GridProcessError error, GridProcess gridProcess, List<IResponseTO> responses) {
		fail(error, gridProcess, responses);
	}

	public void fileRejected(OutgoingHandle handle, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileRejected", STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void fileTransferRequestReceived(IncomingHandle handle, GridProcess gridProcess, List<IResponseTO> responses) {
		
		GetOperation getOperation = gridProcess.getOperations().getFinalPhaseOperations().get(handle);
		
		if (getOperation != null && getOperation.isTransferActive()) {
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileTransferRequestReceived", 
					STATE_NAME), 
					LoggerResponseTO.ERROR));
			
			return;
		}
		
		getOperation.setHandle(handle);
		runOperation(getOperation, gridProcess, responses);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#hereIsExecutionResult(org.ourgrid.common.executor.ExecutorResult)
	 */
	public void hereIsExecutionResult(ExecutorResult result, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("hereIsExecutionResult", STATE_NAME),
				LoggerResponseTO.WARN));
	}

	public void hereIsFileInfo(long handle, FileInfo fileInfo, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("hereIsFileInfo", STATE_NAME),
				LoggerResponseTO.WARN));
		
	}

	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		
		GridProcessOperations operations = gridProcess.getOperations();
		GetOperation getOperation = operations.getFinalPhaseOperation(handle);
		
		if (!getOperation.isTransferActive()) {
			
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("incomingTransferCompleted", 
					STATE_NAME), 
					LoggerResponseTO.ERROR));
			return;
		}
		
		
		getOperation.getGridResult().getGetOperationTransferTime(getOperation).setEndTime();
		
		gridProcess.incDataTransfered(amountWritten);
		operations.removeFinalPhaseOperation(handle);
		
		if (operations.areAllFinalPhaseOperationsFinished()) {
			gridProcess.getResult().setFinalPhaseEndTime();
			sabotageCheck(gridProcess, responses);
			
			if (!gridProcess.getResult().wasSabotaged()) {
				finish(gridProcess, responses);
			}
		}
		
	}

	public void incomingTransferFailed(IncomingHandle handle, Exception failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getIncomingTransferFailedMessage(failCause, handle), 
				LoggerResponseTO.ERROR));
		
		startFinalStateOnSisterProcess(gridProcess, responses);
		
		/* If an incoming transfer fails, the Worker has been lost */
		if (gridProcess.getState().equals(GridProcessState.RUNNING)) {
			fail( new GridProcessError( failCause, GridProcessErrorTypes.FILE_TRANSFER_ERROR ), gridProcess, responses);
		}
		
	}

	private void startFinalStateOnSisterProcess(GridProcess gridProcess, List<IResponseTO> responses) {
		List<GridProcess> gridProcesses = gridProcess.getTask().getGridProcesses();
		GridProcess finalStateProcess = null; 
		
		for (GridProcess eachProcess : gridProcesses) {
			if (!eachProcess.equals(gridProcess) && eachProcess.hasFinalStateStarted() 
					&& eachProcess.getState().isRunnable()) {
				
				if (finalStateProcess == null) {
					finalStateProcess = eachProcess;
				} else {
					if (eachProcess.getResult().getFinalData().getStartTime() < 
							finalStateProcess.getResult().getFinalData().getStartTime()) {
						finalStateProcess = eachProcess;
					}
				}
				
			}
		}
		
		if (finalStateProcess != null) {
			startFinalState(finalStateProcess, responses);
		}
	}

	public void outgoingTransferCancelled(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCancelled", 
				STATE_NAME),
				LoggerResponseTO.ERROR));
	}

	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {

		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCompleted", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferFailed", STATE_NAME),
				LoggerResponseTO.ERROR));
	}

	public void updateTransferProgress(GenericTransferProgress fileTransferProgress, GridProcess gridProcess, List<IResponseTO> responses) {
		
		gridProcess.fileTransferProgressUpdate(fileTransferProgress);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#workerIsReady()
	 */
	public void workerIsReady(GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("workerIsReady", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

}
