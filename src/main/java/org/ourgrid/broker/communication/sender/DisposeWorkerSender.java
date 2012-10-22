package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.DisposeWorkerResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class DisposeWorkerSender implements SenderIF<DisposeWorkerResponseTO> {

	public void execute(DisposeWorkerResponseTO response,
			ServiceManager manager) {
		ServiceID peerServiceID = ServiceID.parse(response.getPeerAddress());
		ServiceID workerServiceID = ServiceID.parse(response.getWorkerAddress());
		
		workerServiceID.getContainerID().setPublicKey(response.getWorkerPublicKey());
		
		LocalWorkerProvider localWorkerProvider = (LocalWorkerProvider) manager.getStub(peerServiceID, LocalWorkerProvider.class);

		localWorkerProvider.disposeWorker(workerServiceID);
	}

}
