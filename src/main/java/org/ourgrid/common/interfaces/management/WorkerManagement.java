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
package org.ourgrid.common.interfaces.management;

import java.security.cert.X509Certificate;
import java.util.List;

import org.ourgrid.common.WorkerLoginResult;

import br.edu.ufcg.lsd.commune.api.Remote;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

/**
 * 
 * Provides methods to the Worker Management.
 * 
 * Through this interface, the Peer can:
 * - Set itself like the Master Peer of this Worker.
 * - Order that the Worker be allocated to a Remote Peer or to a Broker.
 * - Stop a Worker execution
 */
@Remote
public interface WorkerManagement {

	/**
	 * The worker logged into its master peer.
	 * @param workerManagementClient 
	 * @param loginResult The result of the login procedure.
	 */
	public void loginSucceeded(WorkerManagementClient workerManagementClient, WorkerLoginResult loginResult);

	/**
	 * Order that the Worker be allocated to a Remote Peer.
	 * @param peerPubKey Peer public key.
	 * @param usersDN User´s Domain Name
	 * @param caCertificates X509 Certificates
	 */
	public void workForPeer(String peerPubKey, List<String> usersDN, List<X509Certificate> caCertificates);
	
	/**
	 * Order that the Worker be allocated to a Remote Peer.
	 * @param peerPubKey Peer public key
	 */
	public void workForPeer(String peerPubKey);

	/**
	 * Order that the Worker be allocated to a Broker specified by the DeploymentID.
	 * @param broker Broker DeploymentID
	 */
	public void workForBroker(DeploymentID brokerID);

	/**
	 * Stops the Worker execution.
	 */
	public void stopWorking();
	
}
