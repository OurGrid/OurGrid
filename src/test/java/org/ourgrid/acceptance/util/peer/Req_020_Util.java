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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.matchers.DiscoveryServiceRepetitionRunnableMatcher;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.communication.dao.DiscoveryServiceAdvertDAO;
import org.ourgrid.peer.communication.receiver.DiscoveryServiceClientReceiver;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.container.servicemanager.actions.RepetitionRunnable;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_020_Util extends PeerAcceptanceUtil {
	
	private static long PROP_DS_UPDATE_INTERVAL = 60;

	public Req_020_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Verifies if the peer is interested on DiscoveryService 
	 * @param dsID the DiscoveryService peer objectID
	 * @return True if the peer is interested on ds , false otherwise
	 */
	public boolean isPeerInterestedOnDiscoveryService(PeerComponent component, ServiceID dsID) {
		ObjectDeployment dsMonitorOD = getDiscoveryServiceMonitorDeployment();
	    return AcceptanceTestUtil.isInterested(component, dsID, dsMonitorOD.getDeploymentID());
	}

//	/**
//	 * Creates a DiscoveryService peer mock, publishes it, and notifies its recovery
//	 * @param nwName DiscoveryService peer EntityID's user name
//	 * @param nwServer DiscoveryService peer EntityID's server name
//	 * @return The DeploymentID created for DiscoveryService mock to be published
//	 */
//	public DeploymentID notifyDiscoveryServiceRecovery(String nwName, String nwServer) {
//	    // Mock ds peer
//		Peer dsPeer = EasyMock.createMock(Peer.class);
//	
//		DeploymentID nwDeploymentID = 
//			AcceptanceTestUtil.publishTestObject(nwName, nwServer, PeerConstants.NODEWIZ_MODULE_NAME, 
//					PeerConstants.NODEWIZ_OBJECT_NAME, dsPeer);
//		
//		//Record mock behavior
//	    EasyMock.reset(dsPeer);
//	    EasyMock.expect(dsPeer.getDeploymentID()).andReturn(nwDeploymentID).times(4);
//	    EasyMock.replay(dsPeer);
//		
//	    return notifyDiscoveryServiceRecovery(dsPeer, nwDeploymentID);
//	}
	
//	/**
//	 * Creates a DiscoveryService peer mock, publishes it, and notifies its recovery
//	 * @param nwDeploymentID DiscoveryService peer DeploymentID
//	 */
//	public void notifyDiscoveryServiceRecovery(DeploymentID nwDeploymentID) {
//	    // Mock ds peer
//	    Peer dsPeer = EasyMock.createMock(Peer.class);
//	    AcceptanceTestUtil.publishTestObject(nwDeploymentID, dsPeer);
//	
//	    //Record mock behavior
//	    EasyMock.reset(dsPeer);
//	    EasyMock.expect(dsPeer.getDeploymentID()).andReturn(nwDeploymentID).anyTimes();
//	    EasyMock.replay(dsPeer);
//	    notifyDiscoveryServiceRecovery(dsPeer, nwDeploymentID);
//	}

	/**
	 * Looks a DiscoveryService peer up according to an DeploymentID and notifies its failure
	 * @param dsID DiscoveryService peer DeploymentID
	 */
	public void notifyDiscoveryServiceFailure(DeploymentID dsID, Future<?> future) {
		
	    // Mock ds peer
		DiscoveryService dsMock = (DiscoveryService) AcceptanceTestUtil.getBoundObject(dsID);
	
	    // Record mock behavior
	    EasyMock.reset(dsMock);
	    createStub(dsMock, DiscoveryService.class, dsID);
	    EasyMock.replay(dsMock);
	    
	    DiscoveryServiceAdvertDAO.getInstance().setAdvertActionFuture(future);
	    
	    EasyMock.reset(future);
	    EasyMock.expect(future.cancel(true)).andReturn(true).once();
	    EasyMock.replay(future);
	    
	    //Get ds monitor
	    DiscoveryServiceClientReceiver dsMonitor = getDiscoveryServiceMonitor();
	
	    //Notify failure of Worker
	    dsMonitor.doNotifyFailure(dsMock, dsID);
	
		//Verify mock behavior
	    EasyMock.verify(dsMock, future);
	    
	    DiscoveryServiceAdvertDAO.getInstance().setAdvertActionFuture(null);
	}

	private DeploymentID notifyDiscoveryServiceRecovery(DiscoveryService discoveryService, 
			DeploymentID nwDeploymentID, PeerComponent component) {
		
		//Get ds monitor
		DiscoveryServiceClientReceiver dsMonitor = getDiscoveryServiceMonitor();
		
		//Notify recovery of ds
		dsMonitor.doNotifyRecovery(discoveryService, nwDeploymentID);
		
		//Verify mock behavior
		EasyMock.verify(discoveryService);
		
		return nwDeploymentID;
	}

	/**
	 * Creates a DiscoveryService peer mock, publishes it, notifies its recovery 
	 * and schedule repetition for idle workers' adverts
	 * @param peerComponent The peer Component
	 * @param nwName DiscoveryService peer EntityID's user name
	 * @param nwServer DiscoveryService peer EntityID's server name
	 * @param requestSpecs alive requests to be scheduled for repetition
	 * @return The DeploymentID created for DiscoveryService mock to be published
	 */
	@SuppressWarnings("unchecked")
	public Future<?> notifyDiscoveryServiceRecovery(PeerComponent peerComponent, DeploymentID dsID) {
		
		//Changes temporarily the logger mock
		CommuneLogger oldLogger = peerComponent.getLogger();
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		peerComponent.setLogger(newLogger);
		
		ScheduledExecutorService timer = EasyMock.createMock(ScheduledExecutorService.class);

		ScheduledExecutorService oldTimer = peerComponent.getTimer();
		peerComponent.setTimer(timer);
		
		//Creates and publishes DiscoveryService peer mock
		DiscoveryService discoveryService = EasyMock.createMock(DiscoveryService.class);
		AcceptanceTestUtil.publishTestObject(peerComponent, dsID, discoveryService,
				DiscoveryService.class);
		
		PeerManager peerManager = (PeerManager) getPeerControl();
		
		RepetitionRunnable runnable = new RepetitionRunnable(peerComponent, peerManager, 
				PeerConstants.DS_ACTION_NAME, null);
		
		ScheduledFuture<?> future = EasyMock.createMock(ScheduledFuture.class);
		EasyMock.replay(future);
		
		long frequence = PROP_DS_UPDATE_INTERVAL;
		EasyMock.expect( (ScheduledFuture) timer.scheduleWithFixedDelay(
				DiscoveryServiceRepetitionRunnableMatcher.eqMatcher(runnable),
				EasyMock.eq(frequence), EasyMock.eq(frequence),
				EasyMock.same(TimeUnit.SECONDS))).andReturn(future).anyTimes();
	
		String dsRequestSize = peerComponent.getContext().getProperty(
				PeerConfiguration.PROP_DS_REQUEST_SIZE);
		discoveryService.getRemoteWorkerProviders(getDiscoveryServiceClient(), 
				Integer.valueOf(dsRequestSize));
		
		EasyMock.replay(timer);
		EasyMock.replay(newLogger);
		EasyMock.replay(discoveryService);
		
		notifyDiscoveryServiceRecovery(discoveryService, dsID, peerComponent);
		
		//Verifies logger and peer mocks
		EasyMock.verify(newLogger);
		EasyMock.verify(discoveryService);
		
		EasyMock.verify(timer);
		EasyMock.verify(future);
		
		peerComponent.setLogger(oldLogger);
		peerComponent.setTimer(oldTimer);
		
		EasyMock.reset(discoveryService);
		
		return future;
		
	}
	
	public TestStub receiveRemoteWorkerProvider(PeerComponent component, RequestSpecification requestSpec, 
			String rwpUser, String rwpServer, String rwpPublicKey, WorkerSpecification... workers) {
		return receiveRemoteWorkerProvider(component, requestSpec, new DeploymentID(new ContainerID(
				rwpUser, rwpServer, PeerConstants.MODULE_NAME, rwpPublicKey), 
				PeerConstants.REMOTE_WORKER_PROVIDER), null, workers);
	}
	
	public TestStub receiveRemoteWorkerProvider(PeerComponent component, DeploymentID deploymentID) {
		return receiveRemoteWorkerProvider(component, null, deploymentID, null, new WorkerSpecification[0]);
	}
	
	public TestStub receiveRemoteWorkerProvider(PeerComponent component, List<DeploymentID> deploymentID) {
		return receiveRemoteWorkerProvider(component, null, null, deploymentID, new WorkerSpecification[0]);
	}
	
	private TestStub receiveRemoteWorkerProvider(PeerComponent component, RequestSpecification requestSpec, 
			DeploymentID rwpID, List<DeploymentID> rwpIDs, WorkerSpecification... workers) {
		//Creates and publish rwp mock
		
		RemoteWorkerProvider rwp = null;
		TestStub rwpStub = null;
		
	    //Replace the logger
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		CommuneLogger oldLogger = component.getLogger();
	    component.setLogger(newLogger);
	    
	    Collection<String> rwps = getCurrentRWPs(component);
	    DiscoveryServiceClient dsClient = getDiscoveryServiceClient();
	    
	    if (rwpID != null && !rwps.contains(rwpID.getServiceID().toString())) {
	   
	    	rwp = EasyMock.createMock(RemoteWorkerProvider.class);
	 	    AcceptanceTestUtil.publishTestObject(component, rwpID, rwp, RemoteWorkerProvider.class, true);
	    	
	    	EasyMock.replay(rwp);
	    	
	    	newLogger.debug("Receiving worker provider with id [" + rwpID.getContainerID().getUserAtServer() + "]");

			List<String> rwpsServiceIDs = getCurrentProvidersUserAtServer(component);
			rwpsServiceIDs.add(rwpID.getContainerID().getUserAtServer());
			EasyMock.replay(newLogger);
	    	
			dsClient.hereIsRemoteWorkerProviderList(rwpsServiceIDs);
			
			getRemoteWorkerProviderClient().workerProviderIsUp(
					(RemoteWorkerProvider) rwp, rwpID, AcceptanceTestUtil.getCertificateMock(rwpID));

			//Verify mocks
			EasyMock.verify(newLogger);
			
			rwpStub = new TestStub(rwpID, rwp);
			
	    } else{
	    	List<String> rwpsUserAtServer = new LinkedList<String>();
	    	for (DeploymentID deploymentID : rwpIDs) {
	    		rwpsUserAtServer.add(DeploymentID.getLoginAndServer(deploymentID.toString()));
	    	}
			
			dsClient.hereIsRemoteWorkerProviderList(rwpsUserAtServer);
			
	    	for (DeploymentID deploymentID : rwpIDs) {
				getRemoteWorkerProviderClient().workerProviderIsUp(
						(RemoteWorkerProvider) rwp, deploymentID, AcceptanceTestUtil.getCertificateMock(deploymentID));
	    	}
	    }
	    
		component.setLogger(oldLogger);
		
		return rwpStub;
	}
	

	public Collection<String> getCurrentRWPs(PeerComponent component) {
		DiscoveryServiceClientDAO dsDAO = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		return dsDAO.getRemoteWorkerProvidersAddress();
	}
	
	public List<String> getCurrentProvidersUserAtServer(PeerComponent component) {
		
		DiscoveryServiceClientDAO dsDAO = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		
		List<String> providersServiceID = new ArrayList<String>();
		
		for (String serviceID : dsDAO.getRemoteWorkerProvidersAddress()) {
			providersServiceID.add(ServiceID.parse(serviceID).getContainerID().getUserAtServer());
		}
		
		return providersServiceID;
	}

}