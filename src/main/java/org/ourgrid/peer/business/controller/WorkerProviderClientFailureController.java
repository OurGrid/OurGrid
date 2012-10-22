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
package org.ourgrid.peer.business.controller;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.common.statistics.control.JobControl;
import org.ourgrid.common.statistics.control.LoginControl;
import org.ourgrid.common.statistics.control.UserControl;
import org.ourgrid.peer.business.controller.accounting.AccountingCommitController;
import org.ourgrid.peer.business.controller.allocation.RedistributionController;
import org.ourgrid.peer.business.controller.messages.ConsumerMessages;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.business.dao.RequestDAO;
import org.ourgrid.peer.business.dao.UsersDAO;
import org.ourgrid.peer.business.util.RequestUtils;
import org.ourgrid.peer.to.AllocableWorker;
import org.ourgrid.peer.to.PeerUser;
import org.ourgrid.peer.to.Request;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * Implements Peer actions when a consumer fails.
 */
public class WorkerProviderClientFailureController {

	private static WorkerProviderClientFailureController instance = null;
	
	public static WorkerProviderClientFailureController getInstance() {
		if (instance == null) {
			instance = new WorkerProviderClientFailureController();
		}
		return instance;
	}
	
	private WorkerProviderClientFailureController() {}

	public void doNotifyRecovery(ServiceManager serviceManager, LocalWorkerProviderClient monitorable, DeploymentID monitorableID) {}

	
	/**
	 * Notifies that a Broker has failed
	 * @param monitorable The Broker that has failed.
	 * @param monitorableID The DeploymentID of the Broker that has failed.
	 */
	@Req("REQ022")
	public void doNotifyFailure(List<IResponseTO> responses, String myCertPath,
			String brokerContainerID, String brokerPublicKey, String brokerUserAtServer, String brokerAddress) {

		UsersDAO usersDAO = PeerDAOFactory.getInstance().getUsersDAO();
		
		PeerUser user = UserControl.getInstance().getUser(responses, brokerUserAtServer);
		
		if(user == null) {
			LoggerResponseTO to = new LoggerResponseTO(
					ConsumerMessages.getUnknownLocalConsumerFailureMessage(brokerContainerID), 
					LoggerResponseTO.DEBUG);
			responses.add(to);
			return;
		}
		
		if(!UserControl.getInstance().userExists(responses, brokerPublicKey)) {
			LoggerResponseTO to = new LoggerResponseTO(
					ConsumerMessages.getWrongPubKeyLocalConsumerFailureMessage(brokerContainerID, brokerPublicKey), 
					LoggerResponseTO.DEBUG);
			responses.add(to);
			return;
		}
	
		if(!usersDAO.isLoggedUser(brokerPublicKey)) {
			LoggerResponseTO to = new LoggerResponseTO(
					ConsumerMessages.getOfflineLocalConsumerFailureMessage(brokerContainerID), 
					LoggerResponseTO.DEBUG);
			responses.add(to);
			return;
		}
		
		LoggerResponseTO to = new LoggerResponseTO(
				ConsumerMessages.getLocalConsumerFailureMessage(brokerContainerID, brokerPublicKey), 
				LoggerResponseTO.INFO);
		responses.add(to);
		
		LoginControl.getInstance().localConsumerFailure(responses, user);
		
		finishUserRequests(responses, brokerContainerID, brokerPublicKey, myCertPath);
		
		usersDAO.removeLoggedUser(brokerPublicKey);
		PeerDAOFactory.getInstance().getConsumerDAO().removeLocalConsumer(brokerPublicKey);
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(brokerAddress);
		responses.add(releaseTO);
	}

	/**
	 * Finishes the requests that still running from the Broker that has failed.
	 * @param monitorablePubKey The PublicKey from the Broker that has failed.
	 * @param brokerCertPath 
	 * @param serviceManager The ServiceManager.
	 */
	public void finishUserRequests(List<IResponseTO> responses, String monitorableContainerID, 
			String monitorablePubKey, String myCertPath) {
		
		RequestDAO requestDAO = PeerDAOFactory.getInstance().getRequestDAO();
		
		for(Request request : requestDAO.getRunningRequests()) {
			
			if(request.getConsumer().getPublicKey().equals(monitorablePubKey)) {

				RequestSpecification requestSpec = request.getSpecification();
				requestDAO.removeRequest(requestSpec.getRequestId());
				AccountingCommitController.getInstance().commitAccounting(responses, requestSpec, myCertPath);
				
				RequestUtils.cancelScheduledRequest(responses, request.getSpecification().getRequestId());
				
				List<AllocableWorker> allocs = new ArrayList<AllocableWorker>(request.getAllocableWorkers()); 
				for(AllocableWorker alloc : allocs) {
					RedistributionController.getInstance().redistributeWorker(responses, alloc);
				}
				
				JobControl.getInstance().finishRequest(responses, request, true);
			}
			
		}
		
	}

}