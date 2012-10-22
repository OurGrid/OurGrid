package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.Assert;

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
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_508_Test extends DiscoveryServiceAcceptanceTestCase {
	
	Req_502_Util req_502_Util = new Req_502_Util(getComponentContext());
	Req_504_Util req_504_Util = new Req_504_Util(getComponentContext());
	Req_505_Util req_505_Util = new Req_505_Util(getComponentContext());
	Req_506_Util req_506_Util = new Req_506_Util(getComponentContext());
	Req_508_Util req_508_Util = new Req_508_Util(getComponentContext());
	Req_511_Util req_511_Util = new Req_511_Util(getComponentContext());
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest,service = REMOTE_WORKERPROVIDER and publickey = dsClientPK;
          o PeerStatusProvider: username = test, servername = servertest,service = PEER and publickey = dsClientPK;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the doNotifyFailure message with the deploymentID:
          o username = test, servername = servertest and service = DS_CLIENT and publickey = dsClientPK;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list is empty.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-508.1", reqs = "")
	@Test public void test_AT_508_1_FailureNotificationLeavingDSEmpty() throws Exception{
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		// Create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));
		
		// Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
				
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		peers.clear();
		
		// Call getCompleteStatus message
		req_504_Util.getEmptyCompleteStatusWithStartedDS(component, expectedLocalDSNetwork);
	}
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters: 
          o WorkerProvider: username = test, servername = servertest,service = REMOTE_WORKERPROVIDER and publickey = dsClientPK;
          o PeerStatusProvider: username = test, servername = servertest,service = PEER and publickey = dsClientPK;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the doNotifyFailure message with the deploymentID:
          o username = test, servername = servertest and service = DS_CLIENT and publickey = dsClientPK;
     * Call the leaveCommunity message with the id of the peer added;
     * Verify if the following warn message was logged:
         1. The client with ID [deploymentID] is not joined to the community.
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list is empty.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-508.2", reqs = "")
	@Test public void test_AT_508_2_FailureNotificationForARemovedClient() throws Exception{
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		// Create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));
		
		// Call joinCommunity	
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
				
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call leaveCommunity message
		req_506_Util.leaveCommunity(component, dscTestStub, true,  new ArrayList<TestStub>(), new ArrayList<String>());
		
		peers.clear();
		
		// Call the getCompleteStatus message
		req_504_Util.getEmptyCompleteStatusWithStartedDS(component, expectedLocalDSNetwork);
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailureWithNonLoggedClient(component, dscTestStub);
		
		// Call getCompleteStatus message
		req_504_Util.getEmptyCompleteStatusWithStartedDS(component, expectedLocalDSNetwork);
	}
	
	/**
     * Create a DS;
     * Start a DS with the correct public key;
     * Call the joinCommunity message with the following parameters:
          o WorkerProvider: username = test, servername = servertest,service = REMOTE_WORKERPROVIDER and publickey = dsClientPK;
          o PeerStatusProvider: username = test, servername = servertest,service = PEER and publickey = dsClientPK;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the peer added.
     * Call the doNotifyFailure message with the deploymentID set to null.
     * Verify if the following warn message was logged:
         1. Client ID invalid: null.
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list contains the Peer added.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-508.3", reqs = "")
	@Test public void test_AT_508_3_FailureNotificationWithNullMonitorableID() throws Exception{
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		// Create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));
		
		// Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call doNotifyFailure message with null deploymentID
		req_508_Util.doNotifyFailureWithNullID(component, dscTestStub);
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	
	@ReqTest(test = "AT-508.4", reqs = "")
	@Test public void test_AT_508_4_FailureNotificationLeavingDSWithPeers() throws Exception{

		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		// Create WorkerProvider 1
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));

		// Create WorkerProvider 2
		TestStub dscTestStub2 = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test2", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));
		
		// Call getRemoteWorkerProviders, and expects a serviceIDList containing the first peer added
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub2);
		
		// Call getRemoteWorkerProviders, and expects a serviceIDList containing both peers added
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_Util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		peers.add(dscTestStub2.getDeploymentID().getContainerID().getUserAtServer());
				
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		peers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		// Call getCompleteStatus message
		//Expected: the DS shall contain only the peers in the ServiceIDList
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@ReqTest(test = "AT-508.5", reqs = "")
	@Test public void test_AT_508_5_FailureNotificationInADSWithPeerAddresses() throws Exception{
		
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		// Create a WorkerProvider
		TestStub dscTestStub = req_505_Util.createDiscoveryServiceClient(new ServiceID(new ContainerID("test", "servertest", "PEER", "dsClientPK"), "REMOTE_WORKERPROVIDER"));

		// Create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		// Get address of the remote DS
		String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
		
		// Create and set the peers of the remote DS in DAO
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, false), peers);
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		Set<String> remotePeers = new HashSet<String>();
		remotePeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	
	/**
	 * 
	 */
	@ReqTest(test = "AT-508.6", reqs = "")
	@Test public void test_AT_508_6_RemoveALocalPeerLeavingDSWithPeerAddresses() throws Exception{
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
			    
		Set<String> remotePeers = new HashSet<String>();
		remotePeers.add(dscRemoteTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		// Call doNotifyFailure message
		req_508_Util.doNotifyFailure(component, dscTestStub);
		
		localPeers.remove(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		
		// Call getCompleteStatus message
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
}