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

import org.ourgrid.common.interfaces.control.DiscoveryServiceControlClient;
import org.ourgrid.common.interfaces.management.DiscoveryServiceManager;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 *
 */
public class DiscoveryServiceSyncComponentClient 
		extends SyncApplicationClient<DiscoveryServiceManager, DiscoveryServiceSyncManagerClient> {

	public DiscoveryServiceSyncComponentClient(ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super("DS_SYNC_UI", context);
	}
	
	public DiscoveryServiceSyncComponentClient(ModuleContext context, boolean waitForever) throws CommuneNetworkException, ProcessorStartException {
		super("DS_SYNC_UI", context, waitForever);
	}

	public DiscoveryServiceCompleteStatus getDiscoveryServiceCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, DiscoveryServiceCompleteStatus.class, getQueueTimeout());
	}
	
	public ControlOperationResult query( String query ) {
		getManager().query((DiscoveryServiceControlClient)getManagerClient(), query);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.servicemanager.client.ApplicationClient#createInitializationContext()
	 */
	@Override
	protected InitializationContext<DiscoveryServiceManager, DiscoveryServiceSyncManagerClient> createInitializationContext() {
		return new DiscoveryServiceSyncInitializationContext();
	}

}
