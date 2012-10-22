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
import org.ourgrid.acceptance.util.discoveryservice.Req_507_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class Req_507_Test extends DiscoveryServiceAcceptanceTestCase{

	Req_502_Util req_502_Util = new Req_502_Util(super.getComponentContext());
	Req_504_Util req_504_util = new Req_504_Util(super.getComponentContext());
	Req_505_Util req_505_util = new Req_505_Util(super.getComponentContext());	
	Req_511_Util req_511_Util = new Req_511_Util(super.getComponentContext());
	Req_507_Util req_507_Util = new Req_507_Util(super.getComponentContext());
	
	@ReqTest(test = "AT-507.1", reqs = "")
	@Test public void test_AT_507_1_UnknownSenderSendsHereAreDiscoveryServicesMessage() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//join network and receive list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		
		req_507_Util.hereAreDiscoveryServices(component, dsTestStub, true, dsList);

		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
	}
	
	@Test public void test_AT_507_2_DSReceivesHereAreDiscoveryServicesMessage() throws Exception{
		
		//create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//create a remote DS
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		//join network and receive list
		List<ServiceID> dsList = new ArrayList<ServiceID>();
		dsList.add(dsTestStub.getDeploymentID().getServiceID());
		
		req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);
		
		Assert.assertTrue(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		//Setting up the expected map for the status of the local DS			
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), false), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
				
		req_507_Util.hereAreDiscoveryServices(component, dsTestStub, false, dsList);
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);
		
	}
	
	@ReqTest(test = "AT-507.3", reqs = "")
	@Test public void test_AT_507_3_DSWithJustAddressesReceivesHereAreDiscoveryServicesMessage() throws Exception{
		//Create and start a DS
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		ServiceID localDSServiceID = dsOD.getDeploymentID().getServiceID();
		
		//Get the instance of DAO and add the remote DS in it
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		//Create a remote DS
		List<TestStub> dsList = new ArrayList<TestStub>();
		TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("remoteds", "servertest", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
		dsList.add(dsTestStub);
		
		//The remote DS joins the network
		List<ServiceID> dsServiceIDList = new ArrayList<ServiceID>();
		dsServiceIDList.add(dsTestStub.getDeploymentID().getServiceID());
		
		//Set the remote DS and its peers in DAO
		String remoteDSAddress = dsTestStub.getDeploymentID().getServiceID().toString();
		
		dsDao.addDiscoveryService(new DiscoveryServiceInfo(remoteDSAddress, false), new HashSet<String>());
	    
		Assert.assertFalse(dsDao.getDSInfo(dsTestStub.getDeploymentID().getServiceID().toString()).isUp());
		
		req_507_Util.hereAreDiscoveryServices(component, dsTestStub, false, dsServiceIDList, true);
		
		//Setting up the expected map for the status of the local DS			
		Map < DiscoveryServiceInfo , Set <String> > expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>> ();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(dsTestStub.getDeploymentID().getServiceID().toString(), false), new HashSet<String>());
		
		//Call getCompletStatusMessage
		req_504_util.getCompleteStatus(component, expectedLocalDSNetwork);

		}	
}