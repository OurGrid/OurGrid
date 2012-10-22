package org.ourgrid.broker.response;

import org.ourgrid.broker.communication.sender.BrokerResponseConstants;
import org.ourgrid.common.interfaces.to.BrokerCompleteStatus;
import org.ourgrid.common.internal.response.AbstractStatusResponseTO;



public class HereIsBrokerCompleteStatusResponseTO extends AbstractStatusResponseTO {
	
	
	private final String RESPONSE_TYPE = BrokerResponseConstants.HERE_IS_COMPLETE_STATUS;

	
	private BrokerCompleteStatus completeStatus;
	private String myAddress;
	
	
	public String getResponseType() {
		return this.RESPONSE_TYPE;
	}

	
	public void setCompleteStatus(BrokerCompleteStatus completeStatus) {
		this.completeStatus = completeStatus;
	}

	public BrokerCompleteStatus getCompleteStatus() {
		return completeStatus;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
