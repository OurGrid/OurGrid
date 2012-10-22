package org.ourgrid.acceptance.discoveryservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import junit.framework.Assert;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_504_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceComponentContextFactory;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.config.DiscoveryServiceConfiguration;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.PropertiesFileParser;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestContext;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_511_Test extends DiscoveryServiceAcceptanceTestCase{

	private static final String REMOTE_DS_ADDRESS = "server@test/DS/DS_OBJECT";
	
	Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	Req_504_Util req_504_Util = new Req_504_Util(super.getComponentContext());
	Req_511_Util req_511_Util = new Req_511_Util(super.getComponentContext());
	
	@Override
	protected TestContext getComponentContext() {
		TestContext testContext = new TestContext(
				new DiscoveryServiceComponentContextFactory(
						new PropertiesFileParser(DS_PROP_FILEPATH
						)).createContext());
		
		testContext.set(DiscoveryServiceConfiguration.PROP_DS_NETWORK, REMOTE_DS_ADDRESS);
		
		return testContext;
	}

	/**
	 * 
     */
	@ReqTest(test = "AT-511.1", reqs = "")
	@Test public void test_AT_511_1_addsOneDS() throws Exception{

		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();

		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest1", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

		//join network and receive list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);
		
		Set<String> localPeers = new HashSet<String>();
		Set<String> remotePeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), remotePeers);
		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);

		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
	}
	
	/**
	 * 
     */
	@ReqTest(test = "AT-511.2", reqs = "")
	@Test public void test_AT_511_2_addManyDSs() throws Exception{

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

		//each one will join network and receive the actual list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub1.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub1);
		
		Set<String> localPeers = new HashSet<String>();
		Set<String> remotePeers = new HashSet<String>();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub1.getDeploymentID().getServiceID().toString(), true), remotePeers);		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub2.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub2);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub2.getDeploymentID().getServiceID().toString(), true), remotePeers);
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub3.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub3);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub3.getDeploymentID().getServiceID().toString(), true), remotePeers);
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub4.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub4);

		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub4.getDeploymentID().getServiceID().toString(), true), remotePeers);
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		dsList.add(dsTestStub5.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub5);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub5.getDeploymentID().getServiceID().toString(), true), remotePeers);
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
	@ReqTest(test = "AT-511.3", reqs = "")
	@Test public void test_AT_511_3_startedWithDSAdresses() throws Exception{

		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();

		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
        
	    String remoteDSAddress = "test1@servertest/DS/DS_OBJECT";
	       
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, false), new HashSet<String>());
		
		Set<String> localPeers = new HashSet<String>();
		Set<String> remotePeers = new HashSet<String>();
	    
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), localPeers);
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(remoteDSAddress, true), remotePeers);		
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	    
		//create a remote DS
		TestStub dsTestStub1 = req_511_Util.createDiscoveryService(new ServiceID("server", "test", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//join network and receive list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(ServiceID.parse(remoteDSAddress));
		dsList.add(dsTestStub1.getDeploymentID().getServiceID());
		
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub1);
		
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub1.getDeploymentID().getServiceID().toString(), true), remotePeers);
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
		Assert.assertFalse(dsDao.getDSInfo(remoteDSAddress).isUp());
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub1.getDeploymentID().getServiceID().toString()).isUp());
		
	}
	
}