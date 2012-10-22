package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.JobEndedResponseTO;
import org.ourgrid.broker.util.UtilConverter;
import org.ourgrid.common.interfaces.to.JobEndedInterested;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class JobEndedSender implements SenderIF<JobEndedResponseTO> {

	public void execute(JobEndedResponseTO response, ServiceManager manager) {
		JobEndedInterested interested = manager.createStub(new DeploymentID(response.getInterestedID()), 
				JobEndedInterested.class);
		interested.jobEnded(response.getJobID(), UtilConverter.getJobState(response.getState()));
	}
}
