/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.worker.communication.receiver;

import java.io.File;

import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GridProcessHandle;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConfiguration;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.request.IncomingTransferCompletedRequestTO;
import org.ourgrid.worker.request.IncomingTransferFailedRequestTO;
import org.ourgrid.worker.request.OutgoingTransferCancelledRequestTO;
import org.ourgrid.worker.request.OutgoingTransferCompletedRequestTO;
import org.ourgrid.worker.request.OutgoingTransferFailedRequestTO;
import org.ourgrid.worker.request.StartWorkRequestTO;
import org.ourgrid.worker.request.TransferRejectedRequestTO;
import org.ourgrid.worker.request.TransferRequestReceivedRequestTO;
import org.ourgrid.worker.request.UpdateTransferProgressRequestTO;
import org.ourgrid.worker.request.WorkerClientIsDownRequestTO;
import org.ourgrid.worker.request.WorkerClientIsUpRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

/**
 * Worker implementation, called by Broker to start work, 
 * The messages are given to WorkerController.
 * 
 * @see org.ourgrid.common.interfaces.control.WorkerControl
 * @see org.ourgrid.worker.communication.receiver.WorkerComponentReceiver
 */
@Req("REQ003")
public class WorkerReceiver implements Worker {
	
	private static final long serialVersionUID = 1L;

	private ServiceManager serviceManager;

