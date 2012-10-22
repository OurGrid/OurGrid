package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsNetworkOfFavorsStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsNetworkOfFavorsStatusSender implements SenderIF<HereIsNetworkOfFavorsStatusResponseTO>{


	public void execute(HereIsNetworkOfFavorsStatusResponseTO response,
			ServiceManager manager) {
		
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
		ServiceID statusProviderID = ServiceID.parse(response.getPeerAddress());
		
		client.hereIsNetworkOfFavorsStatus(statusProviderID, response.getNofStatus());
	}

	
}
