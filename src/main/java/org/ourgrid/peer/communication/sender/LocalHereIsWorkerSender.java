package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.LocalWorkerProviderClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.response.LocalHereIsWorkerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class LocalHereIsWorkerSender implements SenderIF<LocalHereIsWorkerResponseTO>{


	public void execute(LocalHereIsWorkerResponseTO response,
			ServiceManager manager) {
		
		LocalWorkerProviderClient lwpc = (LocalWorkerProviderClient) manager.getStub(
				ServiceID.parse(response.getLwpcAddress()), LocalWorkerProviderClient.class);
		
		DeploymentID workerID = new DeploymentID(ServiceID.parse(response.getWorkerAddress()));
		workerID.setPublicKey(response.getWorkerPublicKey());
		
		lwpc.hereIsWorker(workerID.getServiceID(), 
				response.getWorkerSpec(), response.getRequestSpec());
	}

	
}
