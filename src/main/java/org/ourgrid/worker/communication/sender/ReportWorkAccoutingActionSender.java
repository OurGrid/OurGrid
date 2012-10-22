package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.ReportWorkAccountingActionResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ReportWorkAccoutingActionSender implements SenderIF<ReportWorkAccountingActionResponseTO> {

	public void execute(ReportWorkAccountingActionResponseTO response, ServiceManager manager) {
        ServiceID serviceID = ServiceID.parse(response.getMasterPeerAddress());
        WorkerManagementClient workerManagementClient = (WorkerManagementClient) manager.getStub(serviceID, WorkerManagementClient.class);
        
        workerManagementClient.reportWorkAccounting(response.getWorkAccountings());
	}
}