package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_504_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_505_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_506_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_508_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_510_Util;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.common.statistics.beans.status.PeerStatus;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_510_Test extends DiscoveryServiceAcceptanceTestCase{

	private Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	private Req_504_Util req_504_Util = new Req_504_Util(super.getComponentContext());
	private Req_505_Util req_505_Util = new Req_505_Util(super.getComponentContext());
	private Req_506_Util req_506_Util = new Req_506_Util(super.getComponentContext());
	private Req_508_Util req_508_Util = new Req_508_Util(super.getComponentContext());
	private Req_510_Util req_510_Util = new Req_510_Util(super.getComponentContext());


	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test3, servername = servertest3 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test3, servername = servertest3 and service = PEER;
	 * Call the leaveCommunity message with a client with the following attributes:
     *     o username = test, servername = servertest;
	 * Call the doNotifyFailure message with the deploymentID:
     *     o username = test2, servername = servertest2 and service = DS_CLIENT and publickey = dsClientPK;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the getPeerStatusChangeHistory message;
     * Verify if the list contains the following ordered data:
     *     o  Address: test@servertest Status: UP
     *     o  Address: test@servertest Status: DOWN
     *     o  Address: test@servertest Status: UP
     *     o  Address: test2@servertest2 Status: UP
     *     o  Address: test2@servertest2 Status: DOWN
     *     o  Address: test3@servertest3 Status: UP
	 *
     */
	@ReqTest(test = "AT-510.1", reqs = "")
	@Test public void test_AT_510_queryNormalPeerStatusHistoryChange() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//create a WorkerProvider (first time)
		TestStub dscTestStub1 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test1", "servertest1", "PEER", PeerConstants.DS_CLIENT));

		//Call joinCommunity (first time)
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub1);
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//create a WorkerProvider (second time)
		TestStub dscTestStub2 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test2", "servertest2", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (second time)
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		
		localPeers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);

		//create a WorkerProvider (third time)
		TestStub dscTestStub3 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test3", "servertest3", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (third time)
		dscServiceIDList.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub3);
		
		localPeers.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the leaveCommunity (username = test, servername = servertest)
		req_506_Util.leaveCommunity(component, dscTestStub2);
		
		localPeers.remove(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the doNotifyFailure (username = test2, servername = servertest2)
		//service = DS_CLIENT and publickey = dsClientPK
		req_508_Util.doNotifyFailure(component, dscTestStub1);
		
		localPeers.remove(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);

		//Call the joinCommunity message 
		dscServiceIDList.clear();
		dscServiceIDList.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		dscServiceIDList.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub1, true);
		
		localPeers.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//creating history list
		List<DS_PeerStatusChange> historyList = new ArrayList<DS_PeerStatusChange>();
		
		DS_PeerStatusChange change = new DS_PeerStatusChange();
		change.setPeerAddress("test1@servertest1");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test2@servertest2");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test3@servertest3");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test2@servertest2");
		change.setCurrentStatus(PeerStatus.DOWN);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test1@servertest1");
		change.setCurrentStatus(PeerStatus.DOWN);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test1@servertest1");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		//Call the getPeerStatusChangeHistory message;
		req_510_Util.getPeerStatusChangeHistory(component, historyList);
		
	}

	
	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test3, servername = servertest3 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test3, servername = servertest3 and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test4, servername = servertest4 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test4, servername = servertest4 and service = PEER;
	 * Call the leaveCommunity message with a client with the following attributes:
     *     o username = test, servername = servertest;
	 * Call the leaveCommunity message with a client with the following attributes:
     *     o username = test, servername = servertest;
	 * Call the doNotifyFailure message with the deploymentID:
     *     o username = test2, servername = servertest2 and service = DS_CLIENT and publickey = dsClientPK;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test5, servername = servertest5 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test5, servername = servertest5 and service = PEER;
	 * Call the doNotifyFailure message with the deploymentID:
     *     o username = test5, servername = servertest5 and service = DS_CLIENT and publickey = dsClientPK;
	 * Verify if the list contains the following ordered data:
     *     o Address: test@servertest Status: UP
     *     o Address: test@servertest Status: DOWN
     *     o Address: test2@servertest2 Status: UP
     *     o Address: test2@servertest2 Status: DOWN
     *     o Address: test2@servertest2 Status: UP
     *     o Address: test3@servertest3 Status: UP
     *     o Address: test4@servertest4 Status: UP
     *     o Address: test5@servertest5 Status: UP
     *     o Address: test5@servertest5 Status: DOWN
	 *
	 */
	@ReqTest(test = "AT-510.2", reqs = "")
	@Test public void test_AT_510_queryIlegalPeerStatusHistoryChange() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//create a WorkerProvider
		TestStub dscTestStub2 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test2", "servertest2", "PEER", PeerConstants.DS_CLIENT));

		//Call joinCommunity (first time)
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//create a WorkerProvider
		TestStub dscTestStub1 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test1", "servertest1", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (second time)
		dscServiceIDList.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub1);

		localPeers.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//create a WorkerProvider
		TestStub dscTestStub3 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test3", "servertest3", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (fourth time)
		dscServiceIDList.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub3);

		localPeers.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//create a WorkerProvider 
		TestStub dscTestStub4 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test4", "servertest4", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (fourth time)
		dscServiceIDList.add(dscTestStub4.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub4);

		localPeers.add(dscTestStub4.getDeploymentID().getContainerID().getUserAtServer());		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the leaveCommunity (username = test, servername = servertest)
		req_506_Util.leaveCommunity(component, dscTestStub1);

		localPeers.remove(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the leaveCommunity (username = test, servername = servertest) (again)
		req_506_Util.leaveCommunity(component, dscTestStub1, false, null, null);

		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the doNotifyFailure (username = test2, servername = servertest2)
		//(service = DS_CLIENT and publickey = dsClientPK)
		req_508_Util.doNotifyFailure(component, dscTestStub2);

		localPeers.remove(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the joinCommunity message (username = test2, servername = servertest2)
		dscServiceIDList.clear();
		dscServiceIDList.add(dscTestStub4.getDeploymentID().getContainerID().getUserAtServer());
		dscServiceIDList.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);

		localPeers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());				
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the joinCommunity message (username = test2, servername = servertest2) (again)
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2, true);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);		
		
		Thread.sleep(10);
		
		//create a WorkerProvider
		TestStub dscTestStub5 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test5", "servertest5", "PEER", PeerConstants.DS_CLIENT));
		
		//Call joinCommunity (username = test5, servername = servertest5)
		dscServiceIDList.add(dscTestStub5.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub5);

		localPeers.add(dscTestStub5.getDeploymentID().getContainerID().getUserAtServer());				
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//Call the doNotifyFailure (username = test5, servername = servertest5)
		//(service = DS_CLIENT and publickey = dsClientPK)
		req_508_Util.doNotifyFailure(component, dscTestStub5);

		localPeers.remove(dscTestStub5.getDeploymentID().getContainerID().getUserAtServer());				
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Thread.sleep(10);
		
		//creating history list
		List<DS_PeerStatusChange> historyList = new ArrayList<DS_PeerStatusChange>();
		
		DS_PeerStatusChange change = new DS_PeerStatusChange();
		change.setPeerAddress("test2@servertest2");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test1@servertest1");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test3@servertest3");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test4@servertest4");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test1@servertest1");
		change.setCurrentStatus(PeerStatus.DOWN);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test2@servertest2");
		change.setCurrentStatus(PeerStatus.DOWN);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test2@servertest2");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test5@servertest5");
		change.setCurrentStatus(PeerStatus.UP);
		historyList.add(change);
		
		change = new DS_PeerStatusChange();
		change.setPeerAddress("test5@servertest5");
		change.setCurrentStatus(PeerStatus.DOWN);
		historyList.add(change);
		
		req_510_Util.getPeerStatusChangeHistory(component, historyList);
	}
}
