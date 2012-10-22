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
package org.ourgrid.worker.business.controller;

import java.util.List;

import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.internal.response.UndeployServiceResponseTO;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.business.dao.ExecutionDAO;
import org.ourgrid.worker.business.dao.WorkAccountingDAO;
import org.ourgrid.worker.business.dao.WorkerDAOFactory;
import org.ourgrid.worker.business.dao.WorkerStatusDAO;
import org.ourgrid.worker.response.WorkerIsUnavailableMessageHandleResponseTO;

public class WorkerController {

	private static WorkerController instance = null;
	
	@Req("REQ079")
	public static synchronized WorkerController getInstance() {
		if (instance == null) {
			instance = new WorkerController();
		}
		return instance;
	}
	
	public void interruptWorkingAndCancelPreparingAllocation(List<IResponseTO> responses,
			boolean releaseRemotePeer) {
		cleanWorker(responses, true, releaseRemotePeer, true, true);
	}
	
	public void interruptWorking(List<IResponseTO> responses, boolean releaseRemotePeer) {
		cleanWorker(responses, true, releaseRemotePeer, true, false);
	}
	
	public void cleanWorker(List<IResponseTO> responses) {
		cleanWorker(responses, false, false, false, false);
	}
	
	public void cleanWorker(List<IResponseTO> responses, boolean releaseConsumer, 
			boolean releaseRemotePeer, boolean interruptWorking, boolean cancelPreparingAllocation) {
		
		WorkerStatusDAO workerStatusDAO = WorkerDAOFactory.getInstance().getWorkerStatusDAO();
		ExecutionDAO executionDAO = WorkerDAOFactory.getInstance().getExecutionDAO();

		if (workerStatusDAO.hasConsumer()) {
			
			if (releaseConsumer) {
				String consumerAddress = workerStatusDAO.getConsumerAddress();
				
				ReleaseResponseTO releaseResponseTO = new ReleaseResponseTO();
				releaseResponseTO.setStubAddress(consumerAddress);
				responses.add(releaseResponseTO);
				
				workerStatusDAO.setConsumerAddress(null);				
				workerStatusDAO.setConsumerDeploymentID(null);
				
				workerStatusDAO.setWorkingState(false);
			}
			
			if (workerStatusDAO.isAllocatedForRemotePeer() && !executionDAO.isExecutionFinished()) {
				finishWorkAccounting();
			}
		}
		
		if (releaseConsumer && workerStatusDAO.isAllocatedForBroker()) {
			undeployWorker(responses);
			workerStatusDAO.setConsumerPublicKey(null);
		}
		
		if (releaseRemotePeer && workerStatusDAO.isAllocatedForRemotePeer()) {
			String remotePeerDeploymentID = workerStatusDAO.getRemotePeerDeploymentID();
			
			if (remotePeerDeploymentID != null) {
				ReleaseResponseTO releaseResponse = new ReleaseResponseTO();
				releaseResponse.setStubAddress(StringUtil.deploymentIDToAddress(
						remotePeerDeploymentID));
				responses.add(releaseResponse);
			}
			
			workerStatusDAO.setCaCertificates(null);
			workerStatusDAO.setUsersDN(null);
			
			undeployRemoteWorkerManagement(responses);
			workerStatusDAO.setRemotePeerPublicKey(null);
			workerStatusDAO.setRemotePeerDN(null);
			
		}
		
		workerStatusDAO.setFileTransferErrorState(false);
		executionDAO.setExecutionFinished(false);
		ExecutionController.getInstance().cancelActiveExecution(responses,
				interruptWorking, cancelPreparingAllocation);
		FileTransferController.getInstance().cancelCurrentTransfers(responses);
		EnvironmentController.getInstance().unmountEnvironment(responses);
	}
	
	public void finishCPUWorkAccounting() {
		WorkAccountingDAO accountingDAO = WorkerDAOFactory.getInstance().getWorkAccountingDAO();
		WorkAccounting accounting = accountingDAO.getCurrentWorkAccounting();
		accounting.stopCPUTiming();
	}
	
	public void finishWorkAccounting() {
		WorkAccountingDAO accountingDAO = WorkerDAOFactory.getInstance().getWorkAccountingDAO();
		WorkAccounting accounting = accountingDAO.getCurrentWorkAccounting();
		accounting.stopCPUTiming();
		accountingDAO.addWorkAccounting(accounting);
		accountingDAO.setCurrentWorkAccounting(null);
	}
	
	private void undeployWorker(List<IResponseTO> responses) {
		UndeployServiceResponseTO undeployServiceResponseTO = new UndeployServiceResponseTO();
		undeployServiceResponseTO.setServiceName(WorkerConstants.WORKER);
		
		responses.add(undeployServiceResponseTO);
	}

	private void undeployRemoteWorkerManagement(List<IResponseTO> responses) {
		UndeployServiceResponseTO undeployServiceResponseTO = new UndeployServiceResponseTO();
		undeployServiceResponseTO.setServiceName(WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		responses.add(undeployServiceResponseTO);
	}

}
