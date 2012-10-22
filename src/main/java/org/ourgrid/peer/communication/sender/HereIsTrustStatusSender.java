package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsTrustStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsTrustStatusSender implements SenderIF<HereIsTrustStatusResponseTO>{


	public void execute(HereIsTrustStatusResponseTO response,
			ServiceManager manager) {
		
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
		ServiceID statusProviderID = ServiceID.parse(response.getStatusProviderServiceID());
		
		client.hereIsTrustStatus(statusProviderID, response.getTrustInfo());
		
	}

	
}
