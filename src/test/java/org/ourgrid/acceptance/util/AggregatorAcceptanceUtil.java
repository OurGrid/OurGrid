/*
 * Copyright (C) 2008 Universidade Federal de Campina Grande
 *  
 * This file is part of OurGrid. 
 *
 * OurGrid is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free 
 * Software Foundation, either version 3 of the License, or (at your option) 
 * any later version. 
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT 
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. 
 * 
 * You should have received a copy of the GNU Lesser General Public License 
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.ourgrid.acceptance.util;

import org.junit.After;
import org.junit.Before;
import org.ourgrid.aggregator.AggregatorComponent;
import org.ourgrid.aggregator.AggregatorConfiguration;
import org.ourgrid.aggregator.AggregatorConstants;
import org.ourgrid.common.config.Configuration;
import org.ourgrid.common.interfaces.control.AggregatorControl;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.TestObjectsRegistry;

public class AggregatorAcceptanceUtil extends AcceptanceUtil {

//	private static final String CONF_XML_PATH = "aggregator-hibernate.cfg.xml";
	
	public AggregatorAcceptanceUtil(ModuleContext context) {
		super(context);
	}
	
	@Before
	public static void setUp() throws Exception{
		System.setProperty("OGROOT", ".");
        Configuration.getInstance(AggregatorConfiguration.AGGREGATOR);
   	}
	
	@After
    public static void tearDown() throws Exception {
		if (application != null && !application.getContainerDAO().isStopped()) {
			application.stop();
		}
		TestObjectsRegistry.reset();
    }
	
	public AggregatorControl getAggregatorControl(AggregatorComponent component) {
		ObjectDeployment deployment = getAggregatorDeployment(component);
		return (AggregatorControl) deployment.getObject();
	}
	
	public ObjectDeployment getAggregatorDeployment(AggregatorComponent component) {
		return component.getObject(Module.CONTROL_OBJECT_NAME);
	}
	
//	public Aggregator getAggregator(AggregatorComponent component) {
//		return (Aggregator) getTestProxy(component, 
//				AggregatorConstants.OBJECT_NAME).getObject();
//	}
	
	public ObjectDeployment getAggregatorMonitorDeployment(AggregatorComponent component) {
		return component.getObject(AggregatorConstants.CMMSP_CLIENT_OBJECT_NAME);
	}
}
