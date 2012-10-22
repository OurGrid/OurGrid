package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class LeaveCommunityResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.LEAVE_COMMUNITY;
	
	
	private String dsAddress;
	
	
	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	/**
	 * @return the dsAddress
	 */
	public String getDsAddress() {
		return dsAddress;
	}

	/**
	 * @param dsAddress the dsAddress to set
	 */
	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}
}
