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


import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProvider;

/**
 * Provides an interface to update the status from entities or properties
 * related to the Peer.
 * 
 * @see PeerStatusProviderClient.
 */
@Remote
public interface PeerStatusProvider extends ModuleStatusProvider {

	/**
	 * Gets the status from the workers located in the Peer site (the local workers).
	 * @param client The client of PeerStatusProvider.
	 */
	void getLocalWorkersStatus( PeerStatusProviderClient client );


	/**
	 * Gets the status from the workers that are remote (are not located in Peer's site).
	 * @param client The client of PeerStatusProvider.
	 */
	void getRemoteWorkersStatus( PeerStatusProviderClient client );


	/**
	 * Gets the status from the actual consumers that are from the Peer's site.
	 * @param client The client of PeerStatusProvider.
	 */
	void getLocalConsumersStatus( PeerStatusProviderClient client );


	/**
	 * Gets the status from the actual consumers that are not from the Peer's site.
	 * @param client The client of PeerStatusProvider.
	 */
	void getRemoteConsumersStatus( PeerStatusProviderClient client );
	
	/**
	 * Gets the status from all users.
	 * @param client The client of PeerStatusProvider.
	 */
	void getUsersStatus( PeerStatusProviderClient client );
	
	/**
	 * Gets the status from the scheme definition of local communities of trusted Peers.
	 * @param client The client of PeerStatusProvider.
	 */
	void getTrustStatus( PeerStatusProviderClient client );
	
	/**
	 * Gets the status from the grid NOF scheme.
	 * @param client The client of PeerStatusProvider.
	 */
	void getNetworkOfFavorsStatus( PeerStatusProviderClient client );

	/**
	 * Gets the actual complete status. 
	 * @param client The client of PeerStatusProvider.
	 */
	void getCompleteStatus( PeerStatusProviderClient client );
	
	/**
	 * Registers the client as listener from the PeerStatusProvider.
	 * @param client The client of PeerStatusProvider.
	 */
	void registerAsListener( PeerStatusProviderClient client );

	/**
	 * Gets the complete status history based on a given time. 
	 * @param client The client of PeerStatusProvider.
	 * @param time The time to base the search on the complete status historical.
	 */
	void getCompleteHistoryStatus( PeerStatusProviderClient client, long time );
	
}
