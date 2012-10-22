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
package org.ourgrid.aggregator.communication.receiver;

import java.util.List;
import java.util.Map;

import org.ourgrid.aggregator.request.HereIsCompleteHistoryStatusRequestTO;
import org.ourgrid.aggregator.request.PeerStatusProviderIsDownRequestTO;
import org.ourgrid.aggregator.request.PeerStatusProviderIsUpRequestTO;
import org.ourgrid.common.interfaces.status.ConsumerInfo;
import org.ourgrid.common.interfaces.status.LocalConsumerInfo;
import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.interfaces.status.PeerStatusProvider;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.interfaces.status.RemoteWorkerInfo;
import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.InvokeOnDeploy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * This class implements the interface PeerStatusProviderClient and serves to
 * notify the Peer Status.
 * @see PeerStatusProviderClient
 *
 */
public class PeerStatusProviderClientReceiver implements PeerStatusProviderClient {
	
	private ServiceManager serviceManager;
	
	@InvokeOnDeploy
	public void init(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsConfiguration(Map<String, String> arg0) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsUpTime(long arg0) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsLocalWorkersStatus(ServiceID statusProviderServiceID,
			List<WorkerInfo> localWorkers) {
		//not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsRemoteWorkersStatus(ServiceID statusProviderServiceID,
			List<RemoteWorkerInfo> remoteWorkers) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsLocalConsumersStatus(ServiceID statusProviderServiceID,
			List<LocalConsumerInfo> localConsumers) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsRemoteConsumersStatus(ServiceID statusProviderServiceID,
			List<ConsumerInfo> remoteConsumers) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsUsersStatus(ServiceID statusProviderServiceID,
			List<UserInfo> usersInfo) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsTrustStatus(ServiceID statusProviderServiceID,
			List<TrustyCommunity> trustInfo) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsNetworkOfFavorsStatus(ServiceID statusProviderServiceID,
			NetworkOfFavorsStatus nofStatus) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsCompleteStatus(ServiceID statusProviderServiceID,
			PeerCompleteStatus completeStatus) {
		// not implemented

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hereIsCompleteHistoryStatus(ServiceID statusProviderServiceID,
			PeerCompleteHistoryStatus completeStatus, long time) {
		
		HereIsCompleteHistoryStatusRequestTO request = new HereIsCompleteHistoryStatusRequestTO();
		request.setCompleteStatus(completeStatus);
		request.setProviderAddress(statusProviderServiceID.toString());
		request.setTime(time);
		
		OurGridRequestControl.getInstance().execute(request, serviceManager);

	}
	
	/**
	 * Notifies the Peer Status Provider failure.
	 * @param provider {@link PeerStatusProvider}
	 * @param providerId {@link DeploymentID}
	 */
	@FailureNotification
	public void peerStatusProviderIsDown(PeerStatusProvider provider, DeploymentID providerId) {
		PeerStatusProviderIsDownRequestTO to =  new PeerStatusProviderIsDownRequestTO();
		
		to.setProviderId(providerId.getServiceID().toString());
		OurGridRequestControl.getInstance().execute(to, serviceManager);
	}
	
	/**
	 * Notifies the Peer Status Provider recovery.
	 * @param provider {@link PeerStatusProvider}
	 * @param providerId {@link DeploymentID}
	 */
	@RecoveryNotification
	public void peerStatusProviderIsUp(PeerStatusProvider provider, DeploymentID providerId) {
		PeerStatusProviderIsUpRequestTO request = new PeerStatusProviderIsUpRequestTO();
		request.setProviderId(providerId.getServiceID().toString());
		OurGridRequestControl.getInstance().execute(request, serviceManager);
	}	
	
}
