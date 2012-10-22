package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.HereIsCompleteJobsStatusResponseTO;
import org.ourgrid.common.interfaces.status.BrokerStatusProviderClient;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsCompleteJobsStatusSender implements SenderIF<HereIsCompleteJobsStatusResponseTO> {

	public void execute(HereIsCompleteJobsStatusResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		ServiceID myID = ServiceID.parse(response.getMyAddress());
		BrokerStatusProviderClient client = (BrokerStatusProviderClient) manager.getStub(clientID, BrokerStatusProviderClient.class);
		
		client.hereIsCompleteJobsStatus(myID, response.getJobPackage());
	}
}
