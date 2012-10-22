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

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.bouncycastle.jce.provider.JDKKeyFactory.X509;
import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.matchers.ControlOperationResultMatcher;
import org.ourgrid.matchers.SaveAccountingRepetitionRunnableMatcher;
import org.ourgrid.matchers.WorkerLoginResultMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.accounting.AccountingConstants;
import org.ourgrid.worker.WorkerConstants;

import sun.security.provider.certpath.X509CertPath;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.network.signature.SignatureProperties;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_010_Util extends PeerAcceptanceUtil {

	public Req_010_Util(ModuleContext context) {
		super(context);
	}
	
	public void workerLoginAgain(PeerComponent component, WorkerSpecification workerSpecification, DeploymentID wmDID) {
		workerLogin(component, workerSpecification, wmDID, true);
	}
	
	public void workerLogin(PeerComponent component, WorkerSpecification workerSpecification, DeploymentID wmDID) {
		workerLogin(component, workerSpecification, wmDID, false);
	}

	public void workerLogin(PeerComponent component, WorkerSpecification workerSpecification, DeploymentID wmDID,
			boolean alreadyLogged) {
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		WorkerManagementClient workerManagementClientProxy = getWorkerManagementClientProxy();
		ObjectDeployment workerManagementClientDeployment = getWorkerManagementClientDeployment();

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		WorkerManagement workerManagementMock = EasyMock
				.createMock(WorkerManagement.class);

		EasyMock.reset(workerManagementMock);
		EasyMock.reset(newLogger);

		X509CertPath workerCertPath = AcceptanceTestUtil
				.getCertificateMock(wmDID);
		
		AcceptanceTestUtil.publishTestObject(component, wmDID,
				workerManagementMock, WorkerManagement.class);

		workerManagementMock.loginSucceeded(
				EasyMock.same(workerManagementClient),
				WorkerLoginResultMatcher.noError());
		
		if (alreadyLogged) {
			newLogger.warn("The worker [" + wmDID.getServiceID() + "] was identified " +
					"but it is already logged. Maybe it is recovering? Login with success.");
		}
		
		newLogger.info("The worker [" + wmDID.getServiceID() + "] was identified. " +
				"Login with success.");

		EasyMock.replay(workerManagementMock);
		EasyMock.replay(newLogger);

	    //Login into peer
		AcceptanceTestUtil.setExecutionContext(component,
				workerManagementClientDeployment, wmDID, workerCertPath);
		workerManagementClientProxy.workerLogin(workerManagementMock, workerSpecification);

		EasyMock.verify(workerManagementMock);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}
	
	/**
	 * Sets workers in a peer according to their <code>WorkerSpec</code>, setting the sender public key
	 * @param workers Attributes of the workers to be set
	 * @param senderPubKey The sender public key
	 */
	public void workerLogin(PeerComponent component, List<WorkerSpecification> workers) {
		WorkerManagementClient workerManagementClient = getWorkerManagementClient();
		WorkerManagementClient workerManagementClientProxy = getWorkerManagementClientProxy();
		ObjectDeployment workerManagementClientDeployment = getWorkerManagementClientDeployment();

		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		int i = 1;
		for (WorkerSpecification workerSpecification : workers) {
			WorkerManagement workerManagementMock = EasyMock.createMock(WorkerManagement.class);
			
			EasyMock.reset(workerManagementMock);
			EasyMock.reset(newLogger);
			
			String workerPublicKey = workerSpecification.getServiceID().getPublicKey();
			
			//the worker must have a valid public key
			if (workerPublicKey == null) {
				workerPublicKey = "publicKey" + i++;
			}
			
			DeploymentID workerManagementID = new DeploymentID(new ContainerID(workerSpecification.getUser(), 
					workerSpecification.getServer(), WorkerConstants.WORKER, 
					workerPublicKey), WorkerConstants.LOCAL_WORKER_MANAGEMENT);
			
			X509CertPath workerCertPath = AcceptanceTestUtil.getCertificateMock(workerManagementID);
		
			AcceptanceTestUtil.setExecutionContext(component, workerManagementClientDeployment, 
					workerManagementID, workerCertPath);
			AcceptanceTestUtil.publishTestObject(component, workerManagementID, workerManagementMock, 
					WorkerManagement.class);
			
			workerManagementMock.loginSucceeded(
					EasyMock.same(workerManagementClient), WorkerLoginResultMatcher.noError());
			newLogger.info("The worker [" + workerManagementID.getServiceID() + "] was identified. " +
					"Login with success.");
			
			EasyMock.replay(workerManagementMock);
			EasyMock.replay(newLogger);
			
			workerManagementClientProxy.workerLogin(workerManagementMock, workerSpecification);
			
			EasyMock.verify(workerManagementMock);
			EasyMock.verify(newLogger);
		}
		
		component.setLogger(oldLogger);
	}

	public void setWorkersWithWrongPubKey(PeerComponent component, List<WorkerSpecification> workers, String senderPubKey) {
		// Get the logger mock
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);

		// Create client mock
		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);

		// Get peer control
		PeerControl peerControl = getPeerControl();
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		// Verifies if this is my public key
		if (senderPubKey.equals(peerControlDeployment.getDeploymentID().getPublicKey())) {
			peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		} else {
			newLogger.warn("An unknown entity tried to set the workers. Only the local modules can perform this operation." +
					" Unknown entity public key: [" + senderPubKey + "].");
		}

		EasyMock.replay(peerControlClientMock);
		EasyMock.replay(newLogger);

		// defining workers on peer
		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment,senderPubKey);
		//	    peerControl.setWorkers(peerControlClientMock, workers);

		EasyMock.verify(peerControlClientMock);
		EasyMock.verify(newLogger);

		component.setLogger(oldLogger);
	}


	/**
	 * Creates mocks for timer and logger and starts the peer
	 * @param component The peer component to be started
	 * @return The peer component
	 */
	public PeerComponent startPeer(PeerComponent component) {
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		return startPeer(component, peerControlDeployment.getDeploymentID().getPublicKey());
	}

	/**
	 * Creates mocks for timer and logger and starts the peer
	 * @param component The peer component to be started
	 * @param senderPublicKey The sender public key
	 * @return The peer component
	 */
	@SuppressWarnings("unchecked")
	public PeerComponent startPeer(PeerComponent component, String senderPublicKey) {
		ScheduledExecutorService oldTimer = component.getTimer();
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(newTimer);

		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(logger);

		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		boolean rightPubKey = senderPublicKey.equals(peerControlDeployment.getDeploymentID().getPublicKey()); 
		if (rightPubKey) {
			peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
		} else {
			logger.warn("An unknown entity tried to start the Peer. Only the local modules can perform this operation." +
					" Unknown entity public key: [" + senderPublicKey + "].");
			EasyMock.replay(logger);
		}

		EasyMock.replay(peerControlClientMock);

		ScheduledFuture<?> future = EasyMock.createMock(ScheduledFuture.class);
		EasyMock.replay(future);

		PeerManager peerManager = (PeerManager) getPeerControl();

		RepetitionRunnable runnableSaveAcc = 
				new RepetitionRunnable(component, peerManager, PeerConstants.SAVE_ACCOUNTING_ACTION_NAME, null);

		RepetitionRunnable runnableUpdateAcc = 
				new RepetitionRunnable(component, peerManager, PeerConstants.UPDATE_PEER_UPTIME_ACTION_NAME, null);

		RepetitionRunnable runnableUpdateGc = 
				new RepetitionRunnable(component, peerManager, PeerConstants.INVOKE_GARBAGE_COLLECTOR_ACTION_NAME, null);

		long frequenceAcc = AccountingConstants.RANKING_SAVING_FREQ;

		long frequenceGc = PeerConstants.INVOKE_GARBAGE_COLLECTOR_DELAY;

		//timer.scheduleWithFixedDelay(runnableSaveAcc, frequenceAcc, frequenceAcc, TimeUnit.SECONDS);

		if (rightPubKey) {
			EasyMock.expect( (ScheduledFuture) newTimer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableSaveAcc),
					EasyMock.eq(frequenceAcc), EasyMock.eq(frequenceAcc),
					EasyMock.same(TimeUnit.SECONDS))).andReturn(future);

			EasyMock.expect( (ScheduledFuture) newTimer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableUpdateAcc),
					EasyMock.eq(frequenceAcc), EasyMock.eq(frequenceAcc),
					EasyMock.same(TimeUnit.SECONDS))).andReturn(future);

			EasyMock.expect( (ScheduledFuture) newTimer.scheduleWithFixedDelay(SaveAccountingRepetitionRunnableMatcher.eqMatcher(runnableUpdateGc),
					EasyMock.eq(frequenceGc), EasyMock.eq(frequenceGc),
					EasyMock.same(TimeUnit.SECONDS))).andReturn(future);
		}

		EasyMock.replay(newTimer);

		DeploymentID peerClientID = new DeploymentID(new ContainerID("broker", "broker", "broker", senderPublicKey),"broker");

		AcceptanceTestUtil.publishTestObject(component, peerClientID, peerControlClientMock, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, senderPublicKey);
		peerManager.start(peerControlClientMock);

		EasyMock.verify(peerControlClientMock);
		EasyMock.verify(newTimer);
		EasyMock.verify(future);

		if (!rightPubKey) {
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}

		component.setTimer(oldTimer);

		return component;
	}


	public PeerComponent query(PeerComponent component, String query, String result) {
		String senderPublicKey = context.getProperty(SignatureProperties.PROP_PUBLIC_KEY);
		return query(component, senderPublicKey, query, result);
	}

	public PeerComponent query(PeerComponent component, String senderPublicKey, String query, String result) {
		ScheduledExecutorService oldTimer = component.getTimer();
		ScheduledExecutorService newTimer = EasyMock.createMock(ScheduledExecutorService.class);
		component.setTimer(newTimer);

		CommuneLogger logger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(logger);

		PeerControlClient peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		boolean rightPubKey = senderPublicKey.equals(peerControlDeployment.getDeploymentID().getPublicKey()); 
		if (rightPubKey) {
			peerControlClientMock.operationSucceed(ControlOperationResultMatcher.withResult(result));
		} else {
			logger.warn("An unknown entity tried to query the Peer. Only the local modules can perform this operation." +
					" Unknown entity public key: [" + senderPublicKey + "].");
			EasyMock.replay(logger);
		}

		EasyMock.replay(peerControlClientMock);

		ScheduledFuture<?> future = EasyMock.createMock(ScheduledFuture.class);
		EasyMock.replay(future);

		PeerManager peerManager = (PeerManager) getPeerControl();

		EasyMock.replay(newTimer);

		DeploymentID peerClientID = new DeploymentID(new ContainerID("broker", "broker", "broker", senderPublicKey),"broker");

		AcceptanceTestUtil.publishTestObject(component, peerClientID, peerControlClientMock, PeerControlClient.class);

		AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, senderPublicKey);
		peerManager.query(peerControlClientMock, query);

		EasyMock.verify(peerControlClientMock);
		EasyMock.verify(newTimer);
		EasyMock.verify(future);

		if (!rightPubKey) {
			EasyMock.verify(logger);
			EasyMock.reset(logger);
		}

		component.setTimer(oldTimer);

		return component;
	}

	/**
	 * Creates a peer component ands starts the peer
	 * @return The created peer component
	 * @throws Exception
	 */
	public PeerComponent startPeer() throws Exception {
		PeerComponent component = createPeerComponent(context);
		component = startPeer(component, context.getProperty(SignatureProperties.PROP_PUBLIC_KEY));
		application = component;
		return component;
	}

	/**
	 * Stops the peer verifying logging for NOF Ranking file saving
	 * @param component The peer component
	 */
	public void notNiceStopPeer(PeerComponent component) {
		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		if(peerControlDeployment == null) {
			return;
		}

		stopPeer(component, false, peerControlDeployment.getDeploymentID().getPublicKey());
	}

	/**
	 * Stops the peer without verifying logging
	 * @param component The peer component
	 */
	public void niceStopPeer(PeerComponent component) {
		if (component == null) {
			return;
		}

		ObjectDeployment deployment = getPeerControlDeployment();

		if(deployment == null) {
			return;
		}

		stopPeer(component, true, deployment.getDeploymentID().getPublicKey());
	}

	/**
	 * Stops the peer verifying logging for NOF Ranking file saving
	 * @param component The peer component
	 * @param senderPubKey The publicKey of the component that asked the peer to stop
	 */
	public void notNiceStopPeer(PeerComponent component, String senderPubKey) {
		stopPeer(component, false, senderPubKey);
	}

	/**
	 * Stops the peer without verifying logging
	 * @param component The peer component
	 * @param senderPubKey The publicKey of the component that asked the peer to stop
	 */
	public void niceStopPeer(PeerComponent component, String senderPubKey) {
		stopPeer(component, true, senderPubKey);
	}


	private void stopPeer(PeerComponent component, boolean nice, String senderPubKey) {
		if(component == null) {
			return;
		}

		PeerControlClient peerControlClientMock = null;
		CommuneLogger oldLogger = component.getLogger();
		CommuneLogger newLogger = null;

		ObjectDeployment peerControlDeployment = getPeerControlDeployment();

		if (nice) {
			peerControlClientMock = EasyMock.createNiceMock(PeerControlClient.class);
			newLogger = EasyMock.createNiceMock(CommuneLogger.class);
		} else {
			peerControlClientMock = EasyMock.createMock(PeerControlClient.class);
			newLogger = EasyMock.createMock(CommuneLogger.class);

			if(senderPubKey.equals(peerControlDeployment.getDeploymentID().getPublicKey())) {
				newLogger.debug("Saving the Network of favours data.");
				peerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
			} else {
				newLogger.warn("An unknown entity tried to stop the Peer. Only the local modules can perform this operation." +
						" Unknown entity public key: [" + senderPubKey + "].");
				newLogger.warn("An unknown sender tried to save the Network of favors data. This message was ignored." +
						" Sender public key: " + senderPubKey);
			}

			EasyMock.replay(peerControlClientMock);
			EasyMock.replay(newLogger);
		}

		component.setLogger(newLogger);

		// Stop the peer
		if (peerControlDeployment != null) {
			DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "pcc", "peer", senderPubKey), "broker");

			AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClientMock, PeerControlClient.class);
			AcceptanceTestUtil.setExecutionContext(component, peerControlDeployment, senderPubKey);
			getPeerControl().stop(false, false, peerControlClientMock);
		}

		if (!nice) {
			EasyMock.verify(peerControlClientMock);
			EasyMock.verify(newLogger);
		}

		component.setLogger(oldLogger);
	}

}