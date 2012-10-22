package org.ourgrid.acceptance.aggregator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.classextension.EasyMock;
import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.T_602_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConfiguration;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.aggregator.communication.receiver.CommunityStatusProviderClientReceiver;
import org.ourgrid.aggregator.communication.receiver.PeerStatusProviderClientReceiver;
import org.ourgrid.common.interfaces.CommunityStatusProvider;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;
import br.edu.ufcg.lsd.commune.testinfra.util.TestStub;

public class T_602_Created extends AggregatorAcceptanceTestCase {
	
	private T_602_Util t_602_Util = new T_602_Util(getComponentContext());
	private AggregatorComponent component;
	
	/*
	 * Create an Aggregator;
	 * Start the Aggregator with the public key "wrongPublicKey" - Verify if the following warn message was logged:
     *     o An unknown entity tried to start the Aggregator. Only the local modules can perform this operation. 
     *       Unknown entity public key: [senderPublicKey].
	 */
	@ReqTest(test = "AT-602.1", reqs = "start component - wrong public key")
	@Test public void test_AT_602_1_StartAggregatorWithWrongPublicKey() throws Exception {
		t_602_Util.startAggregatorWithWorngPublicKey("wrongPublicKey");		
	}
	
	/*
	 * Create an Aggregator;
	 * Start an Aggregator with the correct public key;
	 * Verify if the following message was logged:
     *     o "Aggregator has been successfully started."
	 * Get(lookup) the remote object "AGGREGATOR_DS_MONITOR_OBJECT" and verify if its type is:
     *     o org.ourgrid.aggregator.communication.receiver.CommunityStatusProviderClientReceiver
	 * Verify if the ControlClient received the operation succeed message.
	 *
	 */
	@ReqTest(test = "AT-602.2", reqs = "start component correctly")
	@Test public void test_AT_602_2_StartAggregatorCorrectly() throws Exception {
		component = t_602_Util.startAggregator();
		
		assertTrue(isBound(component, AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME, 
				CommunityStatusProviderClientReceiver.class));
		
		assertTrue(isBound(component, AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME, 
				PeerStatusProviderClientReceiver.class));
		
		assertTrue(isModuleStarted(component, AggregatorConstants.MODULE_NAME));
		
		
		//registro do interresse do ds
		ObjectDeployment aggMonitorTest = aggAccept.getAggregatorMonitorDeployment(component);
		
		ServiceID serviceID = new ServiceID(
				new ContainerID(
						getComponentContext().getProperty(AggregatorConfiguration.PROP_DS_USERNAME),
						getComponentContext().getProperty(AggregatorConfiguration.PROP_DS_SERVERNAME),
						DiscoveryServiceConstants.MODULE_NAME),
				DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER);
		
		DeploymentID aggID = new DeploymentID(serviceID);
		CommunityStatusProvider rwp = EasyMock.createMock(CommunityStatusProvider.class);
		
		AcceptanceTestUtil
				.isInterested(component, (new TestStub(aggID, rwp)).
						getDeploymentID().getServiceID(), 
						aggMonitorTest.getDeploymentID());
		
		
		
	}
	
	/*
	 * Create an Aggregator
	 * Stop an Aggregator without start him.
	 * Verify if the following message was logged:
	 *  o "Aggregator has not been started".
	 */
	@ReqTest(test = "AT-602.3", reqs = "stop component")
	@Test public void test_AT_602_3_StopAggregatorWithoutStartThisComponent() throws Exception {
		t_602_Util.stopAggregatorWithoutStartThisComponent();
		
	}
	
	
	
	/*
	 * Stop an Aggregator with the correct public key
	 * 
	 */
	@ReqTest(test = "AT-602.3", reqs = "stop component")
	@Test public void test_AT_602_4_StopAggregator() throws Exception {
		component = t_602_Util.startAggregator();
		t_602_Util.stopAggregatorAfterStart(component);
		
		assertFalse(isBound(component, AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME ));
		
		assertFalse(isBound(component, AggregatorConstants.STATUS_PROVIDER_CLIENT_OBJECT_NAME ));
		
		assertFalse(isBound(component, AggregatorConstants.GET_PEER_STATUS_PROVIDER_ACTION_NAME ));
		
	}
	

}
