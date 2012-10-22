package org.ourgrid.peer.business.requester;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ourgrid.common.internal.IResponseTO;
import org.ourgrid.common.internal.RequesterIF;
import org.ourgrid.common.util.StringUtil;
import org.ourgrid.peer.business.dao.PeerDAOFactory;
import org.ourgrid.peer.dao.DiscoveryServiceClientDAO;
import org.ourgrid.peer.request.HereAreDiscoveryServicesRequestTO;
import org.ourgrid.peer.response.PersistDiscoveryServiceNetworkResponseTO;

public class HereAreDiscoveryServicesRequester implements RequesterIF<HereAreDiscoveryServicesRequestTO> {


	public List<IResponseTO> execute(HereAreDiscoveryServicesRequestTO request) {
		List<IResponseTO> responses = new ArrayList<IResponseTO>();
		
		DiscoveryServiceClientDAO dao = PeerDAOFactory.getInstance().getDiscoveryServiceClientDAO();
		boolean modified = false; 
		for (String dsAdress : request.getDiscoveryServices()) {
//			String[] splitAddress = dsAdress.split("@");
//			ServiceID dsID = new ServiceID(splitAddress[0], splitAddress[1], 
//					DiscoveryServiceConstants.MODULE_NAME, DiscoveryServiceConstants.DS_OBJECT_NAME);
//			modified |= dao.addDsAddress(dsID.toString());
			modified |= dao.addDsAddress(dsAdress);
		}
		if (modified) {
			Set<String> dsUserAtServers = new HashSet<String>();
			for (String dsAddress : dao.getDsAddresses()) {
				dsUserAtServers.add(StringUtil.addressToUserAtServer(dsAddress));
			}
			
			PersistDiscoveryServiceNetworkResponseTO persistNetworkResponse = new PersistDiscoveryServiceNetworkResponseTO();
			persistNetworkResponse.setUsersAtServers(dsUserAtServers);
			responses.add(persistNetworkResponse);
		}
		
		return responses;
	}
	
}
