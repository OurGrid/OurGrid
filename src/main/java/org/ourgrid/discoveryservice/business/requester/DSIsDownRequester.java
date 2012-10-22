package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.request.DSIsDownRequestTO;

public class DSIsDownRequester implements RequesterIF<DSIsDownRequestTO>{

	public List<IResponseTO> execute(DSIsDownRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		DiscoveryServiceInfo dsInfo = discoveryServiceDAO.getDSInfo(request.getDsAddress());
		
		if (dsInfo == null ) {
		 		
			responses.add(new LoggerResponseTO(
				DiscoveryServiceControlMessages.getDSNotMemberOfNetworkMessage(request.getDsAddress()), 
				LoggerResponseTO.WARN));
			
			return responses;
		} 
		
		if (! dsInfo.isUp()) {
			responses.add(new LoggerResponseTO(
					DiscoveryServiceControlMessages.getFailureNotificationFromAFailedDSMessage(request.getDsAddress()), 
					LoggerResponseTO.WARN));
			
			return responses;
		}
		
		discoveryServiceDAO.removeFromNetwork(request.getDsAddress());
		
		responses.add(new LoggerResponseTO(DiscoveryServiceControlMessages.getDiscoveryServiceFailureNotificationMessage(request.getDsAddress()), LoggerResponseTO.INFO));
		
		return responses;
	}
	
}
