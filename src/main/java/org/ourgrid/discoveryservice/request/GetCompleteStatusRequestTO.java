package org.ourgrid.discoveryservice.request;

import org.ourgrid.common.internal.IRequestTO;

public class GetCompleteStatusRequestTO implements IRequestTO {
	
	
	private static final String REQUEST_TYPE = DiscoveryServiceRequestConstants.GET_COMPLETE_STATUS;
	
	
	private boolean canStatusBeUsed;
	private long upTime;
	private String propConfDir; 
	private String contextString;
	private String clientAddress;
	private String myAddress;
	

	public String getRequestType() {
		return REQUEST_TYPE;
	}

	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public String getPropConfDir() {
		return propConfDir;
	}

	public void setPropConfDir(String propConfDir) {
		this.propConfDir = propConfDir;
	}

	public String getContextString() {
		return contextString;
	}

	public void setContextString(String contextString) {
		this.contextString = contextString;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setMyAddress(String myAddress) {
		this.myAddress = myAddress;
	}

	public String getMyAddress() {
		return myAddress;
	}
}
