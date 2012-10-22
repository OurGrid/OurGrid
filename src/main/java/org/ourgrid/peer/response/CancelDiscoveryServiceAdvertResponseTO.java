package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class CancelDiscoveryServiceAdvertResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.CANCEL_DISCOVERY_SERVICE_ADVERT;
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

}
