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
package org.ourgrid.peer.business.controller.actions;

import java.io.Serializable;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepeatedAction;

/**
 * Action that calls saveRanking() method on the <code>PeerAccounting</code>
 * It is stored on the <code>Component</codes> on the peer initialization.
 *
 */
public class SaveAccountingAction implements RepeatedAction{

	public void run(Serializable handler, ServiceManager serviceManager) {
		WorkerManagementClient workerManagementClient = (WorkerManagementClient) serviceManager.getObjectDeployment(
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME).getObject();
		workerManagementClient.saveRanking();
	}

}
