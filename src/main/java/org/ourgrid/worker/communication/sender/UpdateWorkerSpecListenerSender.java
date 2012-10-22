package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.UpdateWorkerSpecListenerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class UpdateWorkerSpecListenerSender implements SenderIF<UpdateWorkerSpecListenerResponseTO> {

	public void execute(UpdateWorkerSpecListenerResponseTO response, ServiceManager manager) {
		
		if(response.getMasterPeerAddress() != null){
			ServiceID serviceID = ServiceID.parse(response.getMasterPeerAddress());
			WorkerManagementClient client = (WorkerManagementClient) manager.getStub(serviceID, WorkerManagementClient.class);
			client.updateWorkerSpec(response.getWorkerSpec());		
		}
	}
}
