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

import org.ourgrid.common.interfaces.control.WorkerControlClient;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.container.control.ModuleStatusProvider;

/**
 * This interface must be implemented by classes that will return
 * informations about general status of a worker. 
 *
 */
@Remote
public interface WorkerStatusProvider extends ModuleStatusProvider {

	/**
	 * This method returns to the callback the Workrer´s Master Peer.
	 * @param client The callback worker.
	 */
	void getMasterPeer(WorkerControlClient client);

	/**
	 * This method returns to the callback the status information about the worker.
	 * @param client The callback worker.
	 */
	void getStatus( WorkerControlClient client );
	
	/**
	 * This method returns to the callback complete information about the worker status.
	 * @param client The callback worker.
	 */
	void getCompleteStatus( WorkerControlClient client );
	
}
