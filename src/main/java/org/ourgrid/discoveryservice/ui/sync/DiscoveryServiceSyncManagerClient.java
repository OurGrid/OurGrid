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
package org.ourgrid.discoveryservice.ui.sync;

import java.util.List;

import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.common.interfaces.management.DiscoveryServiceManager;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DiscoveryServiceSyncManagerClient extends SyncManagerClient<DiscoveryServiceManager> implements DiscoveryServiceStatusProviderClient, 
	DiscoveryServiceControlClient {

	public void hereIsCompleteStatus(
			DiscoveryServiceCompleteStatus completeStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), completeStatus);
	}

	public void hereIsConnectedPeerList(List<ServiceID> connectedPeers) {
		SyncContainerUtil.putResponseObject(getQueue(), connectedPeers);
	}

	@Override
	@RecoveryNotification
	public void controlIsUp(DiscoveryServiceManager control) {
		super.controlIsUp(control);
	}
	
	@Override
	@FailureNotification
	public void controlIsDown(DiscoveryServiceManager control) {
		super.controlIsDown(control);
	}
}
