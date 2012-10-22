package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryServiceClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.DSClientHereIsRemoteWorkerProvidersListResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DSClientHereIsRemoteWorkerProvidersListSender implements SenderIF<DSClientHereIsRemoteWorkerProvidersListResponseTO> {

	public void execute(DSClientHereIsRemoteWorkerProvidersListResponseTO response,
			ServiceManager manager) {
		DiscoveryServiceClient client = (DiscoveryServiceClient) manager.getStub(ServiceID.parse(response.getStubAddress()), DiscoveryServiceClient.class);
		
		client.hereIsRemoteWorkerProviderList(response.getWorkerProviders());
	}

}
