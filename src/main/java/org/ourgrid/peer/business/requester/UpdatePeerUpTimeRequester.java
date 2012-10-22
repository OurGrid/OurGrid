package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.statistics.control.PeerControl;
import org.ourgrid.peer.request.UpdatePeerUpTimeRequestTO;


public class UpdatePeerUpTimeRequester implements RequesterIF<UpdatePeerUpTimeRequestTO> {

	public List<IResponseTO> execute(UpdatePeerUpTimeRequestTO requestTO) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		String senderPubKey = requestTO.getSenderPublicKey();
		
		if(!requestTO.isThisMyPublicKey()) {
			//TODO log
			
			return responses;
		}
		
		PeerControl.getInstance().updatePeerUptime(responses, requestTO.getMyUserAtServer());
		
		return responses;
	}

}
