package org.ourgrid.peer.response;

import java.util.List;

import org.ourgrid.common.interfaces.to.TrustyCommunity;
import org.ourgrid.common.internal.IResponseTO;

public class HereIsTrustStatusResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = PeerResponseConstants.HERE_IS_TRUST_STATUS;
	private List<TrustyCommunity> trustInfo;
	private String clientAddress;
	private String statusProviderServiceID;
	
	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	/**
	 * @return the trustInfo
	 */
	public List<TrustyCommunity> getTrustInfo() {
		return trustInfo;
	}

	/**
	 * @param trustInfo the trustInfo to set
	 */
	public void setTrustInfo(List<TrustyCommunity> trustInfo) {
		this.trustInfo = trustInfo;
	}

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setStatusProviderServiceID(String statusProviderServiceID) {
		this.statusProviderServiceID = statusProviderServiceID;
	}

	public String getStatusProviderServiceID() {
		return statusProviderServiceID;
	}
	
	
	
}
