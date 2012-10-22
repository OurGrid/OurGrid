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
package org.ourgrid.acceptance.peer;

import org.easymock.classextension.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.ourgrid.acceptance.util.JDLCompliantTest;
import org.ourgrid.acceptance.util.RemoteAllocation;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_014_Util;
import org.ourgrid.acceptance.util.peer.Req_018_Util;
import org.ourgrid.acceptance.util.peer.Req_020_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_027_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.GridProcessAccounting;
import org.ourgrid.common.interfaces.to.GridProcessState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class AT_0034 extends PeerAcceptanceTestCase {

    private PeerComponent component;
    private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil(getComponentContext());
    private Req_010_Util req_010_Util = new Req_010_Util(getComponentContext());
    private Req_011_Util req_011_Util = new Req_011_Util(getComponentContext());
    private Req_014_Util req_014_Util = new Req_014_Util(getComponentContext());
    private Req_018_Util req_018_Util = new Req_018_Util(getComponentContext());
    private Req_020_Util req_020_Util = new Req_020_Util(getComponentContext());
    private Req_025_Util req_025_Util = new Req_025_Util(getComponentContext());
    private Req_027_Util req_027_Util = new Req_027_Util(getComponentContext());
    private Req_101_Util req_101_Util = new Req_101_Util(getComponentContext());
    private Req_108_Util req_108_Util = new Req_108_Util(getComponentContext());
	
    @Before
	public void setUp() throws Exception{
		super.setUp();
        component = req_010_Util.startPeer();
	}
	
	/**
	* Verifies if the Peer ignores a received favour accounting, done after the request finish.
    */
	@ReqTest(test="AT-0034", reqs="REQ014, REQ027")
	@Test public void test_AT_0034_AccountingReceivingFavourAfterFinishRequest() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
	    String broker1PubKey = "publicKey1";
	    
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    //Client request workers
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, "os = windows AND mem > 256", 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);

	    //GIS client receive a remote worker provider
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createWorkerSpec("rU1", "rS1");
        remoteWorkerSpec1.putAttribute("os", "windows");
        remoteWorkerSpec1.putAttribute("mem", "512");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmOID, remoteWorkerSpec1, requestSpec1);
		
		WorkerAllocation remoteWorkerAllocation1 = new WorkerAllocation(rwmOID);
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorkerAllocation1));
		
		//Finish the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, broker1PubKey, lwpc1OID.getServiceID(), requestSpec1, 
				new TestStub(rwpID, remotePeer));
	
		//Report a received favour of a finished replica - expect the peer to ignore it
		resetActiveMocks();
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc1OID.getServiceID()+"], because the request "+ request1ID +" " +
				"does not exists.");
		EasyMock.replay(loggerMock);
		
		double cpu = 10.;
		double data = 0.;
		
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(requestSpec1, rwmOID.toString(),
				rwmOID.getPublicKey(), cpu, data, GridProcessState.FINISHED, remoteWorkerSpec1);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc1OID);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Report a received favour of a aborted replica - expect the peer to ignore it
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc1OID.getServiceID()+"], because the request "+ request1ID +" " +
				"does not exists.");
		EasyMock.replay(loggerMock);
		
		cpu = 6.;
		data = 0.;
		
		replicaAccounting = new GridProcessAccounting(requestSpec1, rwmOID.toString(), rwmOID.getPublicKey(), 
				cpu, data, GridProcessState.ABORTED, remoteWorkerSpec1);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc1OID);
		
		EasyMock.verify(loggerMock);
	}
	
	@Category(JDLCompliantTest.class)
	@Test public void test_AT_0034_AccountingReceivingFavourAfterFinishRequestWithJDL() throws Exception {
		//Create an user account
		XMPPAccount user1 = req_101_Util.createLocalUser("user011_1", "server011", "011011");

		//Set mocks for logger and timer
		CommuneLogger loggerMock = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(loggerMock);
		
		//DiscoveryService recovery
		DeploymentID dsID = new DeploymentID(new ContainerID("magoDosNos", "sweetleaf.lab", DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		req_020_Util.notifyDiscoveryServiceRecovery(component, dsID);
		
		//Client login
	    String broker1PubKey = "publicKey1";
	    
	    PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();
		
		PeerControlClient peerControlClient = EasyMock.createMock(PeerControlClient.class);
		
		DeploymentID pccID = new DeploymentID(new ContainerID("pcc", "broker", "broker"), broker1PubKey);
		AcceptanceTestUtil.publishTestObject(component, pccID, peerControlClient, PeerControlClient.class);
		
		AcceptanceTestUtil.setExecutionContext(component, pcOD, pccID);
		
		try {
			peerControl.addUser(peerControlClient, user1.getUsername() + "@" + user1.getServerAddress());
		} catch (CommuneRuntimeException e) {
			//do nothing - the user is already added.
		}

	    DeploymentID lwpc1OID = req_108_Util.login(component, user1, broker1PubKey);
	    
	    LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) AcceptanceTestUtil.getBoundObject(lwpc1OID);
	    
	    //Client request workers
	    long request1ID = 1;
	    RequestSpecification requestSpec1 = new RequestSpecification(0, new JobSpecification("label"), request1ID, buildRequirements(">", 256, "==", "windows"), 1, 0, 0);
	    req_011_Util.requestForLocalConsumer(component, new TestStub(lwpc1OID, lwpc), requestSpec1);

	    //GIS client receive a remote worker provider
        WorkerSpecification remoteWorkerSpec1 = workerAcceptanceUtil.createClassAdWorkerSpec("rU1", "rS1", 512, "windows");
        
		TestStub rwpStub = req_020_Util.receiveRemoteWorkerProvider(component, requestSpec1, "rwpUser", 
				"rwpServer", "rwpPublicKey", remoteWorkerSpec1);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) rwpStub.getObject();
		
		DeploymentID rwpID = rwpStub.getDeploymentID();
		
		//Remote worker provider client receive a remote worker
		DeploymentID rwmOID = req_018_Util.receiveRemoteWorker(component, rwp, rwpID, remoteWorkerSpec1, "rworker1PK", broker1PubKey).getDeploymentID();
		
		//Change worker status to ALLOCATED FOR BROKER
		ObjectDeployment lwpcOD = new ObjectDeployment(component, lwpc1OID, AcceptanceTestUtil.getBoundObject(lwpc1OID));
		
		req_025_Util.changeWorkerStatusToAllocatedForBroker(component, lwpcOD, 
				peerAcceptanceUtil.getRemoteWorkerManagementClientDeployment(), 
				rwmOID, remoteWorkerSpec1, requestSpec1);
		
		WorkerAllocation remoteWorkerAllocation1 = new WorkerAllocation(rwmOID);
		RemoteAllocation remotePeer = new RemoteAllocation(rwp, AcceptanceTestUtil.createList(remoteWorkerAllocation1));
		
		//Finish the request
		ObjectDeployment lwpOD = peerAcceptanceUtil.getLocalWorkerProviderDeployment(); 
		
		req_014_Util.finishRequestWithRemoteWorkers(component, peerAcceptanceUtil.getLocalWorkerProviderProxy(), 
				lwpOD, broker1PubKey, lwpc1OID.getServiceID(), requestSpec1, 
				new TestStub(rwpID, remotePeer));
	
		//Report a received favour of a finished replica - expect the peer to ignore it
		resetActiveMocks();
		
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc1OID.getServiceID()+"], because the request "+ request1ID +" " +
				"does not exists.");
		EasyMock.replay(loggerMock);
		
		double cpu = 10.;
		double data = 0.;
		
		GridProcessAccounting replicaAccounting = new GridProcessAccounting(requestSpec1, rwmOID.toString(),
				rwmOID.getPublicKey(), cpu, data, GridProcessState.FINISHED, remoteWorkerSpec1);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc1OID);
		
		EasyMock.verify(loggerMock);
		EasyMock.reset(loggerMock);
		
		//Report a received favour of a aborted replica - expect the peer to ignore it
		loggerMock.warn("Ignoring a replica accounting from the user ["+lwpc1OID.getServiceID()+"], because the request "+ request1ID +" " +
				"does not exists.");
		EasyMock.replay(loggerMock);
		
		cpu = 6.;
		data = 0.;
		
		replicaAccounting = new GridProcessAccounting(requestSpec1, rwmOID.toString(), rwmOID.getPublicKey(), 
				cpu, data, GridProcessState.ABORTED, remoteWorkerSpec1);
		req_027_Util.reportReplicaAccounting(component, replicaAccounting, lwpc1OID);
		
		EasyMock.verify(loggerMock);
	}
}