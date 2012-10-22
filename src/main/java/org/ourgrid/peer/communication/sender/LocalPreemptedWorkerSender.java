package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.LocalPreemptedWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class LocalPreemptedWorkerSender implements SenderIF<LocalPreemptedWorkerResponseTO>{


	public void execute(LocalPreemptedWorkerResponseTO response,
			ServiceManager manager) {
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) manager.getStub(
				ServiceID.parse(response.getLwpcAddress()), LocalWorkerProviderClient.class);
		
		lwpc.preemptedWorker(ServiceID.parse(response.getWorkerAddress()));
	}

	
}
