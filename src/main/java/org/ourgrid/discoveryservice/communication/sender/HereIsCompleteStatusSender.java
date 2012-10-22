package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.status.DiscoveryServiceStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.HereIsCompleteStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsCompleteStatusSender implements SenderIF<HereIsCompleteStatusResponseTO> {

	public void execute(HereIsCompleteStatusResponseTO response,
			ServiceManager manager) {
		DiscoveryServiceStatusProviderClient client = manager.getStub(ServiceID.parse(response.getClientAddress()), DiscoveryServiceStatusProviderClient.class);
		
		client.hereIsCompleteStatus(response.getDiscoveryServiceCompleteStatus());
	}

}
