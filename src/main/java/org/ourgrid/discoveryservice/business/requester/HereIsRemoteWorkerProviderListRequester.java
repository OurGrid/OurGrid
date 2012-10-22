package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.request.HereIsRemoteWorkerProviderListRequestTO;

public class HereIsRemoteWorkerProviderListRequester implements RequesterIF<HereIsRemoteWorkerProviderListRequestTO>{
	
	public List<IResponseTO> execute(HereIsRemoteWorkerProviderListRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		if (discoveryServiceDAO.getDSInfo(request.getSenderAddress()) == null) {
			responses.add((new LoggerResponseTO(DiscoveryServiceControlMessages.getUnknownSenderHereIsRemoteWorkerProvidersListMessage(request.getSenderAddress()), LoggerResponseTO.DEBUG)));
			return responses;
		}
		
		Set <String> peers = new LinkedHashSet <String> (request.getWorkerProviders());
		discoveryServiceDAO.addDiscoveryService(discoveryServiceDAO.getDSInfo(request.getSenderAddress()), peers);
		
		return responses;
	}	
}
