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
package org.ourgrid.broker.communication.receiver;

import org.ourgrid.broker.business.scheduler.extensions.GenericTransferProgress;
import org.ourgrid.broker.request.IncomingTransferCompletedRequestTO;
import org.ourgrid.broker.request.IncomingTransferFailedRequestTO;
import org.ourgrid.broker.request.OutgoingTransferCancelledRequestTO;
import org.ourgrid.broker.request.OutgoingTransferCompletedRequestTO;
import org.ourgrid.broker.request.OutgoingTransferFailedRequestTO;
import org.ourgrid.broker.request.TransferRejectedRequestTO;
import org.ourgrid.broker.request.TransferRequestReceivedRequestTO;
import org.ourgrid.broker.request.UpdateTransferProgressRequestTO;
import org.ourgrid.broker.request.WCRSendMessageRequestTO;
import org.ourgrid.common.CommonConstants;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.interfaces.to.GenericTransferHandle;
import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.interfaces.to.MessageHandle;
import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.OurGridRequestControl;

import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.processor.filetransfer.IncomingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.OutgoingTransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferHandle;
import br.edu.ufcg.lsd.commune.processor.filetransfer.TransferProgress;

/**
 */
public class WorkerClientReceiver implements WorkerClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void sendMessage(MessageHandle handle) {
		WCRSendMessageRequestTO to = new WCRSendMessageRequestTO();
		to.setHandle(handle);
		ServiceID senderServiceID = serviceManager.getSenderServiceID();
		to.setSenderAddress(senderServiceID.toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void outgoingTransferCancelled(OutgoingTransferHandle handle, long amountWritten) {
		OutgoingTransferCancelledRequestTO to = new OutgoingTransferCancelledRequestTO();
		to.setAmountWritten(amountWritten);
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void outgoingTransferCompleted(OutgoingTransferHandle handle, long amountWritten) {
		OutgoingTransferCompletedRequestTO to = new OutgoingTransferCompletedRequestTO();
		to.setAmountWritten(amountWritten);
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void outgoingTransferFailed(OutgoingTransferHandle handle, Exception failCause, long amountWritten) {
		OutgoingTransferFailedRequestTO to = new OutgoingTransferFailedRequestTO();
		to.setAmountWritten(amountWritten);
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		to.setFailCauseMessage(failCause.getMessage());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void incomingTransferCompleted(IncomingTransferHandle handle, long amountWritten) {
		IncomingTransferCompletedRequestTO to = new IncomingTransferCompletedRequestTO();
		to.setAmountWritten(amountWritten);
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void incomingTransferFailed(IncomingTransferHandle handle, Exception failCause, long amountWritten) {
		IncomingTransferFailedRequestTO to = new IncomingTransferFailedRequestTO();
		to.setAmountWritten(amountWritten);
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		to.setFailCauseMessage(failCause.getMessage());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void transferRejected(OutgoingTransferHandle handle) {
		TransferRejectedRequestTO to = new TransferRejectedRequestTO();
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void updateTransferProgress(TransferProgress transferProgress) {
		UpdateTransferProgressRequestTO to = new UpdateTransferProgressRequestTO();
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setTransferProgress(getTransferProgress(transferProgress));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void transferRequestReceived(IncomingTransferHandle handle) {
		TransferRequestReceivedRequestTO to = new TransferRequestReceivedRequestTO();
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setHandle(getHandle(handle));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	private OutgoingHandle getHandle(OutgoingTransferHandle handle) {
		return new OutgoingHandle(handle.getId(), handle.getLogicalFileName(), 
				handle.getLocalFile(), handle.getDescription(), handle.getDestinationID().toString());
	}
	
	private IncomingHandle getHandle(IncomingTransferHandle handle) {
		IncomingHandle incomingHandle = new IncomingHandle(handle.getId(), handle.getLogicalFileName(), 
				handle.getFileSize(), CommonConstants.GET_TRANSFER, handle.getOppositeID().toString());
		
		incomingHandle.setExecutable(handle.isExecutable());
		incomingHandle.setReadable(handle.isReadable());
		incomingHandle.setWritable(handle.isWritable());
		return incomingHandle;
	}
	
	private GenericTransferProgress getTransferProgress(TransferProgress progress) {
		
		TransferHandle handle = progress.getHandle();
		
		GenericTransferHandle genericHandle = new GenericTransferHandle(handle.getId(), handle.getLogicalFileName(),
				handle.getLocalFile(), handle.getDescription());
		
		return new GenericTransferProgress(genericHandle, progress.getFileName(), progress.getFileSize(),
				progress.getNewStatus().toString(), progress.getAmountWritten(), progress.getProgress(),
				progress.getTransferRate(), progress.isOutgoing());
	}
}
