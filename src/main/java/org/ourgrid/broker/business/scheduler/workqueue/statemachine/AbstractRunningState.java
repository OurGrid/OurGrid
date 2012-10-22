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

import java.util.Collection;
import java.util.List;

import org.ourgrid.broker.business.scheduler.RunningState;
import org.ourgrid.broker.business.scheduler.workqueue.WorkQueueExecutionController;
import org.ourgrid.broker.communication.operations.GetOperation;
import org.ourgrid.broker.communication.operations.Operation;
import org.ourgrid.broker.communication.operations.OperationException;
import org.ourgrid.broker.communication.operations.SabotageCheckOperation;
import org.ourgrid.broker.response.GetFilesMessageHandleResponseTO;
import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessPhase;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.job.GridProcess;
import org.ourgrid.common.replicaexecutor.SabotageCheckResult;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.worker.business.controller.GridProcessError;

/**
 *
 */
public abstract class AbstractRunningState implements RunningState {

	private final WorkQueueExecutionController heuristic;

	public AbstractRunningState(WorkQueueExecutionController heuristic) {
		this.heuristic = heuristic;
	}
	
	protected void startFinalState(GridProcess gridProcess, List<IResponseTO> responses) {
		if (!hasASisterProcessInFinalState(gridProcess)) {
			getFiles(gridProcess, responses);
			gridProcess.setRunningState(new FinalState(getHeuristic()));
		}
	}

	private boolean hasASisterProcessInFinalState(GridProcess gridProcess) {
		List<GridProcess> gridProcesses = gridProcess.getTask().getGridProcesses();
		
		for (GridProcess eachProcess : gridProcesses) {
			if (!eachProcess.equals(gridProcess) && eachProcess.hasFinalStateStarted() 
					&& eachProcess.getState().isRunnable()) {
				return true;
			}
		}
		
		return false;
	}
	

	/**
	 * Request for getting files in the worker
	 * @param gridProcessg
	 */
	private void getFiles(GridProcess gridProcessg, List<IResponseTO> responses) {
		String workerID = gridProcessg.getWorkerEntry().getWorkerID();
		long requestID = gridProcessg.getWorkerEntry().getRequestID();
		
		FileTransferInfo[] files = new FileTransferInfo[gridProcessg.getOperations().getFinalPhaseOperationsList().size()];
		int i = 0;
		
		for (GetOperation operation : gridProcessg.getOperations().getFinalPhaseOperationsList()) {
			files[i] = new FileTransferInfo(operation.getHandle().getId(), operation.getRemoteFilePath());
			i++;
		}	
		
		GetFilesMessageHandleResponseTO to = new GetFilesMessageHandleResponseTO(requestID, 
				StringUtil.deploymentIDToAddress(workerID), files);
		
		responses.add(to);
		
	}
	
	protected void runOperations( Collection<? extends Operation> operationsToRun, GridProcess gridProcess, List<IResponseTO> responses) {

		if ( operationsToRun != null ) {
			for ( Operation operationToRun : operationsToRun ) {
				runOperation(operationToRun, gridProcess, responses);
			}
		}
	}
	
	protected void runOperation( Operation operationToRun, GridProcess gridProcess, List<IResponseTO> responses ) {
		try {
			operationToRun.run(responses);
		} catch ( OperationException e ) {
			this.errorOcurred( new GridProcessError( e.getCause(), GridProcessErrorTypes.APPLICATION_ERROR ), gridProcess, responses);
		}
	}
	
	protected void finish(GridProcess gridProcess, List<IResponseTO> responses) {
		gridProcess.gridProcessPhaseUpdate(GridProcessPhase.FINISHED);
		gridProcess.setGridProcessResult(GridProcessState.FINISHED);
		gridProcess.getOperations().cancelOperations(responses);
		allDone(gridProcess, responses);
	}
	
	protected void fail(GridProcessError error, GridProcess gridProcess, List<IResponseTO> responses) {
		if (hasEnded(gridProcess)) {
			return;
		}
		
		gridProcess.getResult().setExecutionError(error);
		gridProcess.setGridProcessResult(GridProcessState.FAILED);
		gridProcess.getOperations().cancelOperations(responses);
		allDone(gridProcess, responses);
	}
	
	private boolean hasEnded(GridProcess execution) {
		GridProcessState state = execution.getState();
		
		return state.equals(GridProcessState.FAILED) || 
			state.equals(GridProcessState.CANCELLED) || state.equals(GridProcessState.FINISHED);
	}

	private void allDone(GridProcess gridProcess, List<IResponseTO> responses) {
		
		GridProcessState replicaState = gridProcess.getState();
		
		switch ( replicaState ) {
		case FINISHED:
			getHeuristic().executionFinished( gridProcess, responses);
			break;
		case ABORTED:
			getHeuristic().executionAborted( gridProcess, responses);
			break;
		case FAILED:
			getHeuristic().executionFailed( gridProcess, responses );
			break;
		default:
			break;
		}
	}
	
	/**
	 */
	protected void sabotageCheck( GridProcess execution, List<IResponseTO> responses ) {
		
		SabotageCheckOperation sabotageCheck = execution.getOperations().getSabotageCheckOperation();
		
		if (sabotageCheck == null) {
			return;
		}
		
		sabotageCheck.run(responses);
		execution.getOperations().removeSabotageCheckOperation();
		
		SabotageCheckResult sabotageResult = sabotageCheck.getSabotageResult();
		execution.getResult().setSabotageCheckResult(sabotageResult);
		
		if ( sabotageResult.wasSabotaged() ) {

			responses.add(new LoggerResponseTO("Replica Sabotaged " + execution.getHandle(), LoggerResponseTO.ERROR));
			
			fail( new GridProcessError( GridProcessErrorTypes.SABOTAGE_ERROR ) , execution, responses);
		}
		
	}

	/**
	 * @return the heuristic
	 */
	protected WorkQueueExecutionController getHeuristic() {
		return heuristic;
	}
	
}
