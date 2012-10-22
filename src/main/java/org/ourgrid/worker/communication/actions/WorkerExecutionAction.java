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
package org.ourgrid.worker.communication.actions;

import java.util.Map;

import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.WorkerExecutionServiceClient;
import org.ourgrid.worker.WorkerConstants;

/**
 */
public class WorkerExecutionAction implements Runnable {

	private Map<String, String> envVars;
	private String command;
	private Executor executor;
	private WorkerExecutionServiceClient executionClient;

	public WorkerExecutionAction(Map<String, String> envVars, String command,
			Executor executor, WorkerExecutionServiceClient executionClient) {
		this.envVars = envVars;
		this.command = command;
		this.executor = executor;
		this.executionClient = executionClient;
	}
	
	public void run() {
		
		ExecutorHandle executorHandle = null;
		
		try {
			executorHandle = executeCommand();
		} catch (ExecutorException e) {
			executionClient.executionError(e);
			return;
		}
		
		executionClient.executionIsRunning(executorHandle);
		
		try {
			ExecutorResult result = executor.getResult(executorHandle);
			executor.finishCommandExecution(executorHandle);
			executionClient.executionResult( result );
		} catch (ExecutorException ee) {
			executionClient.executionError( ee );
		} catch (Exception e) {
			executionClient.executionError(new ExecutorException(e));
		}
	}

	private ExecutorHandle executeCommand() throws ExecutorException {
		return executor.execute(envVars.get(WorkerConstants.ENV_PLAYPEN), command, envVars);
	}
	
}
