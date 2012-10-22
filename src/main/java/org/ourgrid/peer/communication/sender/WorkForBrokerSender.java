package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.WorkForBrokerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkForBrokerSender implements SenderIF<WorkForBrokerResponseTO> {

	public void execute(WorkForBrokerResponseTO response, ServiceManager manager) {
			ServiceID brokerServiceID = ServiceID.parse(response.getBrokerAddress());
			
			LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) manager.getStub(brokerServiceID, 
					LocalWorkerProviderClient.class);
			
			DeploymentID brokerDeploymentID = manager.getStubDeploymentID(lwpc);

			WorkerManagement workerManagement = (WorkerManagement) manager.getStub(
					ServiceID.parse(response.getWorkerManagementAddress()), WorkerManagement.class);
			
			workerManagement.workForBroker(brokerDeploymentID);
	}

}
