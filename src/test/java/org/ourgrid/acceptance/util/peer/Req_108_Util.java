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
package org.ourgrid.acceptance.util.peer;

import org.easymock.EasyMock;
import org.ourgrid.acceptance.util.PeerAcceptanceUtil;
import org.ourgrid.broker.BrokerConstants;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.deployer.xmpp.XMPPAccount;
import org.ourgrid.matchers.LoginResultMatcher;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.testinfra.AcceptanceTestUtil;

public class Req_108_Util extends PeerAcceptanceUtil {

	public Req_108_Util(ModuleContext context) {
		super(context);
	}

	/**
	 * Expects an user to successfully login at the peer
	 * @param user
	 * @param brokerPublicKey
	 * @return The DeploymentID for the <code>LocalWorkerProvider</code> interface of the user
	 */
	public DeploymentID login(Module application, XMPPAccount user, String brokerPublicKey) {
	    return login(application, user.getUsername(), user.getServerAddress(), brokerPublicKey, 
	    		null, false);
	}

	/**
	 * Expects an user to fail while logging in at the peer
	 * @param userName
	 * @param serverName
	 * @param brokerPublicKey
	 * @param errorMessage The expected error message
	 * @return The DeploymentID for the <code>LocalWorkerProvider</code> interface of the user
	 */
	public DeploymentID wrongLogin(Module application, String userName, String serverName, String brokerPublicKey, String errorMessage) {
	    return login(application, userName, serverName, brokerPublicKey, errorMessage, true);
	}

	/**
	 * Logs an user in a peer.
	 * @param userName
	 * @param serverName
	 * @param brokerPublicKey
	 * @param errorMessage The expected error message for the login process, 
	 * if this message is null, expects the user to successfully login at the peer
	 * @param peerAccounting
	 * @return The DeploymentID for the <code>LocalWorkerProvider</code> interface of the user
	 */
	public DeploymentID login(Module application, String userName, String serverName, String brokerPublicKey, String errorMessage, boolean wrongLogin) {
	    
	    // Create client mock
	    LocalWorkerProviderClient workerProviderClient = EasyMock.createMock(LocalWorkerProviderClient.class);
	    
	    ContainerID brokerContainerID = new ContainerID(userName, serverName, BrokerConstants.MODULE_NAME, brokerPublicKey);

	    if (wrongLogin) {
		    brokerContainerID = new ContainerID(userName, serverName, BrokerConstants.MODULE_NAME, brokerPublicKey);
	    }

	    DeploymentID brokerDeploymentID = new DeploymentID(brokerContainerID, BrokerConstants.LOCAL_WORKER_PROVIDER_CLIENT);
	    brokerDeploymentID.setPublicKey(brokerPublicKey);

		AcceptanceTestUtil.publishTestObject(application, brokerDeploymentID, workerProviderClient,
				LocalWorkerProviderClient.class);
	    
	    //Get bound objects
	    LocalWorkerProvider workerProvider = getLocalWorkerProvider();
	    LocalWorkerProvider workerProviderProxy = getLocalWorkerProviderProxy();
	    
	    // Record mock behavior
	    if (errorMessage == null) {
	        workerProviderClient.loginSucceed(EasyMock.same(workerProvider), LoginResultMatcher.noError());
	    } else {
	        workerProviderClient.loginSucceed(EasyMock.same(workerProvider), 
	                LoginResultMatcher.eqErrorMessage(errorMessage));
	    }
	    
	    EasyMock.replay(workerProviderClient);
	
	    //Login into peer
	    AcceptanceTestUtil.setExecutionContext(application, getLocalWorkerProviderDeployment(), 
	    		brokerDeploymentID);
	    workerProviderProxy.login(workerProviderClient);
	
	    EasyMock.verify(workerProviderClient);
	    
   
	    return brokerDeploymentID;
	}
	

	
}