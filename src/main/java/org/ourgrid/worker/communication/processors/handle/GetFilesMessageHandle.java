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
package org.ourgrid.worker.communication.processors.handle;

import org.ourgrid.common.FileTransferInfo;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.worker.WorkerConstants;


public class GetFilesMessageHandle extends MessageHandle {

	private long requestID;
	private FileTransferInfo[] files;

	public GetFilesMessageHandle(long requestID, FileTransferInfo... files) {
		super(WorkerConstants.GET_FILES_ACTION_NAME);
		this.requestID = requestID;
		this.files = files;
	}

	public long getRequestID() {
		return requestID;
	}

	public FileTransferInfo[] getFiles() {
		return files;
	}
	
}
