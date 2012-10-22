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
package org.ourgrid.broker.communication.operations;

import java.util.List;
import java.util.Map;

import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorFactory;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.replicaexecutor.SabotageCheckResult;
import org.ourgrid.worker.business.controller.GridProcessError;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLoggerFactory;

/**
 * This class implements a mechanism to check sabotage acts.
 */
public class SabotageCheckOperation extends AbstractOperation {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String command;
	private final String executionDirectory;
	private final Map<String, String> envVars;
	private SabotageCheckResult sabotageResult;
	private CommuneLogger logger;

	/**
	 * @param replicaHandle
	 * @param requestID
	 * @param worker
	 * @param workerClient
	 * @param command
	 * @param executionDirectory
	 * @param envVars
	 * @param executor
	 */
	public SabotageCheckOperation(GridProcessHandle replicaHandle, long requestID, String workerID, 
			String command, String executionDirectory, Map<String,String> envVars) {
		
		super(replicaHandle, requestID, workerID);
		this.command = command;
		this.executionDirectory = executionDirectory;
		this.envVars = envVars;
		
		this.logger = CommuneLoggerFactory.getInstance().gimmeALogger(this.getClass());
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.replicaexecutor.operation.Operation#run()
	 */
	public void run(List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO("Running sabotage check command: "+command+" replica: "+ 
				getGridProcessHandle(), LoggerResponseTO.DEBUG));

		
		ExecutorHandle handle;
		ExecutorResult result = null;
		
		
		Executor executor = new ExecutorFactory(logger).buildNewNativeExecutor();
		
		try {
			handle = executor.execute(executionDirectory, command, envVars);
			result = executor.getResult(handle);
			sabotageResult = new SabotageCheckResult(result.getExitValue() != 0, 
					result, null);//if the exit value is not 0 the task was sabotaged.
		} catch (ExecutorException e) {
			//An error occured in the sabotage check command so is not possible to decide
			//about sabotage.
			sabotageResult = new SabotageCheckResult(false, 
					result, new GridProcessError(e, GridProcessErrorTypes.EXECUTION_ERROR));
		}
	}
	
	/**
	 * 
	 * @return The sabotage check command.
	 */
	public String getCommand() {
		return command;
	}

	@Override
	public String toString() {
		return "[" + this.getClass().getSimpleName() + "]. sabotage check command: " +command;
	}

	/**
	 * @return the sabotageResult
	 */
	public SabotageCheckResult getSabotageResult() {
		return sabotageResult;
	}

	@Override
	public GenericTransferHandle getHandle() {
		return null;
	}
}
