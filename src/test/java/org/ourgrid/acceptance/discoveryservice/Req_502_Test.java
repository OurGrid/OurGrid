package org.ourgrid.acceptance.discoveryservice;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_503_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_511_Util;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;


public class Req_502_Test extends DiscoveryServiceAcceptanceTestCase {
	
		private Req_502_Util req_502_Util = new Req_502_Util(getComponentContext());
		private Req_503_Util req_503_Util = new Req_503_Util(getComponentContext());
		private Req_511_Util req_511_Util = new Req_511_Util(getComponentContext());
		
		/**
		 * Create a Discovery Service;
		 * Start the DS with the public key "wrongPublicKey" - Verify if the following warn message was logged:
          	o An unknown entity tried to start the Discovery Service. Only the local modules can perform this operation. Unknown entity public key: [senderPublicKey].
		 * @throws Exception
		 */
		@Test public void test_AT_502_1_UnknownEntitySendAStartCommand() throws Exception{
			// Start the Discovery Service with the public key "wrongPublicKey"
			req_502_Util.startDiscoveryServiceWithWrongPublicKey("wrongPublicKey");
		}
		
		/**
		 * Create a DS;
		 * Start a DS with the correct public key;
    	 * Verify if the following message was logged:
          	o "Discovery Service has been successfully started."
		 * Get(lookup) the remote object "COMMUNITY_STATUS_PROVIDER" and verify if its type is:
          	o org.ourgrid.common.interfaces.CommunityStatusProvider
		 * Verify if the ControlClient received the operation succeed message.
		 * @throws Exception
		 */
		@Test public void test_AT_502_2_StartCommand() throws Exception{
			// Start the Discovery Service
			DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
			
			// Get(lookup) the remote object "COMMUNITY_STATUS_PROVIDER"
			assertTrue(isBound(component, DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER, 
					CommunityStatusProvider.class));
		}
		
		/**
		 * Create a DS;
		 * Start a DS with the correct public key;
		 * Start the same DS again with the correct public key.
		 * Verify if the Control Result Operation contains an exception whose type is:
          	o br.edu.ufcg.lsd.commune.container.control.ComponentAlreadyStartedException
		 * @throws Exception
		 */
		@Test public void test_AT_502_3_TryToStartAAlreadyStartedDS() throws Exception{
			// Start the Discovery Service
			DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
			
			// Start the Discovery Service again
			req_502_Util.startDiscoveryServiceAgain(component);
		}
		
		@Test public void test_AT_502_4_RestartDSWithNetwork() throws Exception{
			// Start the Discovery Service
			DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
			
			//create a remote DS
			TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest1", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
			
			//join network and receive list
			List<ServiceID> dsList = new ArrayList<ServiceID>();
			dsList.add(dsTestStub.getDeploymentID().getServiceID());
			
			req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);

			assertTrue(AcceptanceTestUtil.isInterested(component, dsTestStub.getDeploymentID().getServiceID(), 
					dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment().getDeploymentID()));
			
			//stop local DS
			req_503_Util.stopDiscoveryService(component);
			
			//restart DS
			component = req_502_Util.startDiscoveryService();
			
			assertTrue(AcceptanceTestUtil.isInterested(component, dsTestStub.getDeploymentID().getServiceID(), 
					dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment().getDeploymentID()));
		}
		
		
	    @Test public void test_AT_502_5_VerifyLocalDSRegisterInterest() throws Exception{
	    	
	    	// Start the Discovery Service
			DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
	    	
			//create remote DS 1
			TestStub dsTestStub = req_511_Util.createDiscoveryService(new ServiceID("test1", "servertest1", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
			
			//create remote DS 2
			TestStub dsTestStub2 = req_511_Util.createDiscoveryService(new ServiceID("test2", "servertest2", "DS", DiscoveryServiceConstants.DS_OBJECT_NAME));
			
			//Remote DS 2 joins network and receive list
			List<ServiceID> dsList = new ArrayList<ServiceID>();
			dsList.add(dsTestStub.getDeploymentID().getServiceID());
			
			req_511_Util.getDiscoveryServices(component, dsList, dsTestStub);
			
			assertTrue(AcceptanceTestUtil.isInterested(component, dsTestStub.getDeploymentID().getServiceID(), 
					dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment().getDeploymentID()));
			
			//Remote DS 1 joins network and receive list
			dsList.add(dsTestStub2.getDeploymentID().getServiceID());
			req_511_Util.getDiscoveryServices(component, dsList, dsTestStub2);
			
			assertTrue(AcceptanceTestUtil.isInterested(component, dsTestStub.getDeploymentID().getServiceID(), 
					dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment().getDeploymentID()));
			assertTrue(AcceptanceTestUtil.isInterested(component, dsTestStub2.getDeploymentID().getServiceID(), 
					dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment().getDeploymentID()));
			
			//Stop local DS
			req_503_Util.stopDiscoveryService(component);
			
			//Restart local DS
			req_502_Util.startDiscoveryService();
			
		    //Get DS Monitor Object
			ObjectDeployment dsMonitorDeployment = dsAcceptanceUtil.getDiscoveryServiceMonitorDeployment();
			
			AcceptanceTestUtil.isInterested(component, dsTestStub.getDeploymentID().getServiceID(), dsMonitorDeployment.getDeploymentID());
			AcceptanceTestUtil.isInterested(component, dsTestStub2.getDeploymentID().getServiceID(), dsMonitorDeployment.getDeploymentID());
	    }
}
