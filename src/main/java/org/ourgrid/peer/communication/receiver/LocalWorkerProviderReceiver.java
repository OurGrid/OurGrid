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

import org.ourgrid.broker.status.JobStatusInfo;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.WorkerProviderClientFailureController;
import org.ourgrid.peer.request.DisposeWorkerRequestTO;
import org.ourgrid.peer.request.FinishRequestRequestTO;
import org.ourgrid.peer.request.HereIsJobStatsRequestTO;
import org.ourgrid.peer.request.LoginRequestTO;
import org.ourgrid.peer.request.NotifyLocalWorkerProviderClientFailureRequestTO;
import org.ourgrid.peer.request.PauseRequestRequestTO;
import org.ourgrid.peer.request.ReportReplicaAccountingRequestTO;
import org.ourgrid.peer.request.RequestWorkersRequestTO;
import org.ourgrid.peer.request.ResumeRequestRequestTO;
import org.ourgrid.peer.request.UnwantedWorkerRequestTO;
import org.ourgrid.peer.request.UpdateRequestRequestTO;
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
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;

/**
 * Performs Local Worker Provider Receiver actions
 */
public class LocalWorkerProviderReceiver implements LocalWorkerProvider {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	
	/**
	 * Processes the login request of a Broker client.
	 * Returns to the client the result of the login operation, and in case of successful login
	 * the stub to the Accounting object.
	 * @param workerProviderClient The client callback
	 * @param brokerPublicKey The PublicKey Object of the Broker requesting login
	 */
	@Req("REQ108")
	public void login(
			@MonitoredBy(PeerConstants.LOCAL_WORKER_PROVIDER)
			LocalWorkerProviderClient workerProviderClient) {
		
		LoginRequestTO to = new LoginRequestTO();
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		
		DeploymentID workerProviderClientID = serviceManager.getStubDeploymentID(workerProviderClient);
		to.setWorkerProviderClientAddress(workerProviderClientID.getServiceID().toString());
		to.setUserName(workerProviderClientID.getUserName());
		to.setServerName(workerProviderClientID.getServerName());
		
		ModuleContext containerContext = serviceManager.getContainerContext();
		
		to.setMyUserAtServer(getUserAtServer(containerContext));
		to.setFilePath(containerContext.getProperty(PeerConfiguration.PROP_RANKINGFILE));
		to.setDescription(containerContext.getProperty(PeerConfiguration.PROP_DESCRIPTION));
		to.setEmail(containerContext.getProperty(PeerConfiguration.PROP_EMAIL));
		to.setLabel(containerContext.getProperty(PeerConfiguration.PROP_LABEL));
		to.setLatitude(containerContext.getProperty(PeerConfiguration.PROP_LATITUDE));
		to.setLongitude(containerContext.getProperty(PeerConfiguration.PROP_LONGITUDE));
		to.setMyCertSubjectDN(CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath()));
		
