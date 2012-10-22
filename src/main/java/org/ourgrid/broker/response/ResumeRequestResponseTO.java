package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.internal.IResponseTO;

public class ResumeRequestResponseTO implements IResponseTO {
	
	private static final String RESPONSE_TYPE = BrokerResponseConstants.RESUME_REQUEST;
	
	
	private String peerAddress;
	private long requestID;
	
	

	/**
	 * @return the requestID
	 */
	public long getRequestID() {
		return requestID;
	}


	/**
	 * @param requestID the requestID to set
	 */
	public void setRequestID(long requestID) {
		this.requestID = requestID;
	}


	public String getResponseType() {
		return RESPONSE_TYPE;
	}


	/**
	 * @return the peerAddress
	 */
	public String getPeerAddress() {
		return peerAddress;
	}


	/**
	 * @param peerAddress the peerAddress to set
	 */
	public void setPeerAddress(String peerAddress) {
		this.peerAddress = peerAddress;
	}

}