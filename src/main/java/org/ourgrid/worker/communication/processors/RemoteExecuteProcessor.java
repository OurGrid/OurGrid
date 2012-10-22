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
package org.ourgrid.worker.communication.processors;

import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.communication.processors.handle.RemoteExecuteMessageHandle;
import org.ourgrid.worker.request.RemoteExecuteProcessorRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class RemoteExecuteProcessor implements MessageProcessor<RemoteExecuteMessageHandle> {
	
	@Req("REQ084")
	/**
	 * Request the execution of a remote command. After the command is executed
	 * the <code>Worker</code> must make a call to
	 * {@link WorkerClient#hereIsExecutionResult(ExecutorResult)} delivering a
	 * result to client. If some error that prevents the execution from
	 * finishing occurs, a call to
	 * {@link WorkerClient#errorOcurred(ExecutionError)} must be made instead.
	 * 
	 * @param handle the handle contains environmentVars, a set of environment variables passed by the
	 *        client that should be exported to the execution environment; requestID, the request
	 *        id associated to the current work session; command, the remote command that will be executed.
	 */
	public void process(RemoteExecuteMessageHandle handle, ServiceManager serviceManager) {
		
		RemoteExecuteProcessorRequestTO to = new RemoteExecuteProcessorRequestTO();
		to.setHandle(handle);
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		to.setExecutionClientDeployed(isExecutionClientDeployed(serviceManager));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	private boolean isExecutionClientDeployed(ServiceManager serviceManager) {
		return serviceManager.getObjectDeployment(WorkerConstants.WORKER_EXECUTION_CLIENT) != null;
	}

}
