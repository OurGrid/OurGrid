package org.ourgrid.discoveryservice.communication.sender;

import org.ourgrid.common.interfaces.CommunityStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.discoveryservice.response.HereIsPeerStatusChangeHistoryResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsPeerStatusChangeHistorySender implements SenderIF<HereIsPeerStatusChangeHistoryResponseTO> {

	public void execute(HereIsPeerStatusChangeHistoryResponseTO response,
			ServiceManager manager) {
		CommunityStatusProviderClient client = (CommunityStatusProviderClient) 
				manager.getStub(ServiceID.parse(response.getClientAddress()), 
						CommunityStatusProviderClient.class);
		
		client.hereIsPeerStatusChangeHistory(response.getPeerStatusChangesHistory(), 
				System.currentTimeMillis());
	}

}
