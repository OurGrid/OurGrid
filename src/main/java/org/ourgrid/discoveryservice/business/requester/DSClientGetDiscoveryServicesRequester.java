package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.discoveryservice.business.controller.DiscoveryServiceController;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.request.DSClientGetDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.response.DSClientHereAreDiscoveryServicesResponseTO;

public class DSClientGetDiscoveryServicesRequester implements RequesterIF<DSClientGetDiscoveryServicesRequestTO>{
	
	public List<IResponseTO> execute(DSClientGetDiscoveryServicesRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		String clientUserAtServer = request.getClientUserAtServer();
		
		DiscoveryServiceController.getInstance().joinCommunity(responses, 
				clientUserAtServer, request.getClientAddress(), 
				request.getMyAddress(), request.getOverloadThreshold());
		
		if (discoveryServiceDAO.isPeerUp(clientUserAtServer)) {
			
			DSClientHereAreDiscoveryServicesResponseTO to = new DSClientHereAreDiscoveryServicesResponseTO();
			Set<DiscoveryServiceInfo> discoveryServicesAddresses = discoveryServiceDAO.getAllDiscoveryServicesInfos();
			to.setDiscoveryServices(discoveryServicesAddresses);
			to.setStubAddress(request.getClientAddress());
			
			responses.add(to);
			
		}
		
		
		return responses;
	}
	
}
