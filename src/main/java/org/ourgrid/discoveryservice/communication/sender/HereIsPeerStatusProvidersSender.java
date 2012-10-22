package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.HereIsPeerStatusProvidersResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsPeerStatusProvidersSender implements SenderIF<HereIsPeerStatusProvidersResponseTO> {

	public void execute(HereIsPeerStatusProvidersResponseTO response,
			ServiceManager manager) {
		CommunityStatusProviderClient client = (CommunityStatusProviderClient) manager.getStub(ServiceID.parse(response.getClientAddress()), CommunityStatusProviderClient.class);
		
		client.hereIsStatusProviderList(response.getStatusProviders());
	}

}
