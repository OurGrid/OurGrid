package org.ourgrid.acceptance.discoveryservice;

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
import org.ourgrid.acceptance.util.discoveryservice.Req_509_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_509_Test extends DiscoveryServiceAcceptanceTestCase {

	Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	Req_504_Util req_504_Util = new Req_504_Util(super.getComponentContext());
	Req_505_Util req_505_Util = new Req_505_Util(super.getComponentContext());
	Req_506_Util req_506_Util = new Req_506_Util(super.getComponentContext());
	Req_508_Util req_508_Util = new Req_508_Util(super.getComponentContext());
	Req_509_Util req_509_Util = new Req_509_Util(super.getComponentContext());
	
	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the getCompleteStatus message;
	 * Verify if the conectedPeers list contains the peer added.
	 * Call the getPeerStatusProviders message from a client with the following parameters:
     *     o username = test, servername = servertest;
	 * Verify if the result list contains the PeerStatusProvider added.
	 *
	 * @throws Exception
	 */
	@ReqTest(test = "AT-509.1", reqs = "")
	@Test public void test_AT_509_1_queryingPeerStatusAddedToTheCommunity()
	throws Exception {
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Generate Set of peers and Map of LocalDSInfo 
		Set <String> peersSet = new HashSet <String> (); 
		peersSet.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peersSet);
		
		//Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call the getPeerStatusProviders and verifies
		req_509_Util.getPeerStatusProviders(component, dscServiceIDList);

		//Call the getPeerStatusProviders and verifies
		req_509_Util.getPeerStatusProviders(component, dscServiceIDList);		
	}

	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
	 * Call the getCompleteStatus message;
	 * Verify if the conectedPeers list contains the peer added.
	 * Call the leaveCommunity message with a client with the following attributes:
     *     o username = test, servername = servertest;
	 * Call the getPeerStatusProviders message from a client with the following parameters:
     *     o username = test2, servername = servertest2;
	 * Verify if the result list contains the PeerStatusProvider with username = test2
	 *
	 * @throws Exception
	 */
	@ReqTest(test = "AT-509.2", reqs = "")
	@Test public void test_AT_509_2_queryingPeerStatusAddedThenRemovedFromTheCommunity()
			throws Exception {

		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Generate Set of peer and Map of LocalDSInfo 
		Set <String> peersSet = new HashSet <String> (); 
		peersSet.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peersSet);
		
		//Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		//create another WorkerProvider ServiceID
		TestStub dscTestStub2 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test2", "servertest2", 
				PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity again
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		peersSet.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peersSet);		
		
		//Call getCompletStatusMessage again
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call leaveCommunity message with first peer
		req_506_Util.leaveCommunity(component, dscTestStub);
		
		//Call the getPeerStatusProviders and verifies
		dscServiceIDList.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_509_Util.getPeerStatusProviders(component, dscServiceIDList);
	}

	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *     o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *     o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the getCompleteStatus message;
	 * Verify if the conectedPeers list contains the peer added.
	 * Call the doNotifyFailure message with the deploymentID:
     *     o username = test, servername = servertest and service = DS_CLIENT and publickey = dsClientPK;
	 * Call the getPeerStatusProviders message from a client with the following parameters:
     *     o username = test, servername = servertest;
     * Verify if the result list is empty.
     *
     * @throws Exception
	 */
	@ReqTest(test = "AT-509.3", reqs = "")
	@Test public void test_AT_509_3_queryingPeerStatusToFailedPeer() throws Exception {
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Generate Set of peer and Map of LocalDSInfo 
		Set <String> peersSet = new HashSet <String> (); 
		peersSet.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peersSet);
		
		
		//Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call the doNotifyFailure
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		dscServiceIDList.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		//Call the getPeerStatusProviders and verifies
		req_509_Util.getPeerStatusProviders(component, dscServiceIDList);
	}
	
}	