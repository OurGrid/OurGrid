package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.request.GetLocalWorkersStatusRequestTO;
import org.ourgrid.peer.response.HereIsLocalWorkersStatusResponseTO;


public class GetLocalWorkersStatusRequester extends AbstractGetStatusRequester<GetLocalWorkersStatusRequestTO> {

	public List<IResponseTO> execute(GetLocalWorkersStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (request.canStatusBeUsed()) {
			String peerUserAtServer = request.getPeerUserAtServer();
			
			HereIsLocalWorkersStatusResponseTO response = new HereIsLocalWorkersStatusResponseTO();
			response.setLocalWorkersInfos(getLocalWorkersInfo(responses, peerUserAtServer));
			response.setClientAddress(request.getClientAddress());
			
			responses.add(response);
		}
		
		return responses;
	}
}
