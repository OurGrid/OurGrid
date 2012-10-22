package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.ourgrid.acceptance.util.discoveryservice.Req_512_Util;
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

public class Req_512_Test extends DiscoveryServiceAcceptanceTestCase{

	private Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	private Req_505_Util req_505_util = new Req_505_Util(super.getComponentContext());
	private Req_504_Util req_504_Util = new Req_504_Util(getComponentContext());
	private Req_511_Util req_511_Util = new Req_511_Util(super.getComponentContext());
	private Req_512_Util req_512_Util = new Req_512_Util(super.getComponentContext());


	/**
	 * 
	 */
	@ReqTest(test = "AT-512.1", reqs = "")
	@Test public void test_AT_512_1_addOneDSAndGetDS() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		//create a DSClient
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		//The created DS join the network
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		List<String> dsList2 = new ArrayList<String>();
		dsList2.add(localDSServiceID.toString());
		dsList2.add(dsTestStub.getDeploymentID().getServiceID().toString());
		
		//DSClient requests the network list
		req_512_Util.getDiscoveryServices(component, dscTestStub, dsList2, 1, 10);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);

		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	/**
	 * 
	 */
	@ReqTest(test = "AT-512.2", reqs = "")
	@Test public void test_AT_512_2_addFiveDSs() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create five remote DSs
		TestStub dsTestStub1 = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest1", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub2 = req_511_Util.createDiscoveryService(new ServiceID("test2", "servertest2", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub3 = req_511_Util.createDiscoveryService(new ServiceID("test3", "servertest3", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub4 = req_511_Util.createDiscoveryService(new ServiceID("test4", "servertest4", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub5 = req_511_Util.createDiscoveryService(new ServiceID("test5", "servertest5", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		//create a DSClient
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		//each DS will join network and receive the actual list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub1.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub1);
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub1.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub2.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub2);
	
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub2.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		dsList.add(dsTestStub3.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub3);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub3.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		dsList.add(dsTestStub4.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub4);
	
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub4.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		dsList.add(dsTestStub5.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub5);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub5.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		
		//DSClient requests the network list
		List<String> dsList2 = new ArrayList<String>();
		dsList2.add(dsTestStub1.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub2.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub3.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub4.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub5.getDeploymentID().getServiceID().toString());
		dsList2.add(localDSServiceID.toString());
		req_512_Util.getDiscoveryServices(component, dscTestStub, dsList2, 5, 10);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub1.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub2.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub3.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub4.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub5.getDeploymentID().getServiceID().toString()).isUp());
	}
	/**
	 * 
	 */
	@ReqTest(test = "AT-513.3", reqs = "")
	@Test public void test_AT_512_3_addMoreThenTenDSs() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create thirteen remote DSs
		TestStub dsTestStub1 = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest1", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub2 = req_511_Util.createDiscoveryService(new ServiceID("test2", "servertest2", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub3 = req_511_Util.createDiscoveryService(new ServiceID("test3", "servertest3", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub4 = req_511_Util.createDiscoveryService(new ServiceID("test4", "servertest4", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub5 = req_511_Util.createDiscoveryService(new ServiceID("test5", "servertest5", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub6 = req_511_Util.createDiscoveryService(new ServiceID("test6", "servertest6", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub7 = req_511_Util.createDiscoveryService(new ServiceID("test7", "servertest7", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub8 = req_511_Util.createDiscoveryService(new ServiceID("test8", "servertest8", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub9 = req_511_Util.createDiscoveryService(new ServiceID("test9", "servertest9", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub10 = req_511_Util.createDiscoveryService(new ServiceID("test10", "servertest10", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub11 = req_511_Util.createDiscoveryService(new ServiceID("test11", "servertest11", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub12 = req_511_Util.createDiscoveryService(new ServiceID("test12", "servertest12", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		TestStub dsTestStub13 = req_511_Util.createDiscoveryService(new ServiceID("test13", "servertest13", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		//create a DSClient
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		//each DS will join network and receive the actual list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub1.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub1);
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub1.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub2.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub2);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub2.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub3.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub3);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub3.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub4.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub4);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub4.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub5.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub5);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub5.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub6.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub6);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub6.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub7.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub7);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub7.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub8.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub8);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub8.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub9.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub9);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub9.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub10.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub10);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub10.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub11.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub11);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub11.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub12.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub12);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub12.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub13.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub13);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub13.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		//DSClient requests the network list, the client will receive an list with 10 random DSs contained in the network list
		List<String> dsList2 = new ArrayList<String>();
		dsList2.add(dsTestStub1.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub2.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub3.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub4.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub5.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub6.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub7.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub8.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub9.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub10.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub11.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub12.getDeploymentID().getServiceID().toString());
		dsList2.add(dsTestStub13.getDeploymentID().getServiceID().toString());
		dsList2.add(localDSServiceID.toString());
		req_512_Util.getDiscoveryServices(component, dscTestStub, dsList2, 13, 10);
		
		Set<String> peers = new HashSet<String>();
		peers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), peers);
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub1.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub2.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub3.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub4.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub5.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub6.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub7.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub8.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub9.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub10.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub11.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub12.getDeploymentID().getServiceID().toString()).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub13.getDeploymentID().getServiceID().toString()).isUp());
	}
	
	/**
	 * 
	 */
	@ReqTest(test = "AT-512.4", reqs = "")
	@Test public void test_AT_512_4_addOneDSAndGetDSWithPeerAddresses() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//Get the instance of DAO
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
        
	    String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
	       
	    Set<String> peers = new HashSet<String>();
	       
	    peers.add("test@servertest/PEER/REMOTE_WORKERPROVIDER");
	               
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), peers);
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
	    
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Create a WorkerProvider
		TestStub dscTestStub = req_505_util.createDiscoveryServiceClient(new ServiceID("test", "servertest", 
				PeerConstants.MODULE_NAME, PeerConstants.REMOTE_WORKER_PROVIDER));
		
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		//The created DS join the network
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), peers);
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		List<String> dsList2 = new ArrayList<String>();
		dsList2.add(localDSServiceID.toString());
		dsList2.add(dsTestStub.getDeploymentID().getServiceID().toString());
		
		//DSClient requests the network list
		req_512_Util.getDiscoveryServices(component, dscTestStub, dsList2, 1, 10);
		
		Set<String> localPeers = new HashSet<String>();
		localPeers.add(dscTestStub.getDeploymentID().getContainerID().getUserAtServer());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		
		// Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
}