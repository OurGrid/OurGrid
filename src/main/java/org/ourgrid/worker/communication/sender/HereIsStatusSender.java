package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.HereIsStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsStatusSender implements SenderIF<HereIsStatusResponseTO> {

	public void execute(HereIsStatusResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		WorkerControlClient client = (WorkerControlClient) manager.getStub(clientID, WorkerControlClient.class);
		
		client.hereIsStatus(response.getStatus());
	}
}
