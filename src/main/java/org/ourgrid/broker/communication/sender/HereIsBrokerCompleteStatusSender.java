package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.HereIsBrokerCompleteStatusResponseTO;
import org.ourgrid.common.interfaces.status.BrokerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsBrokerCompleteStatusSender implements SenderIF<HereIsBrokerCompleteStatusResponseTO> {

	public void execute(HereIsBrokerCompleteStatusResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		ServiceID myID = ServiceID.parse(response.getMyAddress());
		BrokerStatusProviderClient client = (BrokerStatusProviderClient) manager.getStub(clientID, BrokerStatusProviderClient.class);
		
		client.hereIsCompleteStatus(myID, response.getCompleteStatus());
	}
}
