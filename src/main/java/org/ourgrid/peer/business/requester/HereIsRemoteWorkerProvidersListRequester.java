package org.ourgrid.peer.business.requester;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.interfaces.RemoteWorkerProvider;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.RegisterInterestResponseTO;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.PeerConstants;
import org.ourgrid.peer.business.controller.RemoteWorkerProviderFailureController;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.HereIsRemoteWorkerProvidersListRequestTO;
import org.ourgrid.peer.to.RemoteConsumer;

public class HereIsRemoteWorkerProvidersListRequester implements RequesterIF<HereIsRemoteWorkerProvidersListRequestTO> {

	public List<IResponseTO> execute(
			HereIsRemoteWorkerProvidersListRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		
		Set<String> oldProvidersAddresses = new LinkedHashSet<String>(dao.getRemoteWorkerProvidersAddress());

		for (String peerUserAtServer : request.getProvidersUserAtServer()) {
			if (peerUserAtServer.equals(request.getMyUserAtServer())) {
				continue;
			}

			String rwpAddress = RemoteWorkerProviderFailureController
					.createProviderAddress((peerUserAtServer));
			
			if (!oldProvidersAddresses.remove( rwpAddress )){
				RegisterInterestResponseTO registerInterestResponse = new RegisterInterestResponseTO();
				registerInterestResponse.setMonitorableAddress(rwpAddress);
				registerInterestResponse.setMonitorableType(RemoteWorkerProvider.class);
				registerInterestResponse.setMonitorName(PeerConstants.REMOTE_WORKER_PROVIDER_CLIENT);
				responses.add(registerInterestResponse);
				
				LoggerResponseTO loggerResponse = new LoggerResponseTO(
						"Receiving worker provider with id [" + peerUserAtServer + "]", 
						LoggerResponseTO.DEBUG);
				responses.add(loggerResponse);
			}
		}
		
		// All peers there aren't part of community anymore must be released
		for (String rwpAddress : oldProvidersAddresses) {
			releaseWorkerProvider(responses, StringUtil.addressToUserAtServer(rwpAddress));
		}
		
		return responses;
	}
	
	private void releaseWorkerProvider(List<IResponseTO> responses, String providerUserAtServer) {
		String rwpcAddress = RemoteWorkerProviderFailureController
				.createProviderClientAddress(providerUserAtServer);
		
		List<RemoteConsumer> remoteConsumers = PeerDAOFactory.getInstance().getConsumerDAO().getRemoteConsumers();
		
		String rwpcPublicKey = null;
		
		// Find the right RemoteConsumer
		for (RemoteConsumer remoteConsumer : remoteConsumers) {
			if (remoteConsumer.getConsumerAddress().equals(rwpcAddress)) {
				rwpcPublicKey = remoteConsumer.getPublicKey();
				break;
			}
		}
		
		RemoteWorkerProviderFailureController.getInstance().doNotifyFailure(responses, providerUserAtServer,
				rwpcPublicKey);
	}
}
