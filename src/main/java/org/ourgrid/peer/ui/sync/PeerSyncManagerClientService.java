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

import java.util.List;

import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PeerSyncManagerClientService extends SyncManagerClient<PeerManager> implements PeerStatusProviderClient, PeerControlClient {

	public void hereIsCompleteStatus(ServiceID statusProviderServiceID,
			PeerCompleteStatus completeStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), completeStatus);
	}

	public void hereIsLocalConsumersStatus(ServiceID statusProviderServiceID,
			List<LocalConsumerInfo> localConsumers) {
		SyncContainerUtil.putResponseObject(getQueue(), localConsumers);
	}

	public void hereIsLocalWorkersStatus(ServiceID statusProviderServiceID,
			List<WorkerInfo> localWorkers) {
		SyncContainerUtil.putResponseObject(getQueue(), localWorkers);
	}

	public void hereIsNetworkOfFavorsStatus(ServiceID statusProviderServiceID,
			NetworkOfFavorsStatus nofStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), nofStatus);
	}

	public void hereIsRemoteConsumersStatus(ServiceID statusProviderServiceID,
			List<ConsumerInfo> remoteConsumers) {
		SyncContainerUtil.putResponseObject(getQueue(), remoteConsumers);
	}

	public void hereIsRemoteWorkersStatus(ServiceID statusProviderServiceID,
			List<RemoteWorkerInfo> remoteWorkers) {
		SyncContainerUtil.putResponseObject(getQueue(), remoteWorkers);
	}

	public void hereIsTrustStatus(ServiceID statusProviderServiceID,
			List<TrustyCommunity> trustInfo) {
		SyncContainerUtil.putResponseObject(getQueue(), trustInfo);
	}

	public void hereIsUsersStatus(ServiceID statusProviderServiceID,
			List<UserInfo> usersInfo) {
		SyncContainerUtil.putResponseObject(getQueue(), usersInfo);
	}

	@RecoveryNotification
	public void controlIsUp(PeerManager control) {
		super.controlIsUp(control);
	}
	
	@FailureNotification
	public void controlIsDown(PeerManager control) {
		super.controlIsDown(control);
	}

	public void hereIsCompleteHistoryStatus(ServiceID statusProviderServiceID,
			PeerCompleteHistoryStatus completeStatus, long time) {
		// TODO Auto-generated method stub
		
	}

}
