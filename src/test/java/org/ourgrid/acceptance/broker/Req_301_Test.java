/*
 * Copyright (c) 2002-2008 Universidade Federal de Campina Grande
 * 
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.ourgrid.acceptance.broker;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.ourgrid.acceptance.util.broker.Req_301_Util;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.interfaces.control.BrokerControl;

import br.edu.ufcg.lsd.commune.Module;

/**
 * Requirement 301
 */
public class Req_301_Test extends BrokerAcceptanceTestCase {
	
	private Req_301_Util req_301_Util = new Req_301_Util(getComponentContext());
	
	/**
	 * Verify the Broker module creation and the deployment of Broker Control.
	 */
	@Test public void test_at_301_1_BrokerCreation() throws Exception {
		//create broker
		BrokerServerModule component = req_301_Util.createBrokerModule();
		
		//verify module name
		assertTrue(isModuleStarted(component, BrokerConstants.MODULE_NAME));
		
		//lookup object and verify if it is a org.ourgrid.common.interfaces.control.BrokerControl
		assertTrue(isBound(component, Module.CONTROL_OBJECT_NAME, BrokerControl.class));
	} 
}