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

import org.ourgrid.broker.communication.actions.HereIsFileInfoMessageHandle;
import org.ourgrid.broker.request.HereIsFileInfoProcessorRequestTO;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.internal.OurGridRequestControl;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class process the message that delivers to the client the file information requested.
 */
public class HereIsFileInfoProcessor implements MessageProcessor<HereIsFileInfoMessageHandle> {

	/**
	 * Delivers the file information requested to the Worker
	 * 
	 * @param handle 
	 * 			  the handle contains the fileInfo.
	 * @param serviceManager
	 *            the service manager of the broker.
	 */
	public void process(HereIsFileInfoMessageHandle handle, ServiceManager serviceManager) {
		HereIsFileInfoProcessorRequestTO to = new HereIsFileInfoProcessorRequestTO();
		ServiceID workerID = serviceManager.getSenderServiceID();

		to.setFileInfo(handle.getFileInfo());
		to.setHandlerId(handle.getHandlerId());
		to.setWorkerAddress(workerID.toString());
		to.setWorkerContainerID(workerID.getContainerID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
