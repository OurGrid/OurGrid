package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.interfaces.status.NetworkOfFavorsStatus;
import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.statistics.control.AccountingControl;
import org.ourgrid.peer.request.GetNetworkOfFavorsStatusRequestTO;
import org.ourgrid.peer.response.HereIsNetworkOfFavorsStatusResponseTO;


public class GetNetworkOfFavorsStatusRequester extends AbstractGetStatusRequester<GetNetworkOfFavorsStatusRequestTO> {

	public List<IResponseTO> execute(GetNetworkOfFavorsStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		if (request.canStatusBeUsed()) {
			NetworkOfFavorsStatus nofStatus = new NetworkOfFavorsStatus(
					AccountingControl.getInstance().getBalances(responses, request.getPeerDNData()));
			
			HereIsNetworkOfFavorsStatusResponseTO to = new HereIsNetworkOfFavorsStatusResponseTO();
			to.setClientAddress(request.getClientAddress());
			to.setPeerAddress(request.getPeerAdress());
			to.setNofStatus(nofStatus);
			
			responses.add(to);
		}
		
		return responses;
	}

}
