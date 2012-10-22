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

import org.ourgrid.common.interfaces.to.GridProcessErrorTypes;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.business.controller.GridProcessError;
import org.ourgrid.worker.business.dao.ExecutionDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ExecutionControllerMessages;
import org.ourgrid.worker.communication.dao.FutureDAO;
import org.ourgrid.worker.request.ExecutionErrorRequestTO;
import org.ourgrid.worker.response.ErrorOcurredMessageHandleResponseTO;

public class ExecutionErrorRequester extends AbstractWorkerExecutionClientRequester<ExecutionErrorRequestTO> {

	public List<IResponseTO> execute(ExecutionErrorRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		FutureDAO futureDAO = WorkerDAOFactory.getInstance().getFutureDAO();
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();
		WorkerStatusDAO workerStatus = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (isExecutionOrExecuteState(futureDAO, executionDAO)) {
			responses.add(new LoggerResponseTO(
					ExecutionControllerMessages.getCanNotOcurredExecutionErrorMessage(),
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		if (getWorkerClientAddress() != null) {
			
			ErrorOcurredMessageHandleResponseTO to = 
					new ErrorOcurredMessageHandleResponseTO(new GridProcessError
							(request.getError(), GridProcessErrorTypes.EXECUTION_ERROR),
							getWorkerStatusDAO().getConsumerAddress());
			to.setClientAddress(getWorkerClientAddress());
			
			responses.add(to);
			workerStatus.setStatus(WorkerStatus.ERROR);
			
			setExecutionAsFinished(false);
			workerStatus.setConsumerAddress(null);
			workerStatus.setConsumerPublicKey(null);
		}
		
		return responses;
	}

	private boolean isExecutionOrExecuteState(FutureDAO futureDAO,
			ExecutionDAO executionDAO) {
		return (!(executionDAO.getCurrentHandle() == null && futureDAO.getExecutionActionFuture() != null)
				&& executionDAO.getCurrentHandle() == null) || executionDAO.isExecutionFinished();
	}

}
