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
package org.ourgrid.peer.ui.sync;

import java.util.Collection;
import java.util.List;

import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

public class PeerSyncApplicationClient extends SyncApplicationClient<PeerManager, PeerSyncManagerClientService> {
	

	public PeerSyncApplicationClient(ModuleContext context) throws CommuneNetworkException,
			ProcessorStartException {
		super("PEER_SYNC_UI", context);
	}
	
	public PeerSyncApplicationClient(ModuleContext context, boolean waitForever) throws CommuneNetworkException,
			ProcessorStartException {
		super("PEER_SYNC_UI", context, waitForever);
	}
	
	public ControlOperationResult addUser( String login ) {
		getManager().addUser((PeerControlClient)getManagerClient(), login);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	public ControlOperationResult removeUser( String login ) {
		getManager().removeUser((PeerControlClient)getManagerClient(), login);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	public ControlOperationResult removeWorker( WorkerSpecification workerSpec ) {
		getManager().removeWorker((PeerControlClient)getManagerClient(), workerSpec);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public ControlOperationResult query( String query ) {
		getManager().query((PeerControlClient)getManagerClient(), query);
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public List<WorkerInfo> getLocalWorkersStatus() {
		getManager().getLocalWorkersStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, List.class, getQueueTimeout());
	}

	public Collection<RemoteWorkerInfo> getRemoteWorkersStatus() {
		getManager().getRemoteWorkersStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, Collection.class, getQueueTimeout());
	}

	public Collection<LocalConsumerInfo>  getLocalConsumersStatus() {
		getManager().getLocalConsumersStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, Collection.class, getQueueTimeout());
	}

	public Collection<ConsumerInfo> getRemoteConsumersStatus() {
		getManager().getRemoteConsumersStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, Collection.class, getQueueTimeout());
	}
	
	public List<UserInfo> getUsersStatus() {
		getManager().getUsersStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, List.class, getQueueTimeout());
	}
	
	public List<TrustyCommunity> getTrustStatus() {
		getManager().getTrustStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, List.class, getQueueTimeout());
	}
	
	public NetworkOfFavorsStatus getNetworkOfFavorsStatus() {
		getManager().getNetworkOfFavorsStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, NetworkOfFavorsStatus.class, getQueueTimeout());
	}

	public PeerCompleteStatus getCompleteStatus() {
		PeerStatusProvider statusProvider = getManager();
		if (statusProvider == null) {
			throw new IllegalStateException("PeerStatusProvider is not alive.");
		}
		statusProvider.getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, PeerCompleteStatus.class, getQueueTimeout());
	}

	@Override
	protected InitializationContext<PeerManager, PeerSyncManagerClientService> createInitializationContext() {
		return new PeerSyncInitializationContext();
	}
}
