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

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.request.DSIsOverloadedRequestTO;
import org.ourgrid.peer.request.HereAreDiscoveryServicesRequestTO;
import org.ourgrid.peer.request.HereIsRemoteWorkerProvidersListRequestTO;
import org.ourgrid.peer.request.NotifyDiscoveryServiceFailureRequestTO;
import org.ourgrid.peer.request.NotifyDiscoveryServiceRecoveryRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 *
 */
public class DiscoveryServiceClientReceiver implements DiscoveryServiceClient {
	
	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void hereIsRemoteWorkerProviderList(List<String> workerProviders) {
		HereIsRemoteWorkerProvidersListRequestTO hereIsRWPListRequestTO = new HereIsRemoteWorkerProvidersListRequestTO();
		hereIsRWPListRequestTO.setMyUserAtServer(serviceManager.getMyDeploymentID().getContainerID().getUserAtServer());
		hereIsRWPListRequestTO.setProvidersUserAtServer(workerProviders);
		
		OurGridRequestControl.getInstance().execute(hereIsRWPListRequestTO, serviceManager);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.DiscoveryServiceClient#hereAreDiscoveryServices(java.util.List)
	 */
	public void hereAreDiscoveryServices(List<String> discoveryServices) {
		HereAreDiscoveryServicesRequestTO to = new HereAreDiscoveryServicesRequestTO();
		to.setDiscoveryServices(discoveryServices);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@FailureNotification
	public void doNotifyFailure(DiscoveryService monitorable, DeploymentID deploymentID) {
		NotifyDiscoveryServiceFailureRequestTO request = new NotifyDiscoveryServiceFailureRequestTO();
		request.setDSServiceID(deploymentID.getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}
	
	@RecoveryNotification
	public void doNotifyRecovery(DiscoveryService monitorableStub, DeploymentID monitorableId) {
		NotifyDiscoveryServiceRecoveryRequestTO request = new NotifyDiscoveryServiceRecoveryRequestTO();
		
		int dsRequestSize = serviceManager.getContainerContext().parseIntegerProperty(
				PeerConfiguration.PROP_DS_REQUEST_SIZE);
		
		request.setDSServiceID(monitorableId.getServiceID().toString());
		request.setDsRequestSize(dsRequestSize);
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}

	@Override
	public void dsIsOverloaded(String dsAddress) {
		DSIsOverloadedRequestTO request = new DSIsOverloadedRequestTO();
		request.setDSAddress(dsAddress);
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}

}
