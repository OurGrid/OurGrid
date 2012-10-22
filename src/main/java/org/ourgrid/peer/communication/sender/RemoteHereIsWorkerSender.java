package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.RemoteHereIsWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RemoteHereIsWorkerSender implements SenderIF<RemoteHereIsWorkerResponseTO>{


	public void execute(RemoteHereIsWorkerResponseTO response,
			ServiceManager manager) {
		
		RemoteWorkerProviderClient rwpc = (RemoteWorkerProviderClient) manager.getStub(ServiceID.parse(response.getRwpcAddress()), RemoteWorkerProviderClient.class);
		
		RemoteWorkerProvider rwp = (RemoteWorkerProvider) manager.getObjectDeployment(
				PeerConstants.REMOTE_WORKER_PROVIDER).getObject();
		
		rwpc.hereIsWorker(rwp, ServiceID.parse(response.getRwmAddress()), response.getWorkerSpec());
		
	}

	
}
