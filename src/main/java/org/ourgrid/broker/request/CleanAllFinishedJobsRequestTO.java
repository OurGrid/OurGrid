package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class CleanAllFinishedJobsRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.CLEAN_ALL_FINISHED_JOBS;
	
	
	private String senderPublicKey;
	private boolean isThisMyPublicKey;
	private boolean canComponentBeUsed;
	private String brokerControlClientAddress;
	
	
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

	public void setBrokerControlClientAddress(String brokerControlClientAddress) {
		this.brokerControlClientAddress = brokerControlClientAddress;
	}

	public String getBrokerControlClientAddress() {
		return brokerControlClientAddress;
	}
}
