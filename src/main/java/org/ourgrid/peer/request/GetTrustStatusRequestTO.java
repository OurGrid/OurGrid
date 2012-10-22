package org.ourgrid.peer.request;


import org.ourgrid.common.internal.IRequestTO;

public class GetTrustStatusRequestTO implements IRequestTO {

	private static final String REQUEST_TYPE = PeerRequestConstants.GET_TRUST_STATUS;
	private String clientAddress;
	private String statusProviderServiceID;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	}


	/**
	 * @return the clientAddress
	 */
	public String getClientAddress() {
		return clientAddress;
	}

	/**
	 * @param clientAddress the clientAddress to set
	 */
	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}


	public void setStatusProviderServiceID(String statusProviderServiceID) {
		this.statusProviderServiceID = statusProviderServiceID;
	}


	public String getStatusProviderServiceID() {
		return statusProviderServiceID;
	}

}
