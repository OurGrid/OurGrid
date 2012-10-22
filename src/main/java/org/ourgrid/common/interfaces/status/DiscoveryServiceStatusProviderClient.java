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

import org.ourgrid.discoveryservice.status.DiscoveryServiceCompleteStatus;

import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProviderClient;

/**
 * Entity that represents a DiscoveryServiceStatusProvider client. From this interface
 * the DiscoveryServiceStatusProvider receive the complete status.
 */
public interface DiscoveryServiceStatusProviderClient extends ModuleStatusProviderClient {

	
	/**
	 * Informs the actual complete status.
	 * @param completeStatus The actual complete status.
	 */
	void hereIsCompleteStatus(DiscoveryServiceCompleteStatus completeStatus);
	
}
