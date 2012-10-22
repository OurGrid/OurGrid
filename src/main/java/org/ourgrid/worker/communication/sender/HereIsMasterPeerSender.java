package org.ourgrid.worker.communication.sender;

import org.ourgrid.common.interfaces.control.WorkerControlClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.worker.response.HereIsMasterPeerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class HereIsMasterPeerSender implements SenderIF<HereIsMasterPeerResponseTO> {

	public void execute(HereIsMasterPeerResponseTO response, ServiceManager manager) {
		ServiceID clientID = ServiceID.parse(response.getClientAddress());
		ServiceID masterPeerServiceId = null;
		
		if (response.getMasterPeerAddress() != null){
			masterPeerServiceId = ServiceID.parse(response.getMasterPeerAddress());
		}
		
		WorkerControlClient client = (WorkerControlClient) manager.getStub(clientID, WorkerControlClient.class);

		client.hereIsMasterPeer(masterPeerServiceId);
	}
}
