package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.DSHereIsRemoteWorkerProvidersListResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DSHereIsRemoteWorkerProvidersListSender implements SenderIF<DSHereIsRemoteWorkerProvidersListResponseTO> {

	public void execute(DSHereIsRemoteWorkerProvidersListResponseTO response,
			ServiceManager manager) {
		DiscoveryService ds = (DiscoveryService) manager.getStub(ServiceID.parse(response.getStubAddress()), DiscoveryService.class);
		
		ds.hereIsRemoteWorkerProviderList(response.getWorkerProviders());
	}

}
