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

import org.ourgrid.common.executor.Executor;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.interfaces.WorkerExecutionServiceClient;

/**
 */
public class PrepareAllocationAction implements Runnable {

	private Executor executor;
	private WorkerExecutionServiceClient executionClient;

	public PrepareAllocationAction(Executor executor,
			WorkerExecutionServiceClient executionClient) {
		this.executor = executor;
		this.executionClient = executionClient;
	}
	
	public void run() {
		try {
			executor.prepareAllocation();
			executor.finishPrepareAllocation();
			
			executionClient.readyForAllocation();
		} catch (ExecutorException e) {
			executionClient.allocationError( e );
		}
	}
	
}
