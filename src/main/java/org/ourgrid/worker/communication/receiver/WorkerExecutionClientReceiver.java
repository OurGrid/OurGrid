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
package org.ourgrid.worker.communication.receiver;

import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.executor.ExecutorHandle;
import org.ourgrid.common.executor.ExecutorResult;
import org.ourgrid.common.interfaces.WorkerExecutionServiceClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.request.AllocationErrorRequestTO;
import org.ourgrid.worker.request.ExecutionErrorRequestTO;
import org.ourgrid.worker.request.ExecutionIsRunningRequestTO;
import org.ourgrid.worker.request.ExecutionResultRequestTO;
import org.ourgrid.worker.request.ReadyForAllocationRequestTO;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

@Req("REQ084")
public class WorkerExecutionClientReceiver implements WorkerExecutionServiceClient {

	private static final long serialVersionUID = 1L;

	private ServiceManager serviceManager;

	@Req("REQ090")
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	@Req("REQ087")
	public void executionError(ExecutorException error) {
		ExecutionErrorRequestTO to = new ExecutionErrorRequestTO();
		to.setError(error);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ087")
	public void executionResult(ExecutorResult result) {
		ExecutionResultRequestTO to = new ExecutionResultRequestTO();
		to.setResult(result);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ087")
	public void executionIsRunning(ExecutorHandle handle) {
		ExecutionIsRunningRequestTO to = new ExecutionIsRunningRequestTO();
		to.setHandle(handle);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	public void allocationError(ExecutorException error) {
		AllocationErrorRequestTO to = new AllocationErrorRequestTO();
		to.setError(error);
		to.setWorkerDeployed(isWorkerDeployed());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void readyForAllocation() {
		ReadyForAllocationRequestTO to = new ReadyForAllocationRequestTO();
		to.setWorkerDeployed(isWorkerDeployed());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	private boolean isWorkerDeployed() {
		return getServiceManager().getObjectDeployment(WorkerConstants.WORKER) != null;
	}
}
