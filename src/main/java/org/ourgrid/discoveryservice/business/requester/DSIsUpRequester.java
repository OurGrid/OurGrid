package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.request.DSIsUpRequestTO;
import org.ourgrid.discoveryservice.response.DSGetDiscoveryServicesResponseTO;
/**
 * Requirement 511
 */
public class DSIsUpRequester implements RequesterIF<DSIsUpRequestTO>{

	public List<IResponseTO> execute(DSIsUpRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		if (discoveryServiceDAO.getDSInfo(request.getDsAddress()) == null) {
			responses.add(new LoggerResponseTO(
					DiscoveryServiceControlMessages.getDSNotMemberOfNetworkMessage(request.getDsAddress()), 
					LoggerResponseTO.WARN));
				
				return responses;
		}
		
		discoveryServiceDAO.dsIsUp(request.getDsAddress());
		
		DSGetDiscoveryServicesResponseTO getDSResponseTO = new DSGetDiscoveryServicesResponseTO();
		getDSResponseTO.setDsAddress(request.getDsAddress());
		responses.add(getDSResponseTO);
				
		responses.add(new LoggerResponseTO(DiscoveryServiceControlMessages.getDiscoveryServiceIsUpNotificationMessage(request.getDsAddress()), LoggerResponseTO.INFO));
				
//		List<String> onlinePeers = discoveryServiceDAO.getMyOnlinePeers();
//		
//		//FIXME wrong place to do that
//		DSHereIsRemoteWorkerProvidersListResponseTO hereIsTo = new DSHereIsRemoteWorkerProvidersListResponseTO();
//		hereIsTo.setStubAddress(request.getDsAddress());
//		hereIsTo.setWorkerProviders(onlinePeers);
//			
//		responses.add(hereIsTo);
			
		return responses;
	}
	
}
