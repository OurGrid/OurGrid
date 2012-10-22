package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class DSIsUpRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.DS_IS_UP;
	
	
	private String dsAddress;
	private String myAddress;
		

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setDsAddress(String dsAddress) {
		this.dsAddress = dsAddress;
	}

	public String getDsAddress() {
		return dsAddress;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
