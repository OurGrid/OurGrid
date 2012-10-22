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

import org.ourgrid.broker.communication.actions.ErrorOcurredMessageHandle;
import org.ourgrid.broker.request.ErrorOcurredProcessorRequestTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class process the message that notifies the client 
 * that If something wrong occurs during an replica execution (including file
 * transfers), the client will receive details about the error.
 */
public class ErrorOcurredProcessor implements MessageProcessor<ErrorOcurredMessageHandle> {

	
	/**
	 * Notifies the client that a error occurs into the Worker during an replica execution.
	 * 
	 * @param handle 
	 * 			  the handle with a detail about the error.
	 * @param serviceManager
	 *            the service manager of the broker.
	 */
	public void process(ErrorOcurredMessageHandle handle, ServiceManager serviceManager) {
		
		ErrorOcurredProcessorRequestTO to = new ErrorOcurredProcessorRequestTO();
		to.setGridProcessError(handle.getGridProcessError());
		
		ServiceID workerID = serviceManager.getSenderServiceID();
		
		to.setWorkerAddress(workerID.toString());
		to.setWorkerContainerID(workerID.getContainerID().toString());
	
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
