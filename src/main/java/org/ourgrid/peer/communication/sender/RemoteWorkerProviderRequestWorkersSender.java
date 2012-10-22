package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.RemoteWorkerProviderRequestWorkersResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RemoteWorkerProviderRequestWorkersSender implements SenderIF<RemoteWorkerProviderRequestWorkersResponseTO>{

	public void execute(RemoteWorkerProviderRequestWorkersResponseTO response,
			ServiceManager manager) {
		
		RemoteWorkerProviderClient rwpc = (RemoteWorkerProviderClient) manager.getObjectDeployment(
				PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT).getObject();
		
		RemoteWorkerProvider rwp = manager.getStub(ServiceID.parse(response.getRemoteWorkerProviderAddress()), 
				RemoteWorkerProvider.class);
		
		if (rwp == null) {
			manager.getLog().error("RemoteWorkerProvider stub [" + response.getRemoteWorkerProviderAddress() + "] is NULL during " +
				"RemoteWorkerProviderRequestWorkers message sending.");
			return;
		}
		
		rwp.requestWorkers(rwpc, response.getRequestSpec());
	}

}
