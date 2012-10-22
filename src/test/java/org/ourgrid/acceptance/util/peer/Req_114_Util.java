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
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.peer.PeerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_114_Util extends PeerAcceptanceUtil {

	public Req_114_Util(ModuleContext context) {
		super(context);
	}
	
	public void notifyRemoteWorkerFailure(PeerComponent component, DeploymentID rwmDeploymentID, 
			RemoteWorkerProvider rwp, DeploymentID rwpDeploymentID) {
		notifyRemoteWorkerFailure(component, false, rwmDeploymentID, rwp, rwpDeploymentID);
	}

	/**
	 * Notifies a remote worker failure and expects the peer to log it.
	 * @param component The peer component
	 * @param rwmDeploymentID The DeploymentID of the <code>RemoteWorkerManagement</code> interface of this worker.
	 * @param rwp The RemoteWorkerProvider of this worker
	 */
	public void notifyRemoteWorkerFailure(PeerComponent component, boolean deliveredWorker, 
			DeploymentID rwmDeploymentID, RemoteWorkerProvider rwp, DeploymentID rwpDeploymentID) {
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		if (deliveredWorker) {
			newLogger.debug("Failure of a remote Worker [" + rwmDeploymentID.getServiceID() + "] that was already delivered.");
		}
		
		component.setLogger(newLogger);
		newLogger.debug("The remote Worker [" + rwmDeploymentID.getServiceID() + "] has failed. Disposing this Worker.");
		EasyMock.replay(newLogger);
		
		EasyMock.reset(rwp);
		
		RemoteWorkerManagement remoteWorker = (RemoteWorkerManagement) AcceptanceTestUtil.getBoundObject(rwmDeploymentID);
		
		rwp.disposeWorker(rwmDeploymentID.getServiceID());
		EasyMock.replay(rwp);
		
		getRemoteWorkerMonitor().doNotifyFailure(remoteWorker, rwmDeploymentID);
		
		EasyMock.verify(rwp);
		EasyMock.verify(newLogger);
		
		component.setLogger(oldLogger);
	}

}