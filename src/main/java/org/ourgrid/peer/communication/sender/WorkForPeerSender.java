package org.ourgrid.peer.communication.sender;

import java.util.List;

import org.ourgrid.common.interfaces.management.WorkerManagement;
import org.ourgrid.common.internal.SenderIF;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.response.WorkForPeerResponseTO;

import br.edu.ufcg.lsd.commune.container.servicemanager.ServiceManager;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class WorkForPeerSender implements SenderIF<WorkForPeerResponseTO> {

	public void execute(WorkForPeerResponseTO response, ServiceManager manager) {
			
			WorkerManagement workerManagement = (WorkerManagement) manager.getStub(
					ServiceID.parse(response.getWorkerManagementAddress()), WorkerManagement.class);
			
			List<String> usersDN = response.getUsersDN();
			
			if (usersDN == null) {
				workerManagement.workForPeer(response.getPeerPublicKey());
			} else {
				workerManagement.workForPeer(response.getPeerPublicKey(), usersDN, 
						PeerDAOFactory.getInstance().getPeerCertificationDAO().getRequestingPeersCAsCertificates());
			}
	}

}
