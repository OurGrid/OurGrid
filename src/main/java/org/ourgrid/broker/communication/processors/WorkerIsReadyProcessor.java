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
package org.ourgrid.broker.communication.processors;

import org.ourgrid.broker.communication.actions.WorkerIsReadyMessageHandle;
import org.ourgrid.broker.request.WorkerIsReadyProcessorRequestTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class process the message that notifies the client 
 * that the Worker is ready to receive calls to other methods. 
 * 
 */
public class WorkerIsReadyProcessor implements MessageProcessor<WorkerIsReadyMessageHandle> {

	@Req("REQ313")
	/**
	 * Notifies the client that the Worker is ready to receive calls to other
	 * methods.
	 * 
	 * This message will be ignored in case the sender's public key is not the
	 * same as the consumer's one, or the worker is not working. 
	 * 
	 * @param handle
	 *            the handle.
	 * @param ServiceManager
	 *            the service manager of the broker.
	 */
	public void process(WorkerIsReadyMessageHandle handle, ServiceManager serviceManager) {
		WorkerIsReadyProcessorRequestTO to = new WorkerIsReadyProcessorRequestTO();
		ServiceID workerID = serviceManager.getSenderServiceID();
		
		to.setWorkerAddress(workerID.toString());
		to.setWorkerContainerID(workerID.getContainerID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
