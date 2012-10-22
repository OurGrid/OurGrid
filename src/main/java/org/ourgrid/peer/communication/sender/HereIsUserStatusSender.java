package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsUserStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsUserStatusSender implements SenderIF<HereIsUserStatusResponseTO>{

	public void execute(HereIsUserStatusResponseTO response,
			ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
	
		client.hereIsUsersStatus(ServiceID.parse(response.getPeerAddress()), response.getUsersInfo());
	}

	
}
