package org.ourgrid.acceptance.discoveryservice;

import static org.junit.Assert.assertFalse;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_501_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_503_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.reqtrace.ReqTest;


public class Req_503_Test extends DiscoveryServiceAcceptanceTestCase {
	
	Req_501_Util req_501_Util = new Req_501_Util(getComponentContext());
	Req_502_Util req_502_Util = new Req_502_Util(getComponentContext());
	Req_503_Util req_503_Util = new Req_503_Util(getComponentContext());
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key "publicKey1";
     * Stop the DS with the public key "wrongPublicKey" - Verify if the following warn message was logged:
         1. An unknown entity tried to stop the Discovery Service. Only the local modules can perform this operation. Unknown entity public key: [senderPublicKey].
	 * @throws Exception
	 */
	@ReqTest(test = "AT-503.1", reqs = "")
	@Test public void test_AT_503_1_UnknownEntitySendAStopCommand() throws Exception {
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		// Stop the DS with the public key "wrongPublicKey"
		req_503_Util.stopDiscoveryService(component, "wrongPublicKey");
	}
	
	/**
	 * Create a DS;
     * Start a DS
     * Stop the DS with the correct public key;
     * Verify if the following remote objects are NOT bound:
          o DS_OBJECT
          o COMMUNITY_STATUS_PROVIDER
          o DS_CLIENT_MONITOR
	 * Verify if the ControlClient received the operation succeed message.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-503.2", reqs = "")
	@Test public void test_AT_503_2_StopCommand() throws Exception {
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		// Stop the Discovery Service
		req_503_Util.stopDiscoveryService(component);
		
		// Verify if DS_OBJECT is NOT bound
		assertFalse(isBound(component, DiscoveryServiceConstants.DS_OBJECT_NAME));
		
		// Verify if COMMUNITY_STATUS_PROVIDER is NOT bound
		assertFalse(isBound(component, DiscoveryServiceConstants.COMMUNITY_STATUS_PROVIDER));
		
		// Verify if DS_CLIENT_MONITOR is NOT bound
		assertFalse(isBound(component, DiscoveryServiceConstants.DS_CLIENT_MONITOR));
	}
	
	/**
	 * Create a DS;
	 * Stop the DS with the correct public key;
	 * Verify if the Control Result Operation contains an exception with the message above.
	 * @throws Exception
	 */
	@ReqTest(test = "AT-503.3", reqs = "")
	@Test public void test_AT_503_3_TryToStopANotStartedDS() throws Exception {
		// Create a DS
		DiscoveryServiceComponent component = req_501_Util.createDiscoveryServiceComponent();
		
		// Stop the DS with the correct public key
		req_503_Util.stopUnstartedDiscoveryService(component);
	}
}
