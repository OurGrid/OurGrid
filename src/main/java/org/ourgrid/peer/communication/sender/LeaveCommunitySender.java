package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.LeaveCommunityResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class LeaveCommunitySender implements SenderIF<LeaveCommunityResponseTO>{

	public void execute(LeaveCommunityResponseTO response,
			ServiceManager manager) {
		DiscoveryService discoveryService = (DiscoveryService) manager.getStub(ServiceID.parse(response.getDsAddress()), 
				DiscoveryService.class);
		
		DiscoveryServiceClient dsClient = (DiscoveryServiceClient) manager.getObjectDeployment(PeerConstants.DS_CLIENT).getObject();
		discoveryService.leaveCommunity(dsClient);
	}

	
}
