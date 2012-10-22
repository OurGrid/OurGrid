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

import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.common.interfaces.management.DiscoveryServiceManager;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.common.internal.OurGridControlReceiver;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.RequestControlIF;
import org.ourgrid.common.internal.request.QueryRequestTO;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.business.requester.DiscoveryServiceRequestControl;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;
import org.ourgrid.discoveryservice.request.GetCompleteStatusRequestTO;
import org.ourgrid.discoveryservice.request.StartDiscoveryServiceRequestTO;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DiscoveryServiceControlReceiver extends OurGridControlReceiver 
	implements DiscoveryServiceManager {

	
	private static final String DISCOVERY_SERVICE_COMPONENT_NAME = "DiscoveryService";


	@Override
	public String getComponentName() {
		return DISCOVERY_SERVICE_COMPONENT_NAME;
	}


	@Override
	/**
	 * Requirement 502
	 */
	protected void startComponent() throws Exception {
		StartDiscoveryServiceRequestTO to = new StartDiscoveryServiceRequestTO();
		
		List<ServiceID> parseNetwork = DiscoveryServiceConfiguration.parseNetwork(getServiceManager());
		
		List<String> networkAddress = new ArrayList<String>();
		
		for (ServiceID id : parseNetwork) {
			networkAddress.add(id.toString());
		}
		
		to.setNetworkAddresses(networkAddress);
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void getCompleteStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) 
			DiscoveryServiceStatusProviderClient client) {
		GetCompleteStatusRequestTO to = new GetCompleteStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		
		ModuleContext containerContext = getServiceManager().getContainerContext();
		
		to.setContextString(containerContext.toString());
		to.setPropConfDir(containerContext.getProperty(ModuleProperties.PROP_CONFDIR));
		to.setUpTime(getServiceManager().getContainerDAO().getUpTime());
		
		DeploymentID objectDeploymentID = getServiceManager().getObjectDeploymentID(DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		if (objectDeploymentID != null) {
			to.setMyAddress(objectDeploymentID.getServiceID().toString());
		}
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	public void query(@MonitoredBy(Module.CONTROL_OBJECT_NAME) DiscoveryServiceControlClient dsControlClient, String query) {
		QueryRequestTO to = new QueryRequestTO();
		
		to.setQuery(query);
		to.setClientAddress(getServiceManager().getStubDeploymentID(
				dsControlClient).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@RecoveryNotification
	public void controlClientIsUp(
			DiscoveryServiceStatusProviderClient statusProviderClient) { }
	
	@FailureNotification
	public void controlClientIsDown(
			DiscoveryServiceStatusProviderClient statusProviderClient) { }
	
	@Override
	protected boolean validateStartSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(DiscoveryServiceControlMessages.getUnknownSenderStartingDiscoveryServiceMessage(senderPublicKey));
			return false;
		}
		return true;
	}
	
	@Override
	protected boolean validateStopSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(DiscoveryServiceControlMessages.getUnknownSenderStoppingDiscoveryServiceMessage(senderPublicKey));
			return false;
		}
		return true;
	}
	
	@Override
	protected RequestControlIF createRequestControl() {
		return new DiscoveryServiceRequestControl();
	}
}
