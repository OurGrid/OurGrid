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

import org.ourgrid.common.filemanager.FileInfo;
import org.ourgrid.common.interfaces.MessageProcessor;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.communication.processors.handle.GetFileInfoMessageHandle;
import org.ourgrid.worker.request.GetFileInfoProcessorRequestTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

/**
 * This class process the message that request a set of metadata related to a file stored in the worker's
 * storage area. That information will be provided to the client through the
 * method {@link WorkerClient#hereIsFileInfo(OperationHandle, FileInfo)}.
 * 
 * @see org.ourgrid.common.filemanager.FileInfo
 */
public class GetFileInfoProcessor implements MessageProcessor<GetFileInfoMessageHandle> {

	@Req("REQ082")
	/**
	 * Returns the information about the file described by the pointed file
	 * path. All file paths must point into the storage directory.
	 * 
	 * This message will be ignored in case the sender's public key is not the
	 * same as the consumer's one, or the worker is not working. Also, an error
	 * might occur if the required file path is invalid.
	 * 
	 * @param handle
	 *            the handle contains the file path.
	 * @param ServiceManager
	 *            the service manager of the worker.
	 */
	public void process(GetFileInfoMessageHandle handle, ServiceManager serviceManager) {
		
		GetFileInfoProcessorRequestTO to = new GetFileInfoProcessorRequestTO();
		to.setHandle(handle);
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
}
