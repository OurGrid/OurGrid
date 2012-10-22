package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.request.GetRemoteConsumersStatusRequestTO;
import org.ourgrid.peer.response.HereIsRemoteConsumersStatusResponseTO;

public class GetRemoteConsumersStatusRequester extends AbstractGetStatusRequester<GetRemoteConsumersStatusRequestTO> {


	public List<IResponseTO> execute(GetRemoteConsumersStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		if (request.canStatusBeUsed()) {
			HereIsRemoteConsumersStatusResponseTO to = new HereIsRemoteConsumersStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAddress());
			to.setRemoteConsumersInfo(getRemoteConsumersStatus());
			
			responses.add(to);
		}
		
		return responses;
	}

	
}
