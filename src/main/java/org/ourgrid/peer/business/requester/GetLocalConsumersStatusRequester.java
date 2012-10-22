package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.request.GetLocalConsumersStatusRequestTO;
import org.ourgrid.peer.response.HereIsLocalConsumersStatusResponseTO;

public class GetLocalConsumersStatusRequester extends AbstractGetStatusRequester<GetLocalConsumersStatusRequestTO> {


	public List<IResponseTO> execute(GetLocalConsumersStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		if (request.canStatusBeUsed()) {
			HereIsLocalConsumersStatusResponseTO to = new HereIsLocalConsumersStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAddress());
			to.setLocalConsumersInfo(getLocalConsumersInfo());
			
			responses.add(to);
		}
		
		return responses;
	}

	
}
