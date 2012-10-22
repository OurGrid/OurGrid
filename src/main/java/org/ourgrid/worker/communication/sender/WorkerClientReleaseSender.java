package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.WorkerClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.internal.response.ReleaseResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerClientReleaseSender implements SenderIF<ReleaseResponseTO> {

	public void execute(ReleaseResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getStubAddress());
		WorkerClient client = (WorkerClient) manager.getStub(clientID, WorkerClient.class);
		
		manager.release(client);
	}
}
