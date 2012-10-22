package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.request.GetRemoteWorkersStatusRequestTO;
import org.ourgrid.peer.response.HereIsRemoteWorkersStatusResponseTO;

public class GetRemoteWorkersStatusRequester extends AbstractGetStatusRequester<GetRemoteWorkersStatusRequestTO> {


	public List<IResponseTO> execute(GetRemoteWorkersStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		if (request.canStatusBeUsed()) {
			HereIsRemoteWorkersStatusResponseTO to = new HereIsRemoteWorkersStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAddress());
			to.setRemoteWorkersInfo(getRemoteWorkersInfo());
			
			responses.add(to);
		}
		
		return responses;
	}

	
}
