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

import java.io.File;
import java.util.List;

import org.ourgrid.broker.business.messages.WorkerClientMessages;
import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.business.scheduler.workqueue.WorkQueueExecutionController;
import org.ourgrid.broker.communication.operations.GridProcessOperations;
import org.ourgrid.broker.communication.operations.InitOperation;
import org.ourgrid.common.exception.UnableToDigestFileException;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.util.JavaFileUtil;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 *
 */
public class InitState extends AbstractRunningState {
	
	private final String STATE_NAME = "Init";

	public InitState(WorkQueueExecutionController heuristic) {
		super(heuristic);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#errorOcurred(org.ourgrid.worker.controller.ExecutionError)
	 */
	public void errorOcurred(GridProcessError error, GridProcess gridProcess, List<IResponseTO> responses) {
		fail(error, gridProcess, responses);
	}

	public void fileRejected(OutgoingHandle handle, GridProcess gridProcess, List<IResponseTO> responses) {
		InitOperation operation = gridProcess.getOperations().getInitPhaseOperation(handle);
		
		if (operation != null && operation.isTransferActive()) {
			fail(  new GridProcessError( new Exception( "File transfer rejected" + operation == null ? "" : ": "
				+ operation.getRemoteFilePath() ), GridProcessErrorTypes.APPLICATION_ERROR ), gridProcess, responses );
		} else {

			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileRejected", STATE_NAME), 
					LoggerResponseTO.ERROR));
		}
		
	}

	public void fileTransferRequestReceived(IncomingHandle handle, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("fileTransferRequestReceived", 
				STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#hereIsExecutionResult(org.ourgrid.common.executor.ExecutorResult)
	 */
	public void hereIsExecutionResult(ExecutorResult result, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("hereIsExecutionResult", 
				STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void hereIsFileInfo(long handlerID, FileInfo fileInfo, 
			GridProcess gridProcess, List<IResponseTO> responses) {
		
		GridProcessOperations operations = gridProcess.getOperations();
		InitOperation initOperation = operations.getPutOperation(handlerID);
		
		if (initOperation.isTransferActive()) {
			
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("hereIsFileInfo", STATE_NAME), 
					LoggerResponseTO.ERROR));
			return;
		}
		
		if (!remoteFileExists(fileInfo, initOperation, gridProcess, responses)) {
			runOperation(initOperation, gridProcess, responses);
		} else {
			responses.add(new LoggerResponseTO("File " + initOperation.getRemoteFilePath() + " exists on storage. Skipping transfer", 
					LoggerResponseTO.DEBUG));
					
			uploadCompleted(handlerID, gridProcess, responses);
		}
		
	}

	public void incomingTransferCompleted(IncomingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("incomingTransferCompleted", 
				STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void incomingTransferFailed(IncomingHandle handle, Exception failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("incomingTransferFailed", STATE_NAME), 
				LoggerResponseTO.WARN));
	}

	public void outgoingTransferCancelled(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		
		InitOperation operation = gridProcess.getOperations().getInitPhaseOperation(handle);
		
		if (operation != null && operation.isTransferActive()) {

			responses.add(new LoggerResponseTO(WorkerClientMessages.getOutgoingTransferCancelledMessage(handle, amountWritten), 
					LoggerResponseTO.ERROR));
			
			if (gridProcess.getState().equals(GridProcessState.RUNNING) ) {
				fail(new GridProcessError( new Exception("Transfer Cancelled"), 
						GridProcessErrorTypes.FILE_TRANSFER_ERROR ), gridProcess, responses);
			}
		} else {
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCancelled", 
					STATE_NAME), 
					LoggerResponseTO.ERROR));
		}
	}

	public void outgoingTransferCompleted(OutgoingHandle handle, long amountWritten, GridProcess gridProcess, List<IResponseTO> responses) {
		InitOperation initOperation = gridProcess.getOperations().getInitPhaseOperation(handle);
		
		if (!initOperation.isTransferActive()) {
			
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferCompleted", 
					STATE_NAME), 
					LoggerResponseTO.ERROR));
			return;
		}	
		
		initOperation.getGridResult().getInitOperationTransferTime(initOperation).setEndTime();
		
		gridProcess.incDataTransfered(amountWritten);
		
		responses.add(new LoggerResponseTO(WorkerClientMessages.getOutgoingTransferCompletedMessage(initOperation.getLocalFilePath(), 
				gridProcess.getHandle()), 
				LoggerResponseTO.DEBUG));
		
		uploadCompleted(handle.getId(), gridProcess, responses);
	}

	public void outgoingTransferFailed(OutgoingHandle handle, String failCause, long amountWritten, 
			GridProcess gridProcess, List<IResponseTO> responses) {
		
		InitOperation operation = gridProcess.getOperations().getInitPhaseOperation(handle);
		
		if (operation != null && operation.isTransferActive()) {
			
			responses.add(new LoggerResponseTO(WorkerClientMessages.getOutgoingTransferFailedMessage(new Exception(failCause), handle), 
					LoggerResponseTO.ERROR));
			
			if (gridProcess.getState().equals(GridProcessState.RUNNING) ) {
				fail(new GridProcessError( new Exception(failCause), GridProcessErrorTypes.FILE_TRANSFER_ERROR ), 
						gridProcess, responses);
			}
			
		} else {
			responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("outgoingTransferFailed", STATE_NAME), 
					LoggerResponseTO.ERROR));
		}
	}

	public void updateTransferProgress(GenericTransferProgress fileTransferProgress, GridProcess gridProcess, List<IResponseTO> responses) {

		gridProcess.fileTransferProgressUpdate(fileTransferProgress);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.states.RunningState#workerIsReady()
	 */
	public void workerIsReady(GridProcess gridProcess,List<IResponseTO> responses) {
		responses.add(new LoggerResponseTO(WorkerClientMessages.getRunningStateInvalidOperation("workerIsReady", STATE_NAME), 
				LoggerResponseTO.ERROR));
	}

	/**
	 * Changes to remote state if all init operations have finished
	 * @param handle 
	 */
	private void uploadCompleted(long handle, GridProcess gridProcess, List<IResponseTO> responses) {
		gridProcess.getOperations().removeInitPhaseOperation(handle);
		
		if (gridProcess.getOperations().areAllInitPhaseOperationsFinished()) {
			
			gridProcess.getResult().setInitPhaseEndTime();
			gridProcess.getResult().setRemotePhaseStartTime();
			
			gridProcess.gridProcessPhaseUpdate(GridProcessPhase.REMOTE);
			gridProcess.setRunningState(new RemoteState(getHeuristic()));
			gridProcess.startCPUTiming();
			
			runOperation(gridProcess.getOperations().getRemotePhaseOperation(), gridProcess, responses);
		}
		
	}
	
	/**
	 * Checks if a file exists based on a FileInfo
	 * @param fileInfo
	 * @param storeOperation
	 * @param gridProcess 
	 * @return True if the file exists, false otherwise.
	 */
	private boolean remoteFileExists(FileInfo fileInfo,
			InitOperation storeOperation, GridProcess gridProcess, List<IResponseTO> responses) {
		
		String remoteFileDigest = fileInfo.getFileDigest();
		
		// if the remote file exists and is not a directory
		if ( remoteFileDigest.equals( "0" ) ) {
			return false;
		} else {

			String localFileDigest = null;
			String localFilePath = storeOperation.getLocalFilePath();
			try {
				localFileDigest = JavaFileUtil.getDigestRepresentation( new File( localFilePath ) );
			} catch ( UnableToDigestFileException e ) {
				errorOcurred(new GridProcessError(e.getCause(), GridProcessErrorTypes.EXECUTION_ERROR), gridProcess, responses);
				return false;
			}

			if ( !localFileDigest.equals( remoteFileDigest ) ) {
				return false;
			} 
		}
		
		return true;
	}
}
