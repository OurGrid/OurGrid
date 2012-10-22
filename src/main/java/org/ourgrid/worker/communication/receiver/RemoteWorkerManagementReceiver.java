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
package org.ourgrid.worker.communication.receiver;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.request.RemoteWorkForBrokerRequestTO;
import org.ourgrid.worker.request.RemoteWorkerManagementClientDoNotifyFailureRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

/**
 * Performs Remote Worker Management actions
 */
@Req("REQ006")
public class RemoteWorkerManagementReceiver implements RemoteWorkerManagement {
	
	private static final long serialVersionUID = 1L;
	private ServiceManager serviceManager;

	@Req("REQ006")
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/**
	 * @return the serviceManager
	 */
	protected ServiceManager getServiceManager() {
		return serviceManager;
	}
	
	@Req("REQ121")
	public void workForBroker(
			@MonitoredBy(WorkerConstants.REMOTE_WORKER_MANAGEMENT)RemoteWorkerManagementClient remotePeer,  String brokerPubKey) {
		
		RemoteWorkForBrokerRequestTO to = new RemoteWorkForBrokerRequestTO();
		to.setConsumerPublicKey(brokerPubKey);
		
		DeploymentID peerDeploymentID = getServiceManager().getStubDeploymentID(remotePeer);
		to.setRemotePeerDID(peerDeploymentID == null ? null : peerDeploymentID.toString());
		to.setRemotePeerPublicKey(peerDeploymentID == null ? null : peerDeploymentID.getPublicKey());
		
		to.setSenderPublicKey(getServiceManager().getSenderPublicKey());
		to.setWorkerDeployed(isWorkerDeployed());
		to.setRemotePeerDN(CertificationUtils.getCertSubjectDN(getServiceManager().getSenderCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	private boolean isWorkerDeployed() {
		return getServiceManager().getObjectDeployment(WorkerConstants.WORKER) != null;
	}
	
	@RecoveryNotification
	public void workerManagementClientIsUp(RemoteWorkerManagementClient rwmc, DeploymentID monitorableID) {}
	
	@FailureNotification
	public void workerManagementClientIsDown(RemoteWorkerManagementClient rwmc, DeploymentID monitorableID) {
		RemoteWorkerManagementClientDoNotifyFailureRequestTO to = new RemoteWorkerManagementClientDoNotifyFailureRequestTO();
		to.setMonitorableAddress(monitorableID.getServiceID().toString());
		to.setMonitorableID(monitorableID.toString());
		to.setMonitorablePublicKey(monitorableID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

}