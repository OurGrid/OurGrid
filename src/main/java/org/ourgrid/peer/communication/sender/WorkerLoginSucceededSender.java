package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.WorkerLoginSucceededResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerLoginSucceededSender implements SenderIF<WorkerLoginSucceededResponseTO>{

	public void execute(WorkerLoginSucceededResponseTO response,
			ServiceManager manager) {
		
		WorkerManagement workerManagement = (WorkerManagement) manager.getStub(ServiceID.parse(response.getWorkerManagementAddress()),
				WorkerManagement.class);
		
		WorkerManagementClient workerManagementClient = (WorkerManagementClient) manager.getObjectDeployment(
				PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME).getObject();
		
		workerManagement.loginSucceeded(workerManagementClient, response.getLoginResult());
	}

	
}
