package org.ourgrid.discoveryservice.business.requester;

import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.statistics.beans.ds.DS_PeerStatusChange;
import org.ourgrid.discoveryservice.PeerStatusChangeUtil;
import org.ourgrid.discoveryservice.request.GetPeerStatusChangeHistoryRequestTO;
import org.ourgrid.discoveryservice.response.HereIsPeerStatusChangeHistoryResponseTO;

public class GetPeerStatusChangeHistoryRequester implements RequesterIF<GetPeerStatusChangeHistoryRequestTO>{

	public List<IResponseTO> execute(GetPeerStatusChangeHistoryRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		List<DS_PeerStatusChange> peerStatusChangesHistory = PeerStatusChangeUtil
				.getPeerStatusChangesHistory(request.getSince(), responses);
		
		HereIsPeerStatusChangeHistoryResponseTO to = new HereIsPeerStatusChangeHistoryResponseTO();
		to.setClientAddress(request.getClientAddress());
		to.setPeerStatusChangesHistory(peerStatusChangesHistory);
		
		responses.add(to);
		
		return responses;
	}
	
}
