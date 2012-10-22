package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.management.RemoteWorkerManagement;
import org.ourgrid.common.interfaces.management.RemoteWorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.RemoteWorkForBrokerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RemoteWorkForBrokerSender implements SenderIF<RemoteWorkForBrokerResponseTO> {

	public void execute(RemoteWorkForBrokerResponseTO response, ServiceManager manager) {
		
		String brokerPublicKey = response.getBrokerPublicKey();
		
		RemoteWorkerManagement workerManagement = (RemoteWorkerManagement) manager.getStub(
				ServiceID.parse(response.getWorkerManagementAddress()), RemoteWorkerManagement.class);
		
		ServiceID rwmcServiceId = ServiceID.parse(response.getWorkerManagementClientAddress());
		RemoteWorkerManagementClient rwmc = (RemoteWorkerManagementClient) manager.getObjectDeployment(
				rwmcServiceId.getServiceName()).getObject();
		
		if (workerManagement == null) {
			manager.getLog().error("RemoteWorkerManagement stub [" + response.getWorkerManagementAddress() + "]  is NULL during " +
				"RemoteWorkForBroker message sending.");
			return;
		}
		
		if (manager.getStubDeploymentID(workerManagement) == null) {
			manager.getLog().error("RemoteWorkerManagement stub [" + response.getWorkerManagementAddress() + "]  is DOWN during " +
				"RemoteWorkForBroker message sending.");
			return;
		}
		
		workerManagement.workForBroker(rwmc, brokerPublicKey);
		
	}

}
