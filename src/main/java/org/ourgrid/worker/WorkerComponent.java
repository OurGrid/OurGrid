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
package org.ourgrid.worker;

import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.ourgrid.reqtrace.Req;
import org.ourgrid.worker.communication.receiver.WorkerComponentReceiver;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.ServerModule;
import br.edu.ufcg.lsd.commune.container.control.ModuleManager;
import br.edu.ufcg.lsd.commune.container.control.ServerModuleManager;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;

/**
 * Perform the Worker component control actions.
 */
@Req("REQ003")
public class WorkerComponent extends ServerModule {
	
	private ExecutorService threadPool = Executors.newSingleThreadExecutor();
	
	public WorkerComponent(ModuleContext context) throws CommuneNetworkException, ProcessorStartException {
		super(WorkerConstants.MODULE_NAME, context);
	}
	
	protected ServerModuleManager createApplicationManager() {
		return new WorkerComponentReceiver();
	}
	
	public Future<?> submitAction(Runnable runnable) {
		return threadPool.submit(runnable);
	}

	public <T extends Serializable> Future<?> submitAction(String actionName, T handler) {
		
		ModuleManager manager = 
			(ModuleManager) this.getObjectRepository().get(Module.CONTROL_OBJECT_NAME).getProxy();
		
		RepetitionRunnable runnable = new RepetitionRunnable(this, manager, actionName, handler);
		
		return threadPool.submit(runnable);
	}
	
	public ExecutorService getExecutorThreadPool() {
		return threadPool;
	}

	public void setExecutorThreadPool(ExecutorService pool) {
		this.threadPool = pool;
	}

}