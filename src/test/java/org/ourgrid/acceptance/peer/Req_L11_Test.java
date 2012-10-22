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

import java.io.File;
import java.util.List;
import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAcceptanceUtil;
import org.ourgrid.acceptance.util.WorkerAllocation;
import org.ourgrid.acceptance.util.peer.Req_010_Util;
import org.ourgrid.acceptance.util.peer.Req_011_Util;
import org.ourgrid.acceptance.util.peer.Req_019_Util;
import org.ourgrid.acceptance.util.peer.Req_025_Util;
import org.ourgrid.acceptance.util.peer.Req_036_Util;
import org.ourgrid.acceptance.util.peer.Req_101_Util;
import org.ourgrid.acceptance.util.peer.Req_106_Util;
import org.ourgrid.acceptance.util.peer.Req_108_Util;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.control.PeerControl;
import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.to.LocalWorkerState;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.interfaces.to.UserInfo;
import org.ourgrid.common.interfaces.to.UserState;
import org.ourgrid.common.interfaces.to.WorkerInfo;
import org.ourgrid.common.specification.job.JobSpecification;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.peer.PeerComponent;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

@ReqTest( reqs = "REQ011" )
public class Req_L11_Test extends PeerAcceptanceTestCase {

	public static final String COMM_FILE_PATH = "req_011" + File.separator;

	
	private PeerComponent peerComponent;
	private WorkerAcceptanceUtil workerAcceptanceUtil = new WorkerAcceptanceUtil( getComponentContext() );
	private PeerAcceptanceUtil peerAcceptanceUtil = new PeerAcceptanceUtil( getComponentContext() );
	
	private Req_010_Util req_010_Util = new Req_010_Util( getComponentContext() );
	private Req_101_Util req_101_Util = new Req_101_Util( getComponentContext() );
	private Req_108_Util req_108_Util = new Req_108_Util( getComponentContext() );
	private Req_019_Util req_019_Util = new Req_019_Util( getComponentContext() );
	private Req_025_Util req_025_Util = new Req_025_Util( getComponentContext() );
	private Req_011_Util req_011_Util = new Req_011_Util( getComponentContext() );
	private Req_106_Util req_106_Util = new Req_106_Util( getComponentContext() );
	private Req_036_Util req_036_Util = new Req_036_Util( getComponentContext() );

	
	@Before
	public void setUp( ) throws Exception {
		File trustFile = new File( PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME );
		trustFile.delete();
		super.setUp();
	}


	@After
	public void tearDown( ) throws Exception {
		File trustFile = new File( PeerConfiguration.TRUSTY_COMMUNITIES_FILENAME );
		trustFile.delete();
		super.tearDown();
	}


