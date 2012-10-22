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
package org.ourgrid.common.interfaces;

import java.util.List;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

/**
 * 
 * Interface that provides basic operations provided by the Discovery Service. 
 * It provides services related to: Leave the community, get informations about
 * the Peers of the community and other Discovery Services.
 * 
 */
@Remote
public interface DiscoveryService {

	/**
	 * A Peer requests to leave the community.
	 * @param discoveryServiceClient The client of DiscoveryService.
	 */
	void leaveCommunity( DiscoveryServiceClient discoveryServiceClient );

	/**
	 * A Peer requests the information to the DiscoveryService about other Peers.
	 * @param dsClient The client of DiscoveryService.
	 */
	void getRemoteWorkerProviders(DiscoveryServiceClient dsClient, int maxResponseSize);

	/**
	 * A DiscoveryService ask another about the actual Discovery Services known. 
	 * @param discoveryService The Discovery Service that requests the information.
	 */
	void getDiscoveryServices(DiscoveryService discoveryService);

	/**
	 * A Peer ask the Discovery Service about the actual Discovery Services known.
	 * @param discoveryServiceClient The client of DiscoveryService.
	 */
	void getDiscoveryServices(DiscoveryServiceClient discoveryServiceClient);

	/**
	 * Informs the actual list of known Discovery Services.
	 * @param discoveryServices A list of known Discovery Services.
	 */
	void hereAreDiscoveryServices(List<ServiceID> discoveryServices);

	/**
	 * Informs the actual list of known remote Peers.
	 * @param workerProviders The list of known remote Peers.
	 */
	void hereIsRemoteWorkerProviderList(List<String> workerProviders);	
}