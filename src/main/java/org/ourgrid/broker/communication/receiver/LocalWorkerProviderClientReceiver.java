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

import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.broker.request.HereIsWorkerRequestTO;
import org.ourgrid.broker.request.LoginSucceedRequestTO;
import org.ourgrid.broker.request.PreemptedWorkerRequestTO;
import org.ourgrid.broker.request.WorkerDoNotifyFailureRequestTO;
import org.ourgrid.broker.request.WorkerDoNotifyRecoveryRequestTO;
import org.ourgrid.common.BrokerLoginResult;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.Worker;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 */
public class LocalWorkerProviderClientReceiver implements LocalWorkerProviderClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * 
	 * @param workerProvider
	 * @param result
	 * @param peerAccounting
	 * @param string 
	 */
	@Req("REQ311")
	public void loginSucceed(
			@MonitoredBy(BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT)LocalWorkerProvider workerProvider,
			BrokerLoginResult result) {
		
		LoginSucceedRequestTO to = new LoginSucceedRequestTO();
		
		DeploymentID peerDeploymentID = serviceManager.getStubDeploymentID(workerProvider);
		
		to.setPeerAddress(peerDeploymentID.getServiceID().toString());
		to.setPeerDeploymentID(peerDeploymentID.toString());
		to.setPeerPublicKey(peerDeploymentID.getPublicKey());
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		to.setResult(result);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@FailureNotification
	public void doNotifyFailure(LocalWorkerProvider monitorable, DeploymentID failedLocalWorkerProviderID) {
		LocalWorkerProviderNotificationReceiver.getInstance().doNotifyFailure(
				serviceManager, monitorable, failedLocalWorkerProviderID);
	}
	
	@RecoveryNotification
	public void doNotifyRecovery(LocalWorkerProvider monitorableStub, DeploymentID recoveredLocalWorkerProviderID) {
		LocalWorkerProviderNotificationReceiver.getInstance().doNotifyRecovery(
				serviceManager, monitorableStub, recoveredLocalWorkerProviderID);
	}
	
	
	@Req("REQ312")
	public void hereIsWorker(
			ServiceID workerServiceID, 
			WorkerSpecification workerSpec, RequestSpecification requestSpec) {
		
		HereIsWorkerRequestTO to = new HereIsWorkerRequestTO();
		to.setRequestSpec(requestSpec);
		
		ServiceID senderServiceID = serviceManager.getSenderServiceID();
		ServiceID lwpcServiceID = new ServiceID(senderServiceID.getContainerID(), PeerConstants.LOCAL_WORKER_PROVIDER);
		
		to.setPeerAddress(lwpcServiceID.toString());
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		
		DeploymentID workerDeploymentID = new DeploymentID(workerServiceID);
		
		to.setWorkerAddress(workerServiceID.toString());
		to.setWorkerID(workerDeploymentID.toString());
		to.setWorkerPublicKey(workerServiceID.getPublicKey());
		to.setWorkerSpec(workerSpec);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@FailureNotification
	public void doNotifyFailure(Worker monitorable, DeploymentID failedWorkerID) {
		WorkerDoNotifyFailureRequestTO to = new WorkerDoNotifyFailureRequestTO();
		
		ServiceID senderServiceID = failedWorkerID.getServiceID();
		
		to.setWorkerContainerID(senderServiceID.getContainerID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void doNotifyRecovery(Worker monitorable, DeploymentID recoveredWorkerID) {
		WorkerDoNotifyRecoveryRequestTO to = new WorkerDoNotifyRecoveryRequestTO();
		
		ServiceID senderServiceID = recoveredWorkerID.getServiceID();
		
		to.setWorkerDeploymentID(recoveredWorkerID.toString());
		to.setWorkerPublicKey(senderServiceID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@Override
	public void preemptedWorker(ServiceID workerServiceID) {
		PreemptedWorkerRequestTO to = new PreemptedWorkerRequestTO();
		to.setWorkerContainerID(workerServiceID.getContainerID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

}
