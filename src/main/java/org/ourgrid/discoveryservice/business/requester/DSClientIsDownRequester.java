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
import org.ourgrid.discoveryservice.request.DSClientIsDownRequestTO;
import org.ourgrid.discoveryservice.response.DSHereIsRemoteWorkerProvidersListResponseTO;

public class DSClientIsDownRequester implements RequesterIF<DSClientIsDownRequestTO>{

	public List<IResponseTO> execute(DSClientIsDownRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String clientAddress = request.getClientAddress();
		
		if (clientAddress == null) {
			responses.add(new LoggerResponseTO(
					DiscoveryServiceControlMessages.getNullMonitorableIDMessage(), 
					LoggerResponseTO.ERROR));
			return responses;
		}
		
		ReleaseResponseTO releaseTO = new ReleaseResponseTO();
		releaseTO.setStubAddress(clientAddress);
		
		responses.add(releaseTO);
		
		DiscoveryServiceDAO dao = DiscoveryServiceDAOFactory.getInstance().getDiscoveryServiceDAO();

		if (!dao.isPeerUp(request.getClientUserAtServer())){
			
			responses.add(new LoggerResponseTO(
					DiscoveryServiceControlMessages.getClientNotLoggedMessage(request.getClientUserAtServer()), 
					LoggerResponseTO.ERROR));
			
			return responses;
		}
		
		dao.peerIsDown(request.getClientUserAtServer());

		Set<DiscoveryServiceInfo> DSs = dao.getAllDiscoveryServicesInfos();
		
		List<String> onlinePeers = dao.getMyOnlinePeers();
		
		for (DiscoveryServiceInfo ds : DSs) {
			DSHereIsRemoteWorkerProvidersListResponseTO to = new DSHereIsRemoteWorkerProvidersListResponseTO();
			to.setStubAddress(ds.getDsAddress());
			to.setWorkerProviders(onlinePeers);
				
			responses.add(to);
		}
		
		PeerStatusChangeUtil.peerIsDown(request.getClientUserAtServer(),
				responses);
		
		return responses;
	}
}