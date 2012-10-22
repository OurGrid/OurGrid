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
package org.ourgrid.broker.communication.operations;

import java.util.List;

import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.internal.IResponseTO;

public abstract class FileTransferOperation extends AbstractOperation {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String localFilePath;
	
	private final String remoteFilePath;
	
	protected boolean transferActive;
	
	private GridProcessExecutionResult gridResult;
	
	/**
	 * @param replicaHandle
	 * @param requestID
	 * @param worker
	 */
	public FileTransferOperation( GridProcessHandle replicaHandle, long requestID, String workerID, 
									String localFilePath, String remoteFilePath, 
									GridProcessExecutionResult gridResult ) {

		super( replicaHandle, requestID, workerID);
		this.localFilePath = localFilePath;
		this.remoteFilePath = remoteFilePath;
		this.transferActive = false;
		this.gridResult = gridResult;
	}

	public abstract void cancelFileTransfer(List<IResponseTO> responses);


	public boolean isTransferActive() {
		return transferActive;
	}


	public void setTransferActive(boolean transferActive) {
		this.transferActive = transferActive;
	}


	public String getLocalFilePath() {
		return localFilePath;
	}


	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	public GridProcessExecutionResult getGridResult() {
		return gridResult;
	}

	public void setGridResult(GridProcessExecutionResult gridResult) {
		this.gridResult = gridResult;
	}

}
