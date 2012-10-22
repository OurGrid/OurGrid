package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.discoveryservice.business.controller.DiscoveryServiceController;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.request.GetRemoteWorkerProvidersRequestTO;
import org.ourgrid.discoveryservice.response.DSClientHereIsRemoteWorkerProvidersListResponseTO;

public class GetRemoteWorkerProvidersRequester implements RequesterIF<GetRemoteWorkerProvidersRequestTO>{
	
	public List<IResponseTO> execute(GetRemoteWorkerProvidersRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceController dsController = DiscoveryServiceController.getInstance();
		
		String clientUserAtServer = request.getClientUserAtServer();
		dsController.joinCommunity(responses, clientUserAtServer, 
				request.getClientAddress(), request.getMyAddress(), 
				request.getOverloadThreshold());
		
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		if (dsDao.isPeerUp(clientUserAtServer)) {
			
			Set<String> onlinePeers = dsController.limitAndShuffleResponse(dsDao.getAllOnlinePeers(), 
					Math.min(request.getMaxResponseSize(), request.getDsMaxResponse()));
			
			DSClientHereIsRemoteWorkerProvidersListResponseTO to = new DSClientHereIsRemoteWorkerProvidersListResponseTO();
			to.setStubAddress(request.getClientAddress());
			to.setWorkerProviders(new LinkedList<String>(onlinePeers));
			
			responses.add(to);
			
		}
		
		return responses;
	}
	
}
