package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.RequestWorkersResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.interfaces.to.RequestSpecification;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.common.specification.job.JobSpecification;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;
import br.edu.ufcg.lsd.commune.network.certification.CertificationUtils;

public class RequestWorkersSender implements SenderIF<RequestWorkersResponseTO> {

	public void execute(RequestWorkersResponseTO response, ServiceManager manager) {
		CertificationUtils.getCertSubjectDN(manager.getMyCertPath());
		
		JobSpecification jobSpec = response.getJobSpec();
		
		RequestSpecification spec = new RequestSpecification(response.getJobID(), jobSpec, response.getRequestID(), jobSpec.getRequirements(), 
				response.getRequiredWorkers(), response.getMaxFails(), response.getMaxReplicas(), manager.getMyCertPath());
		
		LocalWorkerProvider lwp = (LocalWorkerProvider) manager.getStub(ServiceID.parse(response.getPeerAddress()), LocalWorkerProvider.class);
		
		lwp.requestWorkers(spec);
		
	}

}
