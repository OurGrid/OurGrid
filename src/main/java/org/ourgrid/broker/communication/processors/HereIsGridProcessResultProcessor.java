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

import org.ourgrid.broker.communication.actions.HereIsGridProcessResultMessageHandle;
import org.ourgrid.broker.request.HereIsGridProcessResultProcessorRequestTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class process the message that returns to the client 
 * the result of an execution. 
 * 
 */
public class HereIsGridProcessResultProcessor implements MessageProcessor<HereIsGridProcessResultMessageHandle> {

	@Req("REQ314")
	/**
	 * Delivers the result of a remote execution to the client
	 * 
	 * This message will be ignored in case the sender's public key is not the
	 * same as the consumer's one, or the worker is not working. 
	 * 
	 * @param handle
	 *            the handle containing the result of the execution.
	 * @param ServiceManager
	 *            the service manager of the broker.
	 */
	public void process(HereIsGridProcessResultMessageHandle handle, ServiceManager serviceManager) {
		HereIsGridProcessResultProcessorRequestTO to = new HereIsGridProcessResultProcessorRequestTO();
		
		ServiceID senderID = serviceManager.getSenderServiceID();
		ServiceID workerID = new ServiceID(senderID.getContainerID(), WorkerConstants.WORKER);
		
		to.setWorkerAddress(workerID.toString());
		to.setWorkerContainerID(workerID.getContainerID().toString());
		to.setResult(handle.getResult());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
