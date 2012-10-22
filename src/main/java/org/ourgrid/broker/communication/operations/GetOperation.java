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

import java.io.File;
import java.util.List;

import org.ourgrid.broker.response.AcceptTransferResponseTO;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.CancelIncomingTransferResponseTO;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * This class implements the operation of getting a file from a remote
 * directory.
 */
public class GetOperation extends FileTransferOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private IncomingHandle handle;
	
	public GetOperation(GridProcessHandle replicaHandle, long requestID,
			String workerID, String localFilePath, String remoteFilePath,
			String transferDescription, GridProcessExecutionResult gridResult) {
		
		super( replicaHandle, requestID, workerID, localFilePath, remoteFilePath, gridResult);
		
		DeploymentID workerDeploymentID = new DeploymentID(workerID);
		
		int lastIndexOf = localFilePath.lastIndexOf(File.separatorChar);
		
		String logicalFileName = lastIndexOf <  0 ? localFilePath : localFilePath.substring(lastIndexOf);
		
		this.handle = new IncomingHandle(logicalFileName, localFilePath, 0, transferDescription,
				workerDeploymentID.getContainerID().toString());
		
		setType(OperationType.FINAL);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.operations.Operation#run()
	 */
	public void run(List<IResponseTO> responses) throws OperationException {
		acceptTransfer(responses);
	}

	/**
	 * Accepts the transfer associated to this operation
	 */
	private void acceptTransfer(List<IResponseTO> responses) {
		
		getGridResult().getGetOperationTransferTime(this).setInitTime();
		setTransferActive(true);
		
		IncomingHandle handle = (IncomingHandle) getHandle();
		
		AcceptTransferResponseTO to = new AcceptTransferResponseTO();
		to.setDescription(handle.getDescription());
		to.setLocalFilePath(getLocalFilePath());
		to.setFileSize(handle.getFileSize());
		to.setId(handle.getId());
		to.setLogicalFileName(handle.getLogicalFileName());
		to.setSenderContainerID(handle.getSenderContainerID());
		
		to.setExecutable(handle.isExecutable());
		to.setReadable(handle.isReadable());
		to.setWritable(handle.isWritable());
		
		responses.add(to);
	}


	/* (non-Javadoc)
	 * @see org.ourgrid.broker.controller.operations.FileTransferOperation#cancelFileTransfer()
	 */
	public void cancelFileTransfer(List<IResponseTO> responses) {
		if ( isTransferActive() ) {
			
			IncomingHandle handle = (IncomingHandle) getHandle();

				CancelIncomingTransferResponseTO to = new CancelIncomingTransferResponseTO();
				
				IncomingHandle incomingHandle = new IncomingHandle(handle.getId(), handle.getLogicalFileName(), 
						handle.getFileSize(), handle.getDescription(), handle.getOppositeID());
				
				to.setIncomingHandle(incomingHandle);
				
				responses.add(to);
		}
	}


	@Override
	public GenericTransferHandle getHandle() {
		return handle;
	}


	public void setHandle(IncomingHandle handle) {
		this.handle = handle;
	}

}
