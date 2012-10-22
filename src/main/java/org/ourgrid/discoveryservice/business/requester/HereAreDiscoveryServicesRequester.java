package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.ourgrid.common.interfaces.DiscoveryService;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.discoveryservice.DiscoveryServiceConstants;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.request.HereAreDiscoveryServicesRequestTO;
import org.ourgrid.discoveryservice.response.DSHereIsRemoteWorkerProvidersListResponseTO;

public class HereAreDiscoveryServicesRequester implements RequesterIF<HereAreDiscoveryServicesRequestTO>{
	
	public List<IResponseTO> execute(HereAreDiscoveryServicesRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO discoveryServiceDAO = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		DiscoveryServiceInfo dsInfo = discoveryServiceDAO.getDSInfo(request.getSenderAddress());
		
		if (dsInfo == null) {
			responses.add((new LoggerResponseTO(DiscoveryServiceControlMessages.getUnknownSenderHereAreDiscoveryServicesMessage(request.getSenderAddress()), LoggerResponseTO.DEBUG)));
			return responses;
		}
		
		if (!dsInfo.isUp()) {
			responses.add((new LoggerResponseTO(DiscoveryServiceControlMessages.getDiscoveryServiceIsDownNotificationMessage(request.getSenderAddress()), LoggerResponseTO.DEBUG)));
			return responses;
		}
		
		List<String> onlinePeers = discoveryServiceDAO.getMyOnlinePeers();
		
		DSHereIsRemoteWorkerProvidersListResponseTO hereIsTo = new DSHereIsRemoteWorkerProvidersListResponseTO();
		hereIsTo.setStubAddress(request.getSenderAddress());
		hereIsTo.setWorkerProviders(onlinePeers);
			
		responses.add(hereIsTo);
		
		for (String address : request.getDiscoveryServicesAddresses()) {
			if (discoveryServiceDAO.getDSInfo(address) == null &&
					!address.equals(request.getMyAddress())) {
				RegisterInterestResponseTO to = new RegisterInterestResponseTO();
				to.setMonitorableAddress(address);
				to.setMonitorableType(DiscoveryService.class);
				to.setMonitorName(DiscoveryServiceConstants.DS_MONITOR);

				responses.add(to);
				
				discoveryServiceDAO.addDiscoveryService(new DiscoveryServiceInfo(address), new LinkedHashSet<String>());
			}
		}
		
		return responses;
	}
	
}