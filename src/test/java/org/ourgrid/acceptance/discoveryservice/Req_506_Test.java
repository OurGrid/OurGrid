package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.Assert;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_504_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_505_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_506_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_506_Test extends DiscoveryServiceAcceptanceTestCase {
	
	Req_502_Util req_502_Util = new Req_502_Util(getComponentContext());
	Req_504_Util req_504_Util = new Req_504_Util(getComponentContext());
	Req_505_Util req_505_Util = new Req_505_Util(getComponentContext());
	Req_506_Util req_506_Util = new Req_506_Util(getComponentContext());
	Req_511_Util req_511_Util = new Req_511_Util(getComponentContext());

	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the leaveCommunity message with the id of the peer added;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list is empty.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-506.1", reqs = "")
	@Test public void test_AT_506_1_RemoveAPeerOfTheCommunityAndLeaveItEmpty() throws Exception{
		// Start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID  
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("remoteds", "server", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		dsList.add(dsTestStub);
		
		//Create ServiceID List and add local and remote DS's 
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		
		//remote DS joins network
		req_511_Util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);		
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("test", "servertest", "PEER", "REMOTE_WORKERPROVIDER"));
		
		// Call joinCommunity message
		List<TestStub> dsTestStubList = new ArrayList<TestStub>();
		dsTestStubList.add(dsTestStub);

		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub, dsTestStubList);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call leaveCommunity message
		dscServiceIDList.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_506_Util.leaveCommunity(component, dscTestStub,  dsTestStubList, dscServiceIDList);

		peers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the leaveCommunity message with a client with the following attributes:
          o username = invalid, servername = serverinvalid;
     * Verify if the following warn message was logged:
         1. The client with ID [deploymentID] is not joined to the community.
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list is empty.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-506.2", reqs = "")
	@Test public void test_AT_506_2_TryToRemoveAPeerNotMemberOfTheCommunity() throws Exception{
		// Start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		// Call leaveCommunity message
		DeploymentID dscClientID = new DeploymentID(new ServiceID("invalid", "serverinvalid", "PEER", "PEER"));
		DiscoveryServiceClient dscClient = EasyMock.createMock(DiscoveryServiceClient.class);
		TestStub dscClientTestStub = new TestStub(dscClientID, dscClient);
		
		req_506_Util.leaveCommunity(component, dscClientTestStub, false, null, null);
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		
		// Call getCompleteStatus message
		req_504_Util.getEmptyCompleteStatusWithStartedDS(component, expectedLocalDSNetwork);
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Verify if the conectedPeers list contains the peer added.
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest and service = REMOTE_WORKERPROVIDER;
          o PeerStatusProvider: username = test, servername = servertest and service = PEER;
     * Verify if the conectedPeers list contains both peers added.
     * Call the leaveCommunity message with the id of the first peer added;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list still has the second peer.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-506.3", reqs = "")
	@Test public void test_AT_506_3_RemoveAPeerOfTheCommunityAndLeaveItWithPeers() throws Exception{
		// Start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID  
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//create WorkerProvider 1
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("test", "servertest", "PEER", "REMOTE_WORKERPROVIDER"));
		
		//create WorkerProvider 2
		TestStub dscTestStub2 = req_505_Util.createDiscoveryServiceClient(new ServiceID("test2", "servertest", "PEER", "REMOTE_WORKERPROVIDER"));
		
		// Call joinCommunity message
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);

		// Call joinCommunity message again, expecting a ServideIDList containing both stub's addresses
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call leaveCommunity message
		req_506_Util.leaveCommunity(component, dscTestStub);
		dscServiceIDList.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		peers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		// Call getCompleteStatus message
		//Expected: the DS status reports the presence of the other peer added,
		//after the leaveCommunity message called by the first peer
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}

	@ReqTest(test = "AT-506.4", reqs = "")
	@Test public void test_AT_506_4_TryToRemoveAPeerOfACommunityWithJustPeerAddresses() throws Exception{
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("remotepeer", "server", "PEER", "REMOTE_WORKERPROVIDER"));
		
		//The remote DS joins the network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		//Set the remote DS and its peers in DAO
		String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
		
		Set<String> peers = new HashSet<String>();   
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), peers);
		
		Set<String> remotePeers = new HashSet<String>();
		remotePeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		//Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call leaveCommunity in the local DS which contains only peer addresses
		//Expected: warning message since this peer is not logged in the community, although it's address
		//is, in fact, contained in the DS
		req_506_Util.leaveCommunity(component, dscTestStub, true, null, null);
		
		//Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}	
	
	/**
	 * 
	 */
	@ReqTest(test = "AT-506.5", reqs = "")
	@Test public void test_AT_506_5_RemoveALocalPeerLeavingDSWithPeerAddresses() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		dsList.add(dsTestStub);
		
		//Create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("localpeer", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		//Create a remote WorkerProvider
		TestStub dscRemoteTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID("remotepeer", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		//The remote DS join the network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);		
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		//Set the remote DS and its peers in DAO
		String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
		
		Set<String> peers = new HashSet<String>();   
		peers.add(dscRemoteTestStub.getDeploymentID().getContainerID().getUserAtServer());
		dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), peers);
		
		// Call joinCommunity message
		List<String> dscServiceIDList = new ArrayList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		List<String> dscServiceIDList2 = new ArrayList<String>(dscServiceIDList);
		dscServiceIDList2.add(dscRemoteTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList2, dscTestStub, dsList, false, dscServiceIDList);
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Set<String> remotePeers = new HashSet<String>();
		remotePeers.add(dscRemoteTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	    // Call leaveCommunity message
		req_506_Util.leaveCommunity(component, dscTestStub, true, dsList, new ArrayList<String>());
	    
		localPeers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		}
	
}