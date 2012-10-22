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
package org.ourgrid.peer.ui.async.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.common.ui.OurGridUIController;
import org.ourgrid.common.ui.servicesetup.ServiceSetupException;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.peer.ui.async.model.GetPeerCompleteStatusRepeatedAction;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIListener;
import org.ourgrid.peer.ui.async.model.PeerAsyncUIModel;
import org.ourgrid.peer.ui.async.util.DiscoveryServiceRecoveryInterested;

import br.edu.ufcg.lsd.commune.container.servicemanager.client.InitializationContext;
import br.edu.ufcg.lsd.commune.container.servicemanager.client.async.AsyncApplicationClient;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ContainerID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.ConnectionListener;
import br.edu.ufcg.lsd.commune.network.xmpp.CommuneNetworkException;
import br.edu.ufcg.lsd.commune.processor.ProcessorStartException;
import br.edu.ufcg.lsd.commune.processor.interest.InterestRequirements;

/**
 * Asynchronous Component Client for Peer Component
 *
 */
public class PeerAsyncApplicationClient extends AsyncApplicationClient<PeerManager, PeerAsyncManagerClient> implements OurGridUIController {

	/*in seconds*/
	private static final int WAITFORDS_INTERVAL = 10;
	public static final String GET_STATUS_ACTION = "GET_STATUS_ACTION";
	public static final String DS_INTERESTED = "DS_INTERESTED";
	
	private PeerAsyncUIModel model;
	
	public PeerAsyncApplicationClient(ModuleContext context, PeerAsyncUIModel model, ConnectionListener listener) 
		throws CommuneNetworkException, ProcessorStartException {
		
		super("PEER_ASYNC_UI", context, listener);
		this.model = model;
	}
	
	@Override
	protected void deploymentDone() {
		addActionForRepetition(GET_STATUS_ACTION, new GetPeerCompleteStatusRepeatedAction());
	}
	
	@Override
	protected InitializationContext<PeerManager, PeerAsyncManagerClient> createInitializationContext() {
		
		return new PeerAsyncInitializationContext();
	}

	/**
	 * Add a new user for this peer.
	 * 
	 * @param login User login
	 * @param password user password
	 */
	public void addUser( String login ) {
		getManager().addUser(getManagerClient(), login);
	}
	
	/**
	 * Remove a user for this peer.
	 * 
	 * @param login User login
	 */
	public void removeUser( String login ) {
		getManager().removeUser(getManagerClient(), login);
	}
	
	/**
	 * Asynchronous request for the peer users' status.
	 */
	public void getUsersStatus() {
		getManager().getUsersStatus(getManagerClient());
	}
	
	/**
	 * Asynchronous request for the peer local workers' status.
	 */
	public void getLocalWorkersStatus() {
		getManager().getLocalWorkersStatus(getManagerClient());
	}
	
	/**
	 * @return The model associated to this ComponentClient
	 */
	public PeerAsyncUIModel getModel() {
		return model;
	}
	
	/**
	 * Registers a listener to be interested on model changes.
	 * @param listener
	 */
	public void addListener(PeerAsyncUIListener listener) {
		model.addListener(listener);
	}

	/**
	 * Checks if a DiscoveryService peer is Up, by registering interest on its recovery
	 * @param userName The DiscoveryService peer user name in the Jabber server
	 * @param serverName The DiscoveryService peer Jabber server name
	 * @return True if the DiscoveryService peer is UP, false otherwise.
	 */
	public boolean testConnectionWithDiscoveryService(String userName, String serverName) {
		
		ServiceID dsID = new ServiceID(
				new ContainerID(userName, serverName, DiscoveryServiceConstants.MODULE_NAME), 
				DiscoveryServiceConstants.DS_OBJECT_NAME);
		
		DiscoveryServiceRecoveryInterested interested = new DiscoveryServiceRecoveryInterested();
		
		this.deploy(DS_INTERESTED, interested);
		
		InterestRequirements requirements = new InterestRequirements(this.getContext());
		this.registerInterest(DS_INTERESTED, DiscoveryService.class, 
				dsID, requirements);
		
		int sleepTime = 0;
		
		while (sleepTime < WAITFORDS_INTERVAL && !interested.hasBeenRecovered()) {
			sleepTime++;
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			
		}
		
		
		this.undeploy(DS_INTERESTED);
		
		return interested.hasBeenRecovered();
	}
	
	/**
	 * Saves current properties on its correspondent file
	 * @see PeerAsyncUIModel.saveProperties
	 */
	public void saveProperties() throws IOException {
		model.saveProperties();
	}

	/**
	 * Load properties stored on peer properties file on the model.
	 */
	public void loadProperties() {
		model.loadProperties();		
	}

	/**
	 * Sets a property value on the model.
	 * @param prop The property to be set
	 * @param value The new value for this property
	 */
	public void setProperty(String prop, String value) {
		model.setProperty(prop, value);
	}
	
	/**
	 * Install peer as service
	 * @throws ServiceSetupException
	 */
	public void installAsService() throws ServiceSetupException {
		model.getServiceSetup().installAsService();
	}

	/**
	 * Uninstall peer service
	 * @throws ServiceSetupException
	 */
	public void uninstallService() throws ServiceSetupException {
		model.getServiceSetup().uninstallService();
	}

	public void setPeerStartOnRecovery(boolean startPeer) {
		model.setPeerStartOnRecovery(startPeer);
		
	}

	public void peerStarted() {
		
		getModel().peerStarted();
			
		if (getModel().isStatusFutureCancelled()) {
			getModel().setStatusFuture(scheduleActionWithFixedDelay(
					PeerAsyncApplicationClient.GET_STATUS_ACTION, 0, 5, 
					TimeUnit.SECONDS, null));
		}
			
		
	}

	public void peerStopped() {
		getModel().peerStopped();
		getModel().cancelStatusFuture();
		
	}

	public void restoreDefaultPropertiesValues() {
		getModel().restoreDefaultPropertiesValues();
		
	}

	public void removeWorker(WorkerSpecification workerSpec) {
		getManager().removeWorker(getManagerClient(), workerSpec);
	}
	
//	public void addWorker(WorkerSpecification workerSpec) {
//		getManager().addWorker(getManagerClient(), workerSpec);
//	}

	public void getCompleteStatus() {
		getManager().getCompleteStatus(getManagerClient());
	}
	
}
