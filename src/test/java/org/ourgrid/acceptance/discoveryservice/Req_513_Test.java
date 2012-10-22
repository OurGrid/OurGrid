package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_504_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_505_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_513_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_513_Test extends DiscoveryServiceAcceptanceTestCase{

	private Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	private Req_505_Util req_505_util = new Req_505_Util(super.getComponentContext());
	private Req_504_Util req_504_util = new Req_504_Util(super.getComponentContext());
	private Req_511_Util req_511_util = new Req_511_Util(super.getComponentContext());	
	private Req_513_Util req_513_util = new Req_513_Util(super.getComponentContext());	
	
	@ReqTest(test = "AT-513.1", reqs = "")
	@Test public void test_AT_513_1_AnEmptyDSIsNotifiedThatAPeerIsUp() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
	
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//create a DSClient
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		req_513_util.dsClientIsUp(component, dscTestStub, false);
	
		Set<String> localPeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@ReqTest(test = "AT-513.2", reqs = "")
	@Test public void test_AT_513_2_DSWithPeersIsNotifiedThatAnotherPeerIsUp() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a DSClient
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("localpeer1", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));

		//create a remote DS
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		List<TestStub> dsTestStubs = new ArrayList<TestStub>();
		dsTestStubs.add(dsTestStub);

		//join network and receive list
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		
		req_511_util.getDiscoveryServices(component, dsServiceIDList, dsTestStub);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		Set<String> localPeers = new HashSet<String>();
		Set<String> remotePeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		//Get Complete Status
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//Call joinCommunity
		List<String> dscServiceIDList = new LinkedList<String>();
		dscServiceIDList.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		req_505_util.getRemoteWorkerProviders(component, dscServiceIDList, dscTestStub, dsTestStubs, true, dscServiceIDList);

		localPeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());	
		
		//Get Complete Status
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);

		//the DS verifies if DSClient is up
		req_513_util.dsClientIsUp(component, dscTestStub, dsTestStubs, dscServiceIDList, true);
		
		//Get Complete Status
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@ReqTest(test = "AT-513.3", reqs = "")
	@Test public void test_AT_513_3_DSWithJustPeerAddressesIsNotifiedThatAPeerIsUp() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get the instance of DAO
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
        
	    String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
	       
	    Set<String> remotePeers = new HashSet<String>();
	       
	    remotePeers.add("remotepeer@servertest/PEER/REMOTE_WORKERPROVIDER");
	               
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), remotePeers);
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
	    
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("unknownpeer", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(localDSServiceID);
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		Set<String> localPeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(remoteDSAddress, true), remotePeers);
		
		//the DS verifies if DSClient is up
		req_513_util.dsClientIsUp(component, dscTestStub, false);
		
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
	@ReqTest(test = "AT-513.4", reqs = "")
	@Test public void test_AT_513_4_DSWithJustPeerAddressesIsNotifiedThatOneOfTheseIsUp() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get the instance of DAO
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		TestStub dsTestStub = req_511_util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
        
	    String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
	       
	    Set<String> remotePeers = new HashSet<String>();

		//Create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("knownpeer", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
	    
	    remotePeers.add(dscTestStub.getDeploymentID().getServiceID().toString());
	               
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), remotePeers);
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
	    
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(localDSServiceID);
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		Set<String> localPeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(remoteDSAddress, true), remotePeers);
		
		//the DS verifies if DSClient is up
		req_513_util.dsClientIsUp(component, dscTestStub, false);
		
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
}