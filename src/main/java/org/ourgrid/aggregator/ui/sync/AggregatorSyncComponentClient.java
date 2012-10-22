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
package org.ourgrid.aggregator.ui.sync;

import org.ourgrid.aggregator.status.AggregatorCompleteStatus;
import org.ourgrid.common.interfaces.control.AggregatorControlClient;
import org.ourgrid.common.interfaces.management.AggregatorManager;
import org.ourgrid.common.interfaces.status.AggregatorStatusProvider;

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
public class AggregatorSyncComponentClient 
		extends SyncApplicationClient<AggregatorManager, AggregatorSyncManagerClient> {

	public AggregatorSyncComponentClient(ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super("AGGREGATOR_SYNC_UI", context);
	}
	
	public AggregatorSyncComponentClient(ModuleContext context, boolean waitForever) throws CommuneNetworkException, ProcessorStartException {
		super("AGGREGATOR_SYNC_UI", context, waitForever);
	}

	public AggregatorCompleteStatus getAggregatorCompleteStatus() {
		AggregatorStatusProvider statusProvider = getManager();
		if (statusProvider == null) {
			throw new IllegalStateException("AggregatorStatusProvider is not alive.");
		}
		statusProvider.getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue,
				AggregatorCompleteStatus.class, getQueueTimeout());
	}
	
	public ControlOperationResult query( String query ) {
		getManager().query((AggregatorControlClient)getManagerClient(), query);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.servicemanager.client.ApplicationClient#createInitializationContext()
	 */
	@Override
	protected InitializationContext<AggregatorManager, AggregatorSyncManagerClient> createInitializationContext() {
		return new AggregatorSyncInitializationContext();
	}

}
