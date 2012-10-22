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
package org.ourgrid.common.interfaces.control;

import org.ourgrid.common.specification.worker.WorkerSpecification;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleControl;

/**
 * <p>
 * Peer will receive every user and worker related instruction through this interface. The
 * basic allowed operations are:
 * <ul>
 * <li> Add a list of new workers (or a single worker);
 * <li> Add annotations to workers;
 * <li> Remove a worker;
 * <li> Add/remove a user.
 * </ul>
 * <p>
 * The correspondent callback calls or error notification should be made on the
 * <code>PeerControlClient</code> interface.
 */
@Remote
public interface PeerControl extends ModuleControl {

	/**
	 * Removes a worker.
	 * @param peerControlClient The client of PeerControl.
	 * @param workerSpec The WorkerSpec from the worker that will be removed.
	 */
	void removeWorker( PeerControlClient peerControlClient, WorkerSpecification workerSpec );
	
	/**
	 * Adds a new user.
	 * @param peerControlClient The client of PeerControl.
	 * @param login The user login (user@server).
	 */
	void addUser ( PeerControlClient peerControlClient, String login );
	
	/**
	 * Removes a user.
	 * @param peerControlClient The client of PeerControl.
	 * @param login The login of the user that will be removed.
	 */
	void removeUser ( PeerControlClient peerControlClient, String login);
	
	/**
	 * Updates the peer up time.
	 */
	void updatePeerUpTime();
	
	/**
	 * Executes a SQL SELECT in the database of the Peer
	 * @param peerControlClient The client of PeerControl.
	 * @param query SELECT query to be executed in the peer database.
	 */
	void query ( PeerControlClient peerControlClient, String query);

}

