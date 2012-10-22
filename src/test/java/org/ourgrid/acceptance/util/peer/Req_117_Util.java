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

import java.util.concurrent.ScheduledFuture;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.peer.PeerComponent;

import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;



public class Req_117_Util extends PeerAcceptanceUtil {

	public Req_117_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Pauses a request
	 * 
	 * @param requestSpec Request specification
	 * @param future1 Future to be canceled. If there is no future to be canceled, pass null as parameter.
	 * 
	 */
	public void pauseRequest(PeerComponent component, DeploymentID lwpcOID, long requestID, ScheduledFuture<?> future1) {
		
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLogger);
		
		if (future1 != null) {
	
			EasyMock.reset(future1);
			EasyMock.expect(future1.cancel(true)).andReturn(true);
			EasyMock.replay(future1);
		}
	
		newLogger.debug("Request " + requestID + ": Consumer [" + lwpcOID.getServiceID() + "] paused the request.");
		PeerAcceptanceUtil.replay(newLogger);
		
		
		LocalWorkerProvider lwp = getLocalWorkerProviderProxy();
		AcceptanceTestUtil.setExecutionContext(component, getLocalWorkerProviderDeployment(), lwpcOID);
		
		lwp.pauseRequest(requestID);		
		
		PeerAcceptanceUtil.verify(newLogger);
		
		if (future1 != null) {
			EasyMock.verify(future1);
		}
		
		component.setLogger(oldLogger);
	}
	

}