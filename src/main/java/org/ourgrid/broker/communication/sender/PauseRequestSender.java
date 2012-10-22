package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.PauseRequestResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class PauseRequestSender implements SenderIF<PauseRequestResponseTO> {

	public void execute(PauseRequestResponseTO response, ServiceManager manager) {
		ServiceID serviceID = ServiceID.parse(response.getPeerAddress());
		LocalWorkerProvider lwp = (LocalWorkerProvider) manager.getStub(serviceID, LocalWorkerProvider.class);
		
		lwp.pauseRequest(response.getRequestID());
	}
}
