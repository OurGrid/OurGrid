package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsRemoteConsumersStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsRemoteConsumersStatusSender implements SenderIF<HereIsRemoteConsumersStatusResponseTO>{

	public void execute(HereIsRemoteConsumersStatusResponseTO response,
			ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
	
		client.hereIsRemoteConsumersStatus(ServiceID.parse(response.getPeerAddress()), response.getRemoteConsumersInfo());
	}

	
}
