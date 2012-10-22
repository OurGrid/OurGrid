package org.ourgrid.peer.response;

import org.ourgrid.common.internal.IResponseTO;

public class RemotePreemptedWorkerResponseTO implements IResponseTO {

	private static final String RESPONSE_TYPE = PeerResponseConstants.REMOTE_PREEMPTED_WORKER;
	
	
	private String rwpcAddress;
	private String rwmPublicKey;
	

	public String getResponseType() {
		return RESPONSE_TYPE;
	}

	public void setRwpcAddress(String rwpcAddress) {
		this.rwpcAddress = rwpcAddress;
	}

	public String getRwpcAddress() {
		return rwpcAddress;
	}

	public String getRwmPublicKey() {
		return rwmPublicKey;
	}

	public void setRwmPublicKey(String rwmPublicKey) {
		this.rwmPublicKey = rwmPublicKey;
	}
	
}
