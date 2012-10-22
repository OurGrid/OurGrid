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

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.discoveryservice.request.DSIsDownRequestTO;
import org.ourgrid.discoveryservice.request.DSIsUpRequestTO;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class DiscoveryServiceNotificationReceiver {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	@FailureNotification
	public void dsIsDown(DiscoveryService discoveryService, DeploymentID deploymentID) {
		DSIsDownRequestTO to = new DSIsDownRequestTO();
		to.setDsAddress(deploymentID.getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	@RecoveryNotification
	public void dsIsUp(DiscoveryService discoveryService, DeploymentID deploymentID) {
		DSIsUpRequestTO to = new DSIsUpRequestTO();
		to.setDsAddress(deploymentID.getServiceID().toString());
		to.setMyAddress(serviceManager.getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
}
