package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.internal.response.LoggerResponseTO;
import org.ourgrid.common.internal.response.ReleaseResponseTO;
import org.ourgrid.discoveryservice.PeerStatusChangeUtil;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAO;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceDAOFactory;
import org.ourgrid.discoveryservice.business.dao.DiscoveryServiceInfo;
import org.ourgrid.discoveryservice.business.messages.DiscoveryServiceControlMessages;
import org.ourgrid.discoveryservice.request.LeaveCommunityRequestTO;
import org.ourgrid.discoveryservice.response.DSHereIsRemoteWorkerProvidersListResponseTO;
/**
 * 
 * Req 506
 *
 */
public class LeaveCommunityRequester implements RequesterIF<LeaveCommunityRequestTO>{

	public List<IResponseTO> execute(LeaveCommunityRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceDAO dao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();
		
		String clientUserAtServer = request.getClientUserAtServer();
		
		if (!dao.isPeerUp(clientUserAtServer)) {
			responses.add(new LoggerResponseTO(DiscoveryServiceControlMessages.
					getClientNotJoinedToTheCommunityMessage(request.getClientAddress()), LoggerResponseTO.WARN));
			
			ReleaseResponseTO releaseTO = new ReleaseResponseTO();
			releaseTO.setStubAddress(request.getClientAddress());
			
			responses.add(releaseTO);
			
			return responses;
		}
		
		dao.peerIsDown(clientUserAtServer);
		
		responses.add(new LoggerResponseTO(DiscoveryServiceControlMessages.
				getPeerLeftCommunityNotificationMessage(clientUserAtServer), LoggerResponseTO.DEBUG));
		
		//warn the network about the change in peer list
		Set<DiscoveryServiceInfo> DSs = dao.getAllDiscoveryServicesInfos();
		
		for (DiscoveryServiceInfo ds : DSs) {
			DSHereIsRemoteWorkerProvidersListResponseTO to = new DSHereIsRemoteWorkerProvidersListResponseTO();
			to.setStubAddress(ds.getDsAddress());
			to.setWorkerProviders(dao.getMyOnlinePeers());
				
			responses.add(to);
		}
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(request.getClientAddress());
		
		responses.add(releaseTO);
		
		PeerStatusChangeUtil.peerIsDown(
				clientUserAtServer, responses);
		
		return responses;
	}
	
}
