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

import org.ourgrid.common.interfaces.to.OutgoingHandle;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.FileTransferController;
import org.ourgrid.worker.business.dao.FileTransferDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.request.TransferRejectedRequestTO;

public class TransferRejectedRequester implements RequesterIF<TransferRejectedRequestTO> {

	public List<IResponseTO> execute(TransferRejectedRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		OutgoingHandle handle = request.getHandle();
		
		File fileRejected = WorkerDAOFactory.getInstance().
				getFileTransferDAO().getUploadingFile(handle);
		String senderPublicKey = request.getSenderPublicKey();
		
		FileTransferDAO fileTransferDAO = WorkerDAOFactory.getInstance().getFileTransferDAO(); 
	
		if(!workerStatusDAO.isWorkingState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerIsNotInWorkingStateTrasferRejectedMessage(
							senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if(workerStatusDAO.isWorkingState() && fileTransferDAO.
				getUploadingFileHandles().isEmpty()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerDoesNotRequestedAnyTransferMessage(senderPublicKey),
													LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if(fileRejected == null) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerReceivesAFileTransferRejectedWithUnknownHandleMessage(
							handle.getId(), senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		
		String filePath = fileRejected.getAbsolutePath();
		
		if(workerStatusDAO.isFileTransferErrorState()) {
			responses.add(new LoggerResponseTO(WorkerControllerMessages.
					getWorkerWithErrorReceivesAFileTransferRejectedMessage(
							filePath, handle.getId(), senderPublicKey), LoggerResponseTO.WARN));
			
			return responses;
		}
		
		FileTransferController.getInstance().fileRejected(handle, responses);
		
		return responses;
	}
}
