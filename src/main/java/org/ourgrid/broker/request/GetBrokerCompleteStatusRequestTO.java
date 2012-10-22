package org.ourgrid.broker.request;

import org.ourgrid.broker.business.requester.BrokerRequestConstants;
import org.ourgrid.common.internal.request.AbstractStatusRequestTO;

public class GetBrokerCompleteStatusRequestTO extends AbstractStatusRequestTO {

	
	private final String REQUEST_TYPE = BrokerRequestConstants.GET_COMPLETE_STATUS;
	
	
	private long uptime;
	private String configuration;
	private String myAddress;
	
	
	public long getUptime() {
		return uptime;
	}

	public void setUptime(long uptime) {
		this.uptime = uptime;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}