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
package org.ourgrid.common.interfaces.status;

import java.util.List;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.peer.status.PeerCompleteHistoryStatus;
import org.ourgrid.peer.status.PeerCompleteStatus;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProviderClient;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * Entity that represents a PeerStatusProvider client. From this interface
 * the PeerStatusProvider receive the requests about status.
 * 
 * @see PeerStatusProvider
 */
@Remote
public interface PeerStatusProviderClient extends ModuleStatusProviderClient{

	/**
	 * Informs the actual status of the local workers.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param localWorkers The list with the status of the local workers.
	 */
	void hereIsLocalWorkersStatus( ServiceID statusProviderServiceID, List<WorkerInfo> localWorkers );

	/**
	 * Informs the actual status of the remote workers.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param remoteWorkers The list with the status of the remote workers.
	 */
	void hereIsRemoteWorkersStatus( ServiceID statusProviderServiceID, List<RemoteWorkerInfo> remoteWorkers );

	/**
	 * Informs the actual status of the consumers that are from the Peer's site (local consumers).
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param localConsumers The list with the status of the local consumers.
	 */
	void hereIsLocalConsumersStatus( ServiceID statusProviderServiceID, List<LocalConsumerInfo> localConsumers );

	/**
	 * Informs the actual status of the consumers that are not from the Peer's site (remote consumers).
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param remoteConsumers The list with the status of the remote consumers.
	 */
	void hereIsRemoteConsumersStatus( ServiceID statusProviderServiceID, List<ConsumerInfo> remoteConsumers );
	
	/**
	 * Informs the status from all known users.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param usersInfo The list with the status of all known users.
	 */
	void hereIsUsersStatus( ServiceID statusProviderServiceID, List<UserInfo> usersInfo);
	
	/**
	 * Informs the status from the scheme definition of local communities of trusted Peers.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param trustInfo The list with all trust status.
	 */
	void hereIsTrustStatus( ServiceID statusProviderServiceID, List<TrustyCommunity> trustInfo );
	
	/**
	 * Informs the status from the grid NOF scheme.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param nofStatus The list with all NOF status.
	 */
	void hereIsNetworkOfFavorsStatus( ServiceID statusProviderServiceID, NetworkOfFavorsStatus nofStatus );
	
	/**
	 * Informs the actual complete status.
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param completeStatus The PeerCompleteStatus containing the actual complete status.
	 */
	void hereIsCompleteStatus( ServiceID statusProviderServiceID, PeerCompleteStatus completeStatus );

	/**
	 * Informs the complete status history based on a given time. 
	 * @param statusProviderServiceID The PeerStatusProvider ServiceID.
	 * @param completeStatus The PeerCompleteHistoryStatus based on the given time.
	 * @param time The time to base the search on the complete status historical.
	 */
	void hereIsCompleteHistoryStatus( ServiceID statusProviderServiceID, PeerCompleteHistoryStatus completeStatus, long time );
	
}