	@Req("REQ079")
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/**
	 * @return the serviceManager
	 */
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	@Req("REQ079")
	/**
	 * Determines the worker must get prepared for starting its work. For that,
	 * it cannot be working, and both the playpen and the storage directories
	 * must be created. The worker warns its consumer it is ready to work.
	 * 
	 * The worker, however, will only consider this message in case the sender's
	 * public key is the same as the client's one.
	 * 
	 * This message will not be fully completed if either the playpen or the
	 * storage directories are not able to be created.
	 * 
	 * @param workerClient
	 *            the client this worker will be working for.
	 * @param requestID the request id associated to the current work session
	 * @param replicaHandle a handle representing the replica.
	 *            
	 */
	public void startWork(@MonitoredBy(WorkerConstants.WORKER) WorkerClient workerClient,
			long requestID, GridProcessHandle replicaHandle) {
		
		DeploymentID brokerID = getServiceManager().getStubDeploymentID(workerClient);
		
		StartWorkRequestTO to = new StartWorkRequestTO();
		
		to.setClientDeploymentID(brokerID.toString());
		to.setBrokerPublicKey(brokerID.getPublicKey());
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setPlaypenRoot(getPlaypenRoot(getServiceManager().getContainerContext()));
		to.setStorageRoot(getStorageRoot(getServiceManager().getContainerContext()));
		to.setSenderCerthPath(getServiceManager().getSenderCertPath());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ079")
	/**
	 * Returns the playpen root.
	 * @return the playpen root.
	 */
	private String getPlaypenRoot(ModuleContext containerContext) {
		return containerContext.getProperty(WorkerConfiguration.PROP_PLAYPEN_ROOT);
	}
	
	/**
	 * Returns the storage root.
	 * @return the storage root.
	 */
	private String getStorageRoot(ModuleContext containerContext) {
		return containerContext.getProperty(WorkerConfiguration.PROP_STORAGE_DIR);
	}
	
	@Req("REQ080")
	public void transferRequestReceived(IncomingTransferHandle handle){
		
		IncomingHandle incomingHandle = new IncomingHandle();
		
		String destinationFile = parseDestinationFile(handle.getDescription());
		
		incomingHandle.setDescription(handle.getDescription());
		incomingHandle.setLogicalFileName(handle.getLogicalFileName());
		incomingHandle.setLocalFile(new File(destinationFile));
		incomingHandle.setFileSize(handle.getFileSize());
		incomingHandle.setId(handle.getId());
		incomingHandle.setSenderContainerID(handle.getSenderID().getContainerID().toString());

		incomingHandle.setExecutable(handle.isExecutable());
		incomingHandle.setReadable(handle.isReadable());
		incomingHandle.setWritable(handle.isWritable());

		TransferRequestReceivedRequestTO to = new TransferRequestReceivedRequestTO();
		to.setHandle(incomingHandle);
		to.setConsumerPublicKey(getServiceManager().getSenderPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ080")
	public void incomingTransferFailed(IncomingTransferHandle handle,
			Exception failCause, long amountWritten) {
		
		IncomingHandle incomingHandle = new IncomingHandle();
		
		String destinationFile = parseDestinationFile(handle.getDescription());
		
		incomingHandle.setDescription(handle.getDescription());
		incomingHandle.setLogicalFileName(handle.getLogicalFileName());
		incomingHandle.setLocalFile(new File(destinationFile));
		incomingHandle.setFileSize(handle.getFileSize());
		incomingHandle.setId(handle.getId());
		incomingHandle.setSenderContainerID(handle.getSenderID().getContainerID().toString());
		
		incomingHandle.setExecutable(handle.isExecutable());
		incomingHandle.setReadable(handle.isReadable());
		incomingHandle.setWritable(handle.isWritable());
		
		IncomingTransferFailedRequestTO to = new IncomingTransferFailedRequestTO();
		to.setSenderPublicKey(handle.getSenderID().getPublicKey());
		to.setHandle(incomingHandle);		
		to.setAmountWritten(amountWritten);
		to.setFailCause(failCause);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ080")
	public void incomingTransferCompleted(IncomingTransferHandle handle, long amountWritten) {
		
		IncomingHandle incomingHandle = new IncomingHandle();
		
		String destinationFile = parseDestinationFile(handle.getDescription());
		
		incomingHandle.setDescription(handle.getDescription());
		incomingHandle.setLogicalFileName(handle.getLogicalFileName());
		incomingHandle.setLocalFile(new File(destinationFile));
		incomingHandle.setFileSize(handle.getFileSize());
		incomingHandle.setId(handle.getId());
		incomingHandle.setSenderContainerID(handle.getSenderID().getContainerID().toString());
		incomingHandle.setExecutable(handle.isExecutable());
		incomingHandle.setWritable(handle.isExecutable());
		incomingHandle.setReadable(handle.isReadable());

		String senderPublicKey = handle.getSenderID().getPublicKey();
		
		IncomingTransferCompletedRequestTO to = new IncomingTransferCompletedRequestTO();
		to.setAmountWritten(amountWritten);
		to.setHandle(incomingHandle);
		to.setSenderPublicKey(senderPublicKey);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ0123")
	public void updateTransferProgress(TransferProgress transferProgress) {
		
		UpdateTransferProgressRequestTO to = new UpdateTransferProgressRequestTO();
		to.setAmountWritten(transferProgress.getAmountWritten());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}


	@Req("REQ081")
	public void transferRejected(OutgoingTransferHandle handle) {
		
		OutgoingHandle outgoingHandle = new OutgoingHandle();
		outgoingHandle.setDescription(handle.getDescription());
		outgoingHandle.setDestinationID(handle.getDestinationID().toString());
		outgoingHandle.setLocalFile(handle.getLocalFile());
		outgoingHandle.setLogicalFileName(handle.getLogicalFileName());
		outgoingHandle.setId(handle.getId());
		
		TransferRejectedRequestTO to = new TransferRejectedRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setHandle(outgoingHandle);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ081")
	public void outgoingTransferCancelled(OutgoingTransferHandle handle, long amountUploaded) {
		
		OutgoingHandle outgoingHandle = new OutgoingHandle();
		outgoingHandle.setDescription(handle.getDescription());
		outgoingHandle.setDestinationID(handle.getDestinationID().toString());
		outgoingHandle.setLocalFile(handle.getLocalFile());
		outgoingHandle.setLogicalFileName(handle.getLogicalFileName());
		outgoingHandle.setId(handle.getId());
		
		OutgoingTransferCancelledRequestTO to = new OutgoingTransferCancelledRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setHandle(outgoingHandle);
		to.setAmountUploaded(amountUploaded);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ081")
	public void outgoingTransferFailed(OutgoingTransferHandle handle, Exception exception, long amountUploaded) {
		
		OutgoingHandle outgoingHandle = new OutgoingHandle();
		outgoingHandle.setDescription(handle.getDescription());
		outgoingHandle.setDestinationID(handle.getDestinationID().toString());
		outgoingHandle.setLocalFile(handle.getLocalFile());
		outgoingHandle.setLogicalFileName(handle.getLogicalFileName());
		outgoingHandle.setId(handle.getId());
		
		OutgoingTransferFailedRequestTO to = new OutgoingTransferFailedRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setHandle(outgoingHandle);
		to.setAmountUploaded(amountUploaded);
		to.setException(exception);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	@Req("REQ081")
	public void outgoingTransferCompleted(OutgoingTransferHandle handle, long amountUploaded) {
		
		OutgoingHandle outgoingHandle = new OutgoingHandle();
		outgoingHandle.setDescription(handle.getDescription());
		outgoingHandle.setDestinationID(handle.getDestinationID().toString());
		outgoingHandle.setLogicalFileName(handle.getLogicalFileName());
		outgoingHandle.setLocalFile(handle.getLocalFile());
		outgoingHandle.setId(handle.getId());
		
		OutgoingTransferCompletedRequestTO to = new OutgoingTransferCompletedRequestTO();
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setHandle(outgoingHandle);
		to.setAmountUploaded(amountUploaded);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void shutdown(boolean force) {
		// TODO Auto-generated method stub
	}
	
	private static String parseDestinationFile(String description) {
		return FileTransferHandlerUtils.getDestinationFile(description);
	}
	
	@RecoveryNotification
	public void workerClientIsUp(WorkerClient workerClient) {
		WorkerClientIsUpRequestTO to = new WorkerClientIsUpRequestTO();
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		
		to.setClientDeploymentID(getServiceManager().getStubDeploymentID(workerClient).toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@FailureNotification
	public void workerClientIsDown(WorkerClient workerClient) {
		WorkerClientIsDownRequestTO to = new WorkerClientIsDownRequestTO();
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void sendMessage(MessageHandle handle) {
		WorkerDAOFactory.getInstance().getWorkerMessageProcessorDAO().getMessageProcessor(
				handle.getActionName()).process(handle, getServiceManager());
	}
	
}