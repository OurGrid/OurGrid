package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class LWPDoNotifyRecoveryRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.LWP_DO_NOTIFY_RECOVERY;
	
	
	private String peerAddress;
	private String peerID;
	private boolean isClientDeployed;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

	public String getPeerAddress() {
		return peerAddress;
	}

	public void setPeerID(String peerID) {
		this.peerID = peerID;
	}

	public String getPeerID() {
		return peerID;
	}

	public void setClientDeployed(boolean isClientDeployed) {
		this.isClientDeployed = isClientDeployed;
	}

	public boolean isClientDeployed() {
		return isClientDeployed;
	}
}
