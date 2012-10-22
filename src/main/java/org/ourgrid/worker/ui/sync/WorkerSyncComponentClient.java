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

import org.ourgrid.common.interfaces.management.WorkerManager;
import org.ourgrid.common.interfaces.status.WorkerCompleteStatus;

import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncApplicationClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.sync.SyncContainerUtil;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 *
 */
public class WorkerSyncComponentClient extends SyncApplicationClient<WorkerManager, WorkerSyncManagerClient> {

	public WorkerSyncComponentClient(ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super("WORKER_SYNC_UI", context);
	}
	
	public WorkerSyncComponentClient(ModuleContext context, boolean waitForever) throws CommuneNetworkException, ProcessorStartException {
		super("WORKER_SYNC_UI", context, waitForever);
	}

	public ControlOperationResult pauseWorker() {
		getManager().pause(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}

	public ControlOperationResult resumeWorker() {
		getManager().resume(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, ControlOperationResult.class, getQueueTimeout());
	}
	
	public WorkerCompleteStatus getWorkerCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
		return SyncContainerUtil.waitForResponseObject(queue, WorkerCompleteStatus.class, getQueueTimeout());
	}
	
	@Override
	protected InitializationContext<WorkerManager, WorkerSyncManagerClient> createInitializationContext() {
		return new WorkerSyncInitializationContext();
	}

}
