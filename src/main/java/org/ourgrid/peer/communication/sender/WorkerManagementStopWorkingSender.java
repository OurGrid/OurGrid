package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.WorkerManagementStopWorkingResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkerManagementStopWorkingSender implements SenderIF<WorkerManagementStopWorkingResponseTO> {

	public void execute(WorkerManagementStopWorkingResponseTO response,
			ServiceManager manager) {
			
		WorkerManagement workerManagement = (WorkerManagement) manager.getStub(ServiceID.parse(response.getWorkerManagementAddress()), WorkerManagement.class);
		
		workerManagement.stopWorking();
	}

}
