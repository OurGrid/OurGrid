package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.ds.DiscoveryServiceNotificationController;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.DSIsOverloadedRequestTO;
import org.ourgrid.peer.response.ScheduleDelayedInterestOnDSResponseTO;

/**
 * Requester for DSIsOverloaded method
 */
public class DSIsOverloadedRequester implements RequesterIF<DSIsOverloadedRequestTO>{

	/* (non-Javadoc)
	 * @see org.ourgrid.common.internal.RequesterIF#execute(org.ourgrid.common.internal.IRequestTO)
	 */
	@Override
	public List<IResponseTO> execute(DSIsOverloadedRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceNotificationController.getInstance().removeAliveDiscoveryService(
				responses, request.getDSAddress());
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		
		for (String dsId : dao.getDsAddresses()) {
			
			if (dsId.equals(request.getDSAddress())) {
				continue;
			}
			
			RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
			registerInterestResponse.setMonitorableAddress(dsId);
			registerInterestResponse.setMonitorableType(DiscoveryService.class);
			registerInterestResponse.setMonitorName(PeerConstants.DS_CLIENT);
			
			responses.add(registerInterestResponse);
		}
		
		ScheduleDelayedInterestOnDSResponseTO delayedInterestResponse = new ScheduleDelayedInterestOnDSResponseTO();
		delayedInterestResponse.setDsAddress(request.getDSAddress());
		responses.add(delayedInterestResponse);
		
		return responses;
		
	}

}
