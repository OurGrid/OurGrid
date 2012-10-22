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
package org.ourgrid.worker.ui.sync;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.interfaces.management.WorkerManager;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;
import org.ourgrid.common.interfaces.status.WorkerStatusProviderClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;

import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncManagerClient;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerSyncManagerClient extends SyncManagerClient<WorkerManager> implements WorkerStatusProviderClient, 
	WorkerControlClient{

	public void hereIsCompleteStatus(WorkerCompleteStatus completeStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), completeStatus);
		
	}

	public void hereIsMasterPeer(ServiceID masterPeer) {
		SyncContainerUtil.putResponseObject(getQueue(), masterPeer);
		
	}

	public void hereIsStatus(WorkerStatus workerStatus) {
		SyncContainerUtil.putResponseObject(getQueue(), workerStatus);
		
	}

	@Override
	@RecoveryNotification
	public void controlIsUp(WorkerManager control) {
		super.controlIsUp(control);
	}
	
	@Override
	@FailureNotification
	public void controlIsDown(WorkerManager control) {
		super.controlIsDown(control);
	}

	@Override
	public void hereIsMasterPeer(DeploymentID masterPeer) {
		// TODO Auto-generated method stub
		
	}

}
