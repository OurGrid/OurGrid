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

import java.util.List;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkAccounting;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecificationConstants;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.request.NotifyWorkerManagementFailureRequestTO;
import org.ourgrid.peer.request.NotifyWorkerManagementRecoveryRequestTO;
import org.ourgrid.peer.request.ReportWorkAccountingRequestTO;
import org.ourgrid.peer.request.SaveRankingRequestTO;
import org.ourgrid.peer.request.StatusChangedAllocatedForBrokerRequestTO;
import org.ourgrid.peer.request.StatusChangedAllocatedForPeerRequestTO;
import org.ourgrid.peer.request.StatusChangedRequestTO;
import org.ourgrid.peer.request.UpdateWorkerSpecRequestTO;
import org.ourgrid.peer.request.WorkerLoginRequestTO;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

/**
 * Performs Worker Management Client Receiver actions
 */
public class WorkerManagementClientReceiver implements WorkerManagementClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void workerLogin(
			@MonitoredBy(PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME) WorkerManagement workerManagement,
			WorkerSpecification workerSpecification) {
		
		WorkerLoginRequestTO to = new WorkerLoginRequestTO();
		ModuleContext containerContext = serviceManager.getContainerContext();
		
		to.setMyPublicKey(serviceManager.getMyPublicKey());
		to.setWorkerCertPath(serviceManager.getSenderCertPath());
		to.setWorkerAddress(serviceManager.getSenderServiceID().toString());
		to.setWorkerPublicKey(serviceManager.getSenderPublicKey());
		
		workerSpecification.putAttribute(WorkerSpecificationConstants.SITE_NAME, 
				containerContext.getProperty(PeerConfiguration.PROP_LABEL));
		workerSpecification.putAttribute(WorkerSpecificationConstants.SITE_DESCRIPTION, 
				containerContext.getProperty(PeerConfiguration.PROP_DESCRIPTION));

		to.setWorkerSpecification(workerSpecification);
		String serverName = serviceManager.getMyDeploymentID().getServerName();
		String userName = serviceManager.getMyDeploymentID().getUserName();
		to.setMyUserAtServer(userName + "@" + serverName);
		to.setVoluntary(serviceManager.getContainerContext().isEnabled(PeerConfiguration.PROP_VOLUNTARY_PEER));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	
	/**
	 * Processes the change of status of a Worker
	 * @param worker The Worker that changed status
	 * @param workerPublicKey The PublicKey of the Worker that changed Status
	 * @param status The new Status of the Worker
	 */
	@Req("REQ025")
	public void statusChangedAllocatedForBroker(ServiceID workerServiceID, String brokerPublicKey) {
		
		StatusChangedAllocatedForBrokerRequestTO to = new StatusChangedAllocatedForBrokerRequestTO();
		
		ServiceID senderServiceID = serviceManager.getSenderServiceID();
			
		if (workerServiceID != null) {
			to.setWorkerAddress(workerServiceID.toString());
		}
		
		to.setSenderUserAtServer(senderServiceID.getContainerID().getUserAtServer());
		to.setSenderPublicKey(senderServiceID.getPublicKey());
		
		if (brokerPublicKey != null) {
			to.setBrokerPublicKey(brokerPublicKey);
		}
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	/**
	 * Processes the change of status of a Worker
	 * @param worker The Worker that changed status
	 * @param workerPublicKey The PublicKey of the Worker that changed Status
	 */
	@Req("REQ025")
	public void statusChangedAllocatedForPeer(ServiceID remoteWorkerManagementServiceID, String peerPublicKey) {
		StatusChangedAllocatedForPeerRequestTO to = new StatusChangedAllocatedForPeerRequestTO();
		to.setWorkerPublicKey(serviceManager.getSenderPublicKey());
		to.setWorkerUserAtServer(serviceManager.getSenderServiceID().getContainerID().getUserAtServer());
		to.setRemoteWorkerManagementAddress(remoteWorkerManagementServiceID == null ? null : remoteWorkerManagementServiceID.toString());
		to.setPeerPublicKey(peerPublicKey);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	/**
	 * Processes the change of status of a Worker
	 * @param worker The Worker that changed status
	 * @param workerPublicKey The PublicKey of the Worker that changed Status
	 * @param status The new Status of the Worker
	 */
	@Req("REQ025")
	public void statusChanged(WorkerStatus status) {
		StatusChangedRequestTO to = new StatusChangedRequestTO();
		to.setStatus(status);
		to.setWorkerPublicKey(serviceManager.getSenderPublicKey());
		ServiceID senderServiceID = serviceManager.getSenderServiceID();
		
		to.setWorkerUserAtServer(senderServiceID.getContainerID().getUserAtServer());
		
		DeploymentID localWorkerProviderID = serviceManager.getObjectDeployment(
				PeerConstants.LOCAL_WORKER_PROVIDER).getDeploymentID();
		
		to.setLocalWorkerProviderAddress(localWorkerProviderID.getServiceID().toString());
		to.setMyCertPathDN(CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@FailureNotification
	public void doNotifyFailure(WorkerManagement failedWorker, DeploymentID failedWorkerID) {
		NotifyWorkerManagementFailureRequestTO to = new NotifyWorkerManagementFailureRequestTO();
		to.setFailedWorkerAddress(failedWorkerID.getServiceID().toString());
		to.setFailedWorkerPublicKey(failedWorkerID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void doNotifyRecovery(WorkerManagement recoveredWorkerStub, DeploymentID recoveredWorkerID) {
		NotifyWorkerManagementRecoveryRequestTO to = new NotifyWorkerManagementRecoveryRequestTO();
		to.setRecoveredWorkerAddress(recoveredWorkerID.getServiceID().toString());
		to.setRecoveredWorkerPublicKey(recoveredWorkerID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void reportWorkAccounting(List<WorkAccounting> consumersBalances) {
		ReportWorkAccountingRequestTO to = new ReportWorkAccountingRequestTO();
		
		String workerPublicKey = serviceManager.getSenderPublicKey();
		ServiceID workerAddress = serviceManager.getSenderServiceID();
		
		to.setWorkerPublicKey(workerPublicKey);
		to.setWorkerAddress(workerAddress.toString());
		to.setAccountings(consumersBalances);
		to.setWorkerUserAtServer(workerAddress.getContainerID().getUserAtServer());
		to.setMyPublicKey(serviceManager.getMyPublicKey());
		to.setMyCertSubjectDN(CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
		
	}

	public void updateWorkerSpec(WorkerSpecification workerSpec) {
		UpdateWorkerSpecRequestTO to = new UpdateWorkerSpecRequestTO();
		to.setWorkerSpec(workerSpec);
		to.setWorkerPublicKey(serviceManager.getSenderPublicKey());
		to.setWorkerUserAtServer(serviceManager.getSenderServiceID().getContainerID().getUserAtServer());
		to.setMyUserAtServer(serviceManager.getMyDeploymentID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void saveRanking() {
		SaveRankingRequestTO to = new SaveRankingRequestTO();
		
		String senderPublicKey = serviceManager.getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(serviceManager.isThisMyPublicKey(senderPublicKey));
		to.setRankingFilePath(serviceManager.getContainerContext().getProperty(PeerConfiguration.PROP_RANKINGFILE));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

}