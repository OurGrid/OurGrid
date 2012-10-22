package org.ourgrid.acceptance.aggregator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ourgrid.acceptance.util.aggregator.T_601_Util;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.common.interfaces.control.AggregatorControl;
import org.ourgrid.reqtrace.ReqTest;

import br.edu.ufcg.lsd.commune.Module;

public class T_601_Initial extends AggregatorAcceptanceTestCase {
	
	private T_601_Util t_601_Util = new T_601_Util(getComponentContext());
	
	/*
	 * Create an Aggregator.
	 * Verify if a module with name "AGGREGATOR_MODULE" exists.
	 * Do lookup in the object "CONTROL" and verify if its type is org.ourgrid.common.interfaces.control.AggregatorControl.
	 */
	@ReqTest(test = "AT-601.1", reqs = "create component")
	@Test public void test_AT_601_1_AggregatorCreation() throws Exception {
		
		//Create an Aggregator.
		AggregatorComponent component = t_601_Util.createAggregatorComponent();		
		
		//Verify if a module with name "AGGREGATOR_MODULE" exists.
		assertTrue(isModuleStarted(component, AggregatorConstants.MODULE_NAME));

		//Do lookup in the object "CONTROL" and verify if its type is 
		//org.ourgrid.common.interfaces.control.AggregatorControl.
		assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, AggregatorControl.class));
		
	}
	
	

}
