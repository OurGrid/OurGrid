package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.peer.request.GetUserStatusRequestTO;
import org.ourgrid.peer.response.HereIsUserStatusResponseTO;


public class GetUserStatusRequester extends AbstractGetStatusRequester<GetUserStatusRequestTO> {

	public List<IResponseTO> execute(GetUserStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (request.canStatusBeUsed()) {
			HereIsUserStatusResponseTO to = new HereIsUserStatusResponseTO();
			to.setUsersInfo(getUsersInfo(responses));
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAdress());
			responses.add(to);
		}
		
		return responses;
	}

}
