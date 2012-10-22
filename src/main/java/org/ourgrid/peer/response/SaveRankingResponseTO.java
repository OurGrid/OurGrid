package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class SaveRankingResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.SAVE_RANKING;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}
}
