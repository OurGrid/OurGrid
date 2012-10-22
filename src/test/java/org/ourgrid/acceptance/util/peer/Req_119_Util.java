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
package org.ourgrid.acceptance.util.peer;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_119_Util extends PeerAcceptanceUtil {
	
	public Req_119_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Notifies a remote consumer failure and logs it.
	 * Expects workers allocated to this consumer to stop working.
	 * @param peerComponent The peer component
	 * @param remoteClient The <code>RemoteWorkerProviderClient</code> interface of the remote consumer
	 * @param allocations The expected allocations to be made after remote consumer failure.
	 */
	public void notifyRemoteConsumerFailure(PeerComponent peerComponent,
			RemoteWorkerProvider remoteConsumer, DeploymentID remoteConsumerID, 
			boolean removeFromDAO, WorkerAllocation... allocations) {
	
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
	
		peerComponent.setLogger(newLogger);
	
		// Expect the local worker to stop working
		for (WorkerAllocation allocation : allocations) {
			WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(allocation.workerID);
			
			EasyMock.reset(workerManag);
			
			workerManag.stopWorking();
			
			EasyMock.replay(workerManag);
		}
		
		// Expect the info to be logged
		if (removeFromDAO) {
			newLogger.info("The RemoteWorkerProvider ["
					+ createProviderAddress(remoteConsumerID.getContainerID().getUserAtServer())
					+ "] has failed.");
		} else {
			newLogger.warn("The RemoteWorkerProvider ["
					+ createProviderAddress(remoteConsumerID.getContainerID().getUserAtServer())
					+ "] has failed but it was not removed from DAO.");
		}
		
		newLogger.info("The remote consumer ["
						+ remoteConsumerID.getServiceID()
						+ "] has failed. Disposing workers allocated to this consumer.");
		EasyMock.replay(newLogger);
		
		getRemoteWorkerProviderClient().workerProviderIsDown(remoteConsumer, remoteConsumerID, null);
	
		for (WorkerAllocation allocation : allocations) {
			WorkerManagement workerManag = (WorkerManagement) AcceptanceTestUtil.getBoundObject(allocation.workerID);
			EasyMock.verify(workerManag);
		}
		
		EasyMock.verify(newLogger);
	
		peerComponent.setLogger(oldLogger);
	}
	
	private String createProviderAddress(String userAtServer) {
		return StringUtil.userAtServerToAddress(userAtServer, PeerConstants.MODULE_NAME, 
				PeerConstants.REMOTE_WORKER_PROVIDER);
	}
	
}