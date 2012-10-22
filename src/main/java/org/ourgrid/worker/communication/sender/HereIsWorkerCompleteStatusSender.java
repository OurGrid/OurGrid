package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.HereIsWorkerCompleteStatusResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsWorkerCompleteStatusSender implements SenderIF<HereIsWorkerCompleteStatusResponseTO> {

	public void execute(HereIsWorkerCompleteStatusResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		WorkerControlClient client = (WorkerControlClient) manager.getStub(clientID, WorkerControlClient.class);
		
		client.hereIsCompleteStatus(response.getCompleteStatus());
	}
}
