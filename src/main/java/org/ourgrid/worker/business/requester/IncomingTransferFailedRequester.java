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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.to.IncomingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.FileTransferController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.IncomingTransferFailedRequestTO;

public class IncomingTransferFailedRequester implements RequesterIF<IncomingTransferFailedRequestTO> {

	public List<IResponseTO> execute(IncomingTransferFailedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		IncomingHandle handle = request.getHandle();
		
		String senderPublicKey = request.getSenderPublicKey();

		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		
		if (workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerWithErrorReceivesAnIncomingTransferFailedMessage(handle.getId(),
							request.getAmountWritten(), senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (!workerStatusDAO.isWorkingState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateIncomingTrasferFailedMessage(senderPublicKey),
					LoggerResponseTO.WARN));
		    
			return responses;
		}
		
		if (workerStatusDAO.isWorkingState() && WorkerDAOFactory.getInstance().
							getFileTransferDAO().getIncomingFileHandles().isEmpty()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(senderPublicKey),
					LoggerResponseTO.WARN));
		    
			return responses;
		}
		
		if (!workerStatusDAO.getConsumerPublicKey().equals(senderPublicKey)) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getUnknownClientSendsIncomingTransferFailedMessage(senderPublicKey),
					LoggerResponseTO.WARN));
		    
			return responses;
		}

		File incomingFileFailed = WorkerDAOFactory.getInstance().
				getFileTransferDAO().getIncomingFile(handle);
		
		if (incomingFileFailed == null
				) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getIncomingTranferFailedWithUnknownHandleMessage(handle.getId(),
							senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		
		FileTransferController.getInstance().incomingTransferFailed(handle, 
				request.getFailCause(), request.getAmountWritten(), responses);
		
		return responses;
	}
}
