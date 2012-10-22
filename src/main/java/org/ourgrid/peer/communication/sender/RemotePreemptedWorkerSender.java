package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.RemoteWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.RemotePreemptedWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class RemotePreemptedWorkerSender implements SenderIF<RemotePreemptedWorkerResponseTO>{


	public void execute(RemotePreemptedWorkerResponseTO response,
			ServiceManager manager) {
		
		RemoteWorkerProviderClient rwpc = (RemoteWorkerProviderClient) manager.getStub(ServiceID.parse(response.getRwpcAddress()), RemoteWorkerProviderClient.class);
		
		rwpc.preemptedWorker(response.getRwmPublicKey());
	}

	
}
