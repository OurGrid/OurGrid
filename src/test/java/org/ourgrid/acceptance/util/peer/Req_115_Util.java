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


import java.io.IOException;
import java.io.StreamCorruptedException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.matchers.SaveAccountingRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.accounting.AccountingConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ControlOperationResult;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_115_Util extends PeerAcceptanceUtil {

	public Req_115_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Saves the NOF ranking file.
	 * @param peerComponent The peer component
	 */
	public void saveRanking(PeerComponent peerComponent) {
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		
		peerComponent.setLogger(newLogger);
		
		ObjectDeployment peerOD = getPeerControlDeployment();
		
		newLogger.debug("Saving the Network of favours data.");
		EasyMock.replay(newLogger);
		
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		ObjectDeployment paOD = getWorkerManagementClientDeployment();
		
		AcceptanceTestUtil.setExecutionContext(peerComponent, paOD, peerOD.getDeploymentID().getPublicKey());
		workerManagementClient.saveRanking();
		
		EasyMock.verify(newLogger);
		
		peerComponent.setLogger(oldLogger);
	}

	/**
	 * Start the peer verifying accounting saving
	 * @param <U>
	 * @param component
	 * @return
	 */
	public <U extends Exception> Module startPeerVerifyingAccountingSavingWithNoError(
			PeerComponent component) {
		return startPeerVerifyingAccountingSaving(component, null, null, false);
	}

	/**
	 * Expects a reading error while starting the peer verifying accounting saving
	 * @param <U>
	 * @param component
	 * @param logMessage
	 * @return
	 */
	public <U extends Exception> Module startPeerVerifyingAccountingSavingWithReadingError(
			PeerComponent component, String logMessage) {
		return startPeerVerifyingAccountingSaving(component, logMessage, IOException.class, false);
	}

	/**
	 * Expects a corrupted data error while starting the peer verifying accounting saving
	 * @param <U>
	 * @param component
	 * @param logMessage
	 * @return
	 */
	public <U extends Exception> Module startPeerVerifyingAccountingSavingWithCorruptedData(
			PeerComponent component, String logMessage) {
		return startPeerVerifyingAccountingSaving(component, logMessage, StreamCorruptedException.class, true);
	}

	/**
	 * @param <U>
	 * @param component
	 * @param logMessage
	 * @param clazz
	 * @param fatal
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <U extends Exception> Module startPeerVerifyingAccountingSaving(PeerComponent component,
			String logMessage, Class<U> clazz, boolean fatal) {
		
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(timer);
		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(logger);
		
		if (logMessage != null && clazz != null) {
			if (fatal) {
				logger.fatal(EasyMock.eq(logMessage), EasyMock.isA(clazz));
			} else {
				logger.debug(EasyMock.eq(logMessage), EasyMock.isA(clazz));
			}
		}
		
		if (!fatal) {
			logger.info("Peer has been successfully started.");
		}
		
		EasyMock.replay(logger);
		
		PeerControl peerControl = getPeerControl();
		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "pccServer", DiscoveryServiceConstants.MODULE_NAME, 
				peerControlDeployment.getDeploymentID().getPublicKey()),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		AcceptanceTestUtil.publishTestObject(application, pccID, peerControlClientMock,
				PeerControlClient.class);
		
		if(fatal && logMessage != null && clazz != null) {
			peerControlClientMock.operationSucceed(EasyMock.isA(ControlOperationResult.class));
		} else {
			peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		}
		   	
		EasyMock.replay(peerControlClientMock);
		
		ScheduledFuture<?> future = EasyMock.createMock(ScheduledFuture.class);
		EasyMock.replay(future);
		
		PeerManager peerManager = (PeerManager) getPeerControl();
		
		RepetitionRunnable runnableSaveAcc = new RepetitionRunnable(component, peerManager, 
				PeerConstants.SAVE_ACCOUNTING_ACTION_NAME, null);
		
		RepetitionRunnable runnableUpdateTime = 
			new RepetitionRunnable(component, peerManager, PeerConstants.UPDATE_PEER_UPTIME_ACTION_NAME, null);
		
		RepetitionRunnable runnableInvokeGarbageCollector = 
			new RepetitionRunnable(component, peerManager, PeerConstants.INVOKE_GARBAGE_COLLECTOR_ACTION_NAME, null);
		
		long frequenceAcc = AccountingConstants.RANKING_SAVING_FREQ;
		
		EasyMock.expect( (ScheduledFuture) timer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableSaveAcc),
				EasyMock.eq(frequenceAcc), EasyMock.eq(frequenceAcc),
				EasyMock.same(TimeUnit.SECONDS))).andReturn(future);
		
		EasyMock.expect( (ScheduledFuture) timer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableUpdateTime),
				EasyMock.eq(frequenceAcc), EasyMock.eq(frequenceAcc),
				EasyMock.same(TimeUnit.SECONDS))).andReturn(future);
		
		long invokeGCDelay = PeerConstants.INVOKE_GARBAGE_COLLECTOR_DELAY;
		
		EasyMock.expect( (ScheduledFuture) timer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableInvokeGarbageCollector),
				EasyMock.eq(invokeGCDelay), EasyMock.eq(invokeGCDelay),
				EasyMock.same(TimeUnit.SECONDS))).andReturn(future);
		
		EasyMock.replay(timer);
		
		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, peerControlDeployment.getDeploymentID().getPublicKey());
		peerControl.start(peerControlClientMock);
		
		EasyMock.verify(peerControlClientMock);
		EasyMock.verify(timer);
		EasyMock.verify(future);
		EasyMock.verify(logger);
		
		return component;
	} 

}