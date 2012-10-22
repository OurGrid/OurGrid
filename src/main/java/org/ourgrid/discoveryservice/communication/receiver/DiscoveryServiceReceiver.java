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
package org.ourgrid.discoveryservice.communication.receiver;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;
import org.ourgrid.discoveryservice.request.DSClientGetDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.request.DSClientIsDownRequestTO;
import org.ourgrid.discoveryservice.request.DSClientIsUpRequestTO;
import org.ourgrid.discoveryservice.request.DSGetDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.request.GetRemoteWorkerProvidersRequestTO;
import org.ourgrid.discoveryservice.request.HereAreDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.request.HereIsRemoteWorkerProviderListRequestTO;
import org.ourgrid.discoveryservice.request.LeaveCommunityRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DiscoveryServiceReceiver implements DiscoveryService {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	public void leaveCommunity(@MonitoredBy(DiscoveryServiceConstants.DS_OBJECT_NAME) DiscoveryServiceClient dsClient) {
		LeaveCommunityRequestTO to = new LeaveCommunityRequestTO();
		
		ServiceID serviceID = serviceManager.getStubDeploymentID(dsClient).getServiceID();
		
		to.setClientAddress(serviceID.toString());
		to.setClientUserAtServer(serviceID.getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	public void getRemoteWorkerProviders( @MonitoredBy(DiscoveryServiceConstants.DS_OBJECT_NAME) DiscoveryServiceClient dsClient, int maxResponseSize) {
		GetRemoteWorkerProvidersRequestTO to = new GetRemoteWorkerProvidersRequestTO();
		
		ServiceID serviceID = serviceManager.getStubDeploymentID(dsClient).getServiceID();
		
		to.setClientAddress(serviceID.toString());
		to.setClientUserAtServer(serviceID.getContainerID().getUserAtServer().toString());
		to.setMyAddress(serviceManager.getObjectDeploymentID(
				DiscoveryServiceConstants.DS_OBJECT_NAME).getServiceID().toString());
		to.setMaxResponseSize(maxResponseSize);
		to.setOverloadThreshold(serviceManager.getContainerContext().parseIntegerProperty(
				DiscoveryServiceConfiguration.PROP_OVERLOAD_THRESHOLD));
		to.setDsMaxResponse(serviceManager.getContainerContext().parseIntegerProperty(
				DiscoveryServiceConfiguration.PROP_MAX_RESPONSE_SIZE));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void dsClientIsUp(DiscoveryServiceClient discoveryServiceClient, DeploymentID providerDID) {
		DSClientIsUpRequestTO to = new DSClientIsUpRequestTO();
		to.setClientUserAtServer(providerDID.getContainerID().getUserAtServer().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	@FailureNotification
	public void dsClientIsDown(DiscoveryServiceClient discoveryServiceClient, DeploymentID monitorableID) {
		DSClientIsDownRequestTO to = new DSClientIsDownRequestTO();
		
		to.setClientAddress(monitorableID == null ? null : monitorableID.getServiceID().toString());
		to.setClientUserAtServer(monitorableID == null ? null : monitorableID.getServiceID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	public void getDiscoveryServices(@MonitoredBy(DiscoveryServiceConstants.DS_MONITOR) DiscoveryService discoveryService) {
		DSGetDiscoveryServicesRequestTO to = new DSGetDiscoveryServicesRequestTO();
		
		to.setDsAddress(serviceManager.getStubDeploymentID(discoveryService).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void hereAreDiscoveryServices(List<ServiceID> discoveryServicesID) {
		HereAreDiscoveryServicesRequestTO to = new HereAreDiscoveryServicesRequestTO();
		
		List<String> dsAddresses = new ArrayList<String>();
		
		for (ServiceID id : discoveryServicesID) {
			dsAddresses.add(id.toString());
		}
		
		to.setDiscoveryServicesAddresses(dsAddresses);
		to.setMyAddress(serviceManager.getObjectDeploymentID(DiscoveryServiceConstants.DS_OBJECT_NAME).getServiceID().toString());
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void getDiscoveryServices( @MonitoredBy(DiscoveryServiceConstants.DS_OBJECT_NAME) DiscoveryServiceClient discoveryServiceClient) {
		
		DSClientGetDiscoveryServicesRequestTO to = new DSClientGetDiscoveryServicesRequestTO();
		
		DeploymentID stubDeploymentID = serviceManager.getStubDeploymentID(discoveryServiceClient);
		to.setClientAddress(stubDeploymentID.getServiceID().toString());
		to.setClientUserAtServer(stubDeploymentID.getContainerID().getUserAtServer());
		to.setMyAddress(serviceManager.getObjectDeploymentID(
				DiscoveryServiceConstants.DS_OBJECT_NAME).getServiceID().toString());
		to.setOverloadThreshold(serviceManager.getContainerContext().parseIntegerProperty(
				DiscoveryServiceConfiguration.PROP_OVERLOAD_THRESHOLD));
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}

	public void hereIsRemoteWorkerProviderList( List<String> workerProviders ) {
		HereIsRemoteWorkerProviderListRequestTO to = new HereIsRemoteWorkerProviderListRequestTO();
		to.setSenderAddress(serviceManager.getSenderServiceID().toString());
		to.setWorkerProviders(workerProviders);
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
