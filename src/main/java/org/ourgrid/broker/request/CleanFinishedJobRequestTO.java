package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.IRequestTO;




public class CleanFinishedJobRequestTO implements IRequestTO {

	
	private String REQUEST_TYPE = BrokerRequestConstants.CLEAN_FINISHED_JOB;
	
	
	private String senderPublicKey;
	private boolean isThisMyPublicKey;
	private boolean canComponentBeUsed;
	private String brokerControlClientAddress;
	private int jobID;
	
	
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

	public void setJobID(int jobID) {
		this.jobID = jobID;
	}

	public int getJobID() {
		return jobID;
	}
}
