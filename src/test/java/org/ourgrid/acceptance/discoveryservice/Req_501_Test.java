package org.ourgrid.acceptance.discoveryservice;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ourgrid.acceptance.util.discoveryservice.Req_501_Util;
import org.ourgrid.common.interfaces.control.DiscoveryServiceControl;
import org.ourgrid.discoveryservice.DiscoveryServiceComponent;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;

import br.edu.ufcg.lsd.commune.Module;

/**
 * Requirement 501
 */
public class Req_501_Test extends DiscoveryServiceAcceptanceTestCase {
	
	private Req_501_Util req_501_Util = new Req_501_Util(getComponentContext());
	
	/**
	 * #  Create a DS.
	   # Verify if a module with name "DS_MODULE" exists.
	   # Do lookup in the object "CONTROL" and verify if its type is 
		 org.ourgrid.common.interfaces.control.DiscoveryServiceCont
	 * @throws Exception
	 */
	@Test public void test_AT_501_1_DiscoveryServiceCreation() throws Exception{
		//create a DS
		DiscoveryServiceComponent component = req_501_Util.createDiscoveryServiceComponent();
		
		//verify module name
		assertTrue(isModuleStarted(component, DiscoveryServiceConstants.MODULE_NAME));
		
		//lookup object and verify if it is a org.ourgrid.common.interfaces.control.DiscoveryServiceControl
		assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, DiscoveryServiceControl.class));
	}

}
