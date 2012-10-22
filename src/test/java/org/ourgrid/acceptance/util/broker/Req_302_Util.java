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
package org.ourgrid.acceptance.util.broker;

import java.util.LinkedList;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.ourgrid.acceptance.util.BrokerAcceptanceUtil;
import org.ourgrid.broker.BrokerConfiguration;
import org.ourgrid.broker.BrokerServerModule;
import org.ourgrid.common.interfaces.control.BrokerControl;
import org.ourgrid.common.interfaces.control.BrokerControlClient;
import org.ourgrid.matchers.ControlOperationResultMatcher;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.control.ModuleAlreadyStartedException;
import br.edu.ufcg.lsd.commune.container.logging.CommuneLogger;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_302_Util extends BrokerAcceptanceUtil{
	
	private Req_301_Util req_301_Util = new Req_301_Util(context);

	public Req_302_Util(ModuleContext context) {
		super(context);
	}
	
	/**
	 * Create and start a Broker with the default public key
	 * Expect to receive a ControlOperationResult without errors
	 * Expect to log "Broker has been successfully started."
	 * 
	 */
	public BrokerServerModule startBroker(String peerUserAtServer) throws Exception {
		BrokerServerModule component = req_301_Util.createBrokerModule();
		List<String> peersUserAtServer = new LinkedList<String>();
		peersUserAtServer.add(peerUserAtServer);
		return startBroker(component, null, false, peersUserAtServer);
	}
	
	public BrokerServerModule startBroker(List<String> peersUserAtServer) throws Exception {
		BrokerServerModule component = req_301_Util.createBrokerModule();
		return startBroker(component, null, false, peersUserAtServer);
	}
	
	public BrokerServerModule startBrokerAgain(BrokerServerModule component,
			String peerUserAtServer) throws Exception {
		List<String> peersUserAtServer = new LinkedList<String>();
		peersUserAtServer.add(peerUserAtServer);
		return startBroker(component, null, true, peersUserAtServer);
	}
	
	/**
	 *  Start the Broker with the public key "wrongPublicKey" - Verify if
     *  the following warn message was logged:
     *       An unknown entity tried to start the Broker. Only the local modules 
     *  can perform this operation. Unknown entity public key: [wrongPublicKey].
     *  
	 */
	public BrokerServerModule startBroker(BrokerServerModule module, String senderPublicKey, 
			String peerUserAtServer) throws Exception {
		List<String> peersUserAtServer = new LinkedList<String>();
		peersUserAtServer.add(peerUserAtServer);
		return startBroker(module, senderPublicKey, false, peersUserAtServer);
	}

	private BrokerServerModule startBroker(BrokerServerModule component, String senderPublicKey, 
			boolean isBrokerAlreadyStarted, List<String> peersUserAtServer) {
		
		CommuneLogger newLogger = EasyMock.createMock(CommuneLogger.class);
		component.setLogger(newLogger);
		
		BrokerControl brokerControl = getBrokerControl(component);
		ObjectDeployment brokerOD = getBrokerControlDeployment(component);
		BrokerControlClient brokerControlClientMock = EasyMock.createMock(BrokerControlClient.class);
		
		
		if (senderPublicKey == null) {
			senderPublicKey = brokerOD.getDeploymentID().getPublicKey();
		}
		
		if(isBrokerAlreadyStarted) {
			brokerControlClientMock.operationSucceed(ControlOperationResultMatcher.eqType(ModuleAlreadyStartedException.class));
		} else {
			
			if(brokerOD.getDeploymentID().getPublicKey().equals(senderPublicKey)) {
				newLogger.info("Trying to start a broker component.");
				newLogger.info("Trying to set peers " + peersUserAtServer.toString());
				brokerControlClientMock.operationSucceed(ControlOperationResultMatcher.noError());
				newLogger.info("Broker has been successfully started.");
			} else {
				newLogger.warn("An unknown entity tried to perform a control operation on the Broker. Only the" +
						" local modules can perform this operation. Unknown entity public key: [" + senderPublicKey + "].");
			}
		}
		
		EasyMock.replay(newLogger);
		EasyMock.replay(brokerControlClientMock);
		
		AcceptanceTestUtil.setExecutionContext(component, brokerOD, senderPublicKey);
		brokerControl.start(brokerControlClientMock);
		EasyMock.verify(brokerControlClientMock);
		EasyMock.verify(newLogger);
		
		EasyMock.reset(newLogger);
		
		return component;
	}

}
