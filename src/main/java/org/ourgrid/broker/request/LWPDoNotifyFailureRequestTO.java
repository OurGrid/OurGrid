package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class LWPDoNotifyFailureRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.LWP_DO_NOTIFY_FAILURE;
	
	
	private String peerPublicKey;
	private String peerAddress;
	private String peerID;
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerPublicKey(String peerPublicKey) {
		this.peerPublicKey = peerPublicKey;
	}

	public String getPeerPublicKey() {
		return peerPublicKey;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}

	public String getPeerID() {
		return peerID;
	}
}
