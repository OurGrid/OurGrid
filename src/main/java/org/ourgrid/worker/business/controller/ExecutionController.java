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
package org.ourgrid.worker.business.controller;

import static org.ourgrid.worker.WorkerConstants.ENV_PLAYPEN;
import static org.ourgrid.worker.WorkerConstants.ENV_STORAGE;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.EnvironmentDAO;
import org.ourgrid.worker.business.dao.ExecutionDAO;
import org.ourgrid.worker.business.dao.WorkAccountingDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ExecutionControllerMessages;
import org.ourgrid.worker.communication.dao.FutureDAO;
import org.ourgrid.worker.response.CancelBeginAllocationActionResponseTO;
import org.ourgrid.worker.response.CancelExecutionActionResponseTO;
import org.ourgrid.worker.response.ErrorOcurredMessageHandleResponseTO;
import org.ourgrid.worker.response.ExecutorKillCommandResponseTO;
import org.ourgrid.worker.response.ExecutorKillPreparingAllocationResponseTO;
import org.ourgrid.worker.response.ExecutorShutdownResponseTO;
import org.ourgrid.worker.response.SubmitExecutionActionResponseTO;
import org.ourgrid.worker.response.SubmitPrepareAllocationActionResponseTO;

public class ExecutionController {

	private static ExecutionController instance = null;
	
	@Req("REQ084")
	public static synchronized ExecutionController getInstance() {
		if (instance == null) {
			instance = new ExecutionController();
		}
		return instance;
	}
	
	public void beginAllocation(List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(ExecutionControllerMessages.
				getPrepareAllocationActionStartedMessage(), LoggerResponseTO.DEBUG));
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		workerStatusDAO.setPreparingAllocationState(true);
		submitBeginAllocationAction(responses);
	}

	private void submitBeginAllocationAction(List<IResponseTO> responses) {
		SubmitPrepareAllocationActionResponseTO to = new SubmitPrepareAllocationActionResponseTO();
		responses.add(to);
	}

	@Req("REQ084")
	public void scheduleCommand(long requestID,	Map<String, String> envVars, String command,
			String senderPublicKey, List<IResponseTO> responses, boolean isExecutionClientDeployed) {
		
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		EnvironmentDAO environmentDAO = WorkerDAOFactory.getInstance().getEnvironmentDAO();
		WorkAccountingDAO accountingDAO = WorkerDAOFactory.getInstance().getWorkAccountingDAO();
		
		envVars.put(WorkerConstants.ENV_PLAYPEN, environmentDAO.getPlaypenDir());
		envVars.put(WorkerConstants.ENV_STORAGE, environmentDAO.getStorageDir());
		
		if (executionDAO.getCurrentHandle() == null && !executionDAO.isExecutionFinished()) {
			
				if (workerStatusDAO.isAllocatedForRemotePeer()) {
					accountingDAO.getCurrentWorkAccounting().startCPUTiming();
				}
				
				submitExecutionAction(envVars, command, responses);
				
				responses.add(new LoggerResponseTO(ExecutionControllerMessages.getScheduleExecutionMessage(command, requestID, 
						envVars, senderPublicKey), 
						LoggerResponseTO.INFO));
			
		} else {
			
			responses.add(new LoggerResponseTO(ExecutionControllerMessages.getConcurrentExecutionMethod(requestID, command, envVars), 
					LoggerResponseTO.ERROR));
			
			String consumerAddress = WorkerDAOFactory.getInstance().getWorkerStatusDAO().getConsumerAddress();
			ErrorOcurredMessageHandleResponseTO to = new ErrorOcurredMessageHandleResponseTO(
					new GridProcessError(GridProcessErrorTypes.CONCURRENT_RUNNING), consumerAddress);
			responses.add(to);
		}
		
	}
	
	public String solvePlaypenAndStorage(String command, List<IResponseTO> responses) {
		if (command != null) {
			
			String newCommand = command;
			String playpenDir = getPlaypenDir(responses);
			
			if (playpenDir != null) {
				newCommand = newCommand.replace( "$" + ENV_PLAYPEN, playpenDir);
			}
			
			String storageDir = getStorageDir(responses);
			
			if (storageDir != null) {
				newCommand = newCommand.replace( "$" + ENV_STORAGE, storageDir);
			}
			
			return newCommand;
		}
		
		return command;
	}
	
	
	public void cancelActiveExecution(List<IResponseTO> responses, boolean interruptWorking, boolean cancelPreparingAllocation) {
		
		if (interruptWorking) {
			
			ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
			ExecutorHandle currentHandle = executionDAO.getCurrentHandle();
			
			if (currentHandle != null) {
				responses.add(new CancelExecutionActionResponseTO());
				executionDAO.setCurrentHandle(null);
				
				ExecutorKillCommandResponseTO executorKillCommandResponseTO = 
					new ExecutorKillCommandResponseTO();
				
				executorKillCommandResponseTO.setHandle(currentHandle);
				
				responses.add(executorKillCommandResponseTO);
			}
			
			if (cancelPreparingAllocation) {
				
				if (hasActivePreparingAllocation()) {
					cancelPreparingAllocationExecution(responses);
					executionDAO.setExecutingKillPreparingAllocation(true);
					
					ExecutorKillPreparingAllocationResponseTO executorKillPreparingAllocationResponseTO = 
						new ExecutorKillPreparingAllocationResponseTO();
					responses.add(executorKillPreparingAllocationResponseTO);
				}
				
				else {
					shutdown(responses);
				}
			}
		}
	}
	
	private boolean hasActivePreparingAllocation() {
		FutureDAO futureDAO = WorkerDAOFactory.getInstance().getFutureDAO();
		Future<?> beginAllocationFuture = futureDAO.getBeginAllocationFuture();
		return beginAllocationFuture != null && !beginAllocationFuture.isDone();
	}

	public void executionFinish(boolean success) {
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
		executionDAO.setCurrentHandle(null);
		executionDAO.setExecutionFinished(success);
	}

	public void executionIsRunning(ExecutorHandle handle) {
		WorkerDAOFactory.getInstance().getExecutionDAO().setCurrentHandle(handle);
	}
	
	@Req("REQ084")
	private void submitExecutionAction(Map<String, String> envVars,
			String command, List<IResponseTO> responses) {
		
		SubmitExecutionActionResponseTO to = new SubmitExecutionActionResponseTO();
		to.setCommand(command);
		to.setEnvVars(envVars);
		
		responses.add(to);
	}
	
	private String getPlaypenDir(List<IResponseTO> responses) {
		return WorkerDAOFactory.getInstance().getEnvironmentDAO().getPlaypenDir();
	}
	
	private String getStorageDir(List<IResponseTO> responses) {
		return WorkerDAOFactory.getInstance().getEnvironmentDAO().getStorageDir();
	}
	
	private void cancelPreparingAllocationExecution(List<IResponseTO> responses) {
		responses.add(new LoggerResponseTO(ExecutionControllerMessages.
				getPrepareAllocationActionCancelledMessage(), LoggerResponseTO.DEBUG));
		responses.add(new CancelBeginAllocationActionResponseTO());		
	}
	
	/**
	 * Adds to the responses list the ExecutorShutdownResponseTO, which will shutdown
	 * the worker executor and the virtual machine
	 * @param responses
	 */
	public void shutdown(List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO(ExecutionControllerMessages.getShutdowningExecutorMessage(), 
				LoggerResponseTO.DEBUG));
		
		ExecutorShutdownResponseTO executorShutdownResponseTO = new ExecutorShutdownResponseTO();
		responses.add(executorShutdownResponseTO);
	}
	
	
}