		to.setOnDemandPeer(serviceManager.getContainerContext().isEnabled(
				PeerConfiguration.PROP_ONDEMAND_PEER));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
		
	}

	@RecoveryNotification
	public void doNotifyRecovery(LocalWorkerProviderClient monitorable, DeploymentID monitorableID) {
		WorkerProviderClientFailureController.getInstance().doNotifyRecovery(serviceManager, monitorable, monitorableID);
	}

	
	/**
	 * Notifies that a Broker has failed
	 * @param monitorable The Broker that has failed.
	 * @param monitorableID The DeploymentID of the Broker that has failed.
	 */
	@FailureNotification
	public void doNotifyFailure(LocalWorkerProviderClient monitorable, DeploymentID monitorableID) {
		NotifyLocalWorkerProviderClientFailureRequestTO to = new NotifyLocalWorkerProviderClientFailureRequestTO();
		
		String myCertPathDN = CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath());
		String brokerContainerID = monitorableID.getContainerID().toString();
		String brokerUserAtServer = monitorableID.getContainerID().getUserAtServer();
		String brokerPublicKey = monitorableID.getPublicKey();
		String brokerAddress = monitorableID.getServiceID().toString();
		
		to.setMyCertPathDN(myCertPathDN);
		to.setBrokerContainerID(brokerContainerID);
		to.setBrokerUserAtServer(brokerUserAtServer);
		to.setBrokerPublicKey(brokerPublicKey);
		to.setBrokerAddress(brokerAddress);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	

	@Req("REQ011")
	public void requestWorkers(RequestSpecification requestSpec) {
		requestWorkers(requestSpec, serviceManager.getSenderPublicKey());
	}
	
	public void requestWorkers(RequestSpecification requestSpec, String publicKey) {
		RequestWorkersRequestTO request = new RequestWorkersRequestTO();
		request.setRequestSpec(requestSpec);
		request.setBrokerPublicKey(publicKey);
		request.setMyPublicKey(serviceManager.getMyPublicKey());
		request.setMyCertPath(serviceManager.getMyCertPath());
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}
	
	@Req("REQ116")
	public void updateRequest(RequestSpecification requestSpec) {
		
		UpdateRequestRequestTO to = new UpdateRequestRequestTO();
		to.setRequestSpec(requestSpec);
		to.setBrokerPublicKey(serviceManager.getSenderPublicKey());
		to.setMyCertPathDN(CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
		
	}


	@Req("REQ117")
	public void pauseRequest(long requestID) {
		PauseRequestRequestTO to = new PauseRequestRequestTO();
		
		String brokerPublicKey = serviceManager.getSenderPublicKey();
		to.setBrokerPublicKey(brokerPublicKey);
		to.setRequestId(requestID);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
		
	}

	@Req("REQ118")
	public void resumeRequest(long requestID) {
		
		ResumeRequestRequestTO to = new ResumeRequestRequestTO();
		
		String brokerPublicKey = serviceManager.getSenderPublicKey();
		to.setBrokerPublicKey(brokerPublicKey);
		to.setRequestId(requestID);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@Req("REQ014")
	public void finishRequest(RequestSpecification requestSpec) {
		
		FinishRequestRequestTO to = new FinishRequestRequestTO();
		
		String brokerPublicKey = serviceManager.getSenderPublicKey();
		to.setBrokerPublicKey(brokerPublicKey);
		to.setRequestSpec(requestSpec);
		to.setMyCertPathDN(CertificationUtils.getCertSubjectDN(serviceManager.getMyCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@Req("REQ015")
	public void disposeWorker(ServiceID workerServiceID) {
		
		String brokerPublicKey = serviceManager.getSenderPublicKey();
		
		DisposeWorkerRequestTO to = new DisposeWorkerRequestTO();
		to.setBrokerPublicKey(brokerPublicKey);
		
		if (workerServiceID != null) {
			to.setWorkerAddress(workerServiceID.toString());
			to.setWorkerUserAtServer(workerServiceID.getContainerID().getUserAtServer());
			to.setWorkerPublicKey(workerServiceID.getPublicKey());
		}
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@Req("REQ016")
	public void unwantedWorker(ServiceID workerID, RequestSpecification requestSpec) {
		
		UnwantedWorkerRequestTO to = new UnwantedWorkerRequestTO();
		to.setRequestSpec(requestSpec);
		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		
		if (workerID != null) {
			to.setWorkerAddress(workerID.toString());
			to.setWorkerPublicKey(workerID.getPublicKey());
		}
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	private String getUserAtServer(ModuleContext context) {
		String address = 
			context.getProperty(XMPPProperties.PROP_USERNAME) + "@" + 
			context.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
		return address;
	}


	public void hereIsJobStats(JobStatusInfo jobStatusInfo) {
		HereIsJobStatsRequestTO to = new HereIsJobStatsRequestTO();
		to.setJobStatusInfo(jobStatusInfo);
		to.setMyId(serviceManager.getMyDeploymentID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	public void reportReplicaAccounting(GridProcessAccounting replicaAccounting) {
		ReportReplicaAccountingRequestTO to = new ReportReplicaAccountingRequestTO();
		to.setUserPublicKey(serviceManager.getSenderPublicKey());
		to.setAccounting(replicaAccounting);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
}