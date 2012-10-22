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


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import br.edu.ufcg.lsd.commune.CommuneRuntimeException;
import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.message.MessageUtil;
import br.edu.ufcg.lsd.commune.network.xmpp.XMPPProperties;
import br.edu.ufcg.lsd.commune.testinfra.TestObjectsRegistry;

public class AcceptanceUtil {

	protected ModuleContext context;
	protected static Module application;

	public AcceptanceUtil(ModuleContext context) {
		this.context = context;
	}

	public Object getBoundObject(String moduleName, String objName) {
		String username = context.getProperty(XMPPProperties.PROP_USERNAME);
		String servername = context.getProperty(XMPPProperties.PROP_XMPP_SERVERNAME);
		
		ServiceID deploymentID = new ServiceID(username, servername, moduleName, objName);
		return TestObjectsRegistry.getTestObject(deploymentID);
	}
	
	public void createStub(Object stub, Class<?> stubClass, DeploymentID deploymentID) {
		application.createTestStub(stub, stubClass, deploymentID, true);
	}
	
	public ObjectDeployment getTestProxy(Module application, String serviceName) {
		ObjectDeployment objectDeployment = application.getObject(serviceName);
		
		Object testObject = createTestObject(application, objectDeployment);
		
		return new ObjectDeployment(application, objectDeployment.getDeploymentID(), testObject);
	}
	
	public ObjectDeployment getContainerObject(Module application, String serviceName) {
		return application.getObject(serviceName);
	}

	private Object createTestObject(Module application, ObjectDeployment deployment) {

		
	    List<Class<?>> allInterfaces = MessageUtil.getAllInterfaces(deployment.getObject().getClass());
	    Class<?>[] allInterfacesAr = new Class<?>[allInterfaces.size()];
	    allInterfaces.toArray(allInterfacesAr);
		Class<?> proxyClass = Proxy.getProxyClass(application.getClass().getClassLoader(), allInterfacesAr);
	    Object proxy = null;
	    
	    try {
	        Constructor<?> constructor = proxyClass.getConstructor( new Class[]{InvocationHandler.class} );
	        TestMessageCreator invocationHandler = new TestMessageCreator(application, deployment.getDeploymentID());
	        proxy = constructor.newInstance( new Object[]{invocationHandler});
			
	    } catch (Exception e) {
	    	throw new CommuneRuntimeException("Error while creating test object for " + deployment.getDeploymentID(), e);
	    }
		
		return proxy;
	}
}