	@Test
	public void test_at_011_7_Local_Redistribution( ) throws Exception {

		peerComponent = req_010_Util.startPeer();

		// Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser( "user011-1", "server011", "011011" );
		XMPPAccount user2 = req_101_Util.createLocalUser( "user011-2", "server011", "011012" );
		XMPPAccount user3 = req_101_Util.createLocalUser( "user011-3", "server011", "011013" );
		XMPPAccount user4 = req_101_Util.createLocalUser( "user011-4", "server011", "011014" );

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient1 = EasyMock.createMock( PeerControlClient.class );
		DeploymentID pccID1 = new DeploymentID( new ContainerID( "pcc1", "broker", "broker" ), "user1" );
		AcceptanceTestUtil.publishTestObject( peerComponent, pccID1, peerControlClient1, PeerControlClient.class );
		AcceptanceTestUtil.setExecutionContext( peerComponent, pcOD, pccID1 );

		try {
			peerControl.addUser( peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch ( CommuneRuntimeException e ) {
			// do nothing - the user is already added.
		}

		PeerControlClient peerControlClient2 = EasyMock.createMock( PeerControlClient.class );
		DeploymentID pccID2 = new DeploymentID( new ContainerID( "pcc2", "broker", "broker" ), "user2" );
		AcceptanceTestUtil.publishTestObject( peerComponent, pccID2, peerControlClient2, PeerControlClient.class );
		AcceptanceTestUtil.setExecutionContext( peerComponent, pcOD, pccID2 );

		try {
			peerControl.addUser( peerControlClient2, user2.getUsername() + "@" + user2.getServerAddress());
		} catch ( CommuneRuntimeException e ) {
			// do nothing - the user is already added.
		}

		PeerControlClient peerControlClient3 = EasyMock.createMock( PeerControlClient.class );
		DeploymentID pccID3 = new DeploymentID( new ContainerID( "pcc3", "broker", "broker" ), "user3" );
		AcceptanceTestUtil.publishTestObject( peerComponent, pccID3, peerControlClient3, PeerControlClient.class );
		AcceptanceTestUtil.setExecutionContext( peerComponent, pcOD, pccID3 );

		try {
			peerControl.addUser( peerControlClient3, user3.getUsername() + "@" + user3.getServerAddress());
		} catch ( CommuneRuntimeException e ) {
			// do nothing - the user is already added.
		}

		PeerControlClient peerControlClient4 = EasyMock.createMock( PeerControlClient.class );
		DeploymentID pccID4 = new DeploymentID( new ContainerID( "pcc4", "broker", "broker" ), "user4" );
		AcceptanceTestUtil.publishTestObject( peerComponent, pccID4, peerControlClient4, PeerControlClient.class );
		AcceptanceTestUtil.setExecutionContext( peerComponent, pcOD, pccID4 );

		try {
			peerControl.addUser( peerControlClient4, user4.getUsername() + "@" + user4.getServerAddress());
		} catch ( CommuneRuntimeException e ) {
			// do nothing - the user is already added.
		}

		// Workers login
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec( "U1", "S1" );
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec( "U2", "S1" );
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec( "U3", "S1" );
		
		String workerAPubKey = "publicKeyA";
		String workerBPubKey = "publicKeyB";
		String workerCPubKey = "publicKeyC";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADID);
		
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDID);

		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDID);

		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerADID );
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerBDID );
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerCDID );

		// Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login( peerComponent, user1, client1PubKey );
		LocalWorkerProviderClient lwpc = ( LocalWorkerProviderClient ) AcceptanceTestUtil.getBoundObject( lwpcOID1 );

		String client2PubKey = "user2PubKey";
		DeploymentID lwpcOID2 = req_108_Util.login( peerComponent, user2, client2PubKey );
		LocalWorkerProviderClient lwpc2 = ( LocalWorkerProviderClient ) AcceptanceTestUtil.getBoundObject( lwpcOID2 );

		String client3PubKey = "user3PubKey";
		DeploymentID lwpcOID3 = req_108_Util.login( peerComponent, user3, client3PubKey );
		LocalWorkerProviderClient lwpc3 = ( LocalWorkerProviderClient ) AcceptanceTestUtil.getBoundObject( lwpcOID3 );

		String client4PubKey = "user4PubKey";
		DeploymentID lwpcOID4 = req_108_Util.login( peerComponent, user4, client4PubKey );
		LocalWorkerProviderClient lwpc4 = ( LocalWorkerProviderClient ) AcceptanceTestUtil.getBoundObject( lwpcOID4 );

		// Request three workers for client1 - expect to obtain all of them

		WorkerAllocation allocationC = new WorkerAllocation( workerCDID );
		WorkerAllocation allocationB = new WorkerAllocation( workerBDID );
		WorkerAllocation allocationA = new WorkerAllocation( workerADID );

		RequestSpecification spec = new RequestSpecification( 0, new JobSpecification( "label" ), 1, "", 3, 0, 0 );
		req_011_Util.requestForLocalConsumer( peerComponent, new TestStub( lwpcOID1, lwpc ), spec, allocationC,
												allocationB, allocationA );

		// Request two workers for client2 - expect to obtain one of them - the
		// most recently allocated on client1
		allocationA = new WorkerAllocation( workerADID ).addLoserConsumer( lwpcOID1 ).addLoserRequestSpec( spec );
		RequestSpecification spec2 = new RequestSpecification( 0, new JobSpecification( "label" ), 2, "", 2, 0, 0 );
		req_011_Util.requestForLocalConsumer( peerComponent, new TestStub( lwpcOID2, lwpc2 ), spec2, allocationA );

		// Request one worker for client3 - expect to obtain one of them - the
		// most recently allocated on client1

		allocationB = new WorkerAllocation( workerBDID ).addLoserConsumer( lwpcOID1 );
		RequestSpecification spec3 = new RequestSpecification( 0, new JobSpecification( "label" ), 3, "", 1, 0, 0 );
		req_011_Util.requestForLocalConsumer( peerComponent, new TestStub( lwpcOID3, lwpc3 ), spec3, allocationB );

		// Request one worker for client4 - expect to obtain none of them
		RequestSpecification spec4 = new RequestSpecification( 0, new JobSpecification( "label" ), 4, "", 1, 0, 0 );
		req_011_Util.requestForLocalConsumer( peerComponent, new TestStub( lwpcOID4, lwpc4 ), spec4 );

		// Verify the clients' status
		UserInfo userInfo1 = new UserInfo( user1.getUsername(), user1.getServerAddress(), client1PubKey,
											UserState.CONSUMING );
		UserInfo userInfo2 = new UserInfo( user2.getUsername(), user2.getServerAddress(), client2PubKey,
											UserState.CONSUMING );
		UserInfo userInfo3 = new UserInfo( user3.getUsername(), user3.getServerAddress(), client3PubKey,
											UserState.CONSUMING );
		UserInfo userInfo4 = new UserInfo( user4.getUsername(), user4.getServerAddress(), client4PubKey,
											UserState.CONSUMING );
		List< UserInfo > usersInfo = AcceptanceTestUtil.createList( userInfo1, userInfo2, userInfo3, userInfo4 );

		req_106_Util.getUsersStatus( usersInfo );

		// Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo( workerSpecA, LocalWorkerState.IN_USE, lwpcOID2.getServiceID().toString() );
		WorkerInfo workerInfoB = new WorkerInfo( workerSpecB, LocalWorkerState.IN_USE, lwpcOID3.getServiceID().toString() );
		WorkerInfo workerInfoC = new WorkerInfo( workerSpecC, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString() );
		List< WorkerInfo > localWorkersInfo = AcceptanceTestUtil.createList( workerInfoA, workerInfoB, workerInfoC );

		req_036_Util.getLocalWorkersStatus( localWorkersInfo );
	}


	@Test
	public void test_at_011_1_Local_Redistribution( ) throws Exception {

		peerComponent = req_010_Util.startPeer();

		// Create four user accounts
		XMPPAccount user1 = req_101_Util.createLocalUser( "user011-1", "server011", "011011" );

		PeerControl peerControl = peerAcceptanceUtil.getPeerControl();
		ObjectDeployment pcOD = peerAcceptanceUtil.getPeerControlDeployment();

		PeerControlClient peerControlClient1 = EasyMock.createMock( PeerControlClient.class );
		DeploymentID pccID1 = new DeploymentID( new ContainerID( "pcc1", "broker", "broker" ), "user1" );
		AcceptanceTestUtil.publishTestObject( peerComponent, pccID1, peerControlClient1, PeerControlClient.class );
		AcceptanceTestUtil.setExecutionContext( peerComponent, pcOD, pccID1 );

		try {
			peerControl.addUser( peerControlClient1, user1.getUsername() + "@" + user1.getServerAddress());
		} catch ( CommuneRuntimeException e ) {
			// do nothing - the user is already added.
		}

		// Workers login
		Map< String,String > tagsA = AcceptanceTestUtil.createMap( "tag1", "tag2", "tag3" );
		WorkerSpecification workerSpecA = workerAcceptanceUtil.createWorkerSpec( "U1", "S1" );
		WorkerAcceptanceUtil.setAnnotationsWorkerSpec( workerSpecA, tagsA );
		WorkerSpecification workerSpecB = workerAcceptanceUtil.createWorkerSpec( "U2", "S1" );
		Map< String,String > tagsB = AcceptanceTestUtil.createMap( "tag1", "tag2" );
		WorkerAcceptanceUtil.setAnnotationsWorkerSpec( workerSpecB, tagsB );
		WorkerSpecification workerSpecC = workerAcceptanceUtil.createWorkerSpec( "U3", "S1" );

		String workerAPubKey = "publicKeyA";
		String workerBPubKey = "publicKeyB";
		String workerCPubKey = "publicKeyC";
		
		DeploymentID workerADID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecA, workerAPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecA, workerADID);
		
		DeploymentID workerBDID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecB, workerBPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecB, workerBDID);

		DeploymentID workerCDID = req_019_Util.createAndPublishWorkerManagement(peerComponent, workerSpecC, workerCPubKey);
		req_010_Util.workerLogin(peerComponent, workerSpecC, workerCDID);

		// Change workers status to IDLE
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerADID );
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerBDID );
		req_025_Util.changeWorkerStatusToIdle( peerComponent, workerCDID );

		// Login with four valid users
		String client1PubKey = "user1PubKey";
		DeploymentID lwpcOID1 = req_108_Util.login( peerComponent, user1, client1PubKey );
		LocalWorkerProviderClient lwpc = ( LocalWorkerProviderClient ) AcceptanceTestUtil.getBoundObject( lwpcOID1 );

		// Request three workers for client1 - expect to obtain all of them

		WorkerAllocation allocationA = new WorkerAllocation( workerADID );
		WorkerAllocation allocationB = new WorkerAllocation( workerBDID );
		
		JobSpecification jobspec1 = new JobSpecification( "label" );
		jobspec1.setAnnotations( tagsB );
		
		RequestSpecification spec = new RequestSpecification( 0, jobspec1, 1, jobspec1.getRequirements(), 2, 0, 0 );
		req_011_Util.requestForLocalConsumer( peerComponent, new TestStub( lwpcOID1, lwpc ), spec, allocationA,
												allocationB );

		// req_011_Util.requestForLocalConsumer(peerComponent, new
		// TestStub(lwpcOID1, lwpc), spec, allocationC, allocationB);

		// Verify the clients' status
		UserInfo userInfo1 = new UserInfo( user1.getUsername(), user1.getServerAddress(), client1PubKey,
											UserState.CONSUMING );
		List< UserInfo > usersInfo = AcceptanceTestUtil.createList( userInfo1 );

		req_106_Util.getUsersStatus( usersInfo );

		// Verify the workers' status
		WorkerInfo workerInfoA = new WorkerInfo( workerSpecA, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString() );
		WorkerInfo workerInfoB = new WorkerInfo( workerSpecB, LocalWorkerState.IN_USE, lwpcOID1.getServiceID().toString() );
		WorkerInfo workerInfoC = new WorkerInfo( workerSpecC, LocalWorkerState.IDLE );
		List< WorkerInfo > localWorkersInfo = AcceptanceTestUtil.createList( workerInfoA, workerInfoB, workerInfoC );

		req_036_Util.getLocalWorkersStatus( localWorkersInfo );
	}

}