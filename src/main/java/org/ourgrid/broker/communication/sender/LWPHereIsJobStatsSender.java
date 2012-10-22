package org.ourgrid.broker.communication.sender;

import org.ourgrid.broker.response.LWPHereIsJobStatsResponseTO;
import org.ourgrid.common.interfaces.LocalWorkerProvider;
import org.ourgrid.common.internal.SenderIF;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.DeploymentID;

public class LWPHereIsJobStatsSender implements SenderIF<LWPHereIsJobStatsResponseTO> {

	public void execute(LWPHereIsJobStatsResponseTO response,
			ServiceManager manager) {
		
		for (String peerId : response.getJobStatusInfo().getPeersToRequests().keySet()) {
			
			LocalWorkerProvider localWorkerProvider = manager.getStub(
					new DeploymentID(peerId).getServiceID(), LocalWorkerProvider.class);
		
			localWorkerProvider.hereIsJobStats(response.getJobStatusInfo());
		}
	}

}
