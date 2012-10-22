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
package org.ourgrid.peer.communication.receiver;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.peer.request.NotifyRemoteWorkerManagementFailureRequestTO;
import org.ourgrid.peer.request.NotifyRemoteWorkerManagementRecoveryRequestTO;
import org.ourgrid.peer.request.RemoteStatusChangedAllocatedForBrokerRequestTO;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RemoteWorkerManagementClientReceiver implements RemoteWorkerManagementClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	@Req("REQ112")
	public void statusChangedAllocatedForBroker(ServiceID workerServiceID) {
		
		RemoteStatusChangedAllocatedForBrokerRequestTO to = new RemoteStatusChangedAllocatedForBrokerRequestTO();
		to.setWmPublicKey(serviceManager.getSenderPublicKey());
		to.setWorkerAddress(workerServiceID == null ? null : workerServiceID.toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void doNotifyRecovery(RemoteWorkerManagement failedWorker, DeploymentID remoteWorkerID) {
		NotifyRemoteWorkerManagementRecoveryRequestTO to = new NotifyRemoteWorkerManagementRecoveryRequestTO();
		to.setRemoteWorkerAddress(remoteWorkerID.getServiceID().toString());
		to.setRemoteWorkerPublicKey(remoteWorkerID.getPublicKey());
		to.setMyUserAtServer(serviceManager.getMyDeploymentID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@FailureNotification
	public void doNotifyFailure(RemoteWorkerManagement failedWorker, DeploymentID remoteWorkerID) {
		NotifyRemoteWorkerManagementFailureRequestTO to = new NotifyRemoteWorkerManagementFailureRequestTO();
		to.setRemoteWorkerAddress(remoteWorkerID.getServiceID().toString());
		to.setRemoteWorkerPublicKey(remoteWorkerID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}