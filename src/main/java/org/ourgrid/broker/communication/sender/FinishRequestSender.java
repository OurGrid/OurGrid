package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.FinishRequestResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.specification.job.JobSpecification;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class FinishRequestSender implements SenderIF<FinishRequestResponseTO> {

	public void execute(FinishRequestResponseTO response,
			ServiceManager manager) {
		
		JobSpecification jobSpec = response.getJobSpec();
		
		RequestSpecification spec = new RequestSpecification(response.getJobID(), jobSpec, response.getRequestID(), jobSpec.getRequirements(), 
				response.getRequiredWorkers(), response.getMaxFails(), response.getMaxReplicas(), manager.getMyCertPath());
		
		LocalWorkerProvider lwp = (LocalWorkerProvider) manager.getStub(ServiceID.parse(response.getPeerAddress()), LocalWorkerProvider.class);
			
		lwp.finishRequest(spec);
	}

}
