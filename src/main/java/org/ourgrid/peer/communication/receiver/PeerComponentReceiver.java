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
package org.ourgrid.peer.communication.receiver;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.control.PeerControlClient;
import org.ourgrid.common.interfaces.management.PeerManager;
import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.OurGridControlReceiver;
import org.ourgrid.common.internal.OurGridRequestControl;
import org.ourgrid.common.internal.RequestControlIF;
import org.ourgrid.common.internal.request.QueryRequestTO;
import org.ourgrid.common.specification.worker.WorkerSpecification;
import org.ourgrid.peer.PeerConfiguration;
import org.ourgrid.peer.business.controller.messages.PeerControlMessages;
import org.ourgrid.peer.business.requester.PeerRequestControl;
import org.ourgrid.peer.request.AddAnnotationsWorkersRequestTO;
import org.ourgrid.peer.request.AddUserRequestTO;
import org.ourgrid.peer.request.AddWorkerRequestTO;
import org.ourgrid.peer.request.GetCompleteHistoryStatusRequestTO;
import org.ourgrid.peer.request.GetCompleteStatusRequestTO;
import org.ourgrid.peer.request.GetLocalConsumersStatusRequestTO;
import org.ourgrid.peer.request.GetLocalWorkersStatusRequestTO;
import org.ourgrid.peer.request.GetNetworkOfFavorsStatusRequestTO;
import org.ourgrid.peer.request.GetRemoteConsumersStatusRequestTO;
import org.ourgrid.peer.request.GetRemoteWorkersStatusRequestTO;
import org.ourgrid.peer.request.GetTrustStatusRequestTO;
import org.ourgrid.peer.request.GetUserStatusRequestTO;
import org.ourgrid.peer.request.RemoveUserRequestTO;
import org.ourgrid.peer.request.RemoveWorkerRequestTO;
import org.ourgrid.peer.request.SetWorkersRequestTO;
import org.ourgrid.peer.request.StartPeerRequestTO;
import org.ourgrid.peer.request.StopPeerRequestTO;
import org.ourgrid.peer.request.UpdatePeerUpTimeRequestTO;
import org.ourgrid.reqtrace.Req;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.ModuleProperties;
import br.edu.ufcg.lsd.commune.api.FailureNotification;
import br.edu.ufcg.lsd.commune.api.MonitoredBy;
import br.edu.ufcg.lsd.commune.api.RecoveryNotification;
import br.edu.ufcg.lsd.commune.container.control.ModuleControlClient;
import br.edu.ufcg.lsd.commune.container.servicemanager.dao.ContainerDAO;
import br.edu.ufcg.lsd.commune.context.ContainerContextUtils;
import br.edu.ufcg.lsd.commune.context.ModuleContext;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

/**
 * Perform the Peer component control actions.
 */
@Req("REQ010")
public class PeerComponentReceiver extends OurGridControlReceiver implements PeerManager {
	
	@Override
	public String getComponentName() {
		return "Peer";
	}

