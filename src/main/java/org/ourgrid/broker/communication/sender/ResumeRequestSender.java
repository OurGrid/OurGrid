package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.ResumeRequestResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class ResumeRequestSender implements SenderIF<ResumeRequestResponseTO> {

	public void execute(ResumeRequestResponseTO response, ServiceManager manager) {
		ServiceID serviceID = ServiceID.parse(response.getPeerAddress());
		LocalWorkerProvider lwp = (LocalWorkerProvider) manager.getStub(serviceID, LocalWorkerProvider.class);
		
		lwp.resumeRequest(response.getRequestID());
	}
}
