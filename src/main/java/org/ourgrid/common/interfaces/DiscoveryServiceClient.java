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

/**
 * Callback interface that must be implemented by an entity that wants to use a
 * DiscoveryService
 */
@Remote
public interface DiscoveryServiceClient {
	
	/**
	 * This method is called to inform the actual remote worker providers that are available.
	 * @param workerProviders A list of available Remote Worker Providers.
	 */
	void hereIsRemoteWorkerProviderList(List<String> workerProviders);

	/**
	 * Informs the actual discovery services available.
	 * @param discoveryServices A list of available Discovery Services.
	 */
	void hereAreDiscoveryServices(List<String> discoveryServices);

	/**
	 * Tells the peer the DiscoveryService is overloaded
	 * @param dsAddress
	 */
	void dsIsOverloaded(String dsAddress);
}
