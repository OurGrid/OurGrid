package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.RemotePeerStatusChangedAllocatedForBrokerResponseTO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class RemotePeerStatusChangedAllocatedForBrokerSender implements SenderIF<RemotePeerStatusChangedAllocatedForBrokerResponseTO> {

	public void execute(RemotePeerStatusChangedAllocatedForBrokerResponseTO response, ServiceManager manager) {
		DeploymentID deploymentID = new DeploymentID(response.getRemotePeerAddress());
		RemoteWorkerManagementClient client = (RemoteWorkerManagementClient) manager.getStub(deploymentID.getServiceID(), RemoteWorkerManagementClient.class);

		ObjectDeployment objectDeployment = manager.getObjectDeployment(WorkerConstants.WORKER);
		
		client.statusChangedAllocatedForBroker(objectDeployment.getDeploymentID().getServiceID());
	}
}
