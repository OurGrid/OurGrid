package org.ourgrid.discoveryservice.business.controller;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.discoveryservice.PeerStatusChangeUtil;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.response.DSHereIsRemoteWorkerProvidersListResponseTO;
import org.ourgrid.discoveryservice.response.DSIsOverloadedResponseTO;

public class DiscoveryServiceController {

	private static DiscoveryServiceController instance;
	
	public static DiscoveryServiceController getInstance() {
		if (instance == null) {
			instance = new DiscoveryServiceController();
		}
		return instance;
	}
	
	private DiscoveryServiceController() {}
	
	public <T> Set<T> limitAndShuffleResponse(Collection<T> response, int maxResponseSize) {
		
		if (response.size() <= maxResponseSize) {
			return new LinkedHashSet<T>(response);
		} else {
			List<T> tempResponse = new LinkedList<T>(response);
			
			Collections.shuffle(tempResponse);
			
			Set<T> selectedResponse = new LinkedHashSet<T>();
			
			for (int i = 0; i < maxResponseSize; i++) {
				selectedResponse.add(tempResponse.get(i));
			}
			
			return selectedResponse;
		}
		
	}
	
	public void joinCommunity(List<IResponseTO> responses, 
			String clientUserAtServer, String clientAddress, String dsAddress,
			int overloadThreshold) {
		
		DiscoveryServiceDAO dsDao = DiscoveryServiceDAOFactory.getInstance()
				.getDiscoveryServiceDAO();

		if (dsDao.isPeerUp(clientUserAtServer)) {

			responses.add((new LoggerResponseTO(
					DiscoveryServiceControlMessages.getClientAlreadyJoinedToTheCommunityMessage(clientUserAtServer),
					LoggerResponseTO.DEBUG)));

			return;
		}
		
		if (dsDao.isOverloaded(overloadThreshold)) {
			
			responses.add((new LoggerResponseTO(
					DiscoveryServiceControlMessages.getDiscoveryServiceIsOverloadedMessage(clientUserAtServer),
					LoggerResponseTO.DEBUG)));
			
			DSIsOverloadedResponseTO dsIsOverloaded = new DSIsOverloadedResponseTO();
			
			dsIsOverloaded.setDSAddress(clientAddress);
			dsIsOverloaded.setClientAddress(dsAddress);

			responses.add(dsIsOverloaded);
			
			ReleaseResponseTO releaseTo = new ReleaseResponseTO();
			releaseTo.setStubAddress(clientAddress);
			
			responses.add(releaseTo);
			
			return;
		}
		
		responses.add((new LoggerResponseTO(DiscoveryServiceControlMessages.getClientNotLoggedMessage(
				clientUserAtServer), LoggerResponseTO.DEBUG)));

		List<String> onlinePeers = dsDao.getMyOnlinePeers();
		Set<DiscoveryServiceInfo> DSs = dsDao.getAllDiscoveryServicesInfos();

		for (DiscoveryServiceInfo ds : DSs) {
			if (ds.isUp()) {
				DSHereIsRemoteWorkerProvidersListResponseTO to = new DSHereIsRemoteWorkerProvidersListResponseTO();
				to.setStubAddress(ds.getDsAddress());
				to.setWorkerProviders(onlinePeers);
				responses.add(to);
			}
		}

		dsDao.peerIsUp(clientUserAtServer);
		PeerStatusChangeUtil.peerIsUp(clientUserAtServer, responses);
		
	}
	
}
