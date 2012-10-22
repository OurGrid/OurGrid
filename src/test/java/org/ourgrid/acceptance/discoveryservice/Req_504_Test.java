package org.ourgrid.acceptance.discoveryservice;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_501_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_502_Util;
import org.ourgrid.acceptance.util.discoveryservice.Req_504_Util;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;


public class Req_504_Test extends DiscoveryServiceAcceptanceTestCase {

	private Req_501_Util req_501_Util = new Req_501_Util(getComponentContext());
	private Req_502_Util req_502_Util = new Req_502_Util(getComponentContext());
	private Req_504_Util req_504_Util = new Req_504_Util(getComponentContext());
	
	/**
	 * Create a DS;
     * Start a DS with the correct public key;
     * Call the getCompleteStatus message;
     * Verify if the conectedPeers list is empty;
	 * @throws Exception
	 */
	
	@ReqTest(test = "AT-504.1", reqs = "")
	@Test public void test_AT_504_1_DSStatusWithStartedComponent() throws Exception {
		// Start the Discovery Service
		DiscoveryServiceComponent component = req_502_Util.startDiscoveryService();
		
		//Get Local DS ServiceID 
		ObjectDeployment dsOD = component.getObject(DiscoveryServiceConstants.DS_OBJECT_NAME);
		String localDSServiceID = dsOD.getDeploymentID().getServiceID().toString();
		
		Map<DiscoveryServiceInfo, Set<String>> expectedLocalDSNetwork = new TreeMap<DiscoveryServiceInfo, Set<String>>();
		expectedLocalDSNetwork.put(new DiscoveryServiceInfo(localDSServiceID.toString(), true), new HashSet<String>());
		
		// Call the getCompleteStatus message
		req_504_Util.getEmptyCompleteStatusWithStartedDS(component, expectedLocalDSNetwork);
	}
	
	/**
	 * Create a DS;
     * Call the getCompleteStatus message;
     * Verify if the following warn message was logged:
     *     o Received a status request from: "clientAddress", but the component is not started.
	 */
	@ReqTest(test = "AT-504.2", reqs = "")
	@Test public void test_AT_504_2_DSStatusWithNotStartedComponent() throws Exception {
		// Create the Discovery Service
		DiscoveryServiceComponent component = req_501_Util.createDiscoveryServiceComponent();
		
		// Call the getCompleteStatus message
		// Verify if the following warn message was logged:
		//     o Received a status request from: "clientAddress", but the component is not started.
		req_504_Util.getEmptyCompleteStatus(component, false);
	}
}
