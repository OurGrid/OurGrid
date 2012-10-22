package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.GetRemoteWorkerProvidersResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class GetRemoteWorkerProvidersSender implements SenderIF<GetRemoteWorkerProvidersResponseTO>{

	public void execute(GetRemoteWorkerProvidersResponseTO response,
			ServiceManager manager) {
		
		DiscoveryServiceClient dsClient = (DiscoveryServiceClient) manager.getObjectDeployment(
				PeerConstants.DS_CLIENT).getObject();
		
		DiscoveryService discoveryService = manager.getStub(ServiceID.parse(response.getDsAddress()), DiscoveryService.class);
		
		discoveryService.getRemoteWorkerProviders(dsClient, response.getDsRequestSize());
	}

	
}
