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

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.request.HereIsWorkerRequestTO;
import org.ourgrid.peer.request.NotifyRemoteWorkerProviderFailureRequestTO;
import org.ourgrid.peer.request.NotifyRemoteWorkerProviderRecoveryRequestTO;
import org.ourgrid.peer.request.PreemptedWorkerRequestTO;
import org.ourgrid.reqtrace.Req;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

@Req({"REQ018"})
public class RemoteWorkerProviderClientReceiver implements RemoteWorkerProviderClient{

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/**
	 * @param provider
	 * @param worker
	 * @param workerSpec
	 * @param senderPublicKey 
	 */
	public void hereIsWorker(
			@MonitoredBy(PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT) RemoteWorkerProvider provider, 
			ServiceID workerID, WorkerSpecification workerSpec) {
		
		HereIsWorkerRequestTO to = new HereIsWorkerRequestTO();

		to.setSenderPublicKey(serviceManager.getSenderPublicKey());
		to.setProviderCertSubjectDN(CertificationUtils.getCertSubjectDN(serviceManager.getSenderCertPath()));
		to.setMyUserAtServer(serviceManager.getMyDeploymentID().getContainerID().getUserAtServer());
		
		DeploymentID providerID = serviceManager.getStubDeploymentID(provider);
		
		to.setProviderAddress(providerID.getServiceID().toString());
		
		if (workerID != null) {
			to.setWorkerAddress(workerID.toString());
			to.setWorkerContainerID(workerID.getContainerID().toString());
		}
		
		DeploymentID workerClientID = serviceManager.getObjectDeployment(
					PeerConstants.REMOTE_WORKER_MANAGEMENT_CLIENT).getDeploymentID();
		
		to.setWorkerClientAddress(workerClientID.getServiceID().toString());
		to.setWorkerSpec(workerSpec);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void workerProviderIsUp(RemoteWorkerProvider rwp, DeploymentID deploymentID, 
			X509CertPath providerCertPath) {
		
		NotifyRemoteWorkerProviderRecoveryRequestTO request = new NotifyRemoteWorkerProviderRecoveryRequestTO();
		request.setMyCertPath(serviceManager.getMyCertPath());
		request.setRwpCertPath(providerCertPath);
		request.setRwpAdress(deploymentID.getServiceID().toString());
		request.setRwpUserAtServer(deploymentID.getServiceID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
		
	}

	/**
	 * Notifies that the {@link RemoteWorkerProvider} has failed
	 * @param rwp The {@link RemoteWorkerProvider} that has failed.
	 * @param deploymentID The DeploymentID of the {@link RemoteWorkerProvider} that has failed.
	 * @param providerCertPath The certificate of failed {@link RemoteWorkerProvider}
	 */
	@FailureNotification
	public void workerProviderIsDown(RemoteWorkerProvider rwp, DeploymentID deploymentID,
				X509CertPath providerCertPath) {
		
		NotifyRemoteWorkerProviderFailureRequestTO request = new NotifyRemoteWorkerProviderFailureRequestTO();
		request.setRwpUserAtServer(deploymentID.getContainerID().getUserAtServer());
		request.setRwpPublicKey(deploymentID.getPublicKey());
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}

	@Override
	public void preemptedWorker(String workerPublicKey) {
		PreemptedWorkerRequestTO to = new PreemptedWorkerRequestTO();
		to.setRemoteWorkerPublicKey(workerPublicKey);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
