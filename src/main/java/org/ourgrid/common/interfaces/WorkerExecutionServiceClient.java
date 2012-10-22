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
package org.ourgrid.common.interfaces;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;

import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * This interface is responsible to monitor a worker execution.
 */
@Remote
public interface WorkerExecutionServiceClient {
	
	/**
	 * Method called when the Worker is ready to be allocated.
	 */
	void readyForAllocation();
	
	/**
	 * Method called when an allocation has an error.
	 * @param error Allocation Error.
	 */
	void allocationError( ExecutorException error );
	
	/**
	 * Method called when the execution result is available.
	 * @param result Execution result
	 */
	void executionResult( ExecutorResult result );
	
	/**
	 * Method called when the execution has an error.
	 * @param error Execution error.
	 */
	void executionError( ExecutorException error );
	
	/**
	 * Method called when the execution is running.
	 * @param handle Execution handle.
	 */
	void executionIsRunning( ExecutorHandle handle );
	
}
