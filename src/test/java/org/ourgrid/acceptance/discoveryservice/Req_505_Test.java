package org.ourgrid.acceptance.discoveryservice;

import static org.junit.Assert.assertTrue;

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
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_505_Test extends DiscoveryServiceAcceptanceTestCase {

	private Req_502_Util req_502_util = new Req_502_Util(getComponentContext());
	private Req_505_Util req_505_util = new Req_505_Util(getComponentContext());
	private Req_504_Util req_504_util = new Req_504_Util(getComponentContext());
	private Req_506_Util req_506_util = new Req_506_Util(getComponentContext());
	private Req_508_Util req_508_Util = new Req_508_Util(getComponentContext());
	private Req_511_Util req_511_util = new Req_511_Util(getComponentContext());
	

	/**
	 * Create a DS;
	 * Start a DS with the correct public key;
	 * Call the joinCommunity message with the following parameters:
     *    o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
     *    o PeerStatusProvider: username = test, servername = servertest and service = PEER;
	 * Call the getCompleteStatus message;
	 * Verify if the conectedPeers list contains a the peer added.
	 *
	 * @throws Exception
	 */
	@ReqTest(test = "AT-505.1", reqs = "")
	@Test public void test_AT_505_1_AddAPeerToTheCommunity() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub, true);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	/**
	*  Create a DS;
    * Start a DS with the correct public key;
    * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
    * Call the getCompleteStatus message;
    * Verify if the conectedPeers list contains a the peer added.
    * Call again the joinCommunity with the above parameters;
    * Verify if the following warn message was logged:
    * 	The client with ID [serviceID] is already added.
    * Call the getCompleteStatus message;
    * Verify if the conectedPeers list contains only the peer added

	 */
	@ReqTest(test = "AT-505.2", reqs = "")
	@Test public void test_AT_505_2_AddAPeerToTheCommunityAgain() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call joinCommunity
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub, true);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@ReqTest(test = "AT-505.3", reqs = "")
	@Test public void test_AT_505_3_AddAPeerToTheCommunityAndShutItDown() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);

		//Notify RWP failure
		req_505_util.notifyDiscoveryServiceClientFailure(component, dscTestStub);
		
		peers.clear();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the getRemoteWorkerProviders message from a client with the following parameters:
          o username = test, servername = servertest;
     * Verify if the result list contains the WorkerProvider added.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-505.4", reqs = "")
	@Test public void test_AT_505_4_consultingARemotePeerAddedToCommunity() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call the getRemoteWorkerProviders message
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub, true);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test2, servername = servertest2 and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test2, servername = servertest2 and service = PEER;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the leaveCommunity message with a client with the following attributes:
          o username = test, servername = servertest;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test3, servername = servertest3 and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test3, servername = servertest3 and service = PEER;
     * Call the getRemoteWorkerProviders message from a client with the following parameters:
          o username = test2, servername = servertest2;
     * Verify if the result list contains the WorkerProviders with username = test2 and username = test3
	 * @throws Exception
	 */
	@ReqTest(test = "AT-505.5", reqs = "")
	@Test public void test_AT_505_5_consultingARemotePeerAddedToCommunityAndLaterRemoved() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		//create another WorkerProvider
		TestStub dscTestStub2 = req_505_util.createDiscoveryServiceClient(new ServiceID("test2", "servertest2", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub2.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call leaveCommunity message
		req_506_util.leaveCommunity(component, dscTestStub);
		
		//create another WorkerProvider
		TestStub dscTestStub3 = req_505_util.createDiscoveryServiceClient(new ServiceID("test3", "servertest3", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		dscServiceIDList.clear();
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		dscServiceIDList.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub3);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub3.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		peers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub3.getDeploymentID().getContainerID().getUserAtServer());
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer with new DeploymentID.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-505.6", reqs = "")
	@Test public void test_AT_505_6_consultingARemotePeerAddedToCommunityAndLaterHasNewDeploymentID() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();

		//create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		//create another WorkerProvider
		TestStub dscTestStub2 = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		
		dscServiceIDList.clear();
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2, true);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub2.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@ReqTest(test = "AT-505.7", reqs = "")
	@Test public void test_AT_505_7_AddPeerToCommunityWithMoreDSs() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "server", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		dsList.add(dsTestStub);
		
		//Remote DS joins network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();

		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);
		
		//create WorkerProvider 1
		TestStub dscTestStub1 = req_505_util.createDiscoveryServiceClient(new ServiceID("remotepeer", "server", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub1, dsList);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub1.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		TestStub dscTestStub2 = req_505_util.createDiscoveryServiceClient(new ServiceID("remotepeer2", "server", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2, dsList);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub2.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub1.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);

		assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
	}
	
	@ReqTest(test = "AT-505.8", reqs = "")
	@Test public void test_AT_505_8_AddADSInANetworkWithJustPeers() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create WorkerProvider 1
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
				
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "server", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		dsList.add(dsTestStub);
		
		//remote DS joins network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();

		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
				
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
	}
	
	@ReqTest(test = "AT-505.9", reqs = "")
	@Test public void test_AT_505_9_DSWithPeerAddressesReceivesEmptyPeerList() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create WorkerProvider 1
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "server", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		dsList.add(dsTestStub);
		
		//remote DS joins network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();

		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);		
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
	@ReqTest(test = "AT-505.10", reqs = "")
	@Test public void test_AT_505_10_PeerJoinsDSWithPeerAddress() throws Exception{
		
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = dsAcceptanceUtil.getDiscoveryServiceDeployment(component);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "server", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		dsList.add(dsTestStub);
		
		// Create WorkerProvider 1
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test1", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		// Create WorkerProvider 2
		TestStub dscTestStub2 = req_505_util.createDiscoveryServiceClient(new ServiceID("test2", "servertest", PeerConstants.MODULE_NAME, PeerConstants.DS_CLIENT));
		
		//remote DS joins network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);

		assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		// Create the address of a remote DS
		String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
		
		// Create and set the peers of the remote DS in DAO
		Set<String> remoteDSPeers = new HashSet<String>();
		remoteDSPeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), remoteDSPeers);
		
		assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
		
		Set<String> remotePeers = new HashSet<String>();
		remotePeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call joinCommunity
		List<String> localDSCServiceIDList = new LinkedList<String>();
		localDSCServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		List<String> allDSCServiceIDList = new LinkedList<String>(localDSCServiceIDList);
		allDSCServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		req_505_util.getRemoteWorkerProviders(component, allDSCServiceIDList, dscTestStub2, dsList, false, localDSCServiceIDList);
		
		assertTrue(AcceptanceTestUtil.isInterested(component, dscTestStub2.getDeploymentID().getServiceID(), 
				dsAcceptanceUtil.getDiscoveryServiceDeployment(component).getDeploymentID()));
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
			
	}
}