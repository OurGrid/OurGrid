package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class StopBrokerRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.STOP_BROKER;
	
	
	private String senderPublicKey;
	private boolean isThisMyPublicKey;
	private boolean canComponentBeUsed;
	
	
	public String getRequestType() {
		return REQUEST_TYPE;
	
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public boolean isThisMyPublicKey() {
		return isThisMyPublicKey;
	}

	public void setThisMyPublicKey(boolean isThisMyPublicKey) {
		this.isThisMyPublicKey = isThisMyPublicKey;
	}

	public boolean canComponentBeUsed() {
		return canComponentBeUsed;
	}

	public void setCanComponentBeUsed(boolean canComponentBeUsed) {
		this.canComponentBeUsed = canComponentBeUsed;
	}
}
