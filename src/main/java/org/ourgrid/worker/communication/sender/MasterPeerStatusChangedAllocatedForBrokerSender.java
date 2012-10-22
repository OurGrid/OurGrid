package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.MasterPeerStatusChangedAllocatedForBrokerResponseTO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class MasterPeerStatusChangedAllocatedForBrokerSender implements SenderIF<MasterPeerStatusChangedAllocatedForBrokerResponseTO> {

	public void execute(MasterPeerStatusChangedAllocatedForBrokerResponseTO response, ServiceManager manager) {
		ServiceID serviceID = ServiceID.parse(response.getMasterPeerAddress());
		WorkerManagementClient client = (WorkerManagementClient) manager.getStub(serviceID, WorkerManagementClient.class);
		
		ObjectDeployment objectDeployment = manager.getObjectDeployment(WorkerConstants.WORKER);
		
		client.statusChangedAllocatedForBroker(objectDeployment.getDeploymentID().getServiceID(), response.getBrokerPublicKey());
	}
}
