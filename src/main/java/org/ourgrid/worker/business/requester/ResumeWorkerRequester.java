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

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.OperationSucceedResponseTO;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.request.ResumeWorkerRequestTO;

public class ResumeWorkerRequester implements RequesterIF<ResumeWorkerRequestTO> {

	public List<IResponseTO> execute(ResumeWorkerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();

		if (!request.canComponentBeUsed()) {
			OperationSucceedResponseTO to = new OperationSucceedResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setErrorCause(request.getErrorCause());
			responses.add(to);
			return responses;
		}
		
		if (!request.isThisMyPublicKey()) {
			LoggerResponseTO loggerResponseTO = new LoggerResponseTO();
			loggerResponseTO.setMessage(ControlMessages.
					getUnknownEntityTryingToResumeWorkerMessage(request.getSenderPublicKey()));
			loggerResponseTO.setType(LoggerResponseTO.WARN);

			responses.add(loggerResponseTO);
			return responses;
		}
		
		if (WorkerDAOFactory.getInstance().getWorkerStatusDAO().getStatus().
				equals(WorkerStatus.OWNER)) {
			responses.add(new LoggerResponseTO(ControlMessages.
					getOwnerWorkerResumedMessage(), LoggerResponseTO.INFO));
			responses.add(new LoggerResponseTO(ControlMessages.
					getWorkerStatusChangedMessage(WorkerStatus.OWNER, WorkerStatus.IDLE), 
					LoggerResponseTO.DEBUG));

			WorkerDAOFactory.getInstance().getWorkerStatusDAO().setStatus(WorkerStatus.IDLE);
			
			ExecutionController.getInstance().beginAllocation(responses);
		}
		
		OperationSucceedResponseTO operationSuccededResponseTO = new OperationSucceedResponseTO();
		operationSuccededResponseTO.setClientAddress(request.getClientAddress());
		operationSuccededResponseTO.setRemoteClient(request.isRemoteClient());
		responses.add(operationSuccededResponseTO);
		
		return responses;
	}
}
