package org.ourgrid.peer.communication.sender;

import org.ourgrid.common.interfaces.management.WorkerManagementClient;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.response.SaveRankingResponseTO;

import br.edu.ufcg.lsd.commune.container.ObjectDeployment;
import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;

public class SaveRankingSender implements SenderIF<SaveRankingResponseTO>{

	public void execute(SaveRankingResponseTO response,
			ServiceManager manager) {
		
		ObjectDeployment objectDeployment = 
			manager.getObjectDeployment(PeerConstants.WORKER_MANAGEMENT_CLIENT_OBJECT_NAME);
		
		if (objectDeployment != null) {
			WorkerManagementClient accountingController = (WorkerManagementClient) objectDeployment.getObject();
			accountingController.saveRanking();
		}
	}

	
}
