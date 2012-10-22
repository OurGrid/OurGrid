package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsCompleteHistoryStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsCompleteHistoryStatusSender implements SenderIF<HereIsCompleteHistoryStatusResponseTO>{

	public void execute(HereIsCompleteHistoryStatusResponseTO response,
			ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
	
		client.hereIsCompleteHistoryStatus(ServiceID.parse(response.getPeerAddress()), response.getPeerCompleteHistoryStatus(), response.getUntilTime());
	}

	
}
