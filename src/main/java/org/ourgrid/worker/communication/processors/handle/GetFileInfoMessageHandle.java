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

import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.worker.WorkerConstants;


public class GetFileInfoMessageHandle extends MessageHandle {

	private long requestID;
	private String filePath;
	private long handleId;

	public GetFileInfoMessageHandle(long handleId, long requestID, String filePath) {
		super(WorkerConstants.GET_FILE_INFO_ACTION_NAME);
		this.requestID = requestID;
		this.filePath = filePath;
		this.setHandleId(handleId);
	}

	public long getRequestID() {
		return requestID;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setHandleId(long handleId) {
		this.handleId = handleId;
	}

	public long getHandleId() {
		return handleId;
	}

}
