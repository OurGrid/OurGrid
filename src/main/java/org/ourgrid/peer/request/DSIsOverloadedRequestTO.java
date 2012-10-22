package org.ourgrid.peer.request;

import org.ourgrid.common.internal.IRequestTO;

/**
 * RequestTO for DSIsOverloaded method
 */
public class DSIsOverloadedRequestTO implements IRequestTO{

	private static final String REQUEST_TYPE = PeerRequestConstants.DS_IS_OVERLOADED;
	private String dsAddress;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDSAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDSAddress() {
		return dsAddress;
	}
}
