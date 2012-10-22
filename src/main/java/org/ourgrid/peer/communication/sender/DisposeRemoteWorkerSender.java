package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.DisposeRemoteWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DisposeRemoteWorkerSender implements
		SenderIF<DisposeRemoteWorkerResponseTO> {

	public void execute(DisposeRemoteWorkerResponseTO response,
			ServiceManager manager) {

		RemoteWorkerProvider provider = (RemoteWorkerProvider) manager.getStub(
				ServiceID.parse(response.getProviderAddress()),
				RemoteWorkerProvider.class);

		ServiceID workerServiceID = ServiceID.parse(response.getWorkerAddress());
		String workerPublicKey = response.getWorkerPublicKey();
		
		workerServiceID.getContainerID().setPublicKey(workerPublicKey);
		
		if (provider == null) {
			manager.getLog().error("RemoteWorkerProvider stub [" + response.getProviderAddress() + "]  is NULL during " +
				"DisposeRemoteWorker message sending.");
			return;
		}
		
		if (manager.getStubDeploymentID(provider) == null) {
			manager.getLog().error("RemoteWorkerProvider stub [" + response.getProviderAddress() + "]  is DOWN during " +
				"DisposeRemoteWorker message sending.");
			return;
		}
		
		provider.disposeWorker(workerServiceID);
	}

}
