package org.ourgrid.acceptance.util.aggregator;

import org.ourgrid.peer.PeerConstants;

import br.edu.ufcg.lsd.commune.Module;
import br.edu.ufcg.lsd.commune.identification.ServiceID;

public class AggegatorUsableAddresses {

	public static final String PEER_STATUS_PROVIDER_01 = "peer01@test";
	public static final String PEER_STATUS_PROVIDER_02 = "peer02@test";
	
	public static ServiceID userAtServerToServiceID(String userAtServer) {
		String[] addresses = userAtServer.split("@");
			
		return new ServiceID(addresses[0], addresses[1], PeerConstants.MODULE_NAME, Module.CONTROL_OBJECT_NAME);
	}
}
