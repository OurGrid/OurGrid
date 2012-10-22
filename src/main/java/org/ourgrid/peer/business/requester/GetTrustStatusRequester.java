package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.List;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.trust.TrustCommunitiesDAO;
import org.ourgrid.peer.request.GetTrustStatusRequestTO;
import org.ourgrid.peer.response.HereIsTrustStatusResponseTO;

public class GetTrustStatusRequester implements RequesterIF<GetTrustStatusRequestTO> {


	public List<IResponseTO> execute(GetTrustStatusRequestTO request) {
		
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
	
		HereIsTrustStatusResponseTO trustStatusResponseTO = new HereIsTrustStatusResponseTO();
		trustStatusResponseTO.setClientAddress(request.getClientAddress());
		trustStatusResponseTO.setStatusProviderServiceID(request.getStatusProviderServiceID());
		
		TrustCommunitiesDAO trustDAO = PeerDAOFactory.getInstance().getTrustCommunitiesDAO();
		trustStatusResponseTO.setTrustInfo(trustDAO.getTrustyCommunities(responses));
		
		responses.add(trustStatusResponseTO);
		
		return responses;
	}

}
