package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.status.PeerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.HereIsLocalWorkersStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsLocalWorkersStatusSender implements SenderIF<HereIsLocalWorkersStatusResponseTO>{


	public void execute(HereIsLocalWorkersStatusResponseTO response,
			ServiceManager manager) {
		
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		PeerStatusProviderClient client = (PeerStatusProviderClient) manager.getStub(clientID, PeerStatusProviderClient.class);
		client.hereIsLocalWorkersStatus(manager.getMyDeploymentID().getServiceID(), 
				response.getLocalWorkersInfos());
	}

	
}
