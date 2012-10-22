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
import org.ourgrid.worker.communication.processors.handle.GetFilesMessageHandle;
import org.ourgrid.worker.request.GetFilesProcessorRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;


public class GetFilesProcessor implements MessageProcessor<GetFilesMessageHandle> {

	@Req("REQ081")
	/**
	 * Request a set of files to be sent by the <code>Worker</code> to a
	 * client.
	 * 
	 * @param handle the handle contains the path of requested files.
	 */
	public void process(GetFilesMessageHandle handle, ServiceManager serviceManager) {
		
		GetFilesProcessorRequestTO to = new GetFilesProcessorRequestTO();
		to.setHandle(handle);
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

}
