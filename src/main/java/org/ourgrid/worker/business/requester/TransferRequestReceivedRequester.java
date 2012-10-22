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
package org.ourgrid.worker.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.util.FileTransferHandlerUtils;
import org.ourgrid.worker.business.controller.FileTransferController;
import org.ourgrid.worker.business.dao.ExecutionDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.TransferRequestReceivedRequestTO;

public class TransferRequestReceivedRequester implements RequesterIF<TransferRequestReceivedRequestTO> {

	public List<IResponseTO> execute(TransferRequestReceivedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		IncomingHandle handle = request.getHandle();
		String destinationFile = parseDestinationFile(handle.getDescription());
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
		String consumerAddress = workerStatusDAO.getConsumerAddress();
		String senderPublicKey = request.getConsumerPublicKey();
		
		if(!workerStatusDAO.isWorkingState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientTriesToTransferFileOnUnstartedWorkerMessage(senderPublicKey),
					LoggerResponseTO.WARN));
			FileTransferController.getInstance().rejectTransferRequest(handle, responses);
			
			return responses;
		}
		
		if (consumerAddress == null) {
			FileTransferController.getInstance().rejectTransferRequest(handle, responses);
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getUnknownClientRequestsToTransferFileMessage(destinationFile,
							handle.getFileSize(), handle.getId(), handle.getSenderContainerID().
							toString()), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		String consumerContainerID = getContainerID(consumerAddress);
		
		if (!consumerContainerID.equals(handle.getSenderContainerID())) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getUnknownClientRequestsToTransferFileMessage(destinationFile,
							handle.getFileSize(), handle.getId(), handle.getSenderContainerID().
							toString()), LoggerResponseTO.WARN));
			FileTransferController.getInstance().rejectTransferRequest(handle, responses);
			
			return responses;
		}
			
		
		if(workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientRequestsToTransferFileOnWorkerWithErrorMessage(destinationFile,
							handle.getFileSize(), handle.getId(),
							senderPublicKey), LoggerResponseTO.WARN));

			return responses;
		}
		
		if (!executionDAO.isExecutionFinished() && executionDAO.getCurrentHandle() != null) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientTriedToTransferFileOnExecutionWorkerMessage(senderPublicKey), 
					LoggerResponseTO.WARN));
			
			FileTransferController.getInstance().rejectTransferRequest(handle, responses);
			return responses;
		} 
		
		if (executionDAO.isExecutionFinished()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getClientTriedToTransferFileOnExecutionFinishedWorkerMessage(
							senderPublicKey), LoggerResponseTO.WARN));
			
			FileTransferController.getInstance().rejectTransferRequest(handle, responses);
			return responses;
		}
		
		handle.setSenderPublicKey(workerStatusDAO.getConsumerPublicKey());
		
		FileTransferController.getInstance().acceptTransferRequest(
				handle, destinationFile, responses);
		
		return responses;
	}
	
	private static String parseDestinationFile(String description) {
		return FileTransferHandlerUtils.getDestinationFile(description);
	}
	
	private String getContainerID( String serviceID ) {
		String[ ] split = serviceID.split( "/" );
		return split[0] + "/" +split[1];
	}
}
