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
package org.ourgrid.common.interfaces.management;

import br.edu.ufcg.lsd.commune.api.Remote;

/**
 * 
 * Provides methods to the Remote Worker Management.
 * 
 * Through this interface, the Remote Peer can:
 * - Order that the Worker be allocated to a Broker.
 */
@Remote
public interface RemoteWorkerManagement {

	/**
	 * Order that the Worker be allocated to a Broker.
	 * @param remotePeer Remote Peer
	 * @param brokerPubKey Broker public key.
	 */
	public void workForBroker(RemoteWorkerManagementClient remotePeer, 
			String brokerPubKey);

}
