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

import static org.easymock.EasyMock.eq;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.matchers.RequestRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;


public class Req_118_Util extends PeerAcceptanceUtil {

	public Req_118_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Resumes a request without rescheduling it for repetition
	 * @param requestSpec The RequestSpec of the request to be resumed
	 * @param lwpcOID The DeploymentID for the <code>LocalWorkerProviderClient</code> interface of the consumer
	 * @param peer The peer component
	 */
	public void resumeRequestWithNoReschedule(RequestSpecification requestSpec, DeploymentID lwpcOID, PeerComponent peer) {
		resumeRequest(requestSpec, lwpcOID, peer, false);
	}

	/**
	 * Resumes a request and reschedules it for repetition
	 * @param requestSpec The RequestSpec of the request to be resumed
	 * @param lwpcOID The DeploymentID for the <code>LocalWorkerProviderClient</code> interface of the consumer
	 * @param peer The peer component
	 * @return The future of the repeated request
	 */
	public ScheduledFuture<?> resumeRequest(RequestSpecification requestSpec, DeploymentID lwpcOID, PeerComponent peer) {
		return resumeRequest(requestSpec,lwpcOID, peer, true);
	}

	@SuppressWarnings("unchecked")
	private ScheduledFuture<?> resumeRequest(RequestSpecification requestSpec, DeploymentID lwpcOID, PeerComponent peer, boolean reschedule) {
		ScheduledExecutorService newTimer = null;
		ScheduledExecutorService oldTimer = null;
		
		ScheduledFuture<?> future = null;
	
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommuneLogger oldLogger = peer.getLogger();
		
		// Replace the logger
		peer.setLogger(newLogger);
	
		if(reschedule) {
		    
		    // Replace the timer
			newTimer = EasyMock.createMock(ScheduledExecutorService.class);
			oldTimer = peer.getTimer(); 
		    peer.setTimer(newTimer);
	
		    // Record mocks behavior
			long delay = peer.getContext().parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
			newLogger.debug("Request " + requestSpec.getRequestId() + ": request scheduled for repetition in " + delay + " seconds.");
	
			future = EasyMock.createMock(ScheduledFuture.class);
			
			RepetitionRunnable runnable = createRequestWorkersRunnable(peer, requestSpec.getRequestId());
	    	
			EasyMock.expect((ScheduledFuture) newTimer.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
					eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(future).once();
			
			// Replay mocks
			EasyMock.replay(newTimer);
		}
		
		newLogger.debug("Request " + requestSpec.getRequestId() + ": Consumer [" + lwpcOID.getServiceID() + "] resumed the request.");
		EasyMock.replay(newLogger);
		
		LocalWorkerProvider lwp = getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = getLocalWorkerProviderDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peer, lwpOD, lwpcOID);
		lwp.resumeRequest(requestSpec.getRequestId());
		
		EasyMock.verify(newLogger);

		if(reschedule) {
			// Verify mocks 
			EasyMock.verify(newTimer);
			
			// Reset old mocks
			peer.setTimer(oldTimer);
		}
		
		peer.setLogger(oldLogger);
		
		return future;
	}

	
}