/*
 * Copyright (C) 2011 Universidade Federal de Campina Grande
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
package org.ourgrid.acceptance.util.worker;

import java.util.concurrent.Future;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.executor.ExecutorException;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.interfaces.to.WorkerStatus;
import org.ourgrid.worker.WorkerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;

public class Req_125_Util extends WorkerAcceptanceUtil{
	
	public Req_125_Util(ModuleContext context) {
		super(context);
	}

	public void allocationError(WorkerComponent component, Future<?> prepFuture) {
		allocationError(component, null, prepFuture, true, null);
	}
	
	public void allocationError(WorkerComponent component, WorkerManagementClient wmc,
			Future<?> prepFuture) {
		allocationError(component, wmc, prepFuture, true, null);
	}
	
	public void allocationErrorOnNotPreparingWorker(
			WorkerComponent component, WorkerStatus status) {
		allocationError(component, null, null, false, status);
	}
	
	public void allocationErrorOnRemoteExecuteWorker(WorkerComponent component,
			RemoteWorkerManagementClient rwmc, WorkerStatus status) {
		allocationError(component, null, null, false, status);
	}
	
	public void allocationError(WorkerComponent component, WorkerManagementClient wmc,
			Future<?> prepFuture, boolean isPreparingState, WorkerStatus status) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		String cmd = "Preparing Allocation";

		if (!isPreparingState) {
			newLogger.warn("The worker was not in the Preparing Allocation" +
					" state. It was in the state: " + status);
		} else {
			if (wmc != null) {
				EasyMock.reset(wmc);
				wmc.statusChanged(WorkerStatus.ERROR);
				EasyMock.replay(wmc);
			}
			
			newLogger.error("A error ocurred while worker was creating the" +
					" execution environment. Error: Command: " + cmd);
			
			if (prepFuture != null) {
				EasyMock.reset(prepFuture);
				EasyMock.expect(
						prepFuture.isDone()).andReturn(true).anyTimes();
				EasyMock.replay(prepFuture);
			}
		}
		EasyMock.replay(newLogger);
		
		getWorkerExecutionClient().allocationError(new ExecutorException(cmd));
		
		if (wmc != null) {
			EasyMock.verify(wmc);
			EasyMock.reset(wmc);
		}
		
		EasyMock.verify(newLogger);
		component.setLogger(oldLogger);
	}

}
