/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of Commune. 
 *
 * Commune is free software: you can redistribute it and/or modify it under the
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
package org.ourgrid.cmmstatusprovider.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ourgrid.cmmstatusprovider.CommunityStatusProviderCallback;
import org.ourgrid.cmmstatusprovider.DiscoveryServiceStateListener;
import org.ourgrid.cmmstatusprovider.dao.CommunityStatusProviderDAO;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProvider;
import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class AsyncCommunityStatusProviderClientController implements PeerStatusProviderClient,
		DiscoveryServiceStatusProviderClient {

	private ServiceManager serviceManager;

	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}
	
	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsCompleteHistoryStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, org.ourgrid.peer.status.PeerCompleteHistoryStatus, long)
	 */
	public void hereIsCompleteHistoryStatus(ServiceID statusProviderServiceID,
			PeerCompleteHistoryStatus completeStatus, long time) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsCompleteStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, org.ourgrid.peer.status.PeerCompleteStatus)
	 */
	public void hereIsCompleteStatus(ServiceID statusProviderServiceID,
			PeerCompleteStatus completeStatus) {
		
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		Set<CommunityStatusProviderCallback> peersCallback = dao.getPeersCallback(statusProviderServiceID);
		
		if (peersCallback != null) {
			for (CommunityStatusProviderCallback callback : peersCallback) {
				callback.hereIsCompleteStatus(statusProviderServiceID, completeStatus);
			}
		}
		
		dao.clearPeersCallback(statusProviderServiceID);
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsLocalConsumersStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsLocalConsumersStatus(ServiceID statusProviderServiceID,
			List<LocalConsumerInfo> localConsumers) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsLocalWorkersStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsLocalWorkersStatus(ServiceID statusProviderServiceID,
			List<WorkerInfo> localWorkers) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsNetworkOfFavorsStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus)
	 */
	
	public void hereIsNetworkOfFavorsStatus(ServiceID statusProviderServiceID,
			NetworkOfFavorsStatus nofStatus) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsRemoteConsumersStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsRemoteConsumersStatus(ServiceID statusProviderServiceID,
			List<ConsumerInfo> remoteConsumers) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsRemoteWorkersStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsRemoteWorkersStatus(ServiceID statusProviderServiceID,
			List<RemoteWorkerInfo> remoteWorkers) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsTrustStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsTrustStatus(ServiceID statusProviderServiceID,
			List<TrustyCommunity> trustInfo) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.PeerStatusProviderClient#hereIsUsersStatus(br.edu.ufcg.lsd.commune.identification.ServiceID, java.util.List)
	 */
	
	public void hereIsUsersStatus(ServiceID statusProviderServiceID,
			List<UserInfo> usersInfo) {
		
	}

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationStatusProviderClient#hereIsConfiguration(java.util.Map)
	 */
	
	public void hereIsConfiguration(Map<String, String> configuration) {
		
	}

	/* (non-Javadoc)
	 * @see br.edu.ufcg.lsd.commune.container.control.ApplicationStatusProviderClient#hereIsUpTime(long)
	 */
	
	public void hereIsUpTime(long uptime) {
		
	}

	/* (non-Javadoc)
	 * @see org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient#hereIsCompleteStatus(org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus)
	 */
	
	public void hereIsCompleteStatus(DiscoveryServiceCompleteStatus completeStatus) {
		
		ServiceID dsServiceID = serviceManager.getSenderServiceID();
		
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		Set<CommunityStatusProviderCallback> dsCallback = dao.getDsCallback(dsServiceID);
		
		if (dsCallback != null) {
			for (CommunityStatusProviderCallback callback : dsCallback) {
				callback.hereIsCompleteStatus(completeStatus);
			}
		}
		
		dao.clearDsCallback(dsServiceID);
	}

	@RecoveryNotification
	public void peerIsUp(PeerStatusProvider statusProvider, DeploymentID providerDID) {
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		dao.addPeerStatusProvider(providerDID.getServiceID(), statusProvider);
		statusProvider.getCompleteStatus(this);
	}
	
	@FailureNotification
	public void peerIsDown(PeerStatusProvider statusProvider, DeploymentID providerDID) {
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		dao.removePeerStatusProvider(providerDID.getServiceID());
		serviceManager.release(statusProvider);
	}
	
	@RecoveryNotification
	public void dsIsUp(DiscoveryServiceStatusProvider dsStatusProvider, DeploymentID providerDID) {
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		dao.addDSStatusProvider(providerDID.getServiceID(), dsStatusProvider);
		dsStatusProvider.getCompleteStatus(this);
		
		DiscoveryServiceStateListener listener = dao.getDiscoveryServiceStateListener(providerDID.getServiceID().getContainerID().getUserAtServer());
		if (listener != null) {
			listener.dsIsUp();
		}
	}
	
	@FailureNotification
	public void dsIsDown(DiscoveryServiceStatusProvider dsStatusProvider, DeploymentID providerDID) {
		CommunityStatusProviderDAO dao = this.serviceManager.getDAO(CommunityStatusProviderDAO.class);
		dao.removeDSStatusProvider(providerDID.getServiceID());
		serviceManager.release(dsStatusProvider);
		
		DiscoveryServiceStateListener listener = dao.getDiscoveryServiceStateListener(providerDID.getServiceID().getContainerID().getUserAtServer());
		if (listener != null) {
			listener.dsIsDown();
		}
	}

}
