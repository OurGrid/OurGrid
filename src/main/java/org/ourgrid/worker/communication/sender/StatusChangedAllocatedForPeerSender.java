package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.StatusChangedAllocatedForPeerResponseTO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class StatusChangedAllocatedForPeerSender implements SenderIF<StatusChangedAllocatedForPeerResponseTO> {

	public void execute(StatusChangedAllocatedForPeerResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		ServiceID wmClientID = new ServiceID(clientID.getContainerID(), 
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		
		WorkerManagementClient client = (WorkerManagementClient) manager.getStub(wmClientID, WorkerManagementClient.class);
		
		ObjectDeployment objectDeployment = manager.getObjectDeployment(WorkerConstants.REMOTE_WORKER_MANAGEMENT);
		
		client.statusChangedAllocatedForPeer(objectDeployment.getDeploymentID().getServiceID(), response.getRemotePeerPubKey());
	}
}
