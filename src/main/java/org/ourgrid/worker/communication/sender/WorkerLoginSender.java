package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.WorkerConstants;
import org.ourgrid.worker.response.WorkerLoginResponseTO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerLoginSender implements SenderIF<WorkerLoginResponseTO> {

	@Override
	public void execute(WorkerLoginResponseTO response, ServiceManager manager) {
		
		ServiceID peerServiceID = ServiceID.parse(
				response.getWorkerManagementClientAddress());
		
		WorkerManagementClient client = (WorkerManagementClient) manager.
				getStub(peerServiceID, WorkerManagementClient.class);
		
		ObjectDeployment objectDeployment = manager.getObjectDeployment(
				WorkerConstants.LOCAL_WORKER_MANAGEMENT);
		WorkerManagement wm = (WorkerManagement) objectDeployment.getObject();
		
		client.workerLogin(wm, response.getWorkerSpecification());
	}
}
