package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.statistics.control.WorkerControl;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.request.StopPeerRequestTO;
import org.ourgrid.peer.response.LeaveCommunityResponseTO;
import org.ourgrid.peer.response.SaveRankingResponseTO;

public class StopPeerRequester implements RequesterIF<StopPeerRequestTO> {
	
	public List<IResponseTO> execute(StopPeerRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		saveRanking(responses);
		leaveCommunity(responses, request);
		disconnectActiveWorkers(responses, request);
		
		return responses;
	}

	private void saveRanking(List<IResponseTO> responses) {
		responses.add(new SaveRankingResponseTO());
	}
	
	private void leaveCommunity(List<IResponseTO> responses, StopPeerRequestTO request) {
		if (request.isDAOStarted() && request.shouldJoinCommunity()) {
			String discoverServiceAddress = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO().getAliveDiscoveryServiceAddress();
			
			if (discoverServiceAddress != null) {
				LeaveCommunityResponseTO to = new LeaveCommunityResponseTO();
				to.setDsAddress(discoverServiceAddress);
			}
		}
	}
	
	private void disconnectActiveWorkers(List<IResponseTO> responses, StopPeerRequestTO request) {
		if (request.canStatusBeUsed()) {
			Collection<String> localWorkersUserAtServer = WorkerControl.getInstance().getLocalWorkersUserAtServer(responses, request.getMyUserAtServer());
			
			for (String workerUserAtServer : localWorkersUserAtServer) {
				RemoveWorkerRequester.removeWorker(responses, workerUserAtServer);	
			}
		}
	}
	
}
