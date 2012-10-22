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
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_514_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_514_Test extends DiscoveryServiceAcceptanceTestCase {
	
	private Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	private Req_504_Util req_504_Util = new Req_504_Util(super.getComponentContext());
	private Req_511_Util req_511_Util = new Req_511_Util(super.getComponentContext());
	private Req_514_Util req_514_Util = new Req_514_Util(super.getComponentContext());
	
	/**
	 * 
	 */
	@ReqTest(test = "AT-514.1", reqs = "")
	@Test public void test_AT_514_1_DSIsUpNotificationFromADSOutOfNetwork() throws Exception{
		
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
				
		// Notification Failure for DS
		req_514_Util.notMemberOfNetworkDSIsUp(component, dsTestStub);	
	}
	
	/**
	 * 
	 */
	@ReqTest(test = "AT-514.2", reqs = "")
	@Test public void test_AT_514_2_DSIsUpNotificationInALocalDSWithDSAddresses() throws Exception{
		
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
        
	    String remoteDSAddress = "test1@servertest/DS/DS_OBJECT";
	       
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, false), new HashSet<String>());
		
		Assert.assertFalse(dsDao.getDSInfo(remoteDSAddress).isUp());
	    
	    //Create a remote DS
	    TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));

	    // Recovery Notification for DS
		req_514_Util.dsIsUp(component, dsTestStub);	
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());		
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Generate Map of DSInfo 
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
	@ReqTest(test = "AT-514.3", reqs = "")
	@Test public void test_AT_514_3_DSIsUpNotificationInLocalDSWithDSStubs() throws Exception{
		
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
        
		//Create a remote DS 1
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//Create a remote DS 2
		TestStub dsTestStub2 = req_511_Util.createDiscoveryService(new ServiceID("test2", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
	    String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
	       
	    dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, true), new HashSet<String>());
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
	    
		// Remote DS 1 Joins network and receives list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		dsList.add(dsTestStub2.getDeploymentID().getServiceID());
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub2);

		Assert.assertTrue(dsDao.getDSInfo(dsTestStub2.getDeploymentID().getServiceID().toString()).isUp());
		
		// Notification Failure for DS
		req_514_Util.dsIsUp(component, dsTestStub);	
		
		Assert.assertTrue(dsDao.getDSInfo(remoteDSAddress).isUp());
		
		//Generate Map of DSInfo 
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub2.getDeploymentID().getServiceID().toString(), true), new HashSet<String>());

		//Call getCompletStatusMessage
		req_504_Util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
}
