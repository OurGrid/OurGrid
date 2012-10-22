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


public class Req_116_Util extends PeerAcceptanceUtil {

	public Req_116_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Updates a request specification.
	 * If the request is not locally fulfilled, it is rescheduled for repetition.
	 * @param component The peer component
	 * @param newRequestSpec The new request spec to be set.
	 * @param needScheduleRequest True if the request must be schedule, false otherwise.
	 * @param brokerPubKey The consumer public key
	 * @return The future of the updated request
	 */
	public ScheduledFuture<?> updateRequest(PeerComponent component, RequestSpecification newRequestSpec, boolean needScheduleRequest,
			DeploymentID brokerID) {
		
		return updateRequest(component, newRequestSpec, needScheduleRequest, null, brokerID);
	}

	/**
	 * Updates a request specification.
	 * If the request is not locally fulfilled, it is rescheduled for repetition.
	 * If a <code>ScheduledFuture</code> is passed as parameter, it is expected to be canceled
	 * @param component The peer component
	 * @param newRequestSpec The new request spec to be set.
	 * @param needScheduleRequest True if the request must be schedule, false otherwise.
	 * @param brokerPubKey The consumer public key
	 * @param future A request future to be canceled.
	 * @return The future of the updated request
	 */
	@SuppressWarnings("unchecked")
	public ScheduledFuture<?> updateRequest(PeerComponent component, RequestSpecification newRequestSpec, boolean needScheduleRequest,
			ScheduledFuture<?> future, DeploymentID brokerID) {
	
		if (future != null) {
			EasyMock.expect(future.cancel(true)).andReturn(true);
			EasyMock.replay(future);
		}
		
		ScheduledExecutorService oldTimerMock = component.getTimer();
		CommuneLogger oldLoggerMock = component.getLogger();
		
		//Create Mocks
		ScheduledExecutorService newTimerMock = EasyMock.createMock(ScheduledExecutorService.class);
		CommuneLogger newLoggerMock = EasyMock.createMock(CommuneLogger.class);
		
		component.setLogger(newLoggerMock);
		component.setTimer(newTimerMock);
		
		ScheduledFuture<?> newFuture = EasyMock.createMock(ScheduledFuture.class);
		
		if (needScheduleRequest) {
			
			//Records Mocks behavior
			long delay = component.getContext().parseIntegerProperty(PeerConfiguration.PROP_REPEAT_REQUEST_DELAY);
			
			RepetitionRunnable runnable = createRequestWorkersRunnable(component, newRequestSpec.getRequestId());
	    	
			EasyMock.expect((ScheduledFuture) newTimerMock.scheduleWithFixedDelay(RequestRepetitionRunnableMatcher.eqMatcher(runnable), 
					eq(delay), eq(delay), eq(TimeUnit.SECONDS))).andReturn(newFuture).once();
			
			
			newLoggerMock.debug("Request "+newRequestSpec.getRequestId()+": request scheduled for repetition in "+delay+" seconds.");
			
			EasyMock.replay(newLoggerMock);
			EasyMock.replay(newTimerMock);
		}
		
		LocalWorkerProvider lwp = getLocalWorkerProviderProxy();
		ObjectDeployment lwpOD = getLocalWorkerProviderDeployment();
		AcceptanceTestUtil.setExecutionContext(component, lwpOD, brokerID);
		lwp.updateRequest(newRequestSpec);
		
		//Verify Mocks behavior
		if (needScheduleRequest) {
			EasyMock.verify(newLoggerMock);
			EasyMock.verify(newTimerMock);
		}
		
		if (future != null) {
			EasyMock.verify(future);
		}
		
		component.setLogger(oldLoggerMock);
		component.setTimer(oldTimerMock);
		
		return newFuture;
	}
	

	
}