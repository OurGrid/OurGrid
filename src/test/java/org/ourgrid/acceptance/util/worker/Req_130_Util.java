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

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.worker.WorkerComponent;
import org.ourgrid.worker.business.messages.WorkerControllerMessages;
import org.ourgrid.worker.communication.receiver.WorkerReceiver;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_130_Util extends WorkerAcceptanceUtil {

	public Req_130_Util(ModuleContext context) {
		super(context);
	}

	public void workerClientIsUp(WorkerComponent component, DeploymentID brokerID) {
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);
		WorkerReceiver worker = (WorkerReceiver) getWorker();

		EasyMock.reset(workerClient);
		EasyMock.reset(newLogger);

		newLogger.warn(WorkerControllerMessages.getTryToDoWorkerClientIsUpMessage());

		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);

		AcceptanceTestUtil.publishTestObject(component,
				brokerID, workerClient, WorkerClient.class);

		AcceptanceTestUtil.setExecutionContext(component,
				getWorkerManagementDeployment(), brokerID);

		worker.workerClientIsUp(workerClient);

		EasyMock.verify(workerClient);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}

	public void workerClientIsDown(WorkerComponent component, DeploymentID brokerID) {
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerClient workerClient = EasyMock.createMock(WorkerClient.class);
		WorkerReceiver worker = (WorkerReceiver) getWorker();

		EasyMock.reset(workerClient);
		EasyMock.reset(newLogger);

		EasyMock.replay(workerClient);
		EasyMock.replay(newLogger);

		AcceptanceTestUtil.publishTestObject(component,
				brokerID, workerClient, WorkerClient.class);

		AcceptanceTestUtil.setExecutionContext(component,
				getWorkerManagementDeployment(), brokerID);

		worker.workerClientIsDown(workerClient);

		EasyMock.verify(workerClient);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}

}
