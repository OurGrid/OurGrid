package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.request.GetPeerStatusProvidersRequestTO;
import org.ourgrid.discoveryservice.response.HereIsPeerStatusProvidersResponseTO;

public class GetPeerStatusProvidersRequester implements RequesterIF<GetPeerStatusProvidersRequestTO>{
	
	

	public List<IResponseTO> execute(GetPeerStatusProvidersRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		HereIsPeerStatusProvidersResponseTO to = new HereIsPeerStatusProvidersResponseTO();
		to.setClientAddress(request.getClientAddress());
		to.setStatusProviders(dsDao.getAllOnlinePeers());
		
		responses.add(to);
		
		return responses;
	}
	
}
