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
import java.io.FileNotFoundException;
import java.util.List;

import org.ourgrid.broker.response.GetFileInfoMessageHandleResponseTO;
import org.ourgrid.broker.response.StartTransferResponseTO;
import org.ourgrid.common.CommonConstants;
import org.ourgrid.common.exception.FileTransferException;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.GridProcessExecutionResult;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.CancelOutgoingTransferResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.common.util.StringUtil;

import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * This type implements common methods for <code>PutOperation</code> and
 * <code>StoreOperation</code> types.
 */
public class InitOperation extends FileTransferOperation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient File localFile;

	private boolean checkFileInfo;
	
	private OutgoingHandle handle;

	private final long fileLength;

	/**
	 * Constructs a new instance of this type for the given filepath.
	 * 
	 * @param replicaHandle a handle to the currently executing replica
	 * @param requestID the ID of the request that generated this operation
	 * @param worker the worker that will receive the file.
	 * @param localFilePath the filepath that points to the local file to be
	 *        put.
	 * @param remoteFilePath the filepath that points to the remote file to be
	 *        put.
	 */
	public InitOperation( GridProcessHandle replicaHandle, long requestID, String workerID, String localFilePath, 
			String remoteFilePath, String transferDescription, 
			GridProcessExecutionResult gridResult ) {

		super( replicaHandle, requestID, workerID, localFilePath, new File(remoteFilePath).getName(), gridResult);
		
		DeploymentID workerDeploymentID = new DeploymentID(workerID);
		
		String logicalFile = new File(localFilePath).getName();
		
		this.handle = new OutgoingHandle(logicalFile, new File(localFilePath), 
				transferDescription, workerDeploymentID.toString());
		
		setType( OperationType.INIT );
		
		this.localFile = new File( localFilePath );
		this.checkFileInfo = FileTransferHandlerUtils.getOperationType(transferDescription).equals(CommonConstants.STORE_TRANSFER);
		this.fileLength = this.localFile.length();
	}

	public void run(List<IResponseTO> responses) throws OperationException {

		try {
			checkIfLocalFileExistsAndCanRead();
			if (!checkFileInfo) {
				sendFile(responses);
			} else {
				getRemoteFileInfo(responses);
				checkFileInfo = false;
			}
		} catch ( FileTransferException e ) {
			throw new OperationException( e );
		}
	}


	/**
	 * Puts the file pointed by the <code>filePath</code> member to a remote
	 * grid machine (Worker) directory.
	 */
	private void sendFile(List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO("Sending file " + getLocalFilePath() + " to " + getWorkerID(), LoggerResponseTO.INFO));

		
		getGridResult().getInitOperationTransferTime(this).setInitTime();
		setTransferActive( true );
		
		OutgoingHandle handle = (OutgoingHandle) getHandle();
		
		StartTransferResponseTO to = new StartTransferResponseTO();
		
		to.setHandleId(handle.getId());
		to.setLocalFileName(handle.getLocalFile().getAbsolutePath());
		to.setDescription(handle.getDescription());
		to.setId(handle.getDestinationID());
		
		responses.add(to);
	}

	/**
	 * Tries to get remote information on the file to be stored. This method is
	 * to be called after the lock is already gotten.
	 */
	private void getRemoteFileInfo(List<IResponseTO> responses) {
		
		responses.add(new LoggerResponseTO("File info requested: " + getRemoteFilePath() + ", handle: "
				+ getHandle() + ", replica: " + getGridProcessHandle(), LoggerResponseTO.DEBUG));
		
		GetFileInfoMessageHandleResponseTO to = new GetFileInfoMessageHandleResponseTO(handle.getId(), 
				getRequestID(), getRemoteFilePath(), StringUtil.deploymentIDToAddress(getWorkerID()));
		
		responses.add(to);
				
	}


	public void cancelFileTransfer(List<IResponseTO> responses) {

		if ( isTransferActive() ) {
			
			OutgoingHandle handle = (OutgoingHandle) getHandle();
			
				CancelOutgoingTransferResponseTO to = new CancelOutgoingTransferResponseTO();
				
				OutgoingHandle outgoingHandle = new OutgoingHandle(handle.getId(), 
						handle.getLogicalFileName(), new File(handle.getLogicalFileName()), 
						handle.getDescription(), handle.getOppositeID());
				
				to.setOutgoingHandle(outgoingHandle);
				
				responses.add(to);
		}
	}
	
	/**
	 * Checks if the given file exists and is readable.
	 * 
	 * @throws FileTransferException thrown if the file does not exist or is not
	 *         readable.
	 */
	private void checkIfLocalFileExistsAndCanRead() throws FileTransferException {

		if ( !localFile.exists() ) {
			throw new FileTransferException( localFile.getAbsolutePath(),
				new FileNotFoundException( localFile.getAbsolutePath() ), true );
		}
		if ( !localFile.canRead() ) {
			throw new FileTransferException( localFile.getAbsolutePath(), "File exists but cannot be read", true );
		}
		
	}

	@Override
	public GenericTransferHandle getHandle() {
		return handle;
	}

	public long getFileLength() {
		return fileLength;
	}

}
