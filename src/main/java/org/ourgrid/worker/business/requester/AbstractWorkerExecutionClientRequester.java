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
package org.ourgrid.worker.business.requester;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.IRequestTO;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.DeployServiceResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.controller.ExecutionController;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.business.messages.ControlMessages;
import org.ourgrid.worker.communication.receiver.RemoteWorkerManagementReceiver;
import org.ourgrid.worker.communication.receiver.WorkerReceiver;
import org.ourgrid.worker.response.MasterPeerStatusChangedAllocatedForBrokerResponseTO;
import org.ourgrid.worker.response.RemotePeerStatusChangedAllocatedForBrokerResponseTO;
import org.ourgrid.worker.response.StatusChangedAllocatedForPeerResponseTO;
import org.ourgrid.worker.response.StatusChangedResponseTO;


public abstract class AbstractWorkerExecutionClientRequester <U extends IRequestTO> implements RequesterIF<U> {
	

	protected void statusChanged(List<IResponseTO> responses, boolean isWorkerDeployed) {
		
		WorkerStatusDAO statusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		//only send the StatusChanged notification if it's logged
		if (statusDAO.isLogged()) {
			boolean errorState = statusDAO.isErrorState();
			
			if (!errorState && statusDAO.isAllocatedForBroker()) {
				
				statusChangeAllocatedForBroker(responses, isWorkerDeployed);
				
			} else if (!errorState && statusDAO.isAllocatedForRemotePeer()) {
				
				statusChangeAllocatedForPeer(responses);
				
			} else {
				
				StatusChangedResponseTO to = new StatusChangedResponseTO();
				to.setClientAddress(statusDAO.getMasterPeerAddress());
				to.setStatus(statusDAO.getStatus());
				
				responses.add(to);
			}
		}
		
	}
	
	private void statusChangeAllocatedForBroker(List<IResponseTO> responses, boolean isWorkerDeployed) {
		WorkerStatusDAO statusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		if (!isWorkerDeployed) {
			DeployServiceResponseTO deployServiceResponseTO = new DeployServiceResponseTO();
			deployServiceResponseTO.setServiceClass(WorkerReceiver.class);
			deployServiceResponseTO.setServiceName(WorkerConstants.WORKER);
			responses.add(deployServiceResponseTO);
		}
		
		WorkerStatus currentStatus = statusDAO.getStatus();
		
		if (!currentStatus.equals(WorkerStatus.ALLOCATED_FOR_BROKER)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					currentStatus, WorkerStatus.ALLOCATED_FOR_BROKER), LoggerResponseTO.DEBUG));
			statusDAO.setStatus(WorkerStatus.ALLOCATED_FOR_BROKER);
		}
		
		if (statusDAO.isAllocatedForRemotePeer()) {
			
			RemotePeerStatusChangedAllocatedForBrokerResponseTO to = new RemotePeerStatusChangedAllocatedForBrokerResponseTO();
			to.setRemotePeerAddress(statusDAO.getRemotePeerDeploymentID());
			
			responses.add(to);
		} else {
			MasterPeerStatusChangedAllocatedForBrokerResponseTO to = new MasterPeerStatusChangedAllocatedForBrokerResponseTO();
			to.setMasterPeerAddress(statusDAO.getMasterPeerAddress());
			to.setBrokerPublicKey(statusDAO.getConsumerPublicKey());
			
			responses.add(to);
		}
		
	}

	private void statusChangeAllocatedForPeer(List<IResponseTO> responses) {
		WorkerStatusDAO statusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		
		WorkerStatus actualStatus = statusDAO.getStatus();
		
		if (!actualStatus.equals(WorkerStatus.ALLOCATED_FOR_PEER)) {
			responses.add(new LoggerResponseTO(ControlMessages.getWorkerStatusChangedMessage(
					actualStatus, WorkerStatus.ALLOCATED_FOR_PEER), LoggerResponseTO.DEBUG));
			
			statusDAO.setStatus(WorkerStatus.ALLOCATED_FOR_PEER);
		}
		
		DeployServiceResponseTO deployRwmTO = new DeployServiceResponseTO();
		deployRwmTO.setServiceName(WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		deployRwmTO.setServiceClass(RemoteWorkerManagementReceiver.class);
		
		responses.add(deployRwmTO);
		
		StatusChangedAllocatedForPeerResponseTO statusChangeTO = new StatusChangedAllocatedForPeerResponseTO();
		statusChangeTO.setClientAddress(statusDAO.getMasterPeerAddress());
		statusChangeTO.setRemotePeerPubKey(statusDAO.getRemotePeerPublicKey());
		
		responses.add(statusChangeTO);
	}

	protected void setExecutionAsFinished(boolean success) {
		ExecutionController.getInstance().executionFinish(success);
	}

	protected String getWorkerClientAddress() {
		return getWorkerStatusDAO().getConsumerAddress();
	}
	
	protected WorkerStatusDAO getWorkerStatusDAO() {
		return WorkerDAOFactory.getInstance().getWorkerStatusDAO();
	}
}
