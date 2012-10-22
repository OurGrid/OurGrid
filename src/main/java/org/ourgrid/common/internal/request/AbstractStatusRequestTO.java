package org.ourgrid.common.internal.request;

import org.ourgrid.common.internal.IRequestTO;


public abstract class AbstractStatusRequestTO implements IRequestTO {

	
	private boolean canStatusBeUsed;
	
	private String clientAddress;
	

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}
	
	public void setCanStatusBeUsed(boolean canStatusBeUsed) {
		this.canStatusBeUsed = canStatusBeUsed;
	}

	public boolean canStatusBeUsed() {
		return canStatusBeUsed;
	}
}
