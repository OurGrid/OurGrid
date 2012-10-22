package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.StatusChangedResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StatusChangedSender implements SenderIF<StatusChangedResponseTO> {

	public void execute(StatusChangedResponseTO response, ServiceManager manager) {
		
		ServiceID serviceID = ServiceID.parse(response.getClientAddress());
		WorkerManagementClient client = (WorkerManagementClient) manager.getStub(serviceID, WorkerManagementClient.class);
		
		client.statusChanged(response.getStatus());
	}
}