	@Override
	protected void startComponent() throws Exception {
		StartPeerRequestTO to = new StartPeerRequestTO();
		to.setShouldJoinCommunity(shouldJoinCommunity());
		
		ModuleContext containerContext = getServiceManager().getContainerContext();
		to.setNetworkStr(containerContext.getProperty(PeerConfiguration.PROP_DS_NETWORK));
		
		List<ServiceID> dsServiceIDs = PeerConfiguration.parseNetwork(getServiceManager());
		List<String> dsAddress = new ArrayList<String>();
		
		for (ServiceID id : dsServiceIDs) {
			dsAddress.add(id.toString());
		}
		
		to.setProperties(getServiceManager().getContainerContext().getProperties());
		to.setDsAddress(dsAddress);
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		to.setFilePath(containerContext.getProperty(PeerConfiguration.PROP_RANKINGFILE));
		to.setDescription(containerContext.getProperty(PeerConfiguration.PROP_DESCRIPTION));
		to.setEmail(containerContext.getProperty(PeerConfiguration.PROP_EMAIL));
		to.setLabel(containerContext.getProperty(PeerConfiguration.PROP_LABEL));
		to.setLatitude(containerContext.getProperty(PeerConfiguration.PROP_LATITUDE));
		to.setLongitude(containerContext.getProperty(PeerConfiguration.PROP_LONGITUDE));
		to.setMyCertSubjectDN(CertificationUtils.getCertSubjectDN(getServiceManager().getMyCertPath()));
		to.setReceivingCACertificatePath(ContainerContextUtils.normalizeFilePath(getServiceManager().getContainerContext(), 
				getServiceManager().getContainerContext().getProperty(PeerConfiguration.PROP_RECEIVING_CACERTIFICATE_PATH)));
		to.setRequestingCACertificatePath(ContainerContextUtils.normalizeFilePath(getServiceManager().getContainerContext(), 
				getServiceManager().getContainerContext().getProperty(PeerConfiguration.PROP_REQUESTING_CACERTIFICATE_PATH)));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	/**
	 * @param propJoinCommunity
	 * @return
	 */
	private boolean shouldJoinCommunity() {
		
		return getServiceManager().getContainerContext().isEnabled(PeerConfiguration.PROP_JOIN_COMMUNITY);
		
	}

	@Override
	public void stop(boolean callExit, boolean force, 
			@MonitoredBy(Module.CONTROL_OBJECT_NAME) ModuleControlClient client) {
		StopPeerRequestTO to = new StopPeerRequestTO();
		
		ContainerDAO dao = getServiceManager().getContainerDAO();
		
		to.setDAOStarted(dao.isStarted());
		to.setShouldJoinCommunity(shouldJoinCommunity());
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
		
		super.stop(callExit, force, client);
	}

	@Override
	protected boolean validateStartSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(PeerControlMessages.getUnknownSenderStartingPeerMessage(senderPublicKey));
			return false;
		}
		return true;
	}

	@Override
	protected boolean validateStopSenderPublicKey(ModuleControlClient client, String senderPublicKey) {
		
		if(!getServiceManager().isThisMyPublicKey(senderPublicKey)) {
			getServiceManager().getLog().warn(PeerControlMessages.getUnknownSenderStoppingPeerMessage(senderPublicKey));
			return false;
		}
		return true;
	}


	/**
	 * Set the local Workers specification for this Peer.
	 * 
	 * <ul>
	 *   <li> Check if the Peer can have his local worker set;
	 *   <li> Shutdown Peer modules;
	 *   <li> Discard old workers;
	 *   <li> Register new workers;
	 *   <li> Send operation result messsage to client.
	 * </ul>
	 * 
	 * @param controlClient Client that will receive the operation result message
	 * @param newWorkers Collection of local workers specification
	 */
	@Req({"REQ010", "REQ043"})
	public void setWorkers(
			@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerControlClient controlClient, 
			List<WorkerSpecification> newWorkers) {
		
		SetWorkersRequestTO to = new SetWorkersRequestTO();
		to.setCanComponentBeUsed(canComponentBeUsed(controlClient));
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		to.setNewWorkers(newWorkers);
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPubKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	/**
	 * Add a new user for this peer.
	 * 
	 * @param controlClient Client that will receive the operation result message
	 * @param login User login (user@server)
	 */
	public void addUser(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerControlClient peerControlClient, String login) {
		
		AddUserRequestTO to = new AddUserRequestTO();
		
		ModuleContext containerContext = getServiceManager().getContainerContext();

		to.setLogin(login);
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		to.setEmail(containerContext.getProperty(PeerConfiguration.PROP_EMAIL));
		to.setLabel(containerContext.getProperty(PeerConfiguration.PROP_LABEL));
		to.setLatitude(containerContext.getProperty(PeerConfiguration.PROP_LATITUDE));
		to.setLongitude(containerContext.getProperty(PeerConfiguration.PROP_LONGITUDE));
		to.setDescription(containerContext.getProperty(PeerConfiguration.PROP_DESCRIPTION));
		to.setMyCertSubjectDN(CertificationUtils.getCertSubjectDN(getServiceManager().getMyCertPath()));
		
		to.setClientAddress(getServiceManager().getStubDeploymentID(peerControlClient).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	/**
	 * Remove a user for this peer.
	 * 
	 * @param controlClient Client that will receive the operation result message
	 * @param login User login
	 */
	public void removeUser(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerControlClient peerControlClient, String login) {
		
		RemoveUserRequestTO to = new RemoveUserRequestTO();
		
		to.setLogin(login);
		to.setClientAddress(getServiceManager().getStubDeploymentID(
				peerControlClient).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());

	}

	/* Status */
	
	/**
	 * Retrieves to the callback client info on the this Peer's Local Workers
	 * @param client The client that requested info
	 * @param peerStatusProviderServiceID The entityID of this Peer's status provider
	 */
	@Req("REQ036")
	public void getLocalWorkersStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		
			GetLocalWorkersStatusRequestTO to = new GetLocalWorkersStatusRequestTO();
			
			
			String clientAddress = getServiceManager().getStubDeploymentID(
					client).getServiceID().toString();
			to.setClientAddress(clientAddress);
			String statusProviderServiceID = getServiceManager().getMyDeploymentID().getServiceID().toString();
			to.setStatusProviderServiceID(statusProviderServiceID);
			String peerUserAtServer = getServiceManager().getMyDeploymentID().getServiceID().getContainerID().getUserAtServer();
			to.setPeerUserAtServer(peerUserAtServer);
			to.setCanStatusBeUsed(canStatusBeUsed());
			
			OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	/**
	 * Retrieves to the callback client info on this Peer's Users
	 * @param client The client that requested info
	 * @param entityID The entityID of this Peer's status provider
	 */
	@Req("REQ106")
	public void getUsersStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetUserStatusRequestTO to = new GetUserStatusRequestTO();
		String clientAddress = getServiceManager().getStubDeploymentID(
				client).getServiceID().toString();
		to.setClientAddress(clientAddress);
		String peerAddress = getServiceManager().getMyDeploymentID().getServiceID().toString();
		to.setPeerAdress(peerAddress);
		to.setCanStatusBeUsed(canStatusBeUsed());

		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	/**
	 * Retrieves to the callback client info on the this Peer's Remote Workers
	 * @param client The client that requested info
	 * @param pspServiceID The entityID of this Peer's status provider
	 */
	@Req("REQ037")
	public void getRemoteWorkersStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetRemoteWorkersStatusRequestTO to = new GetRemoteWorkersStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(
					client).getServiceID().toString());
		to.setPeerAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	
	/**
	 * Retrieves to the callback client complete info on this Peer
	 * @param client The client that requested info
	 * @param entityID The entityID of this Peer's status provider
	 */
	@Req("REQ038a")
	public void getCompleteStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetCompleteStatusRequestTO to = new GetCompleteStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setPeerAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		to.setUpTime(getServiceManager().getContainerDAO().getUpTime());
		to.setMyCertSubjectDN(CertificationUtils.getCertSubjectDN(getServiceManager().getMyCertPath()));
		
		ModuleContext containerContext = getServiceManager().getContainerContext();
		
		to.setLabel(containerContext.getProperty( PeerConfiguration.PROP_LABEL));
		to.setPropConfDir(containerContext.getProperty(ModuleProperties.PROP_CONFDIR));
		to.setContextString(containerContext.toString());
		to.setPropLabel(containerContext.getProperty( PeerConfiguration.PROP_LABEL));
		to.setPropJoinCommunity(containerContext.getProperty( PeerConfiguration.PROP_JOIN_COMMUNITY));
		to.setJoinCommunityEnabled(containerContext.isEnabled(PeerConfiguration.PROP_JOIN_COMMUNITY));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	

	public void getCompleteHistoryStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client, long time) {
		GetCompleteHistoryStatusRequestTO to = new GetCompleteHistoryStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setPeerAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		to.setTime(time);
		to.setUpTime(getServiceManager().getContainerDAO().getUpTime());
		
		ModuleContext containerContext = getServiceManager().getContainerContext();
		
		to.setLabel(containerContext.getProperty( PeerConfiguration.PROP_LABEL));
		to.setPropConfDir(containerContext.getProperty(ModuleProperties.PROP_CONFDIR));
		to.setContextString(containerContext.toString());
		to.setPropLabel(containerContext.getProperty( PeerConfiguration.PROP_LABEL));
		to.setPropJoinCommunity(containerContext.getProperty( PeerConfiguration.PROP_JOIN_COMMUNITY));
		to.setJoinCommunityEnabled(containerContext.isEnabled(PeerConfiguration.PROP_JOIN_COMMUNITY));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	/**
	 * @param client
	 * @param entityID
	 */
	@Req("REQ035")
	public void getNetworkOfFavorsStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetNetworkOfFavorsStatusRequestTO to = new GetNetworkOfFavorsStatusRequestTO();
		
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(client).getServiceID().toString());
		to.setPeerAdress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		to.setPeerDNData(CertificationUtils.getCertSubjectDN(getServiceManager().getMyCertPath()));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	@Req("REQ110")
	public void getTrustStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		if (canStatusBeUsed()) {
			GetTrustStatusRequestTO to = new GetTrustStatusRequestTO();
			String clientAddress = getServiceManager().getStubDeploymentID(
					client).getServiceID().toString();
			to.setClientAddress(clientAddress);
			String statusProviderServiceID = getServiceManager().getMyDeploymentID().getServiceID().toString();
			to.setStatusProviderServiceID(statusProviderServiceID);
			
			OurGridRequestControl.getInstance().execute(to, getServiceManager());
		}
	}


	/**
	 * Retrieves to the callback client the complete status of remote consumers.
	 * That is, information about workers, consumers, users, etc.
	 * @param client The client that requested info
	 * @param entityID The entityID of this Peer's status provider
	 */
	@Req("REQ034")
	public void getRemoteConsumersStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetRemoteConsumersStatusRequestTO to = new GetRemoteConsumersStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(
					client).getServiceID().toString());
		to.setPeerAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
	
	public void getLocalConsumersStatus(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		GetLocalConsumersStatusRequestTO to = new GetLocalConsumersStatusRequestTO();
		to.setCanStatusBeUsed(canStatusBeUsed());
		to.setClientAddress(getServiceManager().getStubDeploymentID(
					client).getServiceID().toString());
		to.setPeerAddress(getServiceManager().getMyDeploymentID().getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void registerAsListener(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerStatusProviderClient client) {
		// TODO Auto-generated method stub
		
	}
	
	@RecoveryNotification
	public void controlClientIsUp(PeerControlClient client) {
		
	}
	
	@FailureNotification
	public void controlClientIsDown(PeerControlClient client) {
		
	}
	
	@RecoveryNotification
	public void statusProviderClientIsUp(PeerStatusProviderClient statusProviderClient) {
		
	}
	
	@FailureNotification
	public void statusProviderClientIsDown(PeerStatusProviderClient statusProviderClient) {
		
	}

	public void addWorker(@MonitoredBy(Module.CONTROL_OBJECT_NAME)PeerControlClient peerControlClient,
			WorkerSpecification workerSpec) {
		
		AddWorkerRequestTO to = new AddWorkerRequestTO();
		
		String senderPubKey = getServiceManager().getSenderPublicKey();
		to.setSenderPubKey(senderPubKey);
		to.setCanComponentBeUsed(canComponentBeUsed(peerControlClient));
		to.setIsThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPubKey));
		to.setWorkerSpec(workerSpec);
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());

	}

	public void removeWorker(@MonitoredBy(Module.CONTROL_OBJECT_NAME)PeerControlClient peerControlClient,
			WorkerSpecification workerSpec) {
		
		RemoveWorkerRequestTO to = new RemoveWorkerRequestTO();
		to.setCanComponentBeUsed(canComponentBeUsed(peerControlClient));
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPubKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		to.setWorkerUserAtServer(workerSpec.getServiceID().getContainerID().getUserAtServer());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

		/**
	 * Set the local Workers specification for this Peer.
	 * <ul>
	 * <li>Check if the Peer can have his local worker set;
	 * <li>Shutdown Peer modules;
	 * <li>Discard old workers;
	 * <li>Register new workers;
	 * <li>Send operation result message to client.
	 * </ul>
	 * 
	 * @param controlClient Client that will receive the operation result
	 *        message
	 * @param newWorkers Collection of local workers specification
	 */
	// TODO @Req({"REQXX", "REQYY"})
	public void addAnnotationsWorkers( @MonitoredBy( Module.CONTROL_OBJECT_NAME ) PeerControlClient controlClient,
										List< WorkerSpecification > newWorkersAnnotations ) {

		AddAnnotationsWorkersRequestTO to = new AddAnnotationsWorkersRequestTO();

		String senderPubKey = getServiceManager().getSenderPublicKey();
		to.setSenderPubKey(senderPubKey);
		to.setCanComponentBeUsed(canComponentBeUsed(controlClient));
		to.setNewWorkersAnnotations(newWorkersAnnotations);
		to.setClientAddress(getServiceManager().getSenderServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	
	}

	@Override
	protected RequestControlIF createRequestControl() {
		return new PeerRequestControl();
	}
	
	public void updatePeerUpTime() {
		UpdatePeerUpTimeRequestTO to = new UpdatePeerUpTimeRequestTO();
		to.setMyUserAtServer(getServiceManager().getMyDeploymentID().getContainerID().getUserAtServer());
		
		String senderPublicKey = getServiceManager().getSenderPublicKey();
		
		to.setSenderPublicKey(senderPublicKey);
		to.setThisMyPublicKey(getServiceManager().isThisMyPublicKey(senderPublicKey));
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}

	public void query(@MonitoredBy(Module.CONTROL_OBJECT_NAME) PeerControlClient peerControlClient, String query) {
		
		QueryRequestTO to = new QueryRequestTO();
		
		to.setQuery(query);
		to.setClientAddress(getServiceManager().getStubDeploymentID(
				peerControlClient).getServiceID().toString());
		
		OurGridRequestControl.getInstance().execute(to, getServiceManager());
	}
